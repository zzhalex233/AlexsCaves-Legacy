package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.BlockOre;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BasicOreBlock extends BlockOre {
    private final Item drop;
    private final int minDrop;
    private final int maxDrop;
    private final int minExperience;
    private final int maxExperience;

    public BasicOreBlock(float hardness, float resistance, SoundType sound, Item drop, int minDrop, int maxDrop, int minExperience, int maxExperience) {
        this.drop = drop;
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return drop == null ? super.getItemDropped(state, rand, fortune) : drop;
    }

    @Override
    public int quantityDropped(Random random) {
        return minDrop >= maxDrop ? minDrop : minDrop + random.nextInt(maxDrop - minDrop + 1);
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        if (getItemDropped(state, RANDOM, fortune) == Item.getItemFromBlock(this)) {
            return 0;
        }
        return minExperience >= maxExperience ? minExperience : minExperience + RANDOM.nextInt(maxExperience - minExperience + 1);
    }
}
