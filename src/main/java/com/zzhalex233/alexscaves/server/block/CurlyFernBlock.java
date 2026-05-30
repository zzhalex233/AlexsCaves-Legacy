package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CurlyFernBlock extends BlockBush implements IGrowable {
    public static final PropertyEnum<Half> HALF = PropertyEnum.create("half", Half.class);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

    public CurlyFernBlock() {
        setHardness(0.0F);
        setResistance(0.0F);
        setSoundType(SoundType.PLANT);
        setDefaultState(blockState.getBaseState().withProperty(HALF, Half.LOWER).withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), state.withProperty(HALF, Half.UPPER), 3);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canStay(worldIn, pos, state)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    private boolean canStay(World world, BlockPos pos, IBlockState state) {
        return state.getValue(HALF) == Half.UPPER
                ? world.getBlockState(pos.down()).getBlock() == this && world.getBlockState(pos.down()).getValue(HALF) == Half.LOWER
                : super.canPlaceBlockAt(world, pos) && world.getBlockState(pos.up()).getBlock() == this && world.getBlockState(pos.up()).getValue(HALF) == Half.UPPER;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        spawnAsEntity(worldIn, pos, new ItemStack(this));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
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
        return (state.getValue(HALF) == Half.UPPER ? 4 : 0) | state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(HALF, (meta & 4) == 0 ? Half.LOWER : Half.UPPER).withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF, FACING);
    }

    public enum Half implements IStringSerializable {
        LOWER("lower"),
        UPPER("upper");

        private final String name;

        Half(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
