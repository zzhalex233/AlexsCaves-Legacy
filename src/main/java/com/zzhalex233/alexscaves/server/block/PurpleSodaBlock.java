package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.server.block.fluid.ACFluidRegistry;
import com.zzhalex233.alexscaves.server.entity.living.SweetishFishEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;

public class PurpleSodaBlock extends BlockFluidClassic {
    public PurpleSodaBlock() {
        super(ACFluidRegistry.PURPLE_SODA, Material.WATER);
        setQuantaPerBlock(8);
        setTickRate(5);
        setRenderLayer(BlockRenderLayer.TRANSLUCENT);
        setHardness(100.0F);
        setResistance(500.0F);
        setLightOpacity(1);
        disableStats();
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(400) == 0) {
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.PURPLE_SODA_IDLE, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
        }
        if (rand.nextInt(150) == 0) {
            world.spawnParticle(EnumParticleTypes.SPELL_MOB, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 0.55D, 0.05D, 0.95D);
        }
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        entity.fallDistance = 0.0F;
        if (entity instanceof EntityLivingBase && !(entity instanceof SweetishFishEntity) && entity.distanceWalkedModified > entity.distanceWalkedOnStepModified + 1.0F) {
            entity.distanceWalkedOnStepModified = entity.distanceWalkedModified;
            float volume = Math.min(1.0F, (float) Math.sqrt(entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ));
            entity.playSound(ACSoundRegistry.PURPLE_SODA_SWIM, volume, 1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);
        }
    }
}
