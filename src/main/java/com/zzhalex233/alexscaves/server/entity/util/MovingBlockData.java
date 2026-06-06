package com.zzhalex233.alexscaves.server.entity.util;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MovingBlockData {
    private final IBlockState state;
    private final BlockPos offset;
    @Nullable
    private final NBTTagCompound tileData;

    public MovingBlockData(IBlockState state, BlockPos offset, @Nullable NBTTagCompound tileData) {
        this.state = state;
        this.offset = offset;
        this.tileData = tileData;
    }

    public MovingBlockData(World world, NBTTagCompound tag) {
        this(Block.getStateById(tag.getInteger("State")), new BlockPos(tag.getInteger("OffsetX"), tag.getInteger("OffsetY"), tag.getInteger("OffsetZ")), tag.hasKey("TileData") ? tag.getCompoundTag("TileData") : null);
    }

    public IBlockState state() {
        return state;
    }

    public BlockPos offset() {
        return offset;
    }

    @Nullable
    public NBTTagCompound tileData() {
        return tileData == null ? null : tileData.copy();
    }

    public AxisAlignedBB boundingBox(World world, BlockPos basePos) {
        BlockPos blockPos = basePos.add(offset);
        AxisAlignedBB box = state.getCollisionBoundingBox(world, blockPos);
        return box == null ? new AxisAlignedBB(blockPos, blockPos.add(1, 1, 1)) : box.offset(blockPos);
    }

    public NBTTagCompound toTag() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("State", Block.getStateId(state));
        tag.setInteger("OffsetX", offset.getX());
        tag.setInteger("OffsetY", offset.getY());
        tag.setInteger("OffsetZ", offset.getZ());
        if (tileData != null) {
            tag.setTag("TileData", tileData.copy());
        }
        return tag;
    }
}
