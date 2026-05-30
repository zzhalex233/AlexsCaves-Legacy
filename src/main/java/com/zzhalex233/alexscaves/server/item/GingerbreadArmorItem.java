package com.zzhalex233.alexscaves.server.item;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class GingerbreadArmorItem extends ACArmorItem {
    private static final double MIN_SPEED_BOOST = 0.1D;
    private static final double MAX_SPEED_BOOST = 1.0D;
    private static final UUID[] SPEED_MODIFIERS = new UUID[] {
            UUID.fromString("6B58EC54-B76B-40D3-9615-0535FC211DF5"),
            UUID.fromString("3C2335DB-3998-41B6-B6B3-74F5CBB6AA71"),
            UUID.fromString("2CF53DF9-5226-4B40-9C1F-7CA27328993A"),
            UUID.fromString("209E55D4-5BD2-4BE2-AAD5-832C4F8FBCE7")
    };

    public GingerbreadArmorItem(ItemArmor.ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, slot, "gingerbread_armor");
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        if (slot == armorType) {
            double speed = MIN_SPEED_BOOST;
            if (stack.getMaxDamage() > 0) {
                speed += (MAX_SPEED_BOOST - MIN_SPEED_BOOST) * (stack.getItemDamage() / (double) stack.getMaxDamage());
            }
            modifiers.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(SPEED_MODIFIERS[armorType.getIndex()], "Gingerbread speed", speed, 2));
        }
        return modifiers;
    }
}
