package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GalenaSpireBlock extends Block {
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyInteger SHAPE = PropertyInteger.create("shape", 0, 3);
    private static final AxisAlignedBB SHAPE_0 = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    private static final AxisAlignedBB SHAPE_1 = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    private static final AxisAlignedBB SHAPE_2 = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    private static final AxisAlignedBB SHAPE_3_TOP = new AxisAlignedBB(0.375D, 0.5625D, 0.375D, 0.625D, 1.0D, 0.625D);
    private static final AxisAlignedBB SHAPE_3_BOTTOM = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.5625D, 0.625D);

    public GalenaSpireBlock() {
        super(Material.ROCK);
        setHardness(1.5F);
        setSoundType(SoundType.STONE);
        setDefaultState(blockState.getBaseState().withProperty(DOWN, false).withProperty(SHAPE, 3));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        boolean down = facing == EnumFacing.DOWN;
        IBlockState above = world.getBlockState(pos.up());
        IBlockState below = world.getBlockState(pos.down());
        return getDefaultState().withProperty(DOWN, down).withProperty(SHAPE, down ? getShapeInt(below, above, true) : getShapeInt(above, below, false));
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return canAttachTo(worldIn, pos, side == EnumFacing.DOWN);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return canAttachTo(worldIn, pos, false) || canAttachTo(worldIn, pos, true);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canAttachTo(worldIn, pos, state.getValue(DOWN))) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return;
        }
        IBlockState updated = state.withProperty(SHAPE, getShape(worldIn, pos, state.getValue(DOWN)));
        if (updated != state) {
            worldIn.setBlockState(pos, updated, 2);
        }
    }

    public int getShapeInt(IBlockState above, IBlockState below, boolean down) {
        if (above.getBlock() == ACBlockRegistry.TESLA_BULB.block()) {
            return 2;
        }
        if (!isGalenaSpireConnectable(above, down)) {
            return 3;
        }
        int aboveShape = above.getValue(SHAPE);
        if (aboveShape <= 1) {
            boolean connectedUnder = down ? above.getBlock() != ACBlockRegistry.GALENA_SPIRE.block() : below.getBlock() != ACBlockRegistry.GALENA_SPIRE.block();
            return connectedUnder ? 0 : 1;
        }
        return aboveShape - 1;
    }

    public static boolean isGalenaSpireConnectable(IBlockState state, boolean down) {
        return state.getBlock() == ACBlockRegistry.GALENA_SPIRE.block() && state.getValue(DOWN) == down;
    }

    private int getShape(IBlockAccess world, BlockPos pos, boolean down) {
        IBlockState above = world.getBlockState(pos.up());
        IBlockState below = world.getBlockState(pos.down());
        return down ? getShapeInt(below, above, true) : getShapeInt(above, below, false);
    }

    private boolean canAttachTo(World world, BlockPos pos, boolean down) {
        BlockPos support = pos.offset(down ? EnumFacing.UP : EnumFacing.DOWN);
        IBlockState state = world.getBlockState(support);
        return state.isSideSolid(world, support, down ? EnumFacing.UP : EnumFacing.DOWN) || isGalenaSpireConnectable(state, down);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(SHAPE)) {
            case 0:
                return SHAPE_0;
            case 1:
                return SHAPE_1;
            case 2:
                return SHAPE_2;
            case 3:
            default:
                return state.getValue(DOWN) ? SHAPE_3_TOP : SHAPE_3_BOTTOM;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, net.minecraft.entity.player.EntityPlayer player) {
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SHAPE) | (state.getValue(DOWN) ? 4 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(SHAPE, meta & 3).withProperty(DOWN, (meta & 4) != 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DOWN, SHAPE);
    }
}
