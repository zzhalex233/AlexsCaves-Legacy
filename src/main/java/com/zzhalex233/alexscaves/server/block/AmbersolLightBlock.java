package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AmbersolLightBlock extends Block {
    private static final AxisAlignedBB EMPTY = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public AmbersolLightBlock() {
        super(Material.AIR);
        setHardness(-1.0F);
        setResistance(3600000.8F);
        setSoundType(SoundType.GLASS);
        setLightLevel(1.0F);
        setLightOpacity(0);
    }

    public static boolean testSkylight(World world, IBlockState state, BlockPos pos) {
        Block block = state.getBlock();
        return block.isAir(state, world, pos) || block instanceof AmbersolLightBlock || block instanceof BlockAir || state.getLightOpacity(world, pos) == 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return EMPTY;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
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
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return canBlockStay(worldIn, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canBlockStay(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
        } else if (worldIn.getBlockState(pos.down()).getBlock() != this) {
            BlockPos top = getTopOfColumn(pos, worldIn);
            worldIn.scheduleUpdate(top, ACBlockRegistry.AMBERSOL.block(), 3);
        }
    }

    private boolean canBlockStay(World world, BlockPos pos) {
        BlockPos top = getTopOfColumn(pos, world);
        return world.getBlockState(top).getBlock() == ACBlockRegistry.AMBERSOL.block();
    }

    private BlockPos getTopOfColumn(BlockPos current, World world) {
        while (current.getY() < world.getHeight() - 1 && testSkylight(world, world.getBlockState(current), current)) {
            current = current.up();
        }
        return current;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!canBlockStay(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }
}
