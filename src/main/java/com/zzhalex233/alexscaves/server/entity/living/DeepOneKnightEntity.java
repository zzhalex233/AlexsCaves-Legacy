package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DeepOneKnightEntity extends DeepOneBaseEntity {
    private int rangedCooldown;

    public DeepOneKnightEntity(World world) {
        super(world);
        setSize(1.1F, 2.2F);
    }

    @Override
    protected double getBaseHealth() {
        return 60.0D;
    }

    @Override
    protected double getBaseDamage() {
        return 5.0D;
    }

    @Override
    protected void startAttackBehavior(EntityLivingBase target) {
        if (rangedCooldown > 0) {
            rangedCooldown--;
        }
        double distance = getDistance(target);
        if (distance > 8.0D && distance < 30.0D && canEntityBeSeen(target) && rangedCooldown <= 0) {
            double dx = target.posX - posX;
            double dz = target.posZ - posZ;
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            if (target.attackEntityFrom(DamageSource.causeMobDamage(this), 5.0F)) {
                target.addVelocity(dx / horizontal * 0.6D, 0.2D, dz / horizontal * 0.6D);
            }
            playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.75F);
            swingArm(getActiveHand());
            rangedCooldown = 80;
        } else {
            super.startAttackBehavior(target);
        }
    }

    @Override
    protected SoundEvent getAttackSound() {
        return ACSoundRegistry.DEEP_ONE_KNIGHT_ATTACK;
    }

    @Override
    protected SoundEvent getIdleSound() {
        return ACSoundRegistry.DEEP_ONE_KNIGHT_IDLE;
    }

    @Override
    protected SoundEvent getHostileSound() {
        return ACSoundRegistry.DEEP_ONE_KNIGHT_HOSTILE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.DEEP_ONE_KNIGHT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.DEEP_ONE_KNIGHT_DEATH;
    }
}
