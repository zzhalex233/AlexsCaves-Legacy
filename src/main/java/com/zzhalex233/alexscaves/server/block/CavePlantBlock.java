package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CavePlantBlock extends BlockBush {
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.1875D, 0.875D);
    private static final AxisAlignedBB SHAPE_TALL = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.4375D, 0.8125D);
    private final boolean taller;

    public CavePlantBlock(boolean taller) {
        this.taller = taller;
        setHardness(0.0F);
        setResistance(0.0F);
        setSoundType(SoundType.PLANT);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return taller ? SHAPE_TALL : SHAPE;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
