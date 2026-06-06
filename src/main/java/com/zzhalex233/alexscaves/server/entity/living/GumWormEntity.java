package com.zzhalex233.alexscaves.server.entity.living;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class GumWormEntity extends EntityMob {
    private static final DataParameter<Boolean> Z_ROT_DIRECTION = EntityDataManager.createKey(GumWormEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LEAPING = EntityDataManager.createKey(GumWormEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BITING = EntityDataManager.createKey(GumWormEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DIGGING = EntityDataManager.createKey(GumWormEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> TARGET_DIG_PITCH = EntityDataManager.createKey(GumWormEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> RIDING_SEGMENT_ID = EntityDataManager.createKey(GumWormEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LEFT_HOOK_ID = EntityDataManager.createKey(GumWormEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> RIGHT_HOOK_ID = EntityDataManager.createKey(GumWormEntity.class, DataSerializers.VARINT);
    private float prevZRot;
    private float zRot;
    private float prevMouthOpenProgress;
    private float mouthOpenProgress;
    private float prevDigPitch;
    private float digPitch;
    private int timeBetweenAttacks;
    private int leapAttackCooldown;
    private int digSoundTime;
    private int attackNoiseCooldown;
    private int stopDiggingNoiseCooldown;
    private boolean wasDiggingLastTick;
    private int ridingModeTicks;
    private int riderLeapTime;
    private int maxRiderLeapTime = 1;
    private float riderLeapRot;
    private EntityPlayer ridingPlayer;

    public GumWormEntity(World world) {
        super(world);
        setSize(3.25F, 3.25F);
        stepHeight = 1.0F;
        experienceValue = 25;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(Z_ROT_DIRECTION, false);
        dataManager.register(LEAPING, false);
        dataManager.register(BITING, false);
        dataManager.register(DIGGING, false);
        dataManager.register(TARGET_DIG_PITCH, 0.0F);
        dataManager.register(RIDING_SEGMENT_ID, -1);
        dataManager.register(LEFT_HOOK_ID, -1);
        dataManager.register(RIGHT_HOOK_ID, -1);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new GumWormAttackAI());
        tasks.addTask(2, new GumWormDigRandomlyAI());
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(128.0D);
    }

    @Override
    public void onLivingUpdate() {
        noClip = isDigging() && !isLeaping();
        prevZRot = zRot;
        prevMouthOpenProgress = mouthOpenProgress;
        prevDigPitch = digPitch;
        super.onLivingUpdate();
        noClip = isDigging() && !isLeaping();
        mouthOpenProgress = approach(mouthOpenProgress, isMouthOpen() ? 10.0F : 0.0F);
        digPitch = approachAngle(digPitch, getTargetDigPitch(), 5.0F);
        if (isMoving()) {
            zRot += dataManager.get(Z_ROT_DIRECTION) ? -10.0F : 10.0F;
            if (!world.isRemote && rand.nextInt(300) == 0) {
                dataManager.set(Z_ROT_DIRECTION, rand.nextBoolean());
            }
        } else {
            zRot = approachAngle(zRot, 0.0F, 2.0F);
        }
        updateDigging();
        if (timeBetweenAttacks > 0) {
            timeBetweenAttacks--;
        }
        if (leapAttackCooldown > 0) {
            leapAttackCooldown--;
        }
        if (attackNoiseCooldown > 0) {
            attackNoiseCooldown--;
        }
        if (stopDiggingNoiseCooldown > 0) {
            stopDiggingNoiseCooldown--;
        }
        if (!world.isRemote && ticksExisted % 40 == 0) {
            ensureSegments();
        }
        if (!world.isRemote) {
            tickHookState();
            if (isRidingMode()) {
                tickRidingControl();
            }
            if (ridingModeTicks > 0) {
                ridingModeTicks--;
            }
        }
    }

    private void updateDigging() {
        if (isDigging()) {
            setNoGravity(true);
            if (!world.isRemote && ++digSoundTime >= 24) {
                digSoundTime = 0;
                playSound(ACSoundRegistry.GUM_WORM_DIG_LOOP, getSoundVolume(), getSoundPitch());
            }
            if (world.isRemote && ticksExisted % 2 == 0) {
                spawnDigParticles();
            }
        } else {
            setNoGravity(false);
            digSoundTime = 0;
        }
        if (wasDiggingLastTick != isDigging()) {
            wasDiggingLastTick = isDigging();
            if (!isDigging()) {
                attemptPlayStopDiggingNoise();
            }
        }
    }

    private void moveDiggingToward(double x, double y, double z, double speed) {
        setDigging(true);
        getNavigator().clearPath();
        Vec3d delta = new Vec3d(x - posX, y - posY, z - posZ);
        double length = Math.sqrt(delta.x * delta.x + delta.y * delta.y + delta.z * delta.z);
        if (length > 0.001D) {
            Vec3d motion = delta.scale(speed / length);
            motionX = motionX * 0.75D + motion.x * 0.18D;
            motionY = motionY * 0.75D + motion.y * 0.18D;
            motionZ = motionZ * 0.75D + motion.z * 0.18D;
            rotationYaw = (float) (-Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
            renderYawOffset = rotationYaw;
            setTargetDigPitch((float) (-(Math.atan2(motionY, MathHelper.sqrt(motionX * motionX + motionZ * motionZ)) * 180.0D / Math.PI)));
        }
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (isDigging()) {
            move(MoverType.SELF, motionX, motionY, motionZ);
            motionX *= 0.8D;
            motionY *= 0.8D;
            motionZ *= 0.8D;
            return;
        }
        super.travel(strafe, vertical, forward);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.IN_WALL || source == DamageSource.FALL || source == DamageSource.DROWN) {
            return false;
        }
        if (source.getTrueSource() != null && isRidingPlayer(source.getTrueSource())) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    public boolean attackAllAroundMouth(float damageAmount, float knockbackAmount) {
        boolean attackedMainTarget = false;
        AxisAlignedBB hurtBox = getEntityBoundingBox().grow(isLeaping() ? 3.0D : 1.0D);
        EntityLivingBase target = getAttackTarget();
        for (EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class, hurtBox)) {
            if (living != this && !(living instanceof GumWormEntity)) {
                if (living.attackEntityFrom(DamageSource.causeMobDamage(this), damageAmount)) {
                    living.knockBack(this, knockbackAmount, posX - living.posX, posZ - living.posZ);
                }
                if (living == target) {
                    attackedMainTarget = true;
                }
            }
        }
        return attackedMainTarget;
    }

    private void spawnDigParticles() {
        BlockPos pos = new BlockPos(posX, posY - 0.1D, posZ);
        IBlockState state = world.getBlockState(pos);
        if (!canDigBlock(world, pos)) {
            state = world.getBlockState(pos.down());
        }
        if (canDigBlock(world, pos) || canDigBlock(world, pos.down())) {
            int id = net.minecraft.block.Block.getStateId(state);
            for (int i = 0; i < 12; i++) {
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, (rand.nextDouble() - 0.5D) * 0.4D, rand.nextDouble() * 0.3D, (rand.nextDouble() - 0.5D) * 0.4D, id);
            }
        }
    }

    private void ensureSegments() {
        int segments = 0;
        GumWormSegmentEntity ridingSegment = null;
        for (GumWormSegmentEntity segment : world.getEntitiesWithinAABB(GumWormSegmentEntity.class, getEntityBoundingBox().grow(80.0D))) {
            if (segment.getHeadEntity() == this) {
                segments++;
                if (segment.getIndex() == 3 || ridingSegment == null) {
                    ridingSegment = segment;
                }
            }
        }
        if (segments == 0) {
            GumWormSegmentEntity.createWormSegmentsFor(this, 15 + rand.nextInt(5));
        } else if (ridingSegment != null) {
            setRidingSegmentId(ridingSegment.getEntityId());
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (!world.isRemote) {
            GumWormSegmentEntity.createWormSegmentsFor(this, 15 + rand.nextInt(5));
        }
        return livingdata;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int teeth = 1 + rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < teeth; i++) {
            dropItem(ACItemRegistry.GUM_WORM_TOOTH.item(), 1);
        }
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
        if (!isDigging() && !isLeaping()) {
            super.updateFallState(y, onGroundIn, state, pos);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GUM_WORM_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GUM_WORM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GUM_WORM_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return super.getSoundVolume() * 3.0F;
    }

    public void attemptPlayAttackNoise() {
        if (attackNoiseCooldown == 0) {
            playSound(ACSoundRegistry.GUM_WORM_ATTACK, getSoundVolume(), getSoundPitch());
            attackNoiseCooldown = 70;
        }
    }

    public void attemptPlayStopDiggingNoise() {
        if (stopDiggingNoiseCooldown == 0) {
            playSound(ACSoundRegistry.GUM_WORM_DIG_STOP, getSoundVolume(), getSoundPitch());
            stopDiggingNoiseCooldown = 10;
        }
    }

    public boolean isMouthOpen() {
        return isLeaping() || isBiting();
    }

    public boolean isMoving() {
        return motionX * motionX + motionY * motionY + motionZ * motionZ > 0.01D;
    }

    public boolean isLeaping() {
        return dataManager.get(LEAPING);
    }

    public void setLeaping(boolean leaping) {
        dataManager.set(LEAPING, leaping);
    }

    public boolean isBiting() {
        return dataManager.get(BITING);
    }

    public void setBiting(boolean biting) {
        dataManager.set(BITING, biting);
    }

    public boolean isDigging() {
        return dataManager.get(DIGGING);
    }

    public void setDigging(boolean digging) {
        dataManager.set(DIGGING, digging);
    }

    public boolean getZRotDirection() {
        return dataManager.get(Z_ROT_DIRECTION);
    }

    public void setTargetDigPitch(float pitch) {
        dataManager.set(TARGET_DIG_PITCH, pitch);
    }

    public float getTargetDigPitch() {
        return dataManager.get(TARGET_DIG_PITCH);
    }

    public float getMouthOpenProgress(float partialTicks) {
        return (prevMouthOpenProgress + (mouthOpenProgress - prevMouthOpenProgress) * partialTicks) * 0.1F;
    }

    public float getBodyZRot(float partialTicks) {
        return prevZRot + (zRot - prevZRot) * partialTicks;
    }

    public float getViewXRot(float partialTicks) {
        return prevDigPitch + (digPitch - prevDigPitch) * partialTicks;
    }

    public Vec3d getHookPosition(int side) {
        Vec3d offset = new Vec3d(side * -1.0D, -0.5D, -1.15D).rotatePitch(-getViewXRot(1.0F) * 0.017453292F).rotateYaw(-renderYawOffset * 0.017453292F);
        return getPositionVector().add(0.0D, 0.5D * width, 0.0D).add(offset);
    }

    public void onMounted() {
        BlockPos pos = new BlockPos(posX, posY - 1.0D, posZ);
        while (pos.getY() < world.getHeight() && !world.getBlockState(pos).getBlock().isAir(world.getBlockState(pos), world, pos) && world.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
            pos = pos.up();
        }
        setPosition(posX, pos.getY(), posZ);
    }

    public Entity getRidingSegment() {
        int id = dataManager.get(RIDING_SEGMENT_ID);
        return id == -1 ? null : world.getEntityByID(id);
    }

    public void setRidingSegmentId(int id) {
        dataManager.set(RIDING_SEGMENT_ID, id);
    }

    public void setHookId(boolean left, int id) {
        dataManager.set(left ? LEFT_HOOK_ID : RIGHT_HOOK_ID, id);
    }

    public Entity getHook(boolean left) {
        int id = dataManager.get(left ? LEFT_HOOK_ID : RIGHT_HOOK_ID);
        Entity entity = id == -1 ? null : world.getEntityByID(id);
        return entity != null && entity.isEntityAlive() ? entity : null;
    }

    public boolean hasARidingHook() {
        return getHook(true) != null || getHook(false) != null;
    }

    public boolean isRidingMode() {
        return ridingModeTicks > 0;
    }

    public boolean isRidingPlayer(Entity player) {
        Entity leftOwner = getHook(true) instanceof CandyCaneHookEntity ? ((CandyCaneHookEntity) getHook(true)).getOwner() : null;
        Entity rightOwner = getHook(false) instanceof CandyCaneHookEntity ? ((CandyCaneHookEntity) getHook(false)).getOwner() : null;
        return leftOwner == player && rightOwner == player;
    }

    public void tickController(EntityPlayer passenger) {
        ridingPlayer = passenger;
        if (hasARidingHook()) {
            ridingModeTicks = 10;
        }
    }

    public void onPlayerJump(int time) {
        int leapFor = (int) Math.ceil(time * 0.2F) + 10;
        riderLeapTime = leapFor;
        maxRiderLeapTime = Math.max(1, leapFor);
    }

    private void tickHookState() {
        if (getHook(true) == null && dataManager.get(LEFT_HOOK_ID) != -1) {
            dataManager.set(LEFT_HOOK_ID, -1);
        }
        if (getHook(false) == null && dataManager.get(RIGHT_HOOK_ID) != -1) {
            dataManager.set(RIGHT_HOOK_ID, -1);
        }
    }

    private void tickRidingControl() {
        if (!(ridingPlayer != null && ridingPlayer.getRidingEntity() instanceof GumWormSegmentEntity && ((GumWormSegmentEntity) ridingPlayer.getRidingEntity()).getHeadEntity() == this)) {
            return;
        }
        getNavigator().clearPath();
        setAttackTarget(null);
        boolean validRider = isRidingPlayer(ridingPlayer);
        if (riderLeapTime > 0 && validRider) {
            float f = Math.max(1.0F, maxRiderLeapTime);
            float progress = 1.0F - riderLeapTime / f;
            Vec3d leap = new Vec3d(0.0D, Math.sin(progress * Math.PI * 1.5D) * 2.0D, 2.0D).rotateYaw(-riderLeapRot * 0.017453292F);
            setLeaping(true);
            setDigging(false);
            motionX = leap.x;
            motionY = leap.y;
            motionZ = leap.z;
            rotationYaw = riderLeapRot;
            renderYawOffset = rotationYaw;
            riderLeapTime--;
        } else {
            setLeaping(false);
            Vec3d target = new Vec3d(validRider ? ridingPlayer.moveStrafing * 2.5D : 0.0D, 0.0D, 10.0D).rotateYaw(-renderYawOffset * 0.017453292F).add(getPositionVector());
            moveDiggingToward(target.x, target.y, target.z, 3.0D);
            setTargetDigPitch(collidedHorizontally ? -45.0F : 0.0F);
            riderLeapRot = rotationYaw;
        }
        if (isMouthOpen()) {
            attackAllAroundMouth((float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue(), 2.0F);
        }
        setBiting(false);
    }

    public static boolean canDigBlock(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getMaterial().isSolid() && state.isFullCube() && state.getBlockHardness(world, pos) >= 0.0F && state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.BARRIER;
    }

    public static boolean isSafeDig(World world, BlockPos pos) {
        return canDigBlock(world, pos) && canDigBlock(world, pos.down());
    }

    private float approach(float value, float target) {
        return value < target ? Math.min(target, value + 1.0F) : value > target ? Math.max(target, value - 1.0F) : value;
    }

    private float approachAngle(float value, float target, float step) {
        return value + MathHelper.clamp(MathHelper.wrapDegrees(target - value), -step, step);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Digging", isDigging());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setDigging(compound.getBoolean("Digging"));
    }

    private class GumWormAttackAI extends EntityAIBase {
        private int leapTime;

        private GumWormAttackAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return !isRidingMode() && target != null && target.isEntityAlive();
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                return;
            }
            double distance = getDistance(target);
            getLookHelper().setLookPositionWithEntity(target, 180.0F, 30.0F);
            if (leapAttackCooldown <= 0 && distance > 8.0D && distance < 32.0D && !isLeaping()) {
                Vec3d delta = new Vec3d(target.posX - posX, target.posY + 0.5D - posY, target.posZ - posZ).normalize();
                setDigging(false);
                setLeaping(true);
                setBiting(true);
                motionX = delta.x * 1.2D;
                motionY = Math.max(0.35D, delta.y * 0.7D + 0.35D);
                motionZ = delta.z * 1.2D;
                leapTime = 30;
                leapAttackCooldown = 100;
                attemptPlayAttackNoise();
            } else if (isLeaping()) {
                attackAllAroundMouth((float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue(), 1.2F);
                if (--leapTime <= 0 || onGround && leapTime < 20) {
                    setLeaping(false);
                    setBiting(false);
                    setDigging(true);
                }
            } else if (distance < width + target.width + 1.5F && timeBetweenAttacks <= 0) {
                setBiting(true);
                attackAllAroundMouth((float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue(), 0.8F);
                attemptPlayAttackNoise();
                timeBetweenAttacks = 25;
            } else {
                setBiting(false);
                moveDiggingToward(target.posX, target.posY, target.posZ, 1.4D);
            }
        }

        @Override
        public void resetTask() {
            setBiting(false);
            setLeaping(false);
            leapTime = 0;
        }
    }

    private class GumWormDigRandomlyAI extends EntityAIBase {
        private Vec3d target;
        private boolean surface;

        private GumWormDigRandomlyAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (isRidingMode()) {
                return false;
            }
            if (getAttackTarget() != null && getAttackTarget().isEntityAlive()) {
                return false;
            }
            if (!isDigging() && !onGround && !isEntityInsideOpaqueBlock()) {
                return false;
            }
            if (!isDigging() && !isEntityInsideOpaqueBlock() && rand.nextInt(20) != 0) {
                return false;
            }
            surface = isDigging() && ticksExisted % 300 < 40;
            target = generateDigPosition(getPosition(), 18, surface);
            return target != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !isRidingMode() && target != null && isDigging() && getDistanceSq(target.x, target.y, target.z) > 4.0D;
        }

        @Override
        public void startExecuting() {
            setDigging(true);
        }

        @Override
        public void updateTask() {
            if (target != null) {
                moveDiggingToward(target.x, target.y, target.z, 1.0D);
                if (surface && getDistanceSq(target.x, target.y, target.z) < 6.0D) {
                    setDigging(false);
                }
            }
        }

        @Override
        public void resetTask() {
            target = null;
            surface = false;
        }
    }

    private Vec3d generateDigPosition(BlockPos around, int radius, boolean surface) {
        for (int i = 0; i < 20; i++) {
            BlockPos pos = around.add(rand.nextInt(radius * 2 + 1) - radius, rand.nextInt(radius * 2 + 1) - radius, rand.nextInt(radius * 2 + 1) - radius);
            if (!world.isBlockLoaded(pos) || pos.getY() <= 1 || pos.getY() >= world.getHeight()) {
                continue;
            }
            if (surface) {
                while (!world.isAirBlock(pos) && pos.getY() < world.getHeight() - 1) {
                    pos = pos.up();
                }
                if (world.isAirBlock(pos)) {
                    return new Vec3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                }
            } else {
                while (world.isAirBlock(pos) && pos.getY() > 1) {
                    pos = pos.down();
                }
                if (isSafeDig(world, pos)) {
                    return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                }
            }
        }
        return null;
    }
}
