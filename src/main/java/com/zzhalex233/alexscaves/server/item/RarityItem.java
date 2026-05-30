package com.zzhalex233.alexscaves.server.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;

public class RarityItem extends Item {
    private final IRarity rarity;

    public RarityItem(IRarity rarity) {
        this.rarity = rarity;
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return rarity;
    }
}
