package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.entity.IEntityLivingData;

public class HullbreakerEntity extends EntityWaterMob {
    private static final DataParameter<Integer> INTEREST_LEVEL = EntityDataManager.createKey(HullbreakerEntity.class, DataSerializers.VARINT);
    private static final int PART_COUNT = 5;
    private final int[] partIds = new int[] {-1, -1, -1, -1, -1};
    private final float[] yawBuffer = new float[128];
    private int yawPointer = -1;
    private float landProgress;
    private float prevLandProgress;
    private float fishPitch;
    private float prevFishPitch;
    private float pulseAmount;
    private float prevPulseAmount;
    private int swimChangeCooldown;
    private int attackCooldown;
    private int blockBreakCooldown;
    private double swimVecX;
    private double swimVecY;
    private double swimVecZ;

    public HullbreakerEntity(World world) {
        super(world);
        setSize(3.6F, 2.8F);
        experienceValue = 50;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(INTEREST_LEVEL, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new HullbreakerAttackAI());
        tasks.addTask(2, new HullbreakerSwimAI());
        tasks.addTask(2, new HullbreakerFindTargetAI());
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(400.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
    }

    @Override
    public void onLivingUpdate() {
        tickMultipart();
        super.onLivingUpdate();
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        prevPulseAmount = pulseAmount;
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        boolean grounded = onGround && !isInWater();
        if (grounded && landProgress < 5.0F) {
            landProgress++;
        } else if (!grounded && landProgress > 0.0F) {
            landProgress--;
        }
        pulseAmount += getInterestLevel() * 0.45F;
        updateFishPitch();
        if (!world.isRemote) {
            ensureParts();
            if (isInWater()) {
                setAir(300);
            } else {
                handleDryingOut();
            }
        }
        if (isInWater()) {
            motionX += swimVecX * 0.04D;
            motionY += swimVecY * 0.035D;
            motionZ += swimVecZ * 0.04D;
            if (collidedHorizontally) {
                motionY += 0.08D;
            }
            motionX *= 0.9D;
            motionY *= 0.9D;
            motionZ *= 0.9D;
            faceMotion();
        } else {
            motionX *= 0.85D;
            motionZ *= 0.85D;
        }
        if (blockBreakCooldown > 0) {
            blockBreakCooldown--;
        }
    }

    private void updateFishPitch() {
        double horizontal = Math.sqrt(motionX * motionX + motionZ * motionZ);
        float target = isInWater() && horizontal > 0.02D ? (float) (-(Math.atan2(motionY, horizontal) * 57.2957763671875D)) : 0.0F;
        fishPitch = approachAngle(fishPitch, MathHelper.clamp(target, -80.0F, 80.0F), 2.5F);
    }

    private void faceMotion() {
        double horizontal = Math.sqrt(motionX * motionX + motionZ * motionZ);
        if (horizontal > 0.03D) {
            rotationYaw = (float) (-Math.atan2(motionX, motionZ) * 57.2957763671875D);
            renderYawOffset = rotationYaw;
        }
    }

    private void updateSwimmingVector() {
        EntityLivingBase target = getAttackTarget();
        if (target != null && target.isEntityAlive() && isInWater()) {
            Vec3d toward = new Vec3d(target.posX - posX, target.posY + target.height * 0.5D - posY, target.posZ - posZ).normalize();
            swimVecX = toward.x * 1.45D;
            swimVecY = toward.y * 0.85D;
            swimVecZ = toward.z * 1.45D;
            setInterestLevel(3);
            return;
        }
        if (--swimChangeCooldown <= 0) {
            swimChangeCooldown = 30 + rand.nextInt(50);
            double angle = rand.nextDouble() * Math.PI * 2.0D;
            swimVecX = Math.cos(angle) * 0.75D;
            swimVecY = rand.nextDouble() * 0.7D - 0.25D;
            swimVecZ = Math.sin(angle) * 0.75D;
            setInterestLevel(0);
        }
    }

    private void handleDryingOut() {
        setAir(getAir() - 1);
        if (onGround) {
            motionX += (rand.nextFloat() * 2.0F - 1.0F) * 0.12F;
            motionY += 0.25D;
            motionZ += (rand.nextFloat() * 2.0F - 1.0F) * 0.12F;
        }
        if (getAir() <= -20) {
            setAir(0);
            attackEntityFrom(DamageSource.DROWN, 4.0F);
        }
    }

    private void tickMultipart() {
        if (yawPointer == -1) {
            for (int i = 0; i < yawBuffer.length; i++) {
                yawBuffer[i] = renderYawOffset;
            }
        }
        if (++yawPointer == yawBuffer.length) {
            yawPointer = 0;
        }
        yawBuffer[yawPointer] = renderYawOffset;
        if (!world.isRemote) {
            movePart(0, offsetFromBody(0.0D, 0.2D, 3.3D, fishPitch, rotationYaw));
            movePart(1, offsetFromBody(swimDegree(1.0F, 4.0F), 0.0D, -3.0D, fishPitch, getYawFromBuffer(2, 1.0F)));
            movePart(2, getPartPosition(1).add(offsetFromYaw(swimDegree(1.0F, 3.0F), 0.0D, -2.0D, fishPitch, getYawFromBuffer(4, 1.0F))));
            movePart(3, getPartPosition(2).add(offsetFromYaw(swimDegree(1.4F, 2.0F), 0.0D, -2.4D, fishPitch, getYawFromBuffer(6, 1.0F))));
            movePart(4, getPartPosition(3).add(offsetFromYaw(swimDegree(1.3F, 1.0F), 0.0D, -2.6D, fishPitch, getYawFromBuffer(8, 1.0F))));
        }
    }

    private Vec3d offsetFromBody(double x, double y, double z, float pitch, float yaw) {
        return getPositionVector().add(0.0D, height * 0.5D, 0.0D).add(offsetFromYaw(x, y, z, pitch, yaw));
    }

    private Vec3d offsetFromYaw(double x, double y, double z, float pitch, float yaw) {
        double yawRad = -yaw * 0.017453292D;
        double pitchRad = -pitch * 0.017453292D;
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);
        double cosPitch = Math.cos(pitchRad);
        double sinPitch = Math.sin(pitchRad);
        double zp = z * cosPitch - y * sinPitch;
        double yp = y * cosPitch + z * sinPitch;
        return new Vec3d(x * cosYaw - zp * sinYaw, yp, zp * cosYaw + x * sinYaw);
    }

    private void movePart(int index, Vec3d pos) {
        Entity entity = partIds[index] == -1 ? null : world.getEntityByID(partIds[index]);
        if (entity instanceof HullbreakerPartEntity && !entity.isDead) {
            entity.setPosition(pos.x, pos.y - entity.height * 0.5D, pos.z);
        }
    }

    private Vec3d getPartPosition(int index) {
        Entity entity = partIds[index] == -1 ? null : world.getEntityByID(partIds[index]);
        return entity == null ? getPositionVector().add(0.0D, height * 0.5D, 0.0D) : entity.getPositionVector().add(0.0D, entity.height * 0.5D, 0.0D);
    }

    private void ensureParts() {
        for (int i = 0; i < PART_COUNT; i++) {
            Entity entity = partIds[i] == -1 ? null : world.getEntityByID(partIds[i]);
            HullbreakerPartEntity part;
            if (entity instanceof HullbreakerPartEntity && !entity.isDead) {
                part = (HullbreakerPartEntity) entity;
            } else {
                float width = i == 0 ? 3.0F : i == 4 ? 1.5F : 2.0F;
                float height = i == 0 ? 2.0F : i < 3 ? 1.5F : 1.0F;
                part = new HullbreakerPartEntity(world, this, i, width, height);
                part.setPosition(posX, posY, posZ);
                world.spawnEntity(part);
                partIds[i] = part.getEntityId();
            }
            part.setParentId(getEntityId());
            part.setPartIndex(i);
        }
    }

    private double swimDegree(float width, float sinOffset) {
        double move = Math.cos(ticksExisted * 0.33F + sinOffset) * Math.min(getMotionMagnitude() * 4.0D, 1.0D) * width * 0.8D;
        double idle = Math.sin(ticksExisted * 0.05F + sinOffset) * width * 0.5D;
        return (move + idle * (1.0D - Math.min(getMotionMagnitude() * 4.0D, 1.0D))) * (1.0D - getLandProgress(1.0F));
    }

    private double getMotionMagnitude() {
        return Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
    }

    public float getYawFromBuffer(int pointer, float partialTick) {
        int i = this.yawPointer - pointer & 127;
        int j = this.yawPointer - pointer - 1 & 127;
        float d0 = this.yawBuffer[j];
        float d1 = MathHelper.wrapDegrees(this.yawBuffer[i] - d0);
        return d0 + d1 * partialTick;
    }

    private void attackAroundMouth(EntityLivingBase target) {
        if (attackCooldown > 0 || target == null || !target.isEntityAlive()) {
            return;
        }
        Entity mouth = getPartEntity(0);
        double distance = mouth == null ? getDistance(target) : mouth.getDistance(target);
        if (distance <= target.width + 3.0D && target.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue())) {
            target.knockBack(this, 1.2F, posX - target.posX, posZ - target.posZ);
            playSound(ACSoundRegistry.HULLBREAKER_ATTACK, getSoundVolume(), getSoundPitch());
            breakBlock();
            attackCooldown = 25;
        }
    }

    private Entity getPartEntity(int index) {
        return partIds[index] == -1 ? null : world.getEntityByID(partIds[index]);
    }

    private void breakBlock() {
        if (world.isRemote || blockBreakCooldown > 0 || !world.getGameRules().getBoolean("mobGriefing")) {
            return;
        }
        Entity head = getPartEntity(0);
        AxisAlignedBB box = (head == null ? getEntityBoundingBox() : head.getEntityBoundingBox()).grow(1.2D);
        boolean broke = false;
        for (BlockPos pos : BlockPos.getAllInBoxMutable(new BlockPos(box.minX, box.minY - 1.0D, box.minZ), new BlockPos(box.maxX, box.maxY + 1.0D, box.maxZ))) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            float hardness = state.getBlockHardness(world, pos);
            if (block != Blocks.AIR && hardness >= 0.0F && hardness <= 15.0F && state.getMaterial().isSolid()) {
                world.destroyBlock(pos, true);
                broke = true;
            }
        }
        if (broke) {
            motionX *= 0.6D;
            motionZ *= 0.6D;
            blockBreakCooldown = 3;
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!world.isRemote && source.getTrueSource() instanceof EntityLivingBase) {
            setAttackTarget((EntityLivingBase) source.getTrueSource());
        }
        return super.attackEntityFrom(source, source.isProjectile() ? amount * 0.65F : amount);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        removeParts();
    }

    @Override
    public void setDead() {
        super.setDead();
        removeParts();
    }

    private void removeParts() {
        if (world.isRemote) {
            return;
        }
        for (int id : partIds) {
            Entity entity = id == -1 ? null : world.getEntityByID(id);
            if (entity instanceof HullbreakerPartEntity) {
                entity.setDead();
            }
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (!world.isRemote) {
            ensureParts();
        }
        return livingdata;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    public boolean getCanSpawnHere() {
        return isInWater() && isNotColliding() && posY < world.getSeaLevel() - 25;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isInWater() ? ACSoundRegistry.HULLBREAKER_IDLE : ACSoundRegistry.HULLBREAKER_LAND_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return isInWater() ? ACSoundRegistry.HULLBREAKER_HURT : ACSoundRegistry.HULLBREAKER_LAND_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isInWater() ? ACSoundRegistry.HULLBREAKER_DEATH : ACSoundRegistry.HULLBREAKER_LAND_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return super.getSoundVolume() + 2.0F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("InterestLevel", getInterestLevel());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setInterestLevel(compound.getInteger("InterestLevel"));
    }

    public float getFishPitch(float partialTick) {
        return prevFishPitch + (fishPitch - prevFishPitch) * partialTick;
    }

    public float getLandProgress(float partialTicks) {
        return (prevLandProgress + (landProgress - prevLandProgress) * partialTicks) * 0.2F;
    }

    public float getPulseAmount(float partialTicks) {
        return (prevPulseAmount + (pulseAmount - prevPulseAmount) * partialTicks) * 0.2F;
    }

    public int getInterestLevel() {
        return dataManager.get(INTEREST_LEVEL);
    }

    public void setInterestLevel(int level) {
        dataManager.set(INTEREST_LEVEL, MathHelper.clamp(level, 0, 3));
    }

    private float approachAngle(float value, float target, float step) {
        return value + MathHelper.clamp(MathHelper.wrapDegrees(target - value), -step, step);
    }

    private class HullbreakerSwimAI extends EntityAIBase {
        private HullbreakerSwimAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return isInWater();
        }

        @Override
        public void updateTask() {
            updateSwimmingVector();
        }
    }

    private class HullbreakerAttackAI extends EntityAIBase {
        private HullbreakerAttackAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                return;
            }
            getLookHelper().setLookPositionWithEntity(target, 90.0F, 30.0F);
            updateSwimmingVector();
            attackAroundMouth(target);
        }
    }

    private class HullbreakerFindTargetAI extends EntityAIBase {
        private int cooldown;

        private HullbreakerFindTargetAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return !world.isRemote && isInWater() && (getAttackTarget() == null || !getAttackTarget().isEntityAlive()) && --cooldown <= 0;
        }

        @Override
        public void startExecuting() {
            cooldown = 20 + rand.nextInt(30);
            EntityPlayer nearest = null;
            for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(32.0D, 16.0D, 32.0D), HullbreakerEntity.this::canAttackPlayer)) {
                if (canEntityBeSeen(player) && (nearest == null || getDistanceSq(player) < getDistanceSq(nearest))) {
                    nearest = player;
                }
            }
            if (nearest != null) {
                setAttackTarget(nearest);
            }
        }
    }

    private boolean canAttackPlayer(EntityPlayer player) {
        return player != null && player.isEntityAlive() && player.isInWater() && !player.capabilities.disableDamage && world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }
}
