package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.entity.item.SeekingArrowEntity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SeekingArrowItem extends ItemArrow {
    @Override
    public EntityArrow createArrow(World world, ItemStack stack, EntityLivingBase shooter) {
        return new SeekingArrowEntity(world, shooter);
    }
}
