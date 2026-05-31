package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class DeepOneEntity extends DeepOneBaseEntity {
    public DeepOneEntity(World world) {
        super(world);
    }

    @Override
    protected double getBaseHealth() {
        return 30.0D;
    }

    @Override
    protected double getBaseDamage() {
        return 3.0D;
    }

    @Override
    protected SoundEvent getAttackSound() {
        return ACSoundRegistry.DEEP_ONE_ATTACK;
    }

    @Override
    protected SoundEvent getIdleSound() {
        return ACSoundRegistry.DEEP_ONE_IDLE;
    }

    @Override
    protected SoundEvent getHostileSound() {
        return ACSoundRegistry.DEEP_ONE_HOSTILE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.DEEP_ONE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.DEEP_ONE_DEATH;
    }
}
