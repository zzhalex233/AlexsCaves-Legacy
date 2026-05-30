package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FlytrapBlock extends BlockBush implements IGrowable {
    public static final PropertyBool OPEN = PropertyBool.create("open");
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.21875D, 0.0D, 0.21875D, 0.78125D, 1.3125D, 0.78125D);

    public FlytrapBlock() {
        setHardness(0.0F);
        setResistance(0.0F);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(OPEN, true));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        if (state.getValue(OPEN)) {
            worldIn.setBlockState(pos, state.withProperty(OPEN, false), 2);
            worldIn.scheduleUpdate(pos, this, 100 + rand.nextInt(100));
        } else {
            worldIn.setBlockState(pos, state.withProperty(OPEN, true), 2);
        }
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
        return state.getValue(OPEN) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(OPEN, (meta & 1) == 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, OPEN);
    }
}
