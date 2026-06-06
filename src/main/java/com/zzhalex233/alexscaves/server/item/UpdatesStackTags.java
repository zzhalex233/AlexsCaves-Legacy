package com.zzhalex233.alexscaves.server.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface UpdatesStackTags {
    default void updateTagFromServer(Entity holder, ItemStack stack, NBTTagCompound tag) {
        stack.setTagCompound(tag == null ? null : tag.copy());
    }
}
