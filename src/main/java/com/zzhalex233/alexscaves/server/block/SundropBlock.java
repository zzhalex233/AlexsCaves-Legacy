package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.client.particle.SundropParticle;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SundropBlock extends DirectionalFacingBlock {
    public SundropBlock() {
        super(Material.CAKE, 2.0F, 5.0F, ACSoundTypes.SQUISHY_CANDY, false);
        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.UP));
        setLightLevel(1.0F);
        setLightOpacity(0);
        setTickRandomly(true);
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isSneaking()) {
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        } else {
            entityIn.fall(fallDistance, 0.0F);
        }
    }

    @Override
    public void onLanded(World worldIn, Entity entityIn) {
        if (entityIn.isSneaking()) {
            super.onLanded(worldIn, entityIn);
        } else if (entityIn.motionY < 0.0D) {
            entityIn.motionY = -entityIn.motionY * (entityIn instanceof EntityLivingBase ? 1.0D : 0.8D);
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(2) == 0) {
            SundropParticle.spawn(worldIn, pos, rand);
        }
    }
}
