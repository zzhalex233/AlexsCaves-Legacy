package com.zzhalex233.alexscaves.server.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class HazmatArmorItem extends ACArmorItem {
    public HazmatArmorItem(ItemArmor.ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, slot, "hazmat_suit");
    }

    public static int getWornAmount(EntityLivingBase entity) {
        int amount = 0;
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ACItemRegistry.HAZMAT_MASK.item()) {
            amount++;
        }
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ACItemRegistry.HAZMAT_CHESTPLATE.item()) {
            amount++;
        }
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == ACItemRegistry.HAZMAT_LEGGINGS.item()) {
            amount++;
        }
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == ACItemRegistry.HAZMAT_BOOTS.item()) {
            amount++;
        }
        return amount;
    }
}
