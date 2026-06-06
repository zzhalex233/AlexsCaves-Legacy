package com.zzhalex233.alexscaves.server.block;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagneticLevitationRailBlock extends BlockRailBase {
    public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, new Predicate<BlockRailBase.EnumRailDirection>() {
        @Override
        public boolean apply(BlockRailBase.EnumRailDirection direction) {
            return direction == BlockRailBase.EnumRailDirection.NORTH_SOUTH || direction == BlockRailBase.EnumRailDirection.EAST_WEST || direction == BlockRailBase.EnumRailDirection.ASCENDING_EAST || direction == BlockRailBase.EnumRailDirection.ASCENDING_WEST || direction == BlockRailBase.EnumRailDirection.ASCENDING_NORTH || direction == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
        }
    });

    public MagneticLevitationRailBlock() {
        super(true);
        setHardness(0.7F);
        setSoundType(SoundType.METAL);
        setLightLevel(3.0F / 15.0F);
        setDefaultState(blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
    }

    @Override
    public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return super.getRailMaxSpeed(world, cart, pos) + 0.3F;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        BlockRailBase.EnumRailDirection direction = BlockRailBase.EnumRailDirection.byMetadata(meta);
        if (direction == BlockRailBase.EnumRailDirection.NORTH_EAST || direction == BlockRailBase.EnumRailDirection.NORTH_WEST || direction == BlockRailBase.EnumRailDirection.SOUTH_EAST || direction == BlockRailBase.EnumRailDirection.SOUTH_WEST) {
            direction = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
        }
        return getDefaultState().withProperty(SHAPE, direction);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SHAPE).getMetadata();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180:
                switch (state.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
                    default:
                        return state;
                }
            case COUNTERCLOCKWISE_90:
                switch (state.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
                    case NORTH_SOUTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST);
                    case EAST_WEST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH);
                    default:
                        return state;
                }
            case CLOCKWISE_90:
                switch (state.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
                    case NORTH_SOUTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.EAST_WEST);
                    case EAST_WEST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT:
                switch (state.getValue(SHAPE)) {
                    case ASCENDING_NORTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_NORTH);
                    default:
                        return super.withMirror(state, mirror);
                }
            case FRONT_BACK:
                switch (state.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.withProperty(SHAPE, BlockRailBase.EnumRailDirection.ASCENDING_EAST);
                    default:
                        return super.withMirror(state, mirror);
                }
            default:
                return super.withMirror(state, mirror);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SHAPE);
    }
}
