package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BasicTranslucentBlock extends BasicCaveBlock {
    public BasicTranslucentBlock(Material material, float hardness, float resistance, SoundType sound) {
        super(material, hardness, resistance, sound);
        setLightOpacity(0);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
