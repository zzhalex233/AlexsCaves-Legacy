package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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

public class TreeStarBlock extends BlockDirectional {
    private static final AxisAlignedBB SHAPE_UP = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.25D, 0.9375D);
    private static final AxisAlignedBB SHAPE_DOWN = new AxisAlignedBB(0.0625D, 0.75D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
    private static final AxisAlignedBB SHAPE_WEST = new AxisAlignedBB(0.75D, 0.0625D, 0.0625D, 1.0D, 0.9375D, 0.9375D);
    private static final AxisAlignedBB SHAPE_EAST = new AxisAlignedBB(0.0D, 0.0625D, 0.0625D, 0.25D, 0.9375D, 0.9375D);
    private static final AxisAlignedBB SHAPE_NORTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.75D, 0.9375D, 0.9375D, 1.0D);
    private static final AxisAlignedBB SHAPE_SOUTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.0D, 0.9375D, 0.9375D, 0.25D);

    public TreeStarBlock() {
        super(Material.PLANTS);
        setHardness(0.0F);
        setResistance(0.0F);
        setSoundType(SoundType.PLANT);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return canAttachTo(worldIn, pos.offset(side.getOpposite()), side);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (canPlaceBlockOnSide(worldIn, pos, facing)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, canPlaceBlockOnSide(world, pos, facing) ? facing : EnumFacing.UP);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canAttachTo(worldIn, pos.offset(state.getValue(FACING).getOpposite()), state.getValue(FACING))) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    private boolean canAttachTo(World world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isLeaves(state, world, pos) || state.isSideSolid(world, pos, side);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case DOWN:
                return SHAPE_DOWN;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case UP:
            default:
                return SHAPE_UP;
        }
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

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
