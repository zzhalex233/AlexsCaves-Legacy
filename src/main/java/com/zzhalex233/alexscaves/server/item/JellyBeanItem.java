package com.zzhalex233.alexscaves.server.item;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class JellyBeanItem extends ItemFood {
    public JellyBeanItem() {
        super(2, 0.1F, false);
        setAlwaysEdible();
        setMaxStackSize(16);
    }

    public static int getBeanColor(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("Rainbow")) {
            return Color.HSBtoRGB((System.currentTimeMillis() % 4000L) / 4000.0F, 1.0F, 0.8F);
        }
        return PotionUtils.getColor(stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 16;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            for (PotionEffect effect : PotionUtils.getFullEffectsFromItem(stack)) {
                if (effect.getPotion().isInstant()) {
                    effect.getPotion().affectEntity(player, player, player, effect.getAmplifier(), 1.0D);
                } else {
                    player.addPotionEffect(new PotionEffect(effect));
                }
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        for (PotionEffect effect : PotionUtils.getFullEffectsFromItem(stack)) {
            String name = I18n.format(effect.getEffectName());
            if (effect.getAmplifier() > 0) {
                name = I18n.format("potion.withAmplifier", name, I18n.format("potion.potency." + effect.getAmplifier()));
            }
            if (effect.getDuration() > 20) {
                name = I18n.format("potion.withDuration", name, net.minecraft.potion.Potion.getPotionDurationString(effect, 1.0F));
            }
            tooltip.add(TextFormatting.GRAY + I18n.format("item.alexscaves.jelly_bean.desc", name));
        }
    }
}
