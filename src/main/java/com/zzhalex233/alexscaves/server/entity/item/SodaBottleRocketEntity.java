package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SodaBottleRocketEntity extends EntityFireworkRocket {
    public SodaBottleRocketEntity(World worldIn) {
        super(worldIn);
    }

    public SodaBottleRocketEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public SodaBottleRocketEntity(World worldIn, ItemStack stack, EntityLivingBase attachedToEntity) {
        super(worldIn, stack, attachedToEntity);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            for (int i = 0; i < 5; i++) {
                world.spawnParticle(EnumParticleTypes.SPELL_MOB, posX, posY - 0.3D, posZ, 0.55D + rand.nextGaussian() * 0.08D, 0.05D, 0.95D + rand.nextGaussian() * 0.08D);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 17) {
            world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
            for (int i = 0; i < 45; i++) {
                world.spawnParticle(EnumParticleTypes.SPELL_MOB, posX + rand.nextGaussian() * 0.95D, posY + rand.nextGaussian() * 0.95D, posZ + rand.nextGaussian() * 0.95D, 0.55D + rand.nextGaussian() * 0.1D, 0.05D, 0.95D + rand.nextGaussian() * 0.1D);
                world.spawnParticle(EnumParticleTypes.SNOWBALL, posX, posY, posZ, rand.nextGaussian() * 0.25D, rand.nextGaussian() * 0.25D, rand.nextGaussian() * 0.25D);
            }
            world.playSound(posX, posY, posZ, SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.AMBIENT, 20.0F, 0.95F + rand.nextFloat() * 0.1F, true);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getItem() {
        return new ItemStack(ACItemRegistry.PURPLE_SODA_BOTTLE_ROCKET.item());
    }
}
