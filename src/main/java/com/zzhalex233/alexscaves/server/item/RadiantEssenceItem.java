package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class RadiantEssenceItem extends RarityItem {

    public RadiantEssenceItem() {
        super(ACItemRegistry.RARITY_RAINBOW);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (this == ACItemRegistry.LICOWITCH_RADIANT_ESSENCE.item()) {
            tooltip.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + I18n.format("item.alexscaves.licowitch_radiant_essence.desc"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
