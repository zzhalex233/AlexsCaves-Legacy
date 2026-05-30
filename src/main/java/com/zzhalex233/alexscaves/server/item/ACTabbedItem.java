package com.zzhalex233.alexscaves.server.item;

import java.util.Arrays;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;

public interface ACTabbedItem {
    List<CreativeTabs> ALL_CAVE_TABS = Arrays.asList(
            ACCreativeTabs.MAGNETIC_CAVES,
            ACCreativeTabs.PRIMORDIAL_CAVES,
            ACCreativeTabs.TOXIC_CAVES,
            ACCreativeTabs.ABYSSAL_CHASM,
            ACCreativeTabs.FORLORN_HOLLOWS,
            ACCreativeTabs.CANDY_CAVITY
    );

    static boolean isAlexsCavesTab(CreativeTabs tab) {
        return ALL_CAVE_TABS.contains(tab);
    }
}
