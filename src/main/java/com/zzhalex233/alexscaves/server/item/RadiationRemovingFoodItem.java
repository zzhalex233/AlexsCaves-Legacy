package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RadiationRemovingFoodItem extends ACFoodItem {
    public RadiationRemovingFoodItem(ACItemRegistry.Food food) {
        super(food);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (entity.isPotionActive(ACEffectRegistry.IRRADIATED)) {
            entity.heal((float) Math.ceil(food().healAmount() * 1.5F + 1.0F));
        }
        return super.onItemUseFinish(stack, world, entity);
    }
}
