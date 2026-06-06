package com.zzhalex233.alexscaves.server.enchantment;

import com.zzhalex233.alexscaves.AlexsCaves;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ACWeaponEnchantment extends Enchantment {
    private final int levels;
    private final int minEnchantability;

    public ACWeaponEnchantment(String name, Rarity rarity, EnumEnchantmentType type, int levels, int minEnchantability, EntityEquipmentSlot... slots) {
        super(rarity, type, slots);
        this.levels = levels;
        this.minEnchantability = minEnchantability;
        setName(AlexsCaves.MODID + "." + name);
    }

    @Override
    public int getMinEnchantability(int level) {
        return 1 + (level - 1) * minEnchantability;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return getMinEnchantability(level) + 30;
    }

    @Override
    public int getMaxLevel() {
        return levels;
    }

    @Override
    protected boolean canApplyTogether(Enchantment enchantment) {
        return this != enchantment && ACEnchantmentRegistry.areCompatible(this, enchantment);
    }
}
