package com.zzhalex233.alexscaves.client.sound;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.ResistorShieldItem;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class ResistorShieldSound extends ItemTickableSound {
    private final boolean scarlet;

    public ResistorShieldSound(EntityLivingBase user, boolean scarlet) {
        super(user, scarlet ? ACSoundRegistry.RESITOR_SHIELD_SCARLET_LOOP : ACSoundRegistry.RESITOR_SHIELD_AZURE_LOOP);
        this.scarlet = scarlet;
    }

    @Override
    protected void tickVolume(ItemStack itemStack) {
        float useAmount = Math.min(ResistorShieldItem.getLerpedUseTime(itemStack, 1.0F) / 10.0F, 1.0F);
        this.volume = 0.5F + useAmount * 0.5F;
        this.pitch = 0.8F + useAmount * 0.2F;
        if (ResistorShieldItem.isScarlet(itemStack) != scarlet) {
            this.donePlaying = true;
        }
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return itemStack.getItem() == ACItemRegistry.RESISTOR_SHIELD.item();
    }
}
