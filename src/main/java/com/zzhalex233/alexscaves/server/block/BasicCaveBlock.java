package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BasicCaveBlock extends Block {
    public BasicCaveBlock(Material material, float hardness, float resistance, SoundType sound) {
        super(material);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
    }

    public BasicCaveBlock light(float value) {
        setLightLevel(value);
        return this;
    }

}
