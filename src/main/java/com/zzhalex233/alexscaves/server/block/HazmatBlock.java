package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class HazmatBlock extends BasicPillarBlock {
    public HazmatBlock() {
        super(Material.IRON, 3.5F, 12.0F, ACSoundTypes.HAZMAT_BLOCK);
    }

    @Override
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos) {
        return PathNodeType.FENCE;
    }

    @Override
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving entity) {
        return PathNodeType.FENCE;
    }
}
