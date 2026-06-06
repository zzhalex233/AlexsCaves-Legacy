package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PewenBranchBlock extends Block {
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 7);
    public static final PropertyBool PINES = PropertyBool.create("pines");

    private static final AxisAlignedBB SHAPE_STRAIGHT = new AxisAlignedBB(0.375D, 0.125D, 0.0D, 0.625D, 0.375D, 1.0D);
    private static final AxisAlignedBB SHAPE_SIDEWAYS = new AxisAlignedBB(0.0D, 0.125D, 0.375D, 1.0D, 0.375D, 0.625D);
    private static final AxisAlignedBB SHAPE_DIAGONAL = new AxisAlignedBB(0.0D, 0.125D, 0.0D, 1.0D, 0.375D, 1.0D);

    public PewenBranchBlock() {
        super(Material.PLANTS);
        setHardness(1.0F);
        setSoundType(ACSoundTypes.PEWEN_BRANCH);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(ROTATION, 0).withProperty(PINES, true));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (int rotation = 0; rotation < 8; rotation++) {
            if (isGoodBase(worldIn.getBlockState(pos.add(getOffsetConnectToPos(rotation))))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, net.minecraft.util.EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        int rotation = Math.floorMod(Math.round(placer.rotationYaw * 8.0F / 360.0F), 8);
        for (int loops = 0; loops < 8 && !isGoodBase(world.getBlockState(pos.add(getOffsetConnectToPos(rotation)))); loops++) {
            rotation = (rotation + 1) & 7;
        }
        return getDefaultState().withProperty(ROTATION, rotation).withProperty(PINES, hasPines(rotation, world, pos));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canSurvive(state, worldIn, pos)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return;
        }
        boolean shouldHavePines = hasPines(state.getValue(ROTATION), worldIn, pos);
        if (shouldHavePines != state.getValue(PINES)) {
            worldIn.setBlockState(pos, state.withProperty(PINES, shouldHavePines), 2);
        }
    }

    private boolean canSurvive(IBlockState state, World world, BlockPos pos) {
        return isGoodBase(world.getBlockState(pos.add(getOffsetConnectToPos(state.getValue(ROTATION)))));
    }

    private boolean isGoodBase(IBlockState state) {
        return state.getBlock() == this || state.isFullCube();
    }

    private boolean hasPines(int rotation, IBlockAccess world, BlockPos pos) {
        BlockPos checkAt = pos.subtract(getOffsetConnectToPos(rotation));
        return world.getBlockState(checkAt).getBlock() != this;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int rotation = state.getValue(ROTATION);
        if ((rotation & 1) == 1) {
            return SHAPE_DIAGONAL;
        }
        return rotation == 2 || rotation == 6 ? SHAPE_SIDEWAYS : SHAPE_STRAIGHT;
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
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        int rotation = state.getValue(ROTATION);
        switch (rot) {
            case CLOCKWISE_90:
                rotation += 2;
                break;
            case CLOCKWISE_180:
                rotation += 4;
                break;
            case COUNTERCLOCKWISE_90:
                rotation += 6;
                break;
            default:
                break;
        }
        return state.withProperty(ROTATION, rotation & 7);
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        int rotation = state.getValue(ROTATION);
        switch (mirrorIn) {
            case LEFT_RIGHT:
                rotation = (8 - rotation) & 7;
                break;
            case FRONT_BACK:
                rotation = (4 - rotation) & 7;
                break;
            default:
                break;
        }
        return state.withProperty(ROTATION, rotation);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ROTATION) | (state.getValue(PINES) ? 8 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ROTATION, meta & 7).withProperty(PINES, (meta & 8) != 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ROTATION, PINES);
    }

    private static BlockPos getOffsetConnectToPos(int rotationValue) {
        switch (rotationValue) {
            case 0:
                return new BlockPos(0, 0, 1);
            case 1:
                return new BlockPos(-1, 0, 1);
            case 2:
                return new BlockPos(-1, 0, 0);
            case 3:
                return new BlockPos(-1, 0, -1);
            case 4:
                return new BlockPos(0, 0, -1);
            case 5:
                return new BlockPos(1, 0, -1);
            case 6:
                return new BlockPos(1, 0, 0);
            case 7:
                return new BlockPos(1, 0, 1);
            default:
                return BlockPos.ORIGIN;
        }
    }
}
