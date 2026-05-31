package com.zzhalex233.alexscaves.server.entity.living;

import java.util.UUID;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
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
import net.minecraft.world.World;

public class WatcherEntity extends EntityMob {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_ATTACK_0 = 1;
    public static final int ANIMATION_ATTACK_1 = 2;
    private static final DataParameter<Boolean> RUNNING = EntityDataManager.createKey(WatcherEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHADE_MODE = EntityDataManager.createKey(WatcherEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> POSSESSED_ENTITY_ID = EntityDataManager.createKey(WatcherEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> POSSESSION_STRENGTH = EntityDataManager.createKey(WatcherEntity.class, DataSerializers.FLOAT);
    private int animation = ANIMATION_NONE;
    private int animationTick;
    private float prevRunProgress;
    private float runProgress;
    private float prevShadeProgress;
    private float shadeProgress;
    private int possessedTimeout;
    private int lastPossessionTimestamp;
    private BlockPos lastPossessionSite;
    private UUID possessedUUID;

    public WatcherEntity(World world) {
        super(world);
        setSize(1.0F, 2.9F);
        stepHeight = 1.1F;
        experienceValue = 12;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(RUNNING, false);
        dataManager.register(SHADE_MODE, false);
        dataManager.register(POSSESSED_ENTITY_ID, -1);
        dataManager.register(POSSESSION_STRENGTH, 0.0F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new WatcherAttackAI());
        tasks.addTask(3, new EntityAIWander(this, 1.0D, 100));
        tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(5, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, WatcherEntity.class));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false, this::canPossessTargetEntity));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(128.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevRunProgress = runProgress;
        prevShadeProgress = shadeProgress;
        runProgress = approach(runProgress, isRunning() ? 5.0F : 0.0F);
        shadeProgress = approach(shadeProgress, isShadeMode() ? 5.0F : 0.0F);
        super.onLivingUpdate();
        if (animation != ANIMATION_NONE && ++animationTick >= 15) {
            setAnimation(ANIMATION_NONE);
        }
        if (isShadeMode()) {
            fallDistance = 0.0F;
            setNoGravity(true);
        } else {
            setNoGravity(false);
        }
        updatePossession();
    }

    private void updatePossession() {
        Entity possessed = getPossessedEntity();
        if (!world.isRemote && possessed instanceof EntityLivingBase && possessed.isEntityAlive()) {
            EntityLivingBase living = (EntityLivingBase) possessed;
            double dist = getDistance(living);
            dataManager.set(POSSESSED_ENTITY_ID, living.getEntityId());
            living.motionX *= 0.2D;
            living.motionZ *= 0.2D;
            living.motionY = Math.min(living.motionY, 0.0D);
            living.velocityChanged = true;
            lastPossessionTimestamp = ticksExisted;
            possessedTimeout++;
            setPossessionStrength(Math.max(0.0F, getPossessionStrength(1.0F) - 0.035F));
            if (dist < 1.0D || possessedTimeout > 140 || getPossessionStrength(1.0F) <= 0.0F || !isEntityAlive()) {
                world.setEntityState(this, (byte) 78);
                clearPossession();
            } else {
                world.setEntityState(this, (byte) 77);
            }
        } else if (!world.isRemote && dataManager.get(POSSESSED_ENTITY_ID) != -1) {
            clearPossession();
        }
    }

    private float approach(float value, float target) {
        if (value < target) {
            return Math.min(target, value + 1.0F);
        }
        return value > target ? Math.max(target, value - 1.0F) : value;
    }

    private void flyToward(double x, double y, double z, double speed) {
        setShadeMode(true);
        getNavigator().clearPath();
        Vec3d delta = new Vec3d(x - posX, y - posY, z - posZ);
        double length = Math.sqrt(delta.x * delta.x + delta.y * delta.y + delta.z * delta.z);
        if (length > 0.001D) {
            Vec3d add = delta.scale(speed * 0.06D / length);
            motionX = (motionX + add.x) * 0.9D;
            motionY = (motionY + add.y + 0.04D) * 0.9D;
            motionZ = (motionZ + add.z) * 0.9D;
            rotationYaw = approachAngle(rotationYaw, (float) (-Math.atan2(add.x, add.z) * 180.0D / Math.PI), 12.0F);
            renderYawOffset = rotationYaw;
        }
    }

    private float approachAngle(float value, float target, float step) {
        return value + MathHelper.clamp(MathHelper.wrapDegrees(target - value), -step, step);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (isShadeMode()) {
            moveRelative(strafe, vertical, forward, 0.02F);
            move(MoverType.SELF, motionX, motionY, motionZ);
            motionX *= 0.9D;
            motionY *= 0.9D;
            motionZ *= 0.9D;
            return;
        }
        super.travel(strafe, vertical, forward);
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
        if (!isShadeMode()) {
            super.updateFallState(y, onGroundIn, state, pos);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id >= 100 && id <= 102) {
            animation = id - 100;
            animationTick = 0;
        } else if (id == 77 || id == 78) {
            for (int i = 0; i < 8; i++) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, 0.02D, 0.0D);
            }
            if (id == 78) {
                playSound(ACSoundRegistry.WATCHER_SCARE, 1.0F, 1.0F);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void setAnimation(int animation) {
        this.animation = animation;
        animationTick = 0;
        world.setEntityState(this, (byte) (100 + animation));
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int gems = rand.nextInt(2) + rand.nextInt(1 + lootingModifier);
        if (gems > 0) {
            entityDropItem(new ItemStack(ACItemRegistry.OCCULT_GEM.item(), gems), 0.0F);
        }
        int tatters = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        if (tatters > 0) {
            entityDropItem(new ItemStack(ACItemRegistry.DARK_TATTERS.item(), tatters), 0.0F);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Running", isRunning());
        compound.setBoolean("ShadeMode", isShadeMode());
        if (possessedUUID != null) {
            compound.setUniqueId("Possessed", possessedUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setRunning(compound.getBoolean("Running"));
        setShadeMode(compound.getBoolean("ShadeMode"));
        if (compound.hasUniqueId("Possessed")) {
            possessedUUID = compound.getUniqueId("Possessed");
        }
    }

    public boolean isRunning() {
        return dataManager.get(RUNNING);
    }

    public void setRunning(boolean running) {
        dataManager.set(RUNNING, running);
    }

    public boolean isShadeMode() {
        return dataManager.get(SHADE_MODE);
    }

    public void setShadeMode(boolean shadeMode) {
        dataManager.set(SHADE_MODE, shadeMode);
    }

    public float getPossessionStrength(float partialTicks) {
        return dataManager.get(POSSESSION_STRENGTH);
    }

    public void setPossessionStrength(float possessionStrength) {
        dataManager.set(POSSESSION_STRENGTH, possessionStrength);
    }

    @Nullable
    public Entity getPossessedEntity() {
        int id = dataManager.get(POSSESSED_ENTITY_ID);
        if (id != -1) {
            return world.getEntityByID(id);
        }
        if (possessedUUID != null && !world.isRemote) {
            for (EntityPlayer player : world.playerEntities) {
                if (possessedUUID.equals(player.getUniqueID())) {
                    return player;
                }
            }
        }
        return null;
    }

    public boolean attemptPossession(EntityLivingBase living) {
        if (ticksExisted - lastPossessionTimestamp > 100 && (lastPossessionSite == null || lastPossessionSite.distanceSq(getPosition()) > 10.0D) && canPossessTargetEntity(living)) {
            lastPossessionSite = getPosition();
            lastPossessionTimestamp = ticksExisted;
            possessedTimeout = 0;
            possessedUUID = living.getUniqueID();
            dataManager.set(POSSESSED_ENTITY_ID, living.getEntityId());
            setPossessionStrength(1.0F);
            playSound(ACSoundRegistry.WATCHER_SCARE, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    private void clearPossession() {
        possessedUUID = null;
        possessedTimeout = 0;
        dataManager.set(POSSESSED_ENTITY_ID, -1);
        setPossessionStrength(0.0F);
    }

    public boolean canPossessTargetEntity(Entity entity) {
        return entity instanceof EntityLivingBase && !(entity instanceof WatcherEntity);
    }

    public float getShadeAmount(float partialTick) {
        return (prevShadeProgress + (shadeProgress - prevShadeProgress) * partialTick) * 0.2F;
    }

    public float getRunAmount(float partialTick) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTick) * 0.2F;
    }

    public int getAnimation() {
        return animation;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.WATCHER_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.WATCHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.WATCHER_DEATH;
    }

    private class WatcherAttackAI extends EntityAIBase {
        private int navigationCheckCooldown;
        private int possessions;
        private boolean canReachViaGround;
        private int retreatFor;
        private Vec3d retreatTo;

        private WatcherAttackAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public void startExecuting() {
            navigationCheckCooldown = 0;
            possessions = 0;
            retreatFor = 0;
            retreatTo = null;
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                return;
            }
            if (--navigationCheckCooldown < 0) {
                canReachViaGround = getNavigator().getPathToEntityLiving(target) != null;
                navigationCheckCooldown = 10 + rand.nextInt(40);
            }
            double dist = getDistance(target);
            getLookHelper().setLookPositionWithEntity(target, 180.0F, 30.0F);
            if (retreatFor-- > 0 && retreatTo != null) {
                Vec3d retreat = retreatTo.subtract(getPositionVector());
                if (retreat.lengthSquared() > 1.0D) {
                    Vec3d motion = retreat.normalize().scale(0.2D);
                    WatcherEntity.this.motionX += motion.x;
                    WatcherEntity.this.motionY += motion.y;
                    WatcherEntity.this.motionZ += motion.z;
                    setShadeMode(true);
                } else {
                    retreatTo = null;
                }
            } else {
                setShadeMode(!canReachViaGround || dist > 16.0D && !onGround);
                if (dist < 6.0D && canEntityBeSeen(target) && onGround) {
                    setShadeMode(false);
                }
                if (dist > target.width + width + 0.5D) {
                    double speed = (isRunning() ? 1.3D : 1.0D) + Math.min(Math.log(possessions + 1.0D), 1.0D) * 0.6D;
                    if (isShadeMode()) {
                        flyToward(target.posX, target.posY + target.height * 0.5D, target.posZ, speed);
                    } else {
                        getNavigator().tryMoveToEntityLiving(target, speed);
                    }
                } else if (canEntityBeSeen(target)) {
                    getNavigator().clearPath();
                    if (animation == ANIMATION_NONE) {
                        setAnimation(rand.nextBoolean() ? ANIMATION_ATTACK_0 : ANIMATION_ATTACK_1);
                        playSound(ACSoundRegistry.WATCHER_ATTACK, 1.0F, getSoundPitch());
                    } else if (animationTick == 8) {
                        if (target.attackEntityFrom(DamageSource.causeMobDamage(WatcherEntity.this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue())) {
                            target.knockBack(WatcherEntity.this, 0.5F, posX - target.posX, posZ - target.posZ);
                        }
                        retreatFor = 30 + rand.nextInt(30);
                        retreatTo = findRetreat(target);
                    }
                }
            }
            if (possessions > 2 || dist < 20.0D) {
                setRunning(true);
            }
            if (attemptPossession(target)) {
                possessions++;
            }
        }

        @Override
        public void resetTask() {
            setShadeMode(false);
            setRunning(false);
            retreatFor = 0;
            retreatTo = null;
        }

        private Vec3d findRetreat(EntityLivingBase target) {
            for (int i = 0; i < 15; i++) {
                Vec3d away = getPositionVector().subtract(target.getPositionVector()).normalize().scale(8.0D + rand.nextInt(16));
                Vec3d pos = getPositionVector().add(away).add(rand.nextInt(9) - 4, rand.nextInt(7) - 3, rand.nextInt(9) - 4);
                if (world.isAirBlock(new BlockPos(pos))) {
                    return pos;
                }
            }
            return null;
        }
    }
}
