package com.zzhalex233.alexscaves.client.sound;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public abstract class ItemTickableSound extends MovingSound {
    protected final EntityLivingBase user;

    public ItemTickableSound(EntityLivingBase user, SoundEvent soundEvent) {
        super(soundEvent, SoundCategory.PLAYERS);
        this.user = user;
        this.repeat = true;
        this.repeatDelay = 0;
        this.attenuationType = AttenuationType.LINEAR;
        this.xPosF = (float) user.posX;
        this.yPosF = (float) user.posY;
        this.zPosF = (float) user.posZ;
    }

    @Override
    public void update() {
        ItemStack itemStack = ItemStack.EMPTY;
        if (user.isHandActive()) {
            if (isValidItem(user.getHeldItemMainhand())) {
                itemStack = user.getHeldItemMainhand();
            }
            if (isValidItem(user.getHeldItemOffhand())) {
                itemStack = user.getHeldItemOffhand();
            }
        }
        if (user.isEntityAlive() && !itemStack.isEmpty()) {
            xPosF = (float) user.posX;
            yPosF = (float) user.posY;
            zPosF = (float) user.posZ;
            tickVolume(itemStack);
        } else {
            donePlaying = true;
        }
    }

    protected abstract void tickVolume(ItemStack itemStack);

    public abstract boolean isValidItem(ItemStack itemStack);

    public boolean isSameEntity(EntityLivingBase user) {
        return this.user.isEntityAlive() && this.user.getEntityId() == user.getEntityId();
    }
}
