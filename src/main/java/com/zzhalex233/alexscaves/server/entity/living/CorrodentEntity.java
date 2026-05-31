package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class CorrodentEntity extends EntityMob {
    public static final int LIGHT_THRESHOLD = 7;
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_BITE = 1;
    private static final DataParameter<Boolean> DIGGING = EntityDataManager.createKey(CorrodentEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> AFRAID = EntityDataManager.createKey(CorrodentEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> DIG_PITCH = EntityDataManager.createKey(CorrodentEntity.class, DataSerializers.FLOAT);

    private final int[] partIds = new int[] {-1};
    private int animation = ANIMATION_NONE;
    private int animationTick;
    private float prevDigPitch;
    private float prevDigProgress;
    private float digProgress;
    private float prevFearProgress;
    private float fearProgress;
    private int timeDigging;
    private int fleeLightFor;
    private int digSoundTime;

    public CorrodentEntity(World world) {
        super(world);
        setSize(0.9F, 0.9F);
        stepHeight = 1.0F;
        experienceValue = 5;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(DIGGING, false);
        dataManager.register(AFRAID, false);
        dataManager.register(DIG_PITCH, 0.0F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new FearLightAI());
        tasks.addTask(2, new CorrodentMeleeAI());
        tasks.addTask(3, new RandomDigAI());
        tasks.addTask(7, new EntityAIWander(this, 1.0D, 20));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        noClip = isDigging();
        prevDigPitch = getDigPitch();
        prevDigProgress = digProgress;
        prevFearProgress = fearProgress;
        digProgress = approach(digProgress, isDigging() ? 5.0F : 0.0F);
        fearProgress = approach(fearProgress, isAfraid() ? 5.0F : 0.0F);
        if (!isDigging() || Math.abs(motionX) + Math.abs(motionY) + Math.abs(motionZ) < 0.03D) {
            setDigPitch(approachAngle(getDigPitch(), 0.0F, 10.0F));
        }
        super.onLivingUpdate();
        noClip = isDigging();
        if (animation != ANIMATION_NONE && ++animationTick >= 15) {
            setAnimation(ANIMATION_NONE);
        }
        if (fleeLightFor > 0) {
            fleeLightFor--;
        }
        updateDigging();
        tickMultipart();
    }

    private void tickMultipart() {
        if (world.isRemote) {
            return;
        }
        CorrodentTailEntity tail = ensureTail();
        if (tail != null) {
            tail.setToTransformation(this, new Vec3d(0.0D, 0.0D, -0.9D), getDigPitch(), renderYawOffset);
        }
    }

    private CorrodentTailEntity ensureTail() {
        Entity entity = partIds[0] == -1 ? null : world.getEntityByID(partIds[0]);
        if (entity instanceof CorrodentTailEntity && !entity.isDead) {
            CorrodentTailEntity tail = (CorrodentTailEntity) entity;
            tail.setParentId(getEntityId());
            return tail;
        }
        CorrodentTailEntity tail = new CorrodentTailEntity(world, this);
        tail.setPosition(posX, posY, posZ);
        world.spawnEntity(tail);
        partIds[0] = tail.getEntityId();
        return tail;
    }

    private void updateDigging() {
        if (isDigging()) {
            timeDigging++;
            setNoGravity(isEntityInsideOpaqueBlock());
            if (!world.isRemote && timeDigging == 1) {
                world.setEntityState(this, (byte) 77);
            }
            if (!world.isRemote && ++digSoundTime >= 24) {
                digSoundTime = 0;
                playSound(ACSoundRegistry.CORRODENT_DIG_LOOP, 0.55F, getSoundPitch());
            }
            if (world.isRemote && ticksExisted % 2 == 0) {
                spawnDigParticles();
            }
            if (!world.isRemote && timeDigging > 40 && !isEntityInsideOpaqueBlock() && onGround) {
                setDigging(false);
                motionY += 0.25D;
            }
            if (!isSafeDig(world, getPosition()) && isEntityInsideOpaqueBlock()) {
                motionY += canDigBlock(world, getPosition().up()) ? 0.08D : -0.06D;
            }
        } else {
            if (timeDigging > 0 && !world.isRemote) {
                playSound(ACSoundRegistry.CORRODENT_DIG_STOP, 0.8F, getSoundPitch());
                world.setEntityState(this, (byte) 77);
            }
            timeDigging = 0;
            digSoundTime = 0;
            setNoGravity(false);
        }
    }

    private void spawnDigParticles() {
        BlockPos pos = new BlockPos(posX, posY - 0.1D, posZ);
        IBlockState state = world.getBlockState(pos);
        if (!canDigBlock(world, pos)) {
            state = world.getBlockState(pos.down());
        }
        if (canDigBlock(world, pos) || canDigBlock(world, pos.down())) {
            int id = net.minecraft.block.Block.getStateId(state);
            for (int i = 0; i < 4; i++) {
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * 0.6D, posZ + (rand.nextDouble() - 0.5D) * width, (rand.nextDouble() - 0.5D) * 0.2D, rand.nextDouble() * 0.2D, (rand.nextDouble() - 0.5D) * 0.2D, id);
            }
        }
    }

    private float approach(float value, float target) {
        if (value < target) {
            return Math.min(target, value + 1.0F);
        }
        return value > target ? Math.max(target, value - 1.0F) : value;
    }

    private float approachAngle(float value, float target, float step) {
        return value + MathHelper.clamp(MathHelper.wrapDegrees(target - value), -step, step);
    }

    private void moveDiggingToward(double x, double y, double z, double speed) {
        setDigging(true);
        getNavigator().clearPath();
        Vec3d delta = new Vec3d(x - posX, y - posY, z - posZ);
        double length = Math.sqrt(delta.x * delta.x + delta.y * delta.y + delta.z * delta.z);
        if (length > 0.001D) {
            Vec3d motion = delta.scale(speed / length);
            this.motionX = this.motionX * 0.75D + motion.x * 0.18D;
            this.motionY = this.motionY * 0.75D + motion.y * 0.18D;
            this.motionZ = this.motionZ * 0.75D + motion.z * 0.18D;
            rotationYaw = (float) (-Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
            renderYawOffset = rotationYaw;
            setDigPitch(approachAngle(getDigPitch(), (float) (-(Math.atan2(motionY, MathHelper.sqrt(motionX * motionX + motionZ * motionZ)) * 180.0D / Math.PI)), 10.0F));
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 77) {
            for (int i = 0; i < 24; i++) {
                BlockPos pos = new BlockPos(posX, posY - 0.1D, posZ);
                IBlockState state = world.getBlockState(pos.down());
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + (rand.nextDouble() - 0.5D) * 1.2D, posY + rand.nextDouble() * 0.8D, posZ + (rand.nextDouble() - 0.5D) * 1.2D, (rand.nextDouble() - 0.5D) * 0.5D, rand.nextDouble() * 0.5D, (rand.nextDouble() - 0.5D) * 0.5D, net.minecraft.block.Block.getStateId(state));
            }
        } else if (id >= 80 && id <= 81) {
            animation = id - 80;
            animationTick = 0;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void setAnimation(int animation) {
        this.animation = animation;
        animationTick = 0;
        world.setEntityState(this, (byte) (80 + animation));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return source == DamageSource.IN_WALL && isDigging() || super.attackEntityFrom(source, amount);
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
        Entity entity = partIds[0] == -1 ? null : world.getEntityByID(partIds[0]);
        if (entity instanceof CorrodentTailEntity) {
            entity.setDead();
        }
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (animation == ANIMATION_BITE && animationTick < 12) {
            super.travel(0.0F, vertical, 0.0F);
            return;
        }
        super.travel(strafe, vertical, forward);
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
        if (!isDigging()) {
            super.updateFallState(y, onGroundIn, state, pos);
        }
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int teeth = rand.nextInt(2) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < teeth; i++) {
            dropItem(ACItemRegistry.CORRODENT_TEETH.item(), 1);
        }
        int dirt = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        if (dirt > 0) {
            entityDropItem(new ItemStack(Blocks.DIRT, dirt, 1), 0.0F);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Digging", isDigging());
        compound.setInteger("FleeLightFor", fleeLightFor);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setDigging(compound.getBoolean("Digging"));
        fleeLightFor = compound.getInteger("FleeLightFor");
    }

    public boolean isDigging() {
        return dataManager.get(DIGGING);
    }

    public void setDigging(boolean digging) {
        if (digging && !isDigging()) {
            setDigPitch(90.0F);
        } else if (!digging && isDigging()) {
            setDigPitch(-90.0F);
        }
        dataManager.set(DIGGING, digging);
    }

    public boolean isAfraid() {
        return dataManager.get(AFRAID);
    }

    public void setAfraid(boolean afraid) {
        dataManager.set(AFRAID, afraid);
    }

    public float getDigPitch() {
        return dataManager.get(DIG_PITCH);
    }

    public void setDigPitch(float pitch) {
        dataManager.set(DIG_PITCH, pitch);
    }

    public float getDigPitch(float partialTick) {
        return prevDigPitch + (getDigPitch() - prevDigPitch) * partialTick;
    }

    public float getDigAmount(float partialTick) {
        return (prevDigProgress + (digProgress - prevDigProgress) * partialTick) * 0.2F;
    }

    public float getAfraidAmount(float partialTick) {
        return (prevFearProgress + (fearProgress - prevFearProgress) * partialTick) * 0.2F;
    }

    public int getAnimation() {
        return animation;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public static boolean canDigBlock(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getMaterial().isSolid() && state.isFullCube() && state.getBlockHardness(world, pos) >= 0.0F && state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.BARRIER;
    }

    public static boolean isSafeDig(World world, BlockPos pos) {
        return canDigBlock(world, pos) && canDigBlock(world, pos.down());
    }

    private boolean canReach(BlockPos target) {
        return getNavigator().getPathToPos(target) != null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.CORRODENT_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.CORRODENT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.CORRODENT_DEATH;
    }

    private class FearLightAI extends EntityAIBase {
        private Vec3d retreatTo;
        private int tryDigTime;
        private BlockPos tryDigPos;

        private FearLightAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return world.getLightFor(EnumSkyBlock.BLOCK, getPosition()) > LIGHT_THRESHOLD && !isDigging();
        }

        @Override
        public void updateTask() {
            fleeLightFor = 50;
            if (retreatTo == null || getDistanceSq(retreatTo.x, retreatTo.y, retreatTo.z) < 6.0D) {
                retreatTo = findDarkerPosition();
            }
            if (retreatTo != null) {
                setAfraid(true);
                getNavigator().tryMoveToXYZ(retreatTo.x, retreatTo.y, retreatTo.z, 1.25D);
                getLookHelper().setLookPosition(retreatTo.x, retreatTo.y, retreatTo.z, 90.0F, 30.0F);
            }
            if (onGround && ++tryDigTime > 20) {
                tryDigTime = 0;
                if (tryDigPos != null && tryDigPos.distanceSq(getPosition()) < 2.25D) {
                    setDigging(true);
                }
                tryDigPos = getPosition();
            }
        }

        @Override
        public void resetTask() {
            setAfraid(false);
            retreatTo = null;
            tryDigPos = null;
            tryDigTime = 0;
            if (onGround) {
                fleeLightFor = 50;
                setDigging(true);
            }
        }

        private Vec3d findDarkerPosition() {
            BlockPos origin = getPosition();
            for (int i = 0; i < 20; i++) {
                BlockPos pos = origin.add(rand.nextInt(31) - 15, rand.nextInt(11) - 5, rand.nextInt(31) - 15);
                if (world.isBlockLoaded(pos) && world.getLightFor(EnumSkyBlock.BLOCK, pos) < LIGHT_THRESHOLD && world.isAirBlock(pos)) {
                    return new Vec3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                }
            }
            return null;
        }
    }

    private class CorrodentMeleeAI extends EntityAIBase {
        private boolean burrowing;
        private int burrowCheckTime;
        private int evadeFor;

        private CorrodentMeleeAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive() && fleeLightFor <= 0;
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                return;
            }
            double dist = getDistance(target);
            float reach = width + target.width;
            getLookHelper().setLookPositionWithEntity(target, 180.0F, 30.0F);
            if (++burrowCheckTime > 40 && animation == ANIMATION_NONE) {
                burrowCheckTime = 0;
                if (!burrowing && onGround && dist > reach && (!canReach(target.getPosition()) || dist > 20.0D || rand.nextInt(20) == 0)) {
                    burrowing = true;
                    evadeFor = 60 + rand.nextInt(40);
                } else if (burrowing && (dist < reach + 1.0F || rand.nextInt(10) == 0)) {
                    burrowing = false;
                    evadeFor = 0;
                }
            }
            if (evadeFor > 0) {
                evadeFor--;
                burrowing = true;
                Vec3d evade = generateDigPosition(target.getPosition(), 8, false);
                if (evade != null) {
                    moveDiggingToward(evade.x, evade.y, evade.z, 1.0D);
                }
            } else if (burrowing) {
                moveDiggingToward(target.posX, target.posY, target.posZ, 2.0D);
                if (!isEntityInsideOpaqueBlock() && dist < reach + 2.0F) {
                    setDigging(false);
                    burrowing = false;
                }
            } else {
                if (!isEntityInsideOpaqueBlock()) {
                    setDigging(false);
                    getNavigator().tryMoveToEntityLiving(target, 1.5D);
                } else {
                    setDigging(true);
                    motionY += 0.08D;
                }
            }
            if (dist < reach + 1.0F && animation == ANIMATION_NONE) {
                setDigging(false);
                setAnimation(ANIMATION_BITE);
                playSound(ACSoundRegistry.CORRODENT_ATTACK, 1.0F, getSoundPitch());
            }
            if (animation == ANIMATION_BITE && animationTick == 8 && dist < reach + 1.25F && canEntityBeSeen(target)) {
                float damage = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 1.5F;
                if (target.attackEntityFrom(DamageSource.causeMobDamage(CorrodentEntity.this), damage)) {
                    target.knockBack(CorrodentEntity.this, 0.65F, posX - target.posX, posZ - target.posZ);
                    if (rand.nextBoolean()) {
                        evadeFor = 60 + rand.nextInt(40);
                    }
                }
            }
        }

        @Override
        public void resetTask() {
            burrowing = false;
            burrowCheckTime = 0;
            evadeFor = 0;
        }
    }

    private class RandomDigAI extends EntityAIBase {
        private Vec3d target;
        private boolean surface;

        private RandomDigAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (isRiding() || getAttackTarget() != null && getAttackTarget().isEntityAlive() || !isDigging() && !onGround && !isEntityInsideOpaqueBlock()) {
                return false;
            }
            if (!isDigging() && !isEntityInsideOpaqueBlock() && rand.nextInt(20) != 0) {
                return false;
            }
            surface = isDigging() && timeDigging > 300;
            target = generateDigPosition(getPosition(), 16, surface);
            return target != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return target != null && isDigging() && getDistanceSq(target.x, target.y, target.z) > 2.0D;
        }

        @Override
        public void startExecuting() {
            setDigging(true);
        }

        @Override
        public void updateTask() {
            if (target != null) {
                moveDiggingToward(target.x, target.y, target.z, 1.0D);
                if (surface && getDistanceSq(target.x, target.y, target.z) < 4.0D) {
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
