package com.zzhalex233.alexscaves.server.potion;

import com.zzhalex233.alexscaves.AlexsCaves;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class ACPotion extends Potion {
    protected ACPotion(String name, boolean badEffect, int color) {
        super(badEffect, color);
        setRegistryName(new ResourceLocation(AlexsCaves.MODID, name));
        setPotionName("effect." + AlexsCaves.MODID + "." + name);
        if (!badEffect) {
            setBeneficial();
        }
    }
}
