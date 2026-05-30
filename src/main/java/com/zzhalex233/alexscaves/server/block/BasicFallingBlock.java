package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BasicFallingBlock extends BlockFalling {
    public BasicFallingBlock(Material material, float hardness, float resistance, SoundType sound) {
        super(material);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
    }
}
