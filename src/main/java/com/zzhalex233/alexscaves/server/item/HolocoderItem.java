package com.zzhalex233.alexscaves.server.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class HolocoderItem extends Item {
    public HolocoderItem() {
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("bound"), (stack, world, entity) -> isBound(stack) ? 1.0F : 0.0F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        NBTTagCompound entityTag = stack.getSubCompound("BoundEntityTag");
        if (entityTag != null && entityTag.hasKey("id")) {
            tooltip.add(TextFormatting.GRAY + I18n.format(EntityList.getTranslationName(new ResourceLocation(entityTag.getString("id")))));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static UUID getBoundEntityUUID(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasUniqueId("BoundEntityUUID") ? tag.getUniqueId("BoundEntityUUID") : null;
    }

    public static boolean isBound(ItemStack stack) {
        return getBoundEntityUUID(stack) != null;
    }
}
