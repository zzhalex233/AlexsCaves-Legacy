package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BioluminescentTorchBlock extends BlockTorch {
    public BioluminescentTorchBlock() {
        setHardness(0.0F);
        setLightLevel(1.0F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.7D;
        double z = pos.getZ() + 0.5D;
        worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 0.25D, 0.95D, 0.8D);
    }
}
