package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BasicPressurePlateBlock extends BlockPressurePlate {
    public BasicPressurePlateBlock(float hardness, float resistance) {
        super(Material.WOOD, Sensitivity.EVERYTHING);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(SoundType.WOOD);
    }
}
