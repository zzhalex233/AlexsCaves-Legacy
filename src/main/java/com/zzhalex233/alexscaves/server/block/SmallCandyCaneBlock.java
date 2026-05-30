package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SmallCandyCaneBlock extends Block {
    public static final PropertyInteger AMOUNT = PropertyInteger.create("amount", 1, 4);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.875D, 0.8125D);

    public SmallCandyCaneBlock() {
        super(Material.CAKE);
        setHardness(0.0F);
        setResistance(0.0F);
        setSoundType(ACSoundTypes.HARD_CANDY);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(AMOUNT, 1));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos.down());
        return state.isSideSolid(worldIn, pos.down(), EnumFacing.UP);
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        return canPlaceBlockAt(worldIn, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canBlockStay(worldIn, pos)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getValue(AMOUNT) < 4;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this) {
            return state.withProperty(AMOUNT, Math.min(4, state.getValue(AMOUNT) + 1));
        }
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
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
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return state.getValue(AMOUNT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() * 4 + state.getValue(AMOUNT) - 1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta / 4)).withProperty(AMOUNT, meta % 4 + 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, AMOUNT);
    }
}
