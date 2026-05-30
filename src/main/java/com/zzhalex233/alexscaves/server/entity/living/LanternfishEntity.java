package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class LanternfishEntity extends BucketableWaterMob {
    private float landProgress;
    private float prevLandProgress;
    private float fishPitch;
    private float prevFishPitch;
    private int swimChangeCooldown;
    private double swimVecX;
    private double swimVecY;
    private double swimVecZ;

    public LanternfishEntity(World world) {
        super(world);
        setSize(0.5F, 0.4F);
        experienceValue = 1;
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
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        boolean grounded = onGround && !isInWater();
        if (grounded && landProgress < 5.0F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0.0F) {
            landProgress--;
        }
        fishPitch = MathHelper.clamp((float) motionY * -3.0F, -1.4F, 1.4F);

        if (!world.isRemote) {
            if (isInWater()) {
                updateSwimmingVector();
                setAir(300);
            } else {
                handleDryingOut();
            }
        }
        if (isInWater()) {
            motionX += swimVecX * 0.03D;
            motionY += swimVecY * 0.02D;
            motionZ += swimVecZ * 0.03D;
            if (collidedHorizontally) {
                motionY += 0.03D;
            }
            motionX *= 0.9D;
            motionY *= 0.9D;
            motionZ *= 0.9D;
            rotationYaw = (float) (-Math.atan2(motionX, motionZ) * (180D / Math.PI));
            renderYawOffset = rotationYaw;
        }
    }

    private void updateSwimmingVector() {
        if (--swimChangeCooldown > 0 && isInWater()) {
            return;
        }
        swimChangeCooldown = 20 + rand.nextInt(40);
        double angle = rand.nextDouble() * Math.PI * 2.0D;
        swimVecX = Math.cos(angle) * 0.7D;
        swimVecY = rand.nextDouble() * 0.5D - 0.2D;
        swimVecZ = Math.sin(angle) * 0.7D;
    }

    private void handleDryingOut() {
        setAir(getAir() - 1);
        if (onGround) {
            motionX += (rand.nextFloat() * 2.0F - 1.0F) * 0.2F;
            motionY += 0.5D;
            motionZ += (rand.nextFloat() * 2.0F - 1.0F) * 0.2F;
            rotationYaw = rand.nextFloat() * 360.0F;
            playSound(ACSoundRegistry.LANTERNFISH_FLOP, getSoundVolume(), getSoundPitch());
        }
        if (getAir() <= -20) {
            setAir(0);
            attackEntityFrom(DamageSource.DROWN, 2.0F);
        }
    }

    public float getLandProgress(float partialTicks) {
        return (prevLandProgress + (landProgress - prevLandProgress) * partialTicks) * 0.2F;
    }

    public float getFishPitch(float partialTicks) {
        return prevFishPitch + (fishPitch - prevFishPitch) * partialTicks;
    }

    @Override
    protected Item getBucketItem() {
        return ACItemRegistry.LANTERNFISH_BUCKET.item();
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
        return ACSoundRegistry.LANTERNFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.LANTERNFISH_HURT;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        dropItem(isBurning() ? ACItemRegistry.COOKED_LANTERNFISH.item() : ACItemRegistry.LANTERNFISH.item(), 1);
    }
}
