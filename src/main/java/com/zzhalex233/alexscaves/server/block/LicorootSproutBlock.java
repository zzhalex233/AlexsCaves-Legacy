package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.state.IBlockState;

public class LicorootSproutBlock extends CavePlantBlock {
    public LicorootSproutBlock() {
        super(true);
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == ACBlockRegistry.BLOCK_OF_CHOCOLATE.block()
                || state.getBlock() == ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.block()
                || super.canSustainBush(state);
    }
}
