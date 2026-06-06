package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.entity.item.BurrowingArrowEntity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BurrowingArrowItem extends ItemArrow {
    @Override
    public EntityArrow createArrow(World world, ItemStack stack, EntityLivingBase shooter) {
        return new BurrowingArrowEntity(world, shooter);
    }
}
