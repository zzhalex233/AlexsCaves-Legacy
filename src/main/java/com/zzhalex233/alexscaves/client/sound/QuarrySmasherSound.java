package com.zzhalex233.alexscaves.client.sound;

import com.zzhalex233.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class QuarrySmasherSound extends MovingSound {
    private final QuarrySmasherEntity quarrySmasher;
    private float moveFade;
    private float prevChainLength;

    public QuarrySmasherSound(QuarrySmasherEntity quarrySmasher) {
        super(ACSoundRegistry.BOUNDROID_CHAIN_LOOP, SoundCategory.BLOCKS);
        this.quarrySmasher = quarrySmasher;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0F;
        this.pitch = 1.0F;
        updatePosition();
    }

    @Override
    public void update() {
        if (quarrySmasher.isDead || quarrySmasher.isSilent()) {
            donePlaying = true;
            return;
        }
        updatePosition();
        float chainLength = quarrySmasher.getChainLength(1.0F);
        float movement = Math.min(Math.abs(prevChainLength - chainLength) * 20.0F, 1.0F);
        if (movement <= 0.3F) {
            moveFade = Math.min(1.0F, moveFade + 0.1F);
        } else {
            moveFade = Math.max(0.0F, moveFade - 0.25F);
        }
        volume = quarrySmasher.isInactive() ? 0.0F : 1.0F - moveFade;
        pitch = 1.0F + movement;
        prevChainLength = chainLength;
    }

    public boolean isSameEntity(QuarrySmasherEntity entity) {
        return !quarrySmasher.isDead && quarrySmasher.getEntityId() == entity.getEntityId();
    }

    private void updatePosition() {
        xPosF = (float) quarrySmasher.posX;
        yPosF = (float) quarrySmasher.posY;
        zPosF = (float) quarrySmasher.posZ;
    }
}
