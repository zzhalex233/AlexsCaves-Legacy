package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class DinosaurChopBlock extends Block {
    public static final PropertyInteger BITES = PropertyInteger.create("bites", 0, 3);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private final int foodAmount;
    private final float saturationAmount;

    public DinosaurChopBlock(int foodAmount, float saturationAmount) {
        super(Material.CAKE);
        this.foodAmount = foodAmount;
        this.saturationAmount = saturationAmount;
        setHardness(1.0F);
        setResistance(1.0F);
        setSoundType(SoundType.CLOTH);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BITES, 0));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.canEat(false)) {
            return false;
        }
        if (!worldIn.isRemote) {
            playerIn.getFoodStats().addStats(foodAmount, saturationAmount);
            int bites = state.getValue(BITES);
            if (bites < 3) {
                worldIn.setBlockState(pos, state.withProperty(BITES, bites + 1), 3);
            } else {
                worldIn.setBlockState(pos, ACBlockRegistry.THIN_BONE.block().getDefaultState().withProperty(ThinBoneBlock.AXIS, state.getValue(FACING).getAxis()), 3);
            }
        }
        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (this == ACBlockRegistry.DINOSAUR_CHOP.block() && rand.nextInt(4) == 0 && isFireBelow(worldIn, pos.down())) {
            worldIn.setBlockState(pos, ACBlockRegistry.COOKED_DINOSAUR_CHOP.block().getDefaultState()
                    .withProperty(FACING, state.getValue(FACING))
                    .withProperty(BITES, state.getValue(BITES)), 3);
        }
    }

    private boolean isFireBelow(World world, BlockPos pos) {
        while (world.isAirBlock(pos) && pos.getY() > 0) {
            pos = pos.down();
        }
        Block block = world.getBlockState(pos).getBlock();
        return block == Blocks.FIRE || block == Blocks.LAVA || block == Blocks.FLOWING_LAVA;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int bites = state.getValue(BITES);
        if (bites == 0) {
            return FULL_BLOCK_AABB;
        }
        double cut = bites * 0.25D;
        switch (state.getValue(FACING)) {
            case NORTH:
                return new AxisAlignedBB(0.0D, 0.0D, cut, 1.0D, 1.0D, 1.0D);
            case SOUTH:
                return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D - cut);
            case EAST:
                return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D - cut, 1.0D, 1.0D);
            case WEST:
                return new AxisAlignedBB(cut, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            default:
                return FULL_BLOCK_AABB;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return (7 - blockState.getValue(BITES)) * 2;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BITES) * 4 + state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta % 4)).withProperty(BITES, Math.min(3, meta / 4));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, BITES);
    }
}
