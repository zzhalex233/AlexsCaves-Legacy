package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BasicDoorBlock extends BlockDoor {
    public BasicDoorBlock(float hardness) {
        super(Material.WOOD);
        setHardness(hardness);
        setSoundType(SoundType.WOOD);
    }
}
