package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChocolateBlock extends BasicPillarBlock {
    public ChocolateBlock(float hardness, float resistance, SoundType sound) {
        super(Material.CAKE, hardness, resistance, sound);
        setTickRandomly(true);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, net.minecraft.block.Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        updateFrosting(worldIn, pos, state);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        updateFrosting(worldIn, pos, state);
    }

    private void updateFrosting(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote && state.getValue(AXIS) == net.minecraft.util.EnumFacing.Axis.Y && world.getBlockState(pos.up()).getBlock() == ACBlockRegistry.BLOCK_OF_FROSTING.block()) {
            world.setBlockState(pos, ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.block().getDefaultState(), 3);
        }
    }
}
