package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockFence;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BasicFenceBlock extends BlockFence {
    public BasicFenceBlock(Material material, float hardness, float resistance, SoundType sound) {
        super(material, MapColor.WOOD);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
    }
}
