package com.zzhalex233.alexscaves.server.potion;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

public class StunnedEffect extends TickingPotion {
    protected StunnedEffect() {
        super("stunned", true, 0XFFFBC5);
        registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160892", -1.0D, 0);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.motionY > 0.0D) {
            entity.motionY *= 0.1D;
            entity.velocityChanged = true;
        }
        if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            living.setMoveForward(0.0F);
            living.setMoveStrafing(0.0F);
            living.setAIMoveSpeed(0.0F);
            living.rotationPitch = 30.0F;
            living.prevRotationPitch = 30.0F;
        }
    }
}
