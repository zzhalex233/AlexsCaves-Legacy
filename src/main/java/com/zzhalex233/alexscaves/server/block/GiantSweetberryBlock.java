package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.BlockRenderLayer;

public class GiantSweetberryBlock extends Block {
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 7);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);
    private static final AxisAlignedBB OUTLINE = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.9375D, 0.6875D);

    public GiantSweetberryBlock() {
        super(Material.PLANTS);
        setHardness(1.0F);
        setSoundType(SoundType.PLANT);
        setDefaultState(blockState.getBaseState().withProperty(ROTATION, 0));
        setTickRandomly(true);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!canPlaceBlockAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canPlaceBlockAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        int rotation = Math.floorMod(Math.round(placer.rotationYaw * 8.0F / 360.0F), 8);
        return getDefaultState().withProperty(ROTATION, rotation);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        int rotation = state.getValue(ROTATION);
        switch (rot) {
            case CLOCKWISE_180:
                return state.withProperty(ROTATION, (rotation + 4) & 7);
            case COUNTERCLOCKWISE_90:
                return state.withProperty(ROTATION, (rotation + 6) & 7);
            case CLOCKWISE_90:
                return state.withProperty(ROTATION, (rotation + 2) & 7);
            default:
                return state;
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return OUTLINE;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return SHAPE;
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
        return state.getValue(ROTATION);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ROTATION, meta & 7);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ROTATION);
    }
}
