package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.entity.item.FloaterEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class FloaterItem extends Item {
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isInWater() || player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        FloaterEntity floater = new FloaterEntity(world);
        floater.copyLocationAndAnglesFrom(player);
        if (!world.isRemote) {
            world.spawnEntity(floater);
        }
        Entity rider = player.getLowestRidingEntity();
        rider.startRiding(floater, true);
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
