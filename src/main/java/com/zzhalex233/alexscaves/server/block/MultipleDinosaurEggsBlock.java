package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MultipleDinosaurEggsBlock extends DinosaurEggBlock {
    public static final PropertyInteger EGGS = PropertyInteger.create("eggs", 1, 4);
    private static final AxisAlignedBB ONE_EGG_SHAPE = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5625D, 0.75D);
    private static final AxisAlignedBB MULTI_EGG_SHAPE = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.4375D, 0.9375D);

    private final int maxEggs;

    public MultipleDinosaurEggsBlock(Class<? extends EntityAgeable> births, int maxEggs) {
        super(births, 8, 9);
        this.maxEggs = maxEggs;
        setDefaultState(blockState.getBaseState().withProperty(HATCH, 0).withProperty(EGGS, 1));
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(EGGS) < maxEggs;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, net.minecraft.util.EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this) {
            return state.withProperty(EGGS, Math.min(maxEggs, state.getValue(EGGS) + 1));
        }
        return getDefaultState();
    }

    @Override
    protected void removeOneEgg(World world, BlockPos pos, IBlockState state) {
        world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_STONE_BREAK, net.minecraft.util.SoundCategory.BLOCKS, 0.7F, 0.9F + world.rand.nextFloat() * 0.2F);
        int eggs = state.getValue(EGGS);
        if (eggs <= 1) {
            world.destroyBlock(pos, false);
        } else {
            world.setBlockState(pos, state.withProperty(EGGS, eggs - 1), 2);
            world.playEvent(2001, pos, net.minecraft.block.Block.getStateId(state));
        }
    }

    @Override
    protected int getDinosaursBornFrom(IBlockState state) {
        return state.getValue(EGGS);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(EGGS) > 1 ? MULTI_EGG_SHAPE : ONE_EGG_SHAPE;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        return getBoundingBox(blockState, world, pos);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HATCH) | ((state.getValue(EGGS) - 1) << 2);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(HATCH, Math.min(2, meta & 3)).withProperty(EGGS, Math.min(maxEggs, ((meta >> 2) & 3) + 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, EGGS, HATCH);
    }
}
