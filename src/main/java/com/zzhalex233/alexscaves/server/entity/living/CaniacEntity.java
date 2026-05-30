package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.ai.CaniacMeleeAI;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CaniacEntity extends EntityMob {
    public static final int LUNGE_DURATION = 30;
    private static final DataParameter<Boolean> RUNNING = EntityDataManager.createKey(CaniacEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> LUNGE_TICKS = EntityDataManager.createKey(CaniacEntity.class, DataSerializers.VARINT);

    private float runProgress;
    private float prevRunProgress;
    private int prevLungeTicks;
    private float leftArmRot;
    private float prevLeftArmRot;
    private float rightArmRot;
    private float prevRightArmRot;
    private boolean hasRunningAttributes;
    private boolean spinSecondArm;
    private int swingSoundTimer;

    public CaniacEntity(World world) {
        super(world);
        setSize(0.9F, 2.3F);
        experienceValue = 5;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(RUNNING, false);
        dataManager.register(LUNGE_TICKS, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityWolf.class, 10.0F, 1.0D, 1.2D));
        tasks.addTask(2, new CaniacMeleeAI(this));
        tasks.addTask(3, new EntityAIWander(this, 1.0D));
        tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(5, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(38.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevRunProgress = runProgress;
        prevLungeTicks = getLungeTicks();
        prevLeftArmRot = leftArmRot;
        prevRightArmRot = rightArmRot;

        if (!world.isRemote) {
            int lungeTicks = getLungeTicks();
            if (lungeTicks > 0) {
                dataManager.set(LUNGE_TICKS, lungeTicks - 1);
                if (lungeTicks == LUNGE_DURATION - 10) {
                    playSound(ACSoundRegistry.CANIAC_ATTACK, 1.0F, getSoundPitch());
                }
            }
        }

        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        } else if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        }

        runProgress += isRunning() ? 1.0F : -1.0F;
        runProgress = MathHelper.clamp(runProgress, 0.0F, 5.0F);
        updateArmSpin();
    }

    private void updateArmSpin() {
        float spinSpeed = isRunning() ? Math.min(30.0F, 8.0F + runProgress * 5.0F) : 0.0F;
        if (spinSpeed > 0.0F && isEntityAlive()) {
            if (!world.isRemote && swingSoundTimer-- <= 0) {
                swingSoundTimer = 5 + rand.nextInt(10);
                playSound(ACSoundRegistry.CANIAC_SWING, 0.7F, getSoundPitch());
            }
            if (isLeftHanded()) {
                leftArmRot += spinSpeed;
                if (MathHelper.wrapDegrees(leftArmRot) > 90.0F) {
                    spinSecondArm = true;
                }
                hurtMobsFromArmSwing(true);
                if (spinSecondArm) {
                    rightArmRot += spinSpeed;
                    hurtMobsFromArmSwing(false);
                }
            } else {
                rightArmRot += spinSpeed;
                if (MathHelper.wrapDegrees(rightArmRot) > 90.0F) {
                    spinSecondArm = true;
                }
                hurtMobsFromArmSwing(false);
                if (spinSecondArm) {
                    leftArmRot += spinSpeed;
                    hurtMobsFromArmSwing(true);
                }
            }
        } else {
            spinSecondArm = false;
            leftArmRot = approachDegrees(leftArmRot, 0.0F, 15.0F);
            rightArmRot = approachDegrees(rightArmRot, 0.0F, 15.0F);
        }
    }

    private float approachDegrees(float value, float target, float step) {
        float delta = MathHelper.wrapDegrees(target - value);
        if (delta > step) {
            delta = step;
        }
        if (delta < -step) {
            delta = -step;
        }
        return value + delta;
    }

    private void hurtMobsFromArmSwing(boolean left) {
        if (world.isRemote || ticksExisted % 4 != 0) {
            return;
        }
        double side = left ? 0.75D : -0.75D;
        double sin = MathHelper.sin(rotationYaw * 0.017453292F);
        double cos = MathHelper.cos(rotationYaw * 0.017453292F);
        double x = posX + side * cos - 1.25D * sin;
        double z = posZ + side * sin + 1.25D * cos;
        AxisAlignedBB box = new AxisAlignedBB(x - 1.0D, posY, z - 1.0D, x + 1.0D, posY + 2.0D, z + 1.0D);
        for (EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
            if (living == this || living.isOnSameTeam(this) || living instanceof CaniacEntity || getDistance(living) >= 3.15F) {
                continue;
            }
            float damage = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            float knockback = 0.1F;
            if (rand.nextFloat() < 0.1F) {
                damage *= 1.5F;
                knockback = 1.0F;
            }
            if (living.attackEntityFrom(DamageSource.causeMobDamage(this), damage)) {
                living.knockBack(this, knockback, posX - living.posX, posZ - living.posZ);
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean attacked = super.attackEntityAsMob(entity);
        if (attacked) {
            playSound(ACSoundRegistry.CANIAC_ATTACK, 1.0F, getSoundPitch());
        }
        return attacked;
    }

    public boolean isRunning() {
        return dataManager.get(RUNNING);
    }

    public void setRunning(boolean running) {
        dataManager.set(RUNNING, running);
    }

    public void startLunge() {
        if (!world.isRemote && getLungeTicks() <= 0) {
            dataManager.set(LUNGE_TICKS, LUNGE_DURATION);
            setRunning(false);
        }
    }

    public boolean isLunging() {
        return getLungeTicks() > 0;
    }

    public int getLungeTicks() {
        return dataManager.get(LUNGE_TICKS);
    }

    public float getRunProgress(float partialTicks) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTicks) * 0.2F;
    }

    public float getLungeProgress(float partialTicks) {
        float lunge = prevLungeTicks + (getLungeTicks() - prevLungeTicks) * partialTicks;
        return lunge <= 0.0F ? 0.0F : 1.0F - lunge / LUNGE_DURATION;
    }

    public float getArmAngle(boolean left, float partialTicks) {
        if (left) {
            return prevLeftArmRot + (leftArmRot - prevLeftArmRot) * partialTicks;
        }
        return prevRightArmRot + (rightArmRot - prevRightArmRot) * partialTicks;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.CANIAC_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.CANIAC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.CANIAC_DEATH;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        dropCount(ACBlockRegistry.CANDY_CANE.item(), rand.nextInt(3) + rand.nextInt(Math.max(0, lootingModifier) + 1));
        dropCount(ACItemRegistry.SHARPENED_CANDY_CANE.item(), rand.nextInt(2) + rand.nextInt(Math.max(0, lootingModifier) + 1));
    }

    private void dropCount(Item item, int count) {
        for (int i = 0; i < count; i++) {
            dropItem(item, 1);
        }
    }
}
