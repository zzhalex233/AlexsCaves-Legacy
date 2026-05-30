package com.zzhalex233.alexscaves.server.potion;

import java.util.Collections;
import java.util.List;

import com.zzhalex233.alexscaves.server.misc.ACDamageSources;
import com.zzhalex233.alexscaves.server.item.HazmatArmorItem;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class IrradiatedEffect extends ACPotion {
    public static final int BLUE_LEVEL = 4;

    protected IrradiatedEffect() {
        super("irradiated", true, 0X77D60E);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        int hazmat = HazmatArmorItem.getWornAmount(entity);
        float damageScale = 1.0F - hazmat * 0.25F;
        if (entity instanceof EntityPlayer && hazmat == 0) {
            ((EntityPlayer) entity).addExhaustion(0.4F);
        }
        if (entity.getRNG().nextFloat() < damageScale + 0.1F) {
            entity.attackEntityFrom(ACDamageSources.RADIATION, damageScale);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        if (amplifier <= 0) {
            return false;
        }
        int interval = 200 / amplifier;
        return interval <= 1 || duration % interval == interval / 2;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
