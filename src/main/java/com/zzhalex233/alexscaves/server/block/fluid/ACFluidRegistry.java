package com.zzhalex233.alexscaves.server.block.fluid;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ACFluidRegistry {
    public static final Fluid PURPLE_SODA = new Fluid("purple_soda",
            new ResourceLocation(AlexsCaves.MODID, "block/purple_soda_still"),
            new ResourceLocation(AlexsCaves.MODID, "block/purple_soda_flowing"))
            .setUnlocalizedName(AlexsCaves.MODID + ".purple_soda")
            .setDensity(1000)
            .setViscosity(1000)
            .setFillSound(ACSoundRegistry.PURPLE_SODA_SUBMERGE)
            .setEmptySound(ACSoundRegistry.PURPLE_SODA_UNSUBMERGE);
    public static final Fluid ACID = new Fluid("acid",
            new ResourceLocation(AlexsCaves.MODID, "block/acid_still"),
            new ResourceLocation(AlexsCaves.MODID, "block/acid_flowing"))
            .setUnlocalizedName(AlexsCaves.MODID + ".acid")
            .setLuminosity(5)
            .setDensity(1024)
            .setViscosity(1024)
            .setFillSound(ACSoundRegistry.ACID_SUBMERGE)
            .setEmptySound(ACSoundRegistry.ACID_UNSUBMERGE);

    private static boolean fluidRegistered;
    private static boolean bucketRegistered;

    static {
        registerFluidOnly();
    }

    public static void registerFluids() {
        registerFluidOnly();
        if (bucketRegistered) {
            return;
        }
        ACID.setBlock(ACBlockRegistry.ACID.block());
        PURPLE_SODA.setBlock(ACBlockRegistry.PURPLE_SODA.block());
        FluidRegistry.addBucketForFluid(ACID);
        FluidRegistry.addBucketForFluid(PURPLE_SODA);
        bucketRegistered = true;
    }

    private static void registerFluidOnly() {
        if (fluidRegistered) {
            return;
        }
        FluidRegistry.registerFluid(ACID);
        FluidRegistry.registerFluid(PURPLE_SODA);
        fluidRegistered = true;
    }
}
