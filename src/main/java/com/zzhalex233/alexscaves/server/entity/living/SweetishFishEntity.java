package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.util.GummyColors;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SweetishFishEntity extends BucketableWaterMob {
    private static final DataParameter<Integer> GUMMY_COLOR = EntityDataManager.createKey(SweetishFishEntity.class, DataSerializers.VARINT);

    private float landProgress;
    private float prevLandProgress;
    private float fishPitch;
    private float prevFishPitch;
    private int swimChangeCooldown;
    private double swimVecX;
    private double swimVecY;
    private double swimVecZ;

    public SweetishFishEntity(World world) {
        this(world, GummyColors.getRandom(world.rand, true));
    }

    public SweetishFishEntity(World world, GummyColors color) {
        super(world);
        setSize(0.7F, 0.55F);
        experienceValue = 1;
        setGummyColor(color);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(GUMMY_COLOR, GummyColors.RED.ordinal());
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
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        boolean inLiquid = isInLiquid();
        boolean grounded = !inLiquid;
        if (grounded && landProgress < 5.0F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0.0F) {
            landProgress--;
        }
        fishPitch = grounded ? 0.0F : MathHelper.clamp((float) motionY * -3.0F, -1.4F, 1.4F);

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
        if (onGround) {
            motionX += (rand.nextFloat() * 2.0F - 1.0F) * 0.2F;
            motionY += 0.5D;
            motionZ += (rand.nextFloat() * 2.0F - 1.0F) * 0.2F;
            rotationYaw = rand.nextFloat() * 360.0F;
            playSound(ACSoundRegistry.SWEETISH_FISH_FLOP, getSoundVolume(), getSoundPitch());
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

    public GummyColors getGummyColor() {
        return GummyColors.fromOrdinal(dataManager.get(GUMMY_COLOR));
    }

    public void setGummyColor(GummyColors color) {
        dataManager.set(GUMMY_COLOR, color.ordinal());
    }

    @Override
    protected Item getBucketItem() {
        switch (getGummyColor()) {
            case GREEN:
                return ACItemRegistry.SWEETISH_FISH_GREEN_BUCKET.item();
            case YELLOW:
                return ACItemRegistry.SWEETISH_FISH_YELLOW_BUCKET.item();
            case BLUE:
                return ACItemRegistry.SWEETISH_FISH_BLUE_BUCKET.item();
            case PINK:
                return ACItemRegistry.SWEETISH_FISH_PINK_BUCKET.item();
            default:
                return ACItemRegistry.SWEETISH_FISH_RED_BUCKET.item();
        }
    }

    @Override
    protected Item getPickupBucketItem() {
        return ACItemRegistry.PURPLE_SODA_BUCKET.item();
    }

    @Override
    protected SoundEvent getBucketFillSound() {
        return ACSoundRegistry.PURPLE_SODA_SUBMERGE;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("GummyColor", getGummyColor().ordinal());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setGummyColor(GummyColors.fromOrdinal(compound.getInteger("GummyColor")));
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
        return isInPurpleSoda() && isNotColliding();
    }

    private boolean isInLiquid() {
        return isInWater() || isInPurpleSoda();
    }

    private boolean isInPurpleSoda() {
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
                        if (state.getBlock() == ACBlockRegistry.PURPLE_SODA.block()) {
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
        return ACSoundRegistry.SWEETISH_FISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.SWEETISH_FISH_HURT;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        dropItem(ACItemRegistry.byName(getDropNameForColor(getGummyColor(), isBurning())).item(), 1);
    }

    public static String getDropNameForColor(GummyColors color, boolean burning) {
        switch (color) {
            case GREEN:
                return burning ? "gelatin_green" : "sweetish_fish_green";
            case YELLOW:
                return burning ? "gelatin_yellow" : "sweetish_fish_yellow";
            case BLUE:
                return burning ? "gelatin_blue" : "sweetish_fish_blue";
            case PINK:
                return burning ? "gelatin_pink" : "sweetish_fish_pink";
            default:
                return burning ? "gelatin_red" : "sweetish_fish_red";
        }
    }
}
