package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetalScaffoldingBlock extends Block {
    public static final int STABILITY_MAX_DISTANCE = 12;
    public static final PropertyInteger DISTANCE = PropertyInteger.create("distance", 0, STABILITY_MAX_DISTANCE);
    public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
    private static final AxisAlignedBB TOP_COLLISION = new AxisAlignedBB(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB BOTTOM_COLLISION = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);

    public MetalScaffoldingBlock() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(15.0F);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(DISTANCE, STABILITY_MAX_DISTANCE).withProperty(BOTTOM, false));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        int distance = getDistance(world, pos);
        return getDefaultState().withProperty(DISTANCE, distance).withProperty(BOTTOM, isBottom(world, pos, distance));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos, this, 1);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        worldIn.scheduleUpdate(pos, this, 1);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int distance = getDistance(worldIn, pos);
        IBlockState updated = state.withProperty(DISTANCE, distance).withProperty(BOTTOM, isBottom(worldIn, pos, distance));
        if (distance == STABILITY_MAX_DISTANCE) {
            if (state.getValue(DISTANCE) == STABILITY_MAX_DISTANCE) {
                worldIn.setBlockToAir(pos);
                worldIn.spawnEntity(new EntityFallingBlock(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, updated));
            } else {
                worldIn.destroyBlock(pos, true);
            }
        } else if (updated != state) {
            worldIn.setBlockState(pos, updated, 3);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return getDistance(worldIn, pos) < STABILITY_MAX_DISTANCE;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        int distance = state.getValue(DISTANCE);
        return state.withProperty(BOTTOM, isBottom(worldIn, pos, distance));
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return blockState.getValue(BOTTOM) && blockState.getValue(DISTANCE) > 0 ? BOTTOM_COLLISION : TOP_COLLISION;
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
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return canPlaceBlockAt(worldIn, pos);
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
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DISTANCE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(DISTANCE, Math.min(meta & 15, STABILITY_MAX_DISTANCE)).withProperty(BOTTOM, false);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DISTANCE, BOTTOM);
    }

    public static int getDistance(IBlockAccess world, BlockPos pos) {
        BlockPos down = pos.down();
        IBlockState below = world.getBlockState(down);
        int distance = STABILITY_MAX_DISTANCE;
        if (below.getBlock() instanceof MetalScaffoldingBlock) {
            distance = below.getValue(DISTANCE);
        } else if (below.isSideSolid(world, down, EnumFacing.UP)) {
            return 0;
        }
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos sidePos = pos.offset(facing);
            IBlockState side = world.getBlockState(sidePos);
            if (side.getBlock() instanceof MetalScaffoldingBlock) {
                distance = Math.min(distance, side.getValue(DISTANCE) + 1);
                if (distance == 1) {
                    break;
                }
            }
        }
        return distance;
    }

    private static boolean isBottom(IBlockAccess world, BlockPos pos, int distance) {
        return distance > 0 && !(world.getBlockState(pos.down()).getBlock() instanceof MetalScaffoldingBlock);
    }
}
