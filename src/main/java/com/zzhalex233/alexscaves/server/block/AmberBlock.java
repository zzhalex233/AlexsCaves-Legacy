package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AmberBlock extends BasicTranslucentBlock {
    private static final float[] CURIOSITY_CHANCES = {0.015F, 0.025F, 0.035F, 0.05F};

    public AmberBlock() {
        super(Material.GLASS, 0.3F, 2.0F, ACSoundTypes.AMBER);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Random random = world instanceof World ? ((World) world).rand : RANDOM;
        drops.add(random.nextFloat() < CURIOSITY_CHANCES[Math.min(Math.max(fortune, 0), CURIOSITY_CHANCES.length - 1)]
                ? new ItemStack(ACItemRegistry.AMBER_CURIOSITY.item()) : new ItemStack(Item.getItemFromBlock(this)));
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }
}
