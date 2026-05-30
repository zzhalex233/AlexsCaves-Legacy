package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.server.entity.item.FallingFrostmintEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class FrostmintBlock extends BasicSlabBlock {
    protected FrostmintBlock() {
        super(Material.CAKE, 1.0F, 1.5F, SoundType.STONE);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && shouldFall(state, worldIn, pos)) {
            FallingFrostmintEntity.fall(worldIn, pos, state);
        }
    }

    @Override
    public int tickRate(World worldIn) {
        return 2;
    }

    public static void scheduleColumnFall(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (world.isRemote || !(state.getBlock() instanceof FrostmintBlock)) {
            return;
        }
        while (pos.getY() > 0 && world.getBlockState(pos.down()).getBlock() instanceof FrostmintBlock) {
            pos = pos.down();
        }
        world.scheduleUpdate(pos, world.getBlockState(pos).getBlock(), 2);
    }

    public static boolean isFree(IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof FrostmintBlock && !((FrostmintBlock) block).isDouble() && state.getValue(HALF) == EnumBlockHalf.BOTTOM) {
            return true;
        }
        return BlockFalling.canFallThrough(state);
    }

    private boolean shouldFall(IBlockState state, World world, BlockPos pos) {
        return pos.getY() >= 0 && (isFree(world.getBlockState(pos.down())) || !isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP);
    }

    public static class Half extends FrostmintBlock {
        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public static class Double extends FrostmintBlock {
        @Override
        public boolean isDouble() {
            return true;
        }
    }
}
