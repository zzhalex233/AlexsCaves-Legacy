package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BasicPillarBlock extends BlockRotatedPillar {
    public BasicPillarBlock(Material material, float hardness, float resistance, SoundType sound) {
        super(material);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
    }

    public BasicPillarBlock light(float value) {
        setLightLevel(value);
        return this;
    }
}
