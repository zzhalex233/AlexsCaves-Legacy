package com.zzhalex233.alexscaves.server.entity.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.QuarryBlock;
import com.zzhalex233.alexscaves.server.block.entity.QuarryTileEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class QuarrySmasherEntity extends Entity {
    private static final DataParameter<Boolean> INACTIVE = EntityDataManager.createKey(QuarrySmasherEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SLAMMING = EntityDataManager.createKey(QuarrySmasherEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> PULLING_ITEMS_FOR = EntityDataManager.createKey(QuarrySmasherEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> QUARRY_X = EntityDataManager.createKey(QuarrySmasherEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> QUARRY_Y = EntityDataManager.createKey(QuarrySmasherEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> QUARRY_Z = EntityDataManager.createKey(QuarrySmasherEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_X = EntityDataManager.createKey(QuarrySmasherEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_Y = EntityDataManager.createKey(QuarrySmasherEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_Z = EntityDataManager.createKey(QuarrySmasherEntity.class, DataSerializers.VARINT);

    public AxisAlignedBB lastMiningArea;
    public int shakeTime;
    private final List<EntityItem> pulledItems = new ArrayList<>();
    private Vec3d prevHeadPosition = Vec3d.ZERO;
    private Vec3d headPosition = Vec3d.ZERO;
    private float inactiveProgress = 10.0F;
    private float prevInactiveProgress = 10.0F;
    private float headGroundProgress;
    private float prevHeadGroundProgress;
    private int damageSustained;
    private int quarryActivityTime;
    private int blockBreakCooldown;

    public QuarrySmasherEntity(World world) {
        super(world);
        setSize(0.9F, 1.2F);
        headPosition = getPositionVector();
        prevHeadPosition = headPosition;
    }

    @Override
    protected void entityInit() {
        dataManager.register(INACTIVE, true);
        dataManager.register(SLAMMING, false);
        dataManager.register(PULLING_ITEMS_FOR, 0);
        dataManager.register(QUARRY_X, Integer.MIN_VALUE);
        dataManager.register(QUARRY_Y, Integer.MIN_VALUE);
        dataManager.register(QUARRY_Z, Integer.MIN_VALUE);
        dataManager.register(TARGET_X, Integer.MIN_VALUE);
        dataManager.register(TARGET_Y, Integer.MIN_VALUE);
        dataManager.register(TARGET_Z, Integer.MIN_VALUE);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevInactiveProgress = inactiveProgress;
        prevHeadGroundProgress = headGroundProgress;
        if (isInactive() && inactiveProgress < 10.0F) {
            inactiveProgress++;
        }
        if (!isInactive() && inactiveProgress > 0.0F) {
            inactiveProgress--;
        }
        if (isSlamming() && headGroundProgress < 5.0F) {
            headGroundProgress++;
        }
        if (!isSlamming() && headGroundProgress > 0.0F) {
            headGroundProgress--;
        }
        if (damageSustained > 0 && ticksExisted % 500 == 0) {
            damageSustained--;
        }
        if (isInactive()) {
            motionY -= 0.2D;
        } else {
            tickActiveQuarry();
        }
        if (world.isRemote) {
            com.zzhalex233.alexscaves.AlexsCaves.PROXY.playQuarrySmasherSound(this);
        }
        tickPullingItems();
        if (blockBreakCooldown > 0) {
            blockBreakCooldown--;
        }
        if (shakeTime > 0) {
            shakeTime--;
        }
        tickHeadPosition();
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.7D;
        motionY *= 0.7D;
        motionZ *= 0.7D;
    }

    private void tickActiveQuarry() {
        BlockPos quarry = getQuarryPos();
        if (quarryActivityTime-- < 0) {
            quarryActivityTime = 20;
            if (quarry == null || world.getBlockState(quarry).getBlock() != ACBlockRegistry.QUARRY.block()) {
                setQuarryPos(null);
                setInactive(true);
            } else if (world.getTileEntity(quarry) instanceof QuarryTileEntity) {
                lastMiningArea = ((QuarryTileEntity) world.getTileEntity(quarry)).getMiningBox();
            }
        }
        BlockPos target = getTargetPos();
        if (!world.isRemote) {
            if (target == null) {
                setTargetPos(findTarget());
            } else {
                Vec3d dist = upFromBottomCenter(quarry == null ? target : new BlockPos(target.getX(), quarry.getY(), target.getZ()), 4.0D).subtract(getPositionVector());
                if (horizontalDistance(dist) < 0.5D) {
                    if (blockBreakCooldown <= 0) {
                        setSlamming(true);
                        if (headPosition.distanceTo(new Vec3d(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D)) < 1.1D) {
                            setSlamming(false);
                            world.playSound(null, target, ACSoundRegistry.BOUNDROID_SLAM, SoundCategory.BLOCKS, 1.5F, 1.0F);
                            world.destroyBlock(target, true);
                            setTargetPos(null);
                            blockBreakCooldown = 35;
                            setPullingItemsFor(20);
                        }
                    }
                } else {
                    Vec3d move = dist.normalize().scale(0.1D);
                    motionX += move.x;
                    motionY += move.y;
                    motionZ += move.z;
                }
            }
        }
    }

    private void tickPullingItems() {
        if (pullingItemsFor() <= 0) {
            return;
        }
        int remaining = pullingItemsFor() - 1;
        setPullingItemsFor(remaining);
        BlockPos quarry = getQuarryPos();
        boolean foundItem = false;
        AxisAlignedBB headBox = new AxisAlignedBB(headPosition.x - 2.0D, headPosition.y - 2.0D, headPosition.z - 2.0D, headPosition.x + 2.0D, headPosition.y + 4.0D, headPosition.z + 2.0D);
        for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class, headBox)) {
            item.velocityChanged = true;
            if (!pulledItems.contains(item)) {
                pulledItems.add(item);
            }
            foundItem = true;
        }
        for (EntityItem item : pulledItems) {
            if (remaining == 0) {
                if (quarry != null && world.getBlockState(quarry).getBlock() == ACBlockRegistry.QUARRY.block()) {
                    item.setPosition(quarry.getX() + 0.5D, quarry.getY() + 1.0D, quarry.getZ() + 0.5D);
                    EnumFacing facing = world.getBlockState(quarry).getValue(QuarryBlock.FACING);
                    item.setDefaultPickupDelay();
                    item.motionX = facing.getXOffset() * 0.1D;
                    item.motionY = 0.4D;
                    item.motionZ = facing.getZOffset() * 0.1D;
                    item.velocityChanged = true;
                }
            } else {
                item.setPosition(headPosition.x, headPosition.y - 0.5D, headPosition.z);
                item.motionX = item.motionY = item.motionZ = 0.0D;
                item.velocityChanged = true;
            }
        }
        if (foundItem && quarry != null && world.getTileEntity(quarry) instanceof QuarryTileEntity) {
            ((QuarryTileEntity) world.getTileEntity(quarry)).spinFor = 13;
            world.playSound(null, quarry, ACSoundRegistry.QUARRY_CRUSH, SoundCategory.BLOCKS, 1.0F, 0.9F + rand.nextFloat() * 0.2F);
        }
        if (remaining == 0) {
            pulledItems.clear();
        }
    }

    private void tickHeadPosition() {
        prevHeadPosition = headPosition;
        Vec3d target = getHeadTargetPos();
        double speed = isInactive() ? 0.5D : isSlamming() ? getHeadGroundProgress(1.0F) * 0.3D : pullingItemsFor() > 5 ? 0.0D : 0.05D;
        headPosition = headPosition.add(target.subtract(headPosition).scale(speed));
    }

    private Vec3d getHeadTargetPos() {
        if (isInactive()) {
            return getPositionVector().add(0.75D, 0.0D, -0.75D);
        }
        if (isSlamming()) {
            BlockPos target = getTargetPos();
            if (target != null) {
                return upFromBottomCenter(target, 1.0D);
            }
        }
        return getPositionVector().add(0.0D, -1.0D + Math.sin(ticksExisted * 0.1D) * 0.5D, 0.0D);
    }

    @Nullable
    public BlockPos findTarget() {
        BlockPos quarry = getQuarryPos();
        if (quarry != null && world.getTileEntity(quarry) instanceof QuarryTileEntity) {
            QuarryTileEntity tile = (QuarryTileEntity) world.getTileEntity(quarry);
            if (tile.hasMiningArea()) {
                return tile.findMineableBlock(quarry.getY() + 3).orElse(null);
            }
        }
        return null;
    }

    public float getChainLength(float partialTicks) {
        return (float) getHeadPosition(partialTicks).subtract(getPositionVector()).length();
    }

    public Vec3d getHeadPosition(float partialTicks) {
        return prevHeadPosition.add(headPosition.subtract(prevHeadPosition).scale(partialTicks));
    }

    public boolean isInactive() {
        return dataManager.get(INACTIVE);
    }

    public void setInactive(boolean inactive) {
        dataManager.set(INACTIVE, inactive);
    }

    public boolean isSlamming() {
        return dataManager.get(SLAMMING);
    }

    public void setSlamming(boolean slamming) {
        dataManager.set(SLAMMING, slamming);
    }

    public int pullingItemsFor() {
        return dataManager.get(PULLING_ITEMS_FOR);
    }

    public void setPullingItemsFor(int pullingItemsFor) {
        dataManager.set(PULLING_ITEMS_FOR, pullingItemsFor);
    }

    public float getInactiveProgress(float partialTicks) {
        return (prevInactiveProgress + (inactiveProgress - prevInactiveProgress) * partialTicks) * 0.1F;
    }

    public boolean isBeingActivated() {
        return inactiveProgress <= prevInactiveProgress;
    }

    public float getHeadGroundProgress(float partialTicks) {
        return (prevHeadGroundProgress + (headGroundProgress - prevHeadGroundProgress) * partialTicks) * 0.2F;
    }

    public void setQuarryPos(@Nullable BlockPos blockPos) {
        setSyncedPos(blockPos, QUARRY_X, QUARRY_Y, QUARRY_Z);
    }

    @Nullable
    public BlockPos getQuarryPos() {
        return getSyncedPos(QUARRY_X, QUARRY_Y, QUARRY_Z);
    }

    public void setTargetPos(@Nullable BlockPos blockPos) {
        setSyncedPos(blockPos, TARGET_X, TARGET_Y, TARGET_Z);
    }

    @Nullable
    public BlockPos getTargetPos() {
        return getSyncedPos(TARGET_X, TARGET_Y, TARGET_Z);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isEntityInvulnerable(source)) {
            return false;
        }
        damageSustained += amount;
        world.setEntityState(this, (byte) 48);
        if (damageSustained >= 10) {
            playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            if (!world.isRemote) {
                entityDropItem(new ItemStack(ACItemRegistry.AZURE_NEODYMIUM_INGOT.item(), 1 + rand.nextInt(2)), 0.0F);
                entityDropItem(new ItemStack(ACItemRegistry.SCARLET_NEODYMIUM_INGOT.item(), 1 + rand.nextInt(2)), 0.0F);
            }
            setDead();
        }
        return true;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 48) {
            shakeTime = 10;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public ItemStack getPickedResult(net.minecraft.util.math.RayTraceResult target) {
        return new ItemStack(ACItemRegistry.QUARRY_SMASHER.item());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        setInactive(compound.getBoolean("Inactive"));
        setSlamming(compound.getBoolean("Slamming"));
        setPullingItemsFor(compound.getInteger("PullingItemsFor"));
        setQuarryPos(readPos(compound, "Quarry"));
        setTargetPos(readPos(compound, "Target"));
        inactiveProgress = compound.getFloat("InactiveProgress");
        headGroundProgress = compound.getFloat("HeadGroundProgress");
        damageSustained = compound.getInteger("DamageSustained");
        if (compound.hasKey("HeadX")) {
            headPosition = new Vec3d(compound.getDouble("HeadX"), compound.getDouble("HeadY"), compound.getDouble("HeadZ"));
            prevHeadPosition = headPosition;
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setBoolean("Inactive", isInactive());
        compound.setBoolean("Slamming", isSlamming());
        compound.setInteger("PullingItemsFor", pullingItemsFor());
        writePos(compound, "Quarry", getQuarryPos());
        writePos(compound, "Target", getTargetPos());
        compound.setFloat("InactiveProgress", inactiveProgress);
        compound.setFloat("HeadGroundProgress", headGroundProgress);
        compound.setInteger("DamageSustained", damageSustained);
        compound.setDouble("HeadX", headPosition.x);
        compound.setDouble("HeadY", headPosition.y);
        compound.setDouble("HeadZ", headPosition.z);
    }

    private void setSyncedPos(@Nullable BlockPos pos, DataParameter<Integer> x, DataParameter<Integer> y, DataParameter<Integer> z) {
        dataManager.set(x, pos == null ? Integer.MIN_VALUE : pos.getX());
        dataManager.set(y, pos == null ? Integer.MIN_VALUE : pos.getY());
        dataManager.set(z, pos == null ? Integer.MIN_VALUE : pos.getZ());
    }

    @Nullable
    private BlockPos getSyncedPos(DataParameter<Integer> x, DataParameter<Integer> y, DataParameter<Integer> z) {
        int posX = dataManager.get(x);
        int posY = dataManager.get(y);
        int posZ = dataManager.get(z);
        return posX == Integer.MIN_VALUE || posY == Integer.MIN_VALUE || posZ == Integer.MIN_VALUE ? null : new BlockPos(posX, posY, posZ);
    }

    private static double horizontalDistance(Vec3d vec) {
        return Math.sqrt(vec.x * vec.x + vec.z * vec.z);
    }

    private static Vec3d upFromBottomCenter(BlockPos blockPos, double up) {
        return new Vec3d(blockPos.getX() + 0.5D, blockPos.getY() + up, blockPos.getZ() + 0.5D);
    }

    private void writePos(NBTTagCompound compound, String key, @Nullable BlockPos blockPos) {
        if (blockPos != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("X", blockPos.getX());
            tag.setInteger("Y", blockPos.getY());
            tag.setInteger("Z", blockPos.getZ());
            compound.setTag(key, tag);
        }
    }

    @Nullable
    private BlockPos readPos(NBTTagCompound compound, String key) {
        if (!compound.hasKey(key, 10)) {
            return null;
        }
        NBTTagCompound tag = compound.getCompoundTag(key);
        return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
    }
}
