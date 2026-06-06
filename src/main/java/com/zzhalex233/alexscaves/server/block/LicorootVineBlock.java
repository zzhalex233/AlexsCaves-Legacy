package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.PropertyBool;
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

public class LicorootVineBlock extends BlockDirectional {
    public static final PropertyBool END = PropertyBool.create("end");
    private static final AxisAlignedBB SHAPE_VERTICAL = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
    private static final AxisAlignedBB SHAPE_HORIZONTAL = new AxisAlignedBB(0.0D, 0.0625D, 0.0D, 1.0D, 0.9375D, 1.0D);

    public LicorootVineBlock() {
        super(Material.PLANTS);
        setHardness(0.0F);
        setResistance(0.0F);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(END, Boolean.TRUE));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, facing).withProperty(END, isEnd(world, pos, facing));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, net.minecraft.block.Block blockIn, BlockPos fromPos) {
        EnumFacing facing = state.getValue(FACING);
        if (!canAttachTo(worldIn, pos.offset(facing.getOpposite()), facing)) {
            worldIn.destroyBlock(pos, true);
            return;
        }
        boolean end = isEnd(worldIn, pos, facing);
        if (state.getValue(END) != end) {
            worldIn.setBlockState(pos, state.withProperty(END, end), 3);
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return canAttachTo(worldIn, pos.offset(side.getOpposite()), side);
    }

    private boolean canAttachTo(IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState state = world.getBlockState(pos);
        return state.isSideSolid(world, pos, side) || state.getBlock() == this && state.getValue(FACING) == side;
    }

    private boolean isEnd(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        IBlockState next = world.getBlockState(pos.offset(facing));
        return next.getBlock() != this || next.getValue(FACING) != facing;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing.Axis axis = state.getValue(FACING).getAxis();
        return axis == EnumFacing.Axis.Y ? SHAPE_VERTICAL : SHAPE_HORIZONTAL;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return true;
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
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7)).withProperty(END, (meta & 8) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() | (state.getValue(END) ? 8 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, END);
    }
}
