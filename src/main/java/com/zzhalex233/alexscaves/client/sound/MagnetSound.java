package com.zzhalex233.alexscaves.client.sound;

import com.zzhalex233.alexscaves.server.block.entity.MagnetTileEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MagnetSound extends MovingSound {
    private final MagnetTileEntity magnet;
    private final boolean azure;
    private float activeAmount;

    public MagnetSound(MagnetTileEntity magnet) {
        super(magnet.isAzure() ? ACSoundRegistry.AZURE_NEODYMIUM_PUSH_LOOP : ACSoundRegistry.SCARLET_NEODYMIUM_PULL_LOOP, SoundCategory.BLOCKS);
        this.magnet = magnet;
        this.azure = magnet.isAzure();
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0F;
        this.pitch = 1.0F;
        updatePosition();
    }

    @Override
    public void update() {
        if (magnet.isInvalid() || magnet.getWorld() == null || magnet.getWorld() != Minecraft.getMinecraft().world) {
            donePlaying = true;
            return;
        }
        boolean active = magnet.isLocallyActive();
        if (active && activeAmount < 1.0F) {
            activeAmount += 0.1F;
        } else if (!active && activeAmount > 0.0F) {
            activeAmount -= 0.1F;
        }
        if (activeAmount <= 0.0F && !active) {
            donePlaying = true;
            return;
        }
        updatePosition();
        volume = activeAmount * 0.5F;
        pitch = 1.0F;
    }

    public boolean isSameBlockEntity(MagnetTileEntity other) {
        return !magnet.isInvalid() && magnet.getPos().equals(other.getPos()) && magnet.getWorld() == other.getWorld() && azure == other.isAzure();
    }

    private void updatePosition() {
        xPosF = magnet.getPos().getX() + 0.5F;
        yPosF = magnet.getPos().getY() + 0.5F;
        zPosF = magnet.getPos().getZ() + 0.5F;
    }
}
