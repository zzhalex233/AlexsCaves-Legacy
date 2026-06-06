package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.block.entity.AmbersolTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AmbersolBlock extends BasicTranslucentBlock {
    public AmbersolBlock() {
        super(Material.GLASS, 3.0F, 10.0F, ACSoundTypes.AMBER);
        setTickRandomly(true);
        setLightLevel(1.0F);
    }

    public static BlockPos fillWithLights(BlockPos current, World world) {
        current = current.down();
        while (current.getY() > 0 && AmbersolLightBlock.testSkylight(world, world.getBlockState(current), current)) {
            if (world.isAirBlock(current)) {
                world.setBlockState(current, ACBlockRegistry.AMBERSOL_LIGHT.block().getDefaultState(), 3);
            }
            current = current.down();
        }
        return current;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new AmbersolTileEntity();
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, java.util.Random rand) {
        fillWithLights(pos, worldIn);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        fillWithLights(pos, worldIn);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        fillWithLights(pos, worldIn);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        fillWithLights(pos, worldIn);
    }
}
