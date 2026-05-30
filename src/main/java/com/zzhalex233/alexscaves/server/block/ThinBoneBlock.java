package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ThinBoneBlock extends BlockRotatedPillar {
    public static final PropertyInteger OFFSET = PropertyInteger.create("offset", 0, 2);
    private static final AxisAlignedBB SHAPE_X = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D);
    private static final AxisAlignedBB SHAPE_Y = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
    private static final AxisAlignedBB SHAPE_Z = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 1.0D);

    public ThinBoneBlock() {
        super(Material.ROCK);
        setHardness(2.0F);
        setResistance(2.0F);
        setSoundType(SoundType.STONE);
        setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y).withProperty(OFFSET, 1));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        int offset = 1;
        if (facing.getAxis().isHorizontal()) {
            offset = hitY < 0.33F ? 0 : hitY < 0.66F ? 1 : 2;
        }
        return getDefaultState().withProperty(AXIS, facing.getAxis()).withProperty(OFFSET, offset);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB shape = state.getValue(AXIS) == EnumFacing.Axis.X ? SHAPE_X : state.getValue(AXIS) == EnumFacing.Axis.Y ? SHAPE_Y : SHAPE_Z;
        if (state.getValue(AXIS) == EnumFacing.Axis.Y || state.getValue(OFFSET) == 1) {
            return shape;
        }
        double offset = state.getValue(OFFSET) == 0 ? -0.375D : 0.375D;
        return shape.offset(0.0D, offset, 0.0D);
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int axis = state.getValue(AXIS) == EnumFacing.Axis.X ? 1 : state.getValue(AXIS) == EnumFacing.Axis.Z ? 2 : 0;
        return axis * 3 + state.getValue(OFFSET);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing.Axis axis = meta / 3 == 1 ? EnumFacing.Axis.X : meta / 3 == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.Y;
        return getDefaultState().withProperty(AXIS, axis).withProperty(OFFSET, meta % 3);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, OFFSET);
    }
}
