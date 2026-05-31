package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class OccultGemItem extends Item {
    public OccultGemItem() {
        setMaxStackSize(64);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("BeholderPos");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("BeholderX") && tag.hasKey("BeholderY") && tag.hasKey("BeholderZ")) {
            tooltip.add(TextFormatting.GRAY + new TextComponentTranslation("item.alexscaves.occult_gem.desc", tag.getInteger("BeholderX"), tag.getInteger("BeholderY"), tag.getInteger("BeholderZ")).getFormattedText());
        }
    }
}
