package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class AlexMealItem extends ACFoodItem {
    public AlexMealItem() {
        super(ACItemRegistry.Food.of(40, 5.0F).effect(new PotionEffect(MobEffects.NAUSEA, 200), 1.0F).withBowlRemainder());
        setMaxStackSize(1);
    }

    @Override
    public net.minecraftforge.common.IRarity getForgeRarity(ItemStack stack) {
        return ACItemRegistry.RARITY_RAINBOW;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + I18n.translateToLocal("item.alexscaves.alex_meal.desc"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
