package com.zzhalex233.alexscaves.server.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ACCreativeTabs {
    public static final CreativeTabs MAGNETIC_CAVES = tab("alexscaves.magnetic_caves", "raw_scarlet_neodymium");
    public static final CreativeTabs PRIMORDIAL_CAVES = tab("alexscaves.primordial_caves", "pine_nuts");
    public static final CreativeTabs TOXIC_CAVES = tab("alexscaves.toxic_caves", "sulfur_dust");
    public static final CreativeTabs ABYSSAL_CHASM = tab("alexscaves.abyssal_chasm", "lanternfish");
    public static final CreativeTabs FORLORN_HOLLOWS = tab("alexscaves.forlorn_hollows", "pure_darkness");
    public static final CreativeTabs CANDY_CAVITY = tab("alexscaves.candy_cavity", "sweet_tooth");

    private static CreativeTabs tab(String label, String iconItemName) {
        return new CreativeTabs(label) {
            @Override
            public ItemStack createIcon() {
                return new ItemStack(ACItemRegistry.byName(iconItemName).item());
            }
        };
    }
}
