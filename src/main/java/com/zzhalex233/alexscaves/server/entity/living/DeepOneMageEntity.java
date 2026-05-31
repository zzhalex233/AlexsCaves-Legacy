package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class DeepOneMageEntity extends DeepOneBaseEntity {
    private int magicCooldown;

    public DeepOneMageEntity(World world) {
        super(world);
        setSize(1.1F, 2.3F);
    }

    @Override
    protected double getBaseHealth() {
        return 80.0D;
    }

    @Override
    protected double getBaseDamage() {
        return 4.0D;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!world.isRemote && !isInWater() && ticksExisted % 40 == 0) {
            addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 100));
        }
        if (magicCooldown > 0) {
            magicCooldown--;
        }
    }

    @Override
    protected void startAttackBehavior(EntityLivingBase target) {
        double distance = getDistance(target);
        if (distance < 4.0D && attackCooldown <= 0) {
            playSound(ACSoundRegistry.DEEP_ONE_MAGE_ATTACK, 1.0F, getSoundPitch());
            attackCooldown = 50;
            AxisAlignedBB box = getEntityBoundingBox().grow(2.0D, 0.5D, 2.0D);
            for (EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class, box, entity -> entity != this && !isOnSameTeam(entity))) {
                living.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getBaseDamage());
                living.addVelocity(living.posX - posX, 0.2D, living.posZ - posZ);
            }
        } else if (distance < 22.0D && canEntityBeSeen(target) && magicCooldown <= 0) {
            playSound(ACSoundRegistry.DEEP_ONE_MAGE_ATTACK, 1.0F, getSoundPitch());
            target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), 6.0F);
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 1));
            if (!world.isRemote) {
                ((net.minecraft.world.WorldServer) world).spawnParticle(EnumParticleTypes.WATER_SPLASH, target.posX, target.posY + target.height * 0.5D, target.posZ, 24, 0.8D, 0.6D, 0.8D, 0.05D);
            }
            magicCooldown = 60;
        } else {
            getNavigator().tryMoveToEntityLiving(target, 1.1D);
        }
    }

    @Override
    protected SoundEvent getAttackSound() {
        return ACSoundRegistry.DEEP_ONE_MAGE_ATTACK;
    }

    @Override
    protected SoundEvent getIdleSound() {
        return ACSoundRegistry.DEEP_ONE_MAGE_IDLE;
    }

    @Override
    protected SoundEvent getHostileSound() {
        return ACSoundRegistry.DEEP_ONE_MAGE_HOSTILE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.DEEP_ONE_MAGE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.DEEP_ONE_MAGE_DEATH;
    }
}
