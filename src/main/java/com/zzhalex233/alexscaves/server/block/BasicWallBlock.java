package com.zzhalex233.alexscaves.server.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BasicWallBlock extends Block {
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyEnum<WallSide> NORTH = PropertyEnum.create("north", WallSide.class);
    public static final PropertyEnum<WallSide> EAST = PropertyEnum.create("east", WallSide.class);
    public static final PropertyEnum<WallSide> SOUTH = PropertyEnum.create("south", WallSide.class);
    public static final PropertyEnum<WallSide> WEST = PropertyEnum.create("west", WallSide.class);
    private static final AxisAlignedBB POST_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 0.8125D, 0.25D);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.75D, 0.75D, 0.8125D, 1.0D);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.25D, 0.8125D, 0.75D);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.75D, 0.0D, 0.25D, 1.0D, 0.8125D, 0.75D);

    public BasicWallBlock(Material material, float hardness, float resistance, SoundType sound) {
        super(material);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
        setDefaultState(blockState.getBaseState()
            .withProperty(UP, true)
            .withProperty(NORTH, WallSide.NONE)
            .withProperty(EAST, WallSide.NONE)
            .withProperty(SOUTH, WallSide.NONE)
            .withProperty(WEST, WallSide.NONE));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        WallSide north = side(world, pos, EnumFacing.NORTH);
        WallSide east = side(world, pos, EnumFacing.EAST);
        WallSide south = side(world, pos, EnumFacing.SOUTH);
        WallSide west = side(world, pos, EnumFacing.WEST);
        boolean straightNorthSouth = north != WallSide.NONE && south != WallSide.NONE && east == WallSide.NONE && west == WallSide.NONE;
        boolean straightEastWest = east != WallSide.NONE && west != WallSide.NONE && north == WallSide.NONE && south == WallSide.NONE;
        return state.withProperty(NORTH, north)
            .withProperty(EAST, east)
            .withProperty(SOUTH, south)
            .withProperty(WEST, west)
            .withProperty(UP, !(straightNorthSouth || straightEastWest));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        IBlockState actual = state.getActualState(source, pos);
        AxisAlignedBB box = actual.getValue(UP) ? POST_AABB : null;
        box = include(box, actual.getValue(NORTH), NORTH_AABB);
        box = include(box, actual.getValue(EAST), EAST_AABB);
        box = include(box, actual.getValue(SOUTH), SOUTH_AABB);
        box = include(box, actual.getValue(WEST), WEST_AABB);
        return box == null ? POST_AABB : box;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        IBlockState actual = isActualState ? state : state.getActualState(world, pos);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, actual.getValue(UP) ? POST_AABB : null);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, actual.getValue(NORTH) == WallSide.NONE ? null : NORTH_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, actual.getValue(EAST) == WallSide.NONE ? null : EAST_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, actual.getValue(SOUTH) == WallSide.NONE ? null : SOUTH_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, actual.getValue(WEST) == WallSide.NONE ? null : WEST_AABB);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return facing.getAxis().isHorizontal();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.UP ? BlockFaceShape.CENTER_BIG : BlockFaceShape.MIDDLE_POLE_THICK;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, UP, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    private WallSide side(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos.offset(facing));
        if (state.getBlock() instanceof BasicWallBlock) {
            return WallSide.LOW;
        }
        if (state.getBlock() instanceof BlockFenceGate || state.getBlock().canBeConnectedTo(world, pos.offset(facing), facing.getOpposite())) {
            return WallSide.LOW;
        }
        return state.getBlockFaceShape(world, pos.offset(facing), facing.getOpposite()) == BlockFaceShape.SOLID ? WallSide.TALL : WallSide.NONE;
    }

    private static AxisAlignedBB include(AxisAlignedBB base, WallSide side, AxisAlignedBB addition) {
        if (side == WallSide.NONE) {
            return base;
        }
        return base == null ? addition : base.union(addition);
    }

    public enum WallSide implements IStringSerializable {
        NONE("none"),
        LOW("low"),
        TALL("tall");

        private final String name;

        WallSide(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
