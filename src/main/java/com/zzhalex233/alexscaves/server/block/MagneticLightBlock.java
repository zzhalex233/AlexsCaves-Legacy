package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MagneticLightBlock extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    private static final AxisAlignedBB SHAPE_UP = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.625D, 0.625D);
    private static final AxisAlignedBB SHAPE_DOWN = new AxisAlignedBB(0.375D, 0.375D, 0.375D, 0.625D, 1.0D, 0.625D);
    private static final AxisAlignedBB SHAPE_EAST = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 0.625D, 0.625D, 0.625D);
    private static final AxisAlignedBB SHAPE_WEST = new AxisAlignedBB(0.375D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D);
    private static final AxisAlignedBB SHAPE_NORTH = new AxisAlignedBB(0.375D, 0.375D, 0.375D, 0.625D, 0.625D, 1.0D);
    private static final AxisAlignedBB SHAPE_SOUTH = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 0.625D);

    public MagneticLightBlock() {
        super(Material.IRON);
        setHardness(0.5F);
        setResistance(1.0F);
        setSoundType(SoundType.METAL);
        setLightLevel(12.0F / 15.0F);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
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
            default:
                return SHAPE_UP;
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return canAttachTo(world, pos.offset(side.getOpposite()), side);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (canPlaceBlockOnSide(world, pos, facing)) {
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
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!canBlockStay(world, pos, state)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canBlockStay(world, pos, state)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    private boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        return canAttachTo(world, pos.offset(facing.getOpposite()), facing);
    }

    private boolean canAttachTo(World world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isLeaves(state, world, pos) || state.getBlock().isSideSolid(state, world, pos, facing);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
