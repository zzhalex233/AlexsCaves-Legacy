package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class FiddleheadBlock extends CavePlantBlock {
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.5D, 0.625D);

    public FiddleheadBlock() {
        super(false);
        setTickRandomly(true);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && rand.nextInt(7) == 0 && worldIn.isAirBlock(pos.up())) {
            EnumFacing facing = EnumFacing.byHorizontalIndex(rand.nextInt(4));
            worldIn.setBlockState(pos, ACBlockRegistry.CURLY_FERN.block().getDefaultState().withProperty(CurlyFernBlock.FACING, facing), 3);
            worldIn.setBlockState(pos.up(), ACBlockRegistry.CURLY_FERN.block().getDefaultState().withProperty(CurlyFernBlock.FACING, facing).withProperty(CurlyFernBlock.HALF, CurlyFernBlock.Half.UPPER), 3);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }
}
