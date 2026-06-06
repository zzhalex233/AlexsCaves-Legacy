package com.zzhalex233.alexscaves.server.block.entity;

import java.util.Optional;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.QuarryBlock;
import com.zzhalex233.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.zzhalex233.alexscaves.server.entity.util.MagnetUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class QuarryTileEntity extends TileEntity implements ITickable {
    private static final int FURTHEST_LIGHT_DISTANCE = 20;
    private BlockPos bottomLeftLight;
    private BlockPos bottomRightLight;
    private BlockPos topLeftLight;
    private BlockPos topRightLight;
    private boolean hasMiningArea;
    private int checkTimer;
    public int spinFor;
    private AxisAlignedBB miningBox;
    private QuarrySmasherEntity serverSmasher;
    private BlockPos lastMineablePos;
    private float previousRotation;
    private float rotation;

    @Override
    public void update() {
        previousRotation = rotation;
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof QuarryBlock)) {
            return;
        }
        if (checkTimer-- < 0) {
            checkTimer = 20 + world.rand.nextInt(20);
            if (searchForLights(state.getValue(QuarryBlock.FACING))) {
                hasMiningArea = true;
                miningBox = new AxisAlignedBB(bottomLeftLight, bottomRightLight).union(new AxisAlignedBB(topLeftLight, topRightLight));
                if (!world.isRemote) {
                    lastMineablePos = findMineableBlock(pos.getY() + 3).orElse(null);
                    if (serverSmasher == null) {
                        serverSmasher = findClosestSmasher();
                    }
                }
            } else {
                hasMiningArea = false;
                miningBox = null;
            }
            markUpdated();
        }
        if (!world.isRemote && serverSmasher != null) {
            serverSmasher.setQuarryPos(pos);
            if (serverSmasher.isDead) {
                serverSmasher = null;
            } else if (hasMiningArea && lastMineablePos != null) {
                serverSmasher.setInactive(false);
            } else {
                serverSmasher.setInactive(true);
                serverSmasher = null;
            }
        }
        if (world.isRemote && hasMiningArea) {
            spawnLightningBetween(bottomLeftLight, bottomRightLight);
            spawnLightningBetween(bottomLeftLight, topLeftLight);
            spawnLightningBetween(bottomRightLight, topRightLight);
            spawnLightningBetween(topLeftLight, topRightLight);
        }
        if (spinFor > 0) {
            spinFor--;
            rotation += Math.min(10, spinFor) * 0.1F;
        }
    }

    private QuarrySmasherEntity findClosestSmasher() {
        if (miningBox == null) {
            return null;
        }
        QuarrySmasherEntity closest = null;
        Vec3d center = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        for (QuarrySmasherEntity smasher : world.getEntitiesWithinAABB(QuarrySmasherEntity.class, miningBox.grow(0.0D, 100.0D, 0.0D))) {
            if (closest == null || smasher.getDistanceSq(center.x, center.y, center.z) < closest.getDistanceSq(center.x, center.y, center.z)) {
                closest = smasher;
            }
        }
        return closest;
    }

    private boolean searchForLights(EnumFacing blockFacing) {
        BlockPos directlyBehind = pos.offset(blockFacing.getOpposite());
        EnumFacing left = blockFacing.getOpposite().rotateYCCW();
        EnumFacing right = blockFacing.getOpposite().rotateY();
        bottomLeftLight = findLight(directlyBehind, left, false);
        if (bottomLeftLight == null) {
            return false;
        }
        bottomRightLight = findLight(directlyBehind, right, true);
        if (bottomRightLight == null) {
            return false;
        }
        topLeftLight = findLight(bottomLeftLight, blockFacing.getOpposite(), true);
        if (topLeftLight == null) {
            return false;
        }
        topRightLight = findLight(bottomRightLight, blockFacing.getOpposite(), true);
        if (topRightLight == null) {
            return false;
        }
        return straightLine(topLeftLight, topRightLight) && straightLine(topRightLight, bottomRightLight) && straightLine(topLeftLight, bottomLeftLight) && straightLine(bottomLeftLight, bottomRightLight);
    }

    private BlockPos findLight(BlockPos start, EnumFacing direction, boolean moveFirst) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(start);
        if (moveFirst) {
            mutable.move(direction);
        }
        int dist = 0;
        while (dist < FURTHEST_LIGHT_DISTANCE && !isMineable(mutable) && world.isBlockLoaded(mutable)) {
            mutable.move(direction);
            dist++;
        }
        return world.getBlockState(mutable).getBlock() == ACBlockRegistry.MAGNETIC_LIGHT.block() ? mutable.toImmutable() : null;
    }

    private void spawnLightningBetween(BlockPos from, BlockPos to) {
        if (from != null && to != null && world.rand.nextInt(4) == 0) {
            Vec3d particleFrom = upFromBottomCenter(from);
            Vec3d particleTo = upFromBottomCenter(to);
            if (world.rand.nextBoolean()) {
                Vec3d swap = particleFrom;
                particleFrom = particleTo;
                particleTo = swap;
            }
            AlexsCaves.PROXY.spawnQuarryBorderLightning(world, particleFrom, particleTo);
        }
    }

    private Vec3d upFromBottomCenter(BlockPos blockPos) {
        return new Vec3d(blockPos.getX() + 0.5D, blockPos.getY() + 0.4D, blockPos.getZ() + 0.5D);
    }

    private boolean straightLine(BlockPos first, BlockPos second) {
        return first.getX() == second.getX() || first.getZ() == second.getZ();
    }

    public Optional<BlockPos> findMineableBlock(double yStart) {
        if (miningBox == null) {
            return Optional.empty();
        }
        BlockPos highest = null;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = (int) miningBox.minX + 1; x < (int) miningBox.maxX; x++) {
            for (int z = (int) miningBox.minZ + 1; z < (int) miningBox.maxZ; z++) {
                mutable.setPos(x, yStart, z);
                while (mutable.getY() > 1 && !isMineable(mutable)) {
                    mutable.move(EnumFacing.DOWN);
                }
                if (isMineable(mutable) && (highest == null || highest.getY() < mutable.getY())) {
                    highest = mutable.toImmutable();
                }
            }
        }
        return Optional.ofNullable(highest);
    }

    public boolean isMineable(BlockPos blockPos) {
        IBlockState state = world.getBlockState(blockPos);
        return !state.getMaterial().isLiquid() && !state.getMaterial().isReplaceable() && state.getBlockHardness(world, blockPos) >= 0.0F && !MagnetUtil.isUnmoveable(state);
    }

    public AxisAlignedBB getMiningBox() {
        return miningBox;
    }

    public boolean hasMiningArea() {
        return hasMiningArea;
    }

    public float getGrindRotation(float partialTicks) {
        return previousRotation + (rotation - previousRotation) * partialTicks;
    }

    public BlockPos[] getLights() {
        return new BlockPos[] {bottomLeftLight, bottomRightLight, topLeftLight, topRightLight};
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return miningBox == null ? new AxisAlignedBB(pos).grow(2.0D) : miningBox.grow(2.0D, 100.0D, 2.0D);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("HasMiningArea", hasMiningArea);
        writePos(compound, "BottomLeftLight", bottomLeftLight);
        writePos(compound, "BottomRightLight", bottomRightLight);
        writePos(compound, "TopLeftLight", topLeftLight);
        writePos(compound, "TopRightLight", topRightLight);
        compound.setFloat("Rotation", rotation);
        compound.setInteger("SpinFor", spinFor);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        hasMiningArea = compound.getBoolean("HasMiningArea");
        bottomLeftLight = readPos(compound, "BottomLeftLight");
        bottomRightLight = readPos(compound, "BottomRightLight");
        topLeftLight = readPos(compound, "TopLeftLight");
        topRightLight = readPos(compound, "TopRightLight");
        if (hasMiningArea && bottomLeftLight != null && bottomRightLight != null && topLeftLight != null && topRightLight != null) {
            miningBox = new AxisAlignedBB(bottomLeftLight, bottomRightLight).union(new AxisAlignedBB(topLeftLight, topRightLight));
        } else {
            miningBox = null;
        }
        rotation = compound.getFloat("Rotation");
        spinFor = compound.getInteger("SpinFor");
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    private void markUpdated() {
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    private void writePos(NBTTagCompound compound, String key, BlockPos blockPos) {
        if (blockPos != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("X", blockPos.getX());
            tag.setInteger("Y", blockPos.getY());
            tag.setInteger("Z", blockPos.getZ());
            compound.setTag(key, tag);
        }
    }

    private BlockPos readPos(NBTTagCompound compound, String key) {
        if (!compound.hasKey(key, 10)) {
            return null;
        }
        NBTTagCompound tag = compound.getCompoundTag(key);
        return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
    }
}
