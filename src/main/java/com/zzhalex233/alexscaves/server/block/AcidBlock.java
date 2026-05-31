package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.server.block.fluid.ACFluidRegistry;
import com.zzhalex233.alexscaves.server.entity.living.RadgillEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;

public class AcidBlock extends BlockFluidClassic {
    public static final DamageSource ACID = new DamageSource("acid").setDamageBypassesArmor();

    public AcidBlock() {
        super(ACFluidRegistry.ACID, Material.WATER);
        setQuantaPerBlock(8);
        setTickRate(5);
        setRenderLayer(BlockRenderLayer.TRANSLUCENT);
        setHardness(100.0F);
        setResistance(500.0F);
        setLightLevel(0.35F);
        setLightOpacity(1);
        disableStats();
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(400) == 0) {
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ACSoundRegistry.ACID_IDLE, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
        }
        if (rand.nextInt(18) == 0) {
            world.spawnParticle(EnumParticleTypes.SPELL_MOB, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 0.45D, 0.9D, 0.05D);
        }
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        entity.fallDistance = 0.0F;
        if (entity instanceof EntityLivingBase && !(entity instanceof RadgillEntity)) {
            EntityLivingBase living = (EntityLivingBase) entity;
            if (!world.isRemote && living.ticksExisted % 20 == 0 && living.attackEntityFrom(ACID, 1.0F)) {
                living.playSound(ACSoundRegistry.ACID_BURN, 0.8F, 1.0F);
            }
            if (entity.distanceWalkedModified > entity.distanceWalkedOnStepModified + 1.0F) {
                entity.distanceWalkedOnStepModified = entity.distanceWalkedModified;
                float volume = Math.min(1.0F, (float) Math.sqrt(entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ));
                entity.playSound(ACSoundRegistry.ACID_SWIM, volume, 1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);
            }
        }
    }
}
