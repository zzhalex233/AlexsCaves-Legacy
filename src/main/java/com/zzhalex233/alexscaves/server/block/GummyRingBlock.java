package com.zzhalex233.alexscaves.server.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GummyRingBlock extends BlockDirectional {
    public static final PropertyBool FLOATING = PropertyBool.create("floating");

    private static final AxisAlignedBB SHAPE_UP = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    private static final AxisAlignedBB SHAPE_UP_FLOATING = new AxisAlignedBB(0.0D, -0.25D, 0.0D, 1.0D, 0.25D, 1.0D);
    private static final AxisAlignedBB SHAPE_DOWN = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB SHAPE_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB SHAPE_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
    private static final AxisAlignedBB SHAPE_EAST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D);
    private static final AxisAlignedBB SHAPE_WEST = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public GummyRingBlock() {
        super(Material.CAKE);
        setHardness(2.0F);
        setResistance(2.0F);
        setSoundType(ACSoundTypes.SQUISHY_CANDY);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(FLOATING, Boolean.FALSE));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, facing).withProperty(FLOATING, isFloating(world, pos, facing));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        updateFloating(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, net.minecraft.block.Block blockIn, BlockPos fromPos) {
        updateFloating(worldIn, pos, state);
    }

    private void updateFloating(World world, BlockPos pos, IBlockState state) {
        boolean floating = isFloating(world, pos, state.getValue(FACING));
        if (state.getValue(FLOATING) != floating) {
            world.setBlockState(pos, state.withProperty(FLOATING, floating), 3);
        }
    }

    private boolean isFloating(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return facing == EnumFacing.UP && world.getBlockState(pos.down()).getMaterial().isLiquid();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return shapeFor(state);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        for (AxisAlignedBB box : collisionBoxesFor(state)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        }
    }

    private AxisAlignedBB shapeFor(IBlockState state) {
        switch (state.getValue(FACING)) {
            case DOWN:
                return SHAPE_DOWN;
            case NORTH:
                return SHAPE_NORTH;
            case EAST:
                return SHAPE_EAST;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            default:
                return state.getValue(FLOATING) ? SHAPE_UP_FLOATING : SHAPE_UP;
        }
    }

    private AxisAlignedBB[] collisionBoxesFor(IBlockState state) {
        double minY = 0.0D;
        double maxY = 0.5D;
        EnumFacing facing = state.getValue(FACING);
        if (facing == EnumFacing.UP && state.getValue(FLOATING)) {
            minY = -0.25D;
            maxY = 0.25D;
        } else if (facing == EnumFacing.DOWN) {
            minY = 0.5D;
            maxY = 1.0D;
        }
        switch (facing) {
            case NORTH:
                return new AxisAlignedBB[] {
                        new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 0.3125D, 1.0D),
                        new AxisAlignedBB(0.0D, 0.6875D, 0.5D, 1.0D, 1.0D, 1.0D),
                        new AxisAlignedBB(0.0D, 0.3125D, 0.5D, 0.3125D, 0.6875D, 1.0D),
                        new AxisAlignedBB(0.6875D, 0.3125D, 0.5D, 1.0D, 0.6875D, 1.0D) };
            case EAST:
                return new AxisAlignedBB[] {
                        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.3125D, 1.0D),
                        new AxisAlignedBB(0.0D, 0.6875D, 0.0D, 0.5D, 1.0D, 1.0D),
                        new AxisAlignedBB(0.0D, 0.3125D, 0.0D, 0.5D, 0.6875D, 0.3125D),
                        new AxisAlignedBB(0.0D, 0.3125D, 0.6875D, 0.5D, 0.6875D, 1.0D) };
            case SOUTH:
                return new AxisAlignedBB[] {
                        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 0.5D),
                        new AxisAlignedBB(0.0D, 0.6875D, 0.0D, 1.0D, 1.0D, 0.5D),
                        new AxisAlignedBB(0.0D, 0.3125D, 0.0D, 0.3125D, 0.6875D, 0.5D),
                        new AxisAlignedBB(0.6875D, 0.3125D, 0.0D, 1.0D, 0.6875D, 0.5D) };
            case WEST:
                return new AxisAlignedBB[] {
                        new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D),
                        new AxisAlignedBB(0.5D, 0.6875D, 0.0D, 1.0D, 1.0D, 1.0D),
                        new AxisAlignedBB(0.5D, 0.3125D, 0.0D, 1.0D, 0.6875D, 0.3125D),
                        new AxisAlignedBB(0.5D, 0.3125D, 0.6875D, 1.0D, 0.6875D, 1.0D) };
            default:
                return new AxisAlignedBB[] {
                        new AxisAlignedBB(0.0D, minY, 0.0D, 1.0D, maxY, 0.3125D),
                        new AxisAlignedBB(0.0D, minY, 0.6875D, 1.0D, maxY, 1.0D),
                        new AxisAlignedBB(0.0D, minY, 0.3125D, 0.3125D, maxY, 0.6875D),
                        new AxisAlignedBB(0.6875D, minY, 0.3125D, 1.0D, maxY, 0.6875D) };
        }
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
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7)).withProperty(FLOATING, (meta & 8) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() | (state.getValue(FLOATING) ? 8 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, FLOATING);
    }
}
