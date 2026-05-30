package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.SoundType;

public class ACSoundTypes {
    public static final SoundType HAZMAT_BLOCK = new SoundType(1.0F, 1.0F, ACSoundRegistry.HAZMAT_BLOCK_BREAK, ACSoundRegistry.HAZMAT_BLOCK_STEP, ACSoundRegistry.HAZMAT_BLOCK_PLACE, ACSoundRegistry.HAZMAT_BLOCK_BREAKING, ACSoundRegistry.HAZMAT_BLOCK_STEP);
    public static final SoundType CINDER_BLOCK = new SoundType(1.0F, 1.0F, ACSoundRegistry.CINDER_BLOCK_BREAK, ACSoundRegistry.CINDER_BLOCK_STEP, ACSoundRegistry.CINDER_BLOCK_PLACE, ACSoundRegistry.CINDER_BLOCK_BREAKING, ACSoundRegistry.CINDER_BLOCK_STEP);
    public static final SoundType UNREFINED_WASTE = new SoundType(1.0F, 1.0F, ACSoundRegistry.UNREFINED_WASTE_BREAK, ACSoundRegistry.UNREFINED_WASTE_STEP, ACSoundRegistry.UNREFINED_WASTE_PLACE, ACSoundRegistry.UNREFINED_WASTE_BREAKING, ACSoundRegistry.UNREFINED_WASTE_STEP);
    public static final SoundType SOFT_CANDY = new SoundType(1.0F, 1.0F, ACSoundRegistry.SOFT_CANDY_BREAK, ACSoundRegistry.SOFT_CANDY_STEP, ACSoundRegistry.SOFT_CANDY_PLACE, ACSoundRegistry.SOFT_CANDY_BREAKING, ACSoundRegistry.SOFT_CANDY_STEP);
    public static final SoundType DENSE_CANDY = new SoundType(1.0F, 1.0F, ACSoundRegistry.DENSE_CANDY_BREAK, ACSoundRegistry.DENSE_CANDY_STEP, ACSoundRegistry.DENSE_CANDY_PLACE, ACSoundRegistry.DENSE_CANDY_BREAKING, ACSoundRegistry.DENSE_CANDY_STEP);
    public static final SoundType HARD_CANDY = new SoundType(1.0F, 1.0F, ACSoundRegistry.HARD_CANDY_BREAK, ACSoundRegistry.HARD_CANDY_STEP, ACSoundRegistry.HARD_CANDY_PLACE, ACSoundRegistry.HARD_CANDY_BREAKING, ACSoundRegistry.HARD_CANDY_STEP);
    public static final SoundType SQUISHY_CANDY = new SoundType(1.0F, 1.0F, ACSoundRegistry.SQUISHY_CANDY_BREAK, ACSoundRegistry.SQUISHY_CANDY_STEP, ACSoundRegistry.SQUISHY_CANDY_PLACE, ACSoundRegistry.SQUISHY_CANDY_BREAKING, ACSoundRegistry.SQUISHY_CANDY_STEP);

    private ACSoundTypes() {
    }
}
