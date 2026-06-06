package com.zzhalex233.alexscaves.client.sound;

import com.zzhalex233.alexscaves.server.entity.item.SubmarineEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;

public class SubmarineSound extends MovingSound {
    private final SubmarineEntity submarine;

    public SubmarineSound(SubmarineEntity submarine) {
        super(ACSoundRegistry.SUBMARINE_MOVE_LOOP, SoundCategory.NEUTRAL);
        this.submarine = submarine;
        this.repeat = true;
        this.repeatDelay = 0;
        this.attenuationType = AttenuationType.LINEAR;
    }

    @Override
    public void update() {
        if (submarine.isDead || !submarine.isBeingRidden() || !submarine.isInWater()) {
            this.donePlaying = true;
            return;
        }
        this.xPosF = (float) submarine.posX;
        this.yPosF = (float) submarine.posY;
        this.zPosF = (float) submarine.posZ;
        this.volume = 0.25F + Math.min(Math.abs(submarine.getAcceleration()), 1.0F) * 0.75F;
        this.pitch = 0.8F + Math.min(Math.abs(submarine.getAcceleration()), 1.0F) * 0.2F;
    }

    public boolean isSameEntity(SubmarineEntity submarine) {
        return this.submarine.getEntityId() == submarine.getEntityId() && !this.submarine.isDead;
    }
}
