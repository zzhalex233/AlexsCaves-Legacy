package com.zzhalex233.alexscaves.server.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class DeepOneBaseEntity extends EntityMob {
    private static final DataParameter<Boolean> SWIMMING = EntityDataManager.createKey(DeepOneBaseEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(DeepOneBaseEntity.class, DataSerializers.BOOLEAN);
    protected int attackCooldown;
    protected float prevFishPitch;
    protected float fishPitch;

    protected DeepOneBaseEntity(World world) {
        super(world);
        setSize(0.9F, 1.95F);
        experienceValue = 8;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SWIMMING, false);
        dataManager.register(ANGRY, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new DeepOneAttackAI());
        tasks.addTask(1, new DeepOneWanderAI());
        tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
        tasks.addTask(3, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(getBaseHealth());
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getBaseDamage());
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    protected abstract double getBaseHealth();

    protected abstract double getBaseDamage();

    protected abstract SoundEvent getAttackSound();

    protected int getAttackInterval() {
        return 28;
    }

    @Override
    public void onLivingUpdate() {
        prevFishPitch = fishPitch;
        super.onLivingUpdate();
        boolean swimming = isInWater();
        setDeepOneSwimming(swimming);
        if (swimming) {
            motionY += 0.005D;
            fishPitch = approach(fishPitch, (float) MathHelper.clamp(motionY * -80.0D, -70.0D, 70.0D), 5.0F);
        } else {
            fishPitch = approach(fishPitch, 0.0F, 8.0F);
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        setSoundsAngry(getAttackTarget() != null);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (isInWater()) {
            moveRelative(strafe, vertical, forward, 0.06F);
            move(MoverType.SELF, motionX, motionY, motionZ);
            motionX *= 0.8D;
            motionY *= 0.8D;
            motionZ *= 0.8D;
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    protected void startAttackBehavior(EntityLivingBase target) {
        double distance = getDistance(target);
        float reach = width + target.width + 0.7F;
        if (distance <= reach && attackCooldown <= 0) {
            attackCooldown = getAttackInterval();
            playSound(getAttackSound(), 1.0F, getSoundPitch());
            attackEntityAsMob(target);
        } else if (distance > reach) {
            getNavigator().tryMoveToEntityLiving(target, isInWater() ? 1.25D : 1.0D);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean attacked = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
        if (attacked && entity instanceof EntityLivingBase) {
            float yaw = rotationYaw * 0.017453292F;
            entity.addVelocity(-MathHelper.sin(yaw) * 0.35D, 0.1D, MathHelper.cos(yaw) * 0.35D);
        }
        return attacked;
    }

    public boolean isDeepOneSwimming() {
        return dataManager.get(SWIMMING);
    }

    public void setDeepOneSwimming(boolean swimming) {
        dataManager.set(SWIMMING, swimming);
    }

    public boolean soundsAngry() {
        return dataManager.get(ANGRY);
    }

    public void setSoundsAngry(boolean angry) {
        dataManager.set(ANGRY, angry);
    }

    public float getFishPitch(float partialTicks) {
        return prevFishPitch + (fishPitch - prevFishPitch) * partialTicks;
    }

    protected float approach(float value, float target, float step) {
        if (value < target) {
            return Math.min(target, value + step);
        }
        return value > target ? Math.max(target, value - step) : value;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return soundsAngry() ? getHostileSound() : getIdleSound();
    }

    protected abstract SoundEvent getIdleSound();

    protected abstract SoundEvent getHostileSound();

    private class DeepOneAttackAI extends EntityAIBase {
        private DeepOneAttackAI() {
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
            if (target != null) {
                faceEntity(target, 30.0F, 30.0F);
                startAttackBehavior(target);
            }
        }
    }

    private class DeepOneWanderAI extends EntityAIBase {
        private int cooldown;

        private DeepOneWanderAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return getAttackTarget() == null && --cooldown <= 0;
        }

        @Override
        public void updateTask() {
            cooldown = 60 + rand.nextInt(60);
            if (isInWater()) {
                Vec3d target = getPositionVector().add(rand.nextDouble() * 12.0D - 6.0D, rand.nextDouble() * 4.0D - 2.0D, rand.nextDouble() * 12.0D - 6.0D);
                getMoveHelper().setMoveTo(target.x, target.y, target.z, 1.0D);
            } else {
                getNavigator().tryMoveToXYZ(posX + rand.nextDouble() * 12.0D - 6.0D, posY, posZ + rand.nextDouble() * 12.0D - 6.0D, 1.0D);
            }
        }
    }
}
