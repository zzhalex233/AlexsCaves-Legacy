package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;

public class BasicFenceGateBlock extends BlockFenceGate {
    public BasicFenceGateBlock(float hardness, float resistance) {
        super(BlockPlanks.EnumType.OAK);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(SoundType.WOOD);
    }
}
