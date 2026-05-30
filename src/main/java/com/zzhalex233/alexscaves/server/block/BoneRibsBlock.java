package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BoneRibsBlock extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool UNDER = PropertyBool.create("under");
    private static final AxisAlignedBB TOP = new AxisAlignedBB(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    private static final AxisAlignedBB NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    private static final AxisAlignedBB SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
    private static final AxisAlignedBB EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BoneRibsBlock() {
        super(Material.ROCK);
        setHardness(2.0F);
        setResistance(2.0F);
        setSoundType(SoundType.STONE);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(UNDER, false));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        BlockPos placedOnPos = pos.offset(facing.getOpposite());
        IBlockState placedOn = world.getBlockState(placedOnPos);
        EnumFacing horizontal = facing.getAxis().isHorizontal() ? facing : placer.getHorizontalFacing().getOpposite();
        boolean under = facing == EnumFacing.DOWN || hitY < 0.5F;
        if (placedOn.getBlock() == this && facing.getAxis().isVertical()) {
            horizontal = placedOn.getValue(FACING);
            under = !placedOn.getValue(UNDER);
        }
        return getDefaultState().withProperty(FACING, horizontal).withProperty(UNDER, under);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB side;
        switch (state.getValue(FACING)) {
            case EAST:
                side = EAST;
                break;
            case SOUTH:
                side = SOUTH;
                break;
            case WEST:
                side = WEST;
                break;
            case NORTH:
            default:
                side = NORTH;
                break;
        }
        return (state.getValue(UNDER) ? BOTTOM : TOP).union(side);
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

    @Override
    public IBlockState withRotation(IBlockState state, net.minecraft.util.Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, net.minecraft.util.Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() | (state.getValue(UNDER) ? 4 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3)).withProperty(UNDER, (meta & 4) != 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, UNDER);
    }
}
