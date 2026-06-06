package com.zzhalex233.alexscaves.server.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

public class RebarBlock extends Block {
    public static final PropertyBool CONNECT_X = PropertyBool.create("connect_x");
    public static final PropertyBool CONNECT_Y = PropertyBool.create("connect_y");
    public static final PropertyBool CONNECT_Z = PropertyBool.create("connect_z");

    private static final AxisAlignedBB CENTER_SHAPE = new AxisAlignedBB(0.4375D, 0.4375D, 0.4375D, 0.5625D, 0.5625D, 0.5625D);
    private static final AxisAlignedBB X_SHAPE = new AxisAlignedBB(0.0D, 0.4375D, 0.4375D, 1.0D, 0.5625D, 0.5625D);
    private static final AxisAlignedBB Y_SHAPE = new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D);
    private static final AxisAlignedBB Z_SHAPE = new AxisAlignedBB(0.4375D, 0.4375D, 0.0D, 0.5625D, 0.5625D, 1.0D);

    public RebarBlock() {
        super(Material.IRON);
        setHardness(2.0F);
        setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty(CONNECT_X, false).withProperty(CONNECT_Y, true).withProperty(CONNECT_Z, false));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos);
        return state.getBlock().isReplaceable(worldIn, pos) || state.getBlock() == this;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDesiredRebarState(world.getBlockState(pos), facing.getAxis());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB shape = CENTER_SHAPE;
        if (state.getValue(CONNECT_X)) {
            shape = shape.union(X_SHAPE);
        }
        if (state.getValue(CONNECT_Y)) {
            shape = shape.union(Y_SHAPE);
        }
        if (state.getValue(CONNECT_Z)) {
            shape = shape.union(Z_SHAPE);
        }
        return shape;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER_SHAPE);
        if (state.getValue(CONNECT_X)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, X_SHAPE);
        }
        if (state.getValue(CONNECT_Y)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, Y_SHAPE);
        }
        if (state.getValue(CONNECT_Z)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, Z_SHAPE);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!worldIn.isRemote && !player.capabilities.isCreativeMode && connectedAxes(state) > 1) {
            IBlockState reduced = removeOneAxis(state);
            spawnAsEntity(worldIn, pos, new ItemStack(this));
            worldIn.setBlockState(pos, reduced, 3);
            return false;
        }
        return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
    }

    public IBlockState getDesiredRebarState(IBlockState blockstate, EnumFacing.Axis clickedAxis) {
        boolean xAxis = blockstate.getBlock() == this && blockstate.getValue(CONNECT_X);
        boolean yAxis = blockstate.getBlock() == this && blockstate.getValue(CONNECT_Y);
        boolean zAxis = blockstate.getBlock() == this && blockstate.getValue(CONNECT_Z);
        if (clickedAxis == EnumFacing.Axis.X) {
            xAxis = true;
        } else if (clickedAxis == EnumFacing.Axis.Y) {
            yAxis = true;
        } else if (clickedAxis == EnumFacing.Axis.Z) {
            zAxis = true;
        }
        return getDefaultState().withProperty(CONNECT_X, xAxis).withProperty(CONNECT_Y, yAxis).withProperty(CONNECT_Z, zAxis);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos);
        return connectedAxes(state) < 3;
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
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                return state.withProperty(CONNECT_X, state.getValue(CONNECT_Z)).withProperty(CONNECT_Z, state.getValue(CONNECT_X));
            default:
                return state;
        }
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(CONNECT_X) ? 1 : 0) | (state.getValue(CONNECT_Y) ? 2 : 0) | (state.getValue(CONNECT_Z) ? 4 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CONNECT_X, (meta & 1) != 0).withProperty(CONNECT_Y, (meta & 2) != 0).withProperty(CONNECT_Z, (meta & 4) != 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONNECT_X, CONNECT_Y, CONNECT_Z);
    }

    private static int connectedAxes(IBlockState state) {
        if (!(state.getBlock() instanceof RebarBlock)) {
            return 0;
        }
        return (state.getValue(CONNECT_X) ? 1 : 0) + (state.getValue(CONNECT_Y) ? 1 : 0) + (state.getValue(CONNECT_Z) ? 1 : 0);
    }

    private static IBlockState removeOneAxis(IBlockState state) {
        if (state.getValue(CONNECT_X)) {
            return state.withProperty(CONNECT_X, false);
        }
        if (state.getValue(CONNECT_Y)) {
            return state.withProperty(CONNECT_Y, false);
        }
        return state.withProperty(CONNECT_Z, false);
    }
}
