package com.zzhalex233.alexscaves.server.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class PrimordialArmorItem extends ACArmorItem {
    public PrimordialArmorItem(ItemArmor.ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, slot, "primordial_armor");
    }

    public static int getExtraSaturationFromArmor(EntityLivingBase entity) {
        int amount = 0;
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ACItemRegistry.PRIMORDIAL_HELMET.item()) {
            amount++;
        }
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ACItemRegistry.PRIMORDIAL_TUNIC.item()) {
            amount++;
        }
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == ACItemRegistry.PRIMORDIAL_PANTS.item()) {
            amount++;
        }
        return amount;
    }
}
