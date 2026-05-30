package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TripodfishEntity extends BucketableWaterMob {
    private static final DataParameter<Boolean> STANDING = EntityDataManager.createKey(TripodfishEntity.class, DataSerializers.BOOLEAN);

    private float landProgress;
    private float prevLandProgress;
    private float fishPitch;
    private float prevFishPitch;
    private float standProgress;
    private float prevStandProgress;
    private int swimChangeCooldown;
    private int standingTime;
    private double swimVecX;
    private double swimVecY;
    private double swimVecZ;

    public TripodfishEntity(World world) {
        super(world);
        setSize(0.95F, 0.8F);
        experienceValue = 1;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(STANDING, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(2, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        prevStandProgress = standProgress;

        boolean grounded = !isInWater();
        if (grounded && landProgress < 5.0F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0.0F) {
            landProgress--;
        }

        if (!world.isRemote) {
            if (isInWater()) {
                setAir(300);
                updateSwimmingState();
            } else {
                handleDryingOut();
                setStanding(false);
            }
        }

        if (isStanding()) {
            if (standProgress < 10.0F) {
                standProgress++;
            }
            standingTime++;
            motionX *= 0.35D;
            motionY -= 0.015D;
            motionZ *= 0.35D;
        } else {
            if (standProgress > 0.0F) {
                standProgress--;
            }
            standingTime = 0;
            if (isInWater()) {
                motionX += swimVecX * 0.035D;
                motionY += swimVecY * 0.025D;
                motionZ += swimVecZ * 0.035D;
                if (collidedHorizontally) {
                    motionY += 0.04D;
                }
                motionX *= 0.9D;
                motionY *= 0.9D;
                motionZ *= 0.9D;
            }
        }

        fishPitch = MathHelper.clamp((float) motionY * -3.0F, -1.4F, 1.4F);
        if (isInWater() && !isStanding() && motionX * motionX + motionZ * motionZ > 0.001D) {
            rotationYaw = (float) (-Math.atan2(motionX, motionZ) * (180D / Math.PI));
            renderYawOffset = rotationYaw;
        }
    }

    private void updateSwimmingState() {
        if (isStanding()) {
            if (standingTime > 240 + rand.nextInt(160)) {
                setStanding(false);
                swimChangeCooldown = 0;
            }
            return;
        }
        if (ticksExisted > 60 && onGround && rand.nextInt(140) == 0) {
            setStanding(true);
            return;
        }
        if (--swimChangeCooldown > 0) {
            return;
        }
        swimChangeCooldown = 30 + rand.nextInt(60);
        double angle = rand.nextDouble() * Math.PI * 2.0D;
        swimVecX = Math.cos(angle) * 0.7D;
        swimVecY = rand.nextDouble() * 0.5D - 0.25D;
        swimVecZ = Math.sin(angle) * 0.7D;
    }

    private void handleDryingOut() {
        setAir(getAir() - 1);
        if (onGround) {
            motionX += (rand.nextFloat() * 2.0F - 1.0F) * 0.2F;
            motionY += 0.5D;
            motionZ += (rand.nextFloat() * 2.0F - 1.0F) * 0.2F;
            rotationYaw = rand.nextFloat() * 360.0F;
            playSound(ACSoundRegistry.TRIPODFISH_FLOP, getSoundVolume(), getSoundPitch());
        }
        if (getAir() <= -20) {
            setAir(0);
            attackEntityFrom(DamageSource.DROWN, 2.0F);
        }
    }

    public boolean isStanding() {
        return dataManager.get(STANDING);
    }

    public void setStanding(boolean standing) {
        dataManager.set(STANDING, standing);
    }

    public float getLandProgress(float partialTicks) {
        return (prevLandProgress + (landProgress - prevLandProgress) * partialTicks) * 0.2F;
    }

    public float getFishPitch(float partialTicks) {
        return prevFishPitch + (fishPitch - prevFishPitch) * partialTicks;
    }

    public float getStandProgress(float partialTicks) {
        return (prevStandProgress + (standProgress - prevStandProgress) * partialTicks) * 0.1F;
    }

    @Override
    protected Item getBucketItem() {
        return ACItemRegistry.TRIPODFISH_BUCKET.item();
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
        return isInWater() && isNotColliding();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TRIPODFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TRIPODFISH_HURT;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        dropItem(isBurning() ? ACItemRegistry.COOKED_TRIPODFISH.item() : ACItemRegistry.TRIPODFISH.item(), 1);
    }
}
