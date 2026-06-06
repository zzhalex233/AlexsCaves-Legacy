package com.zzhalex233.alexscaves.server.item;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class BiomeTreatItem extends CaveInfoItem {
    public BiomeTreatItem() {
        super(false);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.canEat(getCaveBiome(stack) == null)) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            player.getFoodStats().addStats(getCaveBiome(stack) == null ? 20 : 1, 0.1F);
            if (!world.isRemote && getCaveBiome(stack) == null && (player.getFoodStats().getFoodLevel() == 0 || player.capabilities.isCreativeMode)) {
                return create(this, caveBiomeAt(world, player.getPosition()));
            }
        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if (getCaveBiome(stack) == null) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("item.alexscaves.biome_treat.desc"));
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    public static int getBiomeTreatColor(ItemStack stack) {
        String caveBiome = getCaveBiome(stack);
        if (caveBiome == null || caveBiome.isEmpty()) {
            return Color.HSBtoRGB((System.currentTimeMillis() % 4000L) / 4000.0F, 1.0F, 0.8F);
        }
        int index = 0;
        for (int i = 0; i < CAVE_BIOMES.length; i++) {
            if (CAVE_BIOMES[i].equals(caveBiome)) {
                index = i;
                break;
            }
        }
        return Color.HSBtoRGB(index / (float) CAVE_BIOMES.length, 0.75F, 0.9F);
    }
}
