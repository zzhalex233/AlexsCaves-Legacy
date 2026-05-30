package com.zzhalex233.alexscaves.server.item;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class FertilizerItem extends Item {
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (applyFertilizer(stack, world, pos, player, hand)) {
            if (!world.isRemote) {
                world.playEvent(2005, pos, 0);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private static boolean applyFertilizer(ItemStack stack, World world, BlockPos pos, EntityPlayer player, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);
        int hook = ForgeEventFactory.onApplyBonemeal(player, world, pos, state, stack, hand);
        if (hook != 0) {
            return hook > 0;
        }
        Block block = state.getBlock();
        if (block instanceof IGrowable) {
            IGrowable growable = (IGrowable) block;
            if (growable.canGrow(world, pos, state, world.isRemote)) {
                if (!world.isRemote && growable.canUseBonemeal(world, world.rand, pos, state)) {
                    for (int i = 0; i < 4; i++) {
                        growable.grow(world, world.rand, pos, world.getBlockState(pos));
                    }
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                }
                return true;
            }
        }
        return ItemDye.applyBonemeal(stack, world, pos, player, hand);
    }
}
