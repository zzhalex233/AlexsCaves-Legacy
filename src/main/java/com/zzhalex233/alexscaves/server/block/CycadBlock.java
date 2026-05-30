package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
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

public class CycadBlock extends BlockBush implements IGrowable {
    public static final PropertyBool TOP = PropertyBool.create("top");
    private static final AxisAlignedBB TOP_SHAPE = new AxisAlignedBB(-1.0D, 0.0D, -1.0D, 2.0D, 1.0D, 2.0D);
    private static final AxisAlignedBB TRUNK_SHAPE = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);

    public CycadBlock() {
        setHardness(0.0F);
        setResistance(0.0F);
        setSoundType(SoundType.PLANT);
        setDefaultState(blockState.getBaseState().withProperty(TOP, true));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState down = worldIn.getBlockState(pos.down());
        return down.getBlock() == this || super.canPlaceBlockAt(worldIn, pos);
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == this || super.canSustainBush(state);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(TOP, world.getBlockState(pos.up()).getBlock() != this);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        syncTopState(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canPlaceBlockAt(worldIn, pos)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return;
        }
        syncTopState(worldIn, pos, state);
    }

    private void syncTopState(World world, BlockPos pos, IBlockState state) {
        boolean top = world.getBlockState(pos.up()).getBlock() != this;
        if (state.getValue(TOP) != top) {
            world.setBlockState(pos, state.withProperty(TOP, top), 2);
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(TOP) && height(worldIn, pos) < 4 && worldIn.isAirBlock(pos.up());
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        if (canGrow(worldIn, pos, state, false)) {
            worldIn.setBlockState(pos, state.withProperty(TOP, false), 2);
            worldIn.setBlockState(pos.up(), getDefaultState(), 3);
        }
    }

    private int height(World world, BlockPos pos) {
        int height = 1;
        while (world.getBlockState(pos.down(height)).getBlock() == this) {
            height++;
        }
        return height;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(TOP) ? TOP_SHAPE : TRUNK_SHAPE;
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
        return state.getValue(TOP) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TOP, (meta & 1) == 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TOP);
    }
}
