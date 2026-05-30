package com.zzhalex233.alexscaves.server.item;

import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.IRarity;

public class ACRecordItem extends ItemRecord {
    private final IRarity rarity;

    public ACRecordItem(String recordName, SoundEvent sound, IRarity rarity) {
        super(recordName, sound);
        this.rarity = rarity;
        setMaxStackSize(1);
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return rarity;
    }
}
