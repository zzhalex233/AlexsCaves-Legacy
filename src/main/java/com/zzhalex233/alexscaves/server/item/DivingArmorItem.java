package com.zzhalex233.alexscaves.server.item;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class DivingArmorItem extends ACArmorItem {
    private static final UUID SWIM_SPEED_MODIFIER = UUID.fromString("54A7B305-72F5-4ED1-80B2-C4C77593D5BA");

    public DivingArmorItem(ItemArmor.ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, slot, "diving_suit");
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        if (slot == EntityEquipmentSlot.LEGS && slot == armorType) {
            modifiers.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(SWIM_SPEED_MODIFIER, "Diving suit speed", 0.05D, 2));
        }
        return modifiers;
    }
}
