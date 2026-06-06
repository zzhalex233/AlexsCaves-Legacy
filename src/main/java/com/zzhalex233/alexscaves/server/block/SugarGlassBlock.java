package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SugarGlassBlock extends BasicTranslucentBlock {
    public SugarGlassBlock() {
        super(Material.GLASS, 0.3F, 0.0F, SoundType.GLASS);
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (!worldIn.isRemote && fallDistance > 0.5F) {
            worldIn.destroyBlock(pos, true);
        }
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof EntityThrowable) {
            worldIn.destroyBlock(pos, true);
        }
        super.onEntityCollision(worldIn, pos, state, entityIn);
    }
}
