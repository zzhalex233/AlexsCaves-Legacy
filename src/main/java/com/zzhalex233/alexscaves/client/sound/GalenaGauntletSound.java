package com.zzhalex233.alexscaves.client.sound;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.GalenaGauntletItem;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class GalenaGauntletSound extends ItemTickableSound {
    public GalenaGauntletSound(EntityLivingBase user) {
        super(user, ACSoundRegistry.GALENA_GAUNTLET_USE_LOOP);
    }

    @Override
    protected void tickVolume(ItemStack itemStack) {
        float useAmount = GalenaGauntletItem.getLerpedUseTime(itemStack, 1.0F) / 5.0F;
        this.volume = useAmount;
        this.pitch = 0.2F + 0.8F * useAmount;
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return itemStack.getItem() == ACItemRegistry.GALENA_GAUNTLET.item();
    }
}
