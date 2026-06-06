package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SulfurBlock extends BasicCaveBlock {
    public SulfurBlock() {
        super(Material.ROCK, 2.0F, 4.0F, SoundType.STONE);
        setTickRandomly(true);
    }

    public SulfurBlock sound(SoundType soundType) {
        setSoundType(soundType);
        return this;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote || rand.nextInt(10) != 0) {
            return;
        }
        EnumFacing facing = EnumFacing.values()[rand.nextInt(EnumFacing.values().length)];
        BlockPos offset = pos.offset(facing);
        if (worldIn.isAirBlock(offset) && isDrippingAcidAbove(worldIn, offset)) {
            worldIn.setBlockState(offset, ACBlockRegistry.SULFUR_BUD_SMALL.block().getDefaultState().withProperty(SulfurBudBlock.FACING, facing), 3);
        }
    }

    private boolean isDrippingAcidAbove(World world, BlockPos pos) {
        while (world.isAirBlock(pos) && pos.getY() < world.getHeight()) {
            pos = pos.up();
        }
        Block block = world.getBlockState(pos).getBlock();
        return block == ACBlockRegistry.ACID.block() || block == ACBlockRegistry.ACIDIC_RADROCK.block();
    }
}
