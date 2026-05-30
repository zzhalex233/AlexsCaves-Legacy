package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class RadioactiveItem extends Item {
    private final float randomChanceOfRadiation;

    public RadioactiveItem(float randomChanceOfRadiation) {
        this.randomChanceOfRadiation = randomChanceOfRadiation;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
        if (!world.isRemote && entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)) {
            EntityLivingBase living = (EntityLivingBase) entity;
            float hazmatMultiplier = 1.0F - HazmatArmorItem.getWornAmount(living) / 4.0F;
            if (!living.isPotionActive(ACEffectRegistry.IRRADIATED) && world.rand.nextFloat() < stack.getCount() * randomChanceOfRadiation * hazmatMultiplier) {
                living.addPotionEffect(new PotionEffect(ACEffectRegistry.IRRADIATED, 1800));
            }
        }
    }
}
