package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.entity.item.InkBombEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class InkBombItem extends Item {
    private final boolean glowing;

    public InkBombItem(boolean glowing) {
        this.glowing = glowing;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isRemote) {
            InkBombEntity bomb = new InkBombEntity(worldIn, playerIn);
            bomb.setGlowingBomb(glowing);
            bomb.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.65F, 0.9F);
            worldIn.spawnEntity(bomb);
        }
        if (!playerIn.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
