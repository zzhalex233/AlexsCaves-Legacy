package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BasicTrapDoorBlock extends BlockTrapDoor {
    public BasicTrapDoorBlock(float hardness) {
        super(Material.WOOD);
        setHardness(hardness);
        setSoundType(SoundType.WOOD);
    }
}
