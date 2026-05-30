package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class GalenaPillarBlock extends BasicPillarBlock {
    public static final PropertyInteger SHAPE = PropertyInteger.create("shape", 0, 3);

    public GalenaPillarBlock() {
        super(Material.ROCK, 3.5F, 10.0F, SoundType.STONE);
        setDefaultState(getDefaultState().withProperty(AXIS, EnumFacing.Axis.Y).withProperty(SHAPE, 3));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing.Axis axis = facing.getAxis();
        return getDefaultState().withProperty(AXIS, axis).withProperty(SHAPE, getShape(world, pos, axis));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(SHAPE, getShape(world, pos, state.getValue(AXIS)));
    }

    private int getShape(IBlockAccess world, BlockPos pos, EnumFacing.Axis axis) {
        EnumFacing below = EnumFacing.DOWN;
        EnumFacing above = EnumFacing.UP;
        if (axis == EnumFacing.Axis.X) {
            below = EnumFacing.WEST;
            above = EnumFacing.EAST;
        } else if (axis == EnumFacing.Axis.Z) {
            below = EnumFacing.SOUTH;
            above = EnumFacing.NORTH;
        }
        boolean connectsAbove = connects(world, pos.offset(above), axis);
        boolean connectsBelow = connects(world, pos.offset(below), axis);
        if (connectsAbove && connectsBelow) {
            return 3;
        }
        if (!connectsAbove && !connectsBelow) {
            return 0;
        }
        return connectsAbove ? 1 : 2;
    }

    private boolean connects(IBlockAccess world, BlockPos pos, EnumFacing.Axis axis) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == this && state.getValue(AXIS) == axis;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, SHAPE);
    }
}
