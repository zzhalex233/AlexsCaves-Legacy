package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RadgillEntity extends BucketableWaterMob {
    private float landProgress;
    private float prevLandProgress;
    private float fishPitch;
    private float prevFishPitch;
    private int swimChangeCooldown;
    private double swimVecX;
    private double swimVecY;
    private double swimVecZ;

    public RadgillEntity(World world) {
        super(world);
        setSize(0.9F, 0.6F);
        experienceValue = 2;
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
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        boolean inLiquid = isInLiquid();
        boolean grounded = onGround && !inLiquid;
        if (grounded && landProgress < 5.0F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0.0F) {
            landProgress--;
        }
        fishPitch = inLiquid ? MathHelper.clamp((float) motionY * -3.0F, -1.4F, 1.4F) : 0.0F;

        if (!world.isRemote) {
            if (inLiquid) {
                updateSwimmingVector();
                setAir(300);
            } else {
                handleDryingOut();
            }
        }
        if (inLiquid) {
            motionX += swimVecX * 0.035D;
            motionY += swimVecY * 0.025D;
            motionZ += swimVecZ * 0.035D;
            if (collidedHorizontally) {
                motionY += 0.04D;
            }
            motionX *= 0.9D;
            motionY *= 0.9D;
            motionZ *= 0.9D;
            rotationYaw = (float) (-Math.atan2(motionX, motionZ) * (180D / Math.PI));
            renderYawOffset = rotationYaw;
        }
    }

    private void updateSwimmingVector() {
        if (--swimChangeCooldown > 0 && isInLiquid()) {
            return;
        }
        swimChangeCooldown = 20 + rand.nextInt(40);
        double angle = rand.nextDouble() * Math.PI * 2.0D;
        swimVecX = Math.cos(angle) * 0.7D;
        swimVecY = rand.nextDouble() * 0.55D - 0.2D;
        swimVecZ = Math.sin(angle) * 0.7D;
    }

    private void handleDryingOut() {
        setAir(getAir() - 1);
        if (onGround && rand.nextFloat() < 0.1F) {
            motionX += (rand.nextFloat() * 2.0F - 1.0F) * 0.2F;
            motionY += 0.5D;
            motionZ += (rand.nextFloat() * 2.0F - 1.0F) * 0.2F;
            rotationYaw = rand.nextFloat() * 360.0F;
            playSound(ACSoundRegistry.RADGILL_FLOP, getSoundVolume(), getSoundPitch());
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
        return ACItemRegistry.RADGILL_BUCKET.item();
    }

    @Override
    protected Item getPickupBucketItem() {
        return ACItemRegistry.ACID_BUCKET.item();
    }

    @Override
    protected SoundEvent getBucketFillSound() {
        return ACSoundRegistry.ACID_SUBMERGE;
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
        return isInLiquid() && isNotColliding();
    }

    private boolean isInLiquid() {
        return isInWater() || isInAcid();
    }

    private boolean isInAcid() {
        AxisAlignedBB box = getEntityBoundingBox().grow(0.001D);
        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.ceil(box.maxX);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.ceil(box.maxY);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.ceil(box.maxZ);
        BlockPos.PooledMutableBlockPos mutable = BlockPos.PooledMutableBlockPos.retain();
        try {
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        IBlockState state = world.getBlockState(mutable.setPos(x, y, z));
                        if (state.getBlock() == ACBlockRegistry.ACID.block()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } finally {
            mutable.release();
        }
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 2;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.RADGILL_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.RADGILL_HURT;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        dropItem(isBurning() ? ACItemRegistry.COOKED_RADGILL.item() : ACItemRegistry.RADGILL.item(), 1);
    }
}
