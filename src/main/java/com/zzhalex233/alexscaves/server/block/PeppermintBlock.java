package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PeppermintBlock extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    private final AxisAlignedBB shapeUp;
    private final AxisAlignedBB shapeDown;
    private final AxisAlignedBB shapeEast;
    private final AxisAlignedBB shapeWest;
    private final AxisAlignedBB shapeSouth;
    private final AxisAlignedBB shapeNorth;

    public PeppermintBlock(double distFromEdge, double height) {
        super(Material.CAKE);
        setHardness(2.0F);
        setResistance(6.0F);
        setSoundType(ACSoundTypes.HARD_CANDY);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        double min = distFromEdge / 16.0D;
        double max = 1.0D - min;
        double thick = height / 16.0D;
        shapeUp = new AxisAlignedBB(min, 0.0D, min, max, thick, max);
        shapeDown = new AxisAlignedBB(min, 1.0D - thick, min, max, 1.0D, max);
        shapeEast = new AxisAlignedBB(0.0D, min, min, thick, max, max);
        shapeWest = new AxisAlignedBB(1.0D - thick, min, min, 1.0D, max, max);
        shapeSouth = new AxisAlignedBB(min, min, 0.0D, max, max, thick);
        shapeNorth = new AxisAlignedBB(min, min, 1.0D - thick, max, max, 1.0D);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case DOWN:
                return shapeDown;
            case EAST:
                return shapeEast;
            case WEST:
                return shapeWest;
            case SOUTH:
                return shapeSouth;
            case NORTH:
                return shapeNorth;
            case UP:
            default:
                return shapeUp;
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
    public IBlockState withRotation(IBlockState state, net.minecraft.util.Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, net.minecraft.util.Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
