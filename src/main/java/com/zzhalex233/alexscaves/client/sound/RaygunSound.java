package com.zzhalex233.alexscaves.client.sound;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.RaygunItem;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class RaygunSound extends ItemTickableSound {
    public RaygunSound(EntityLivingBase user) {
        super(user, ACSoundRegistry.RAYGUN_LOOP);
    }

    @Override
    protected void tickVolume(ItemStack itemStack) {
        float useAmount = RaygunItem.getLerpedUseTime(itemStack, 1.0F) / 5.0F;
        this.volume = 0.2F + 0.8F * useAmount;
        this.pitch = 0.8F + 0.2F * useAmount;
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return itemStack.getItem() == ACItemRegistry.RAYGUN.item();
    }
}
