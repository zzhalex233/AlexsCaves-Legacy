package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.fluid.ACFluidRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class AcidBucketItem extends PurpleSodaBucketItem {
    @Override
    protected Fluid getFluid() {
        return ACFluidRegistry.ACID;
    }

    @Override
    protected Block getFluidBlock() {
        return ACBlockRegistry.ACID.block();
    }

    @Override
    protected net.minecraft.util.SoundEvent getEmptySound() {
        return ACSoundRegistry.ACID_UNSUBMERGE;
    }

    @Override
    protected FluidStack getContainedFluid() {
        return new FluidStack(ACFluidRegistry.ACID, Fluid.BUCKET_VOLUME);
    }
}
