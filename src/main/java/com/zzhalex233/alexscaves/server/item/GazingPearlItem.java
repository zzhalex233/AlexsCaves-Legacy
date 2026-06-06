package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.entity.util.DeepOneReaction;
import com.zzhalex233.alexscaves.server.level.storage.ACWorldData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.List;
import java.util.Locale;

public class GazingPearlItem extends Item {
    public GazingPearlItem() {
        setMaxStackSize(1);
    }

    public static int getPearlColor(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        float shine = (float) (Math.sin(System.currentTimeMillis() / 4000.0F) + 1.0F) * 0.5F;
        if (tag != null && tag.getBoolean("HasReputation")) {
            int color = 100 - tag.getInteger("Reputation");
            return Color.HSBtoRGB(color / 200.0F, shine * 0.3F + 0.7F, 1.0F);
        }
        float hue = (System.currentTimeMillis() % 10000L) / 10000.0F;
        return Color.HSBtoRGB(hue, shine * 0.3F + 0.7F, 1.0F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.getBoolean("HasReputation")) {
            DeepOneReaction reaction = DeepOneReaction.fromReputation(tag.getInteger("Reputation"));
            tooltip.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + I18n.translateToLocal("item.alexscaves.gazing_pearl.desc_" + reaction.name().toLowerCase(Locale.ROOT)));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }
            long lastReputationTimestamp = tag.getLong("LastReputationTimestamp");
            if (lastReputationTimestamp <= 0L || worldIn.getTotalWorldTime() - lastReputationTimestamp > 100L) {
                ACWorldData data = ACWorldData.get(worldIn);
                if (data != null) {
                    tag.setLong("LastReputationTimestamp", worldIn.getTotalWorldTime());
                    tag.setBoolean("HasReputation", true);
                    tag.setInteger("Reputation", data.getDeepOneReputation(entityIn.getUniqueID()));
                }
            }
        }
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return net.minecraft.item.EnumRarity.UNCOMMON;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != ACItemRegistry.GAZING_PEARL.item() || newStack.getItem() != ACItemRegistry.GAZING_PEARL.item();
    }
}
