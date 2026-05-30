package com.zzhalex233.alexscaves.server.potion;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class BubbledEffect extends TickingPotion {
    protected BubbledEffect() {
        super("bubbled", true, 0X21B5FF);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.isPotionActive(MobEffects.WATER_BREATHING) || entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.disableDamage) {
            return;
        }
        entity.motionX *= 0.8D;
        entity.motionZ *= 0.8D;
        entity.setAir(Math.max(entity.getAir() - 2, -20));
        if (entity.getAir() <= -20) {
            entity.setAir(0);
            entity.attackEntityFrom(DamageSource.DROWN, 2.0F);
        }
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
