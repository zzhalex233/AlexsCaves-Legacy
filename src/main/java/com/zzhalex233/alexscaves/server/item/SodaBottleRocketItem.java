package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.entity.item.SodaBottleRocketEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SodaBottleRocketItem extends Item {
    public SodaBottleRocketItem() {
        setMaxStackSize(64);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            double x = pos.getX() + hitX + facing.getXOffset() * 0.15D;
            double y = pos.getY() + hitY + facing.getYOffset() * 0.15D;
            double z = pos.getZ() + hitZ + facing.getZOffset() * 0.15D;
            world.spawnEntity(new SodaBottleRocketEntity(world, x, y, z, stack));
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        player.addStat(StatList.getObjectUseStats(this));
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isElytraFlying()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!world.isRemote) {
            world.spawnEntity(new SodaBottleRocketEntity(world, stack, player));
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        player.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
