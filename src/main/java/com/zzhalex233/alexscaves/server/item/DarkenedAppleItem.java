package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class DarkenedAppleItem extends ACFoodItem {
    public DarkenedAppleItem(ACItemRegistry.Food food) {
        super(food);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        PotionEffect active = entity.getActivePotionEffect(ACEffectRegistry.DARKNESS_INCARNATE);
        if (active != null) {
            entity.addPotionEffect(new PotionEffect(ACEffectRegistry.DARKNESS_INCARNATE, active.getDuration() + 600, active.getAmplifier()));
        }
        return super.onItemUseFinish(stack, world, entity);
    }
}
