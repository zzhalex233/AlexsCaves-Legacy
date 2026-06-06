package com.zzhalex233.alexscaves.client.sound;

import com.zzhalex233.alexscaves.server.block.entity.NuclearSirenTileEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NuclearSirenSound extends MovingSound {
    private final NuclearSirenTileEntity siren;

    public NuclearSirenSound(NuclearSirenTileEntity siren) {
        super(ACSoundRegistry.NUCLEAR_SIREN, SoundCategory.BLOCKS);
        this.siren = siren;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1F;
        this.pitch = 1.0F;
        updatePosition();
    }

    @Override
    public void update() {
        if (siren.isInvalid() || siren.getWorld() == null || siren.getWorld() != Minecraft.getMinecraft().world || !canPlay()) {
            donePlaying = true;
            return;
        }
        updatePosition();
        volume = siren.getVolume(1.0F);
        pitch = 1.0F;
    }

    public boolean isSameBlockEntity(NuclearSirenTileEntity other) {
        return !siren.isInvalid() && siren.getPos().equals(other.getPos()) && siren.getWorld() == other.getWorld();
    }

    private boolean canPlay() {
        return siren.isActivated(siren.getWorld().getBlockState(siren.getPos())) && siren.getVolume(1.0F) > 0.0F;
    }

    private void updatePosition() {
        xPosF = siren.getPos().getX() + 0.5F;
        yPosF = siren.getPos().getY() + 0.5F;
        zPosF = siren.getPos().getZ() + 0.5F;
    }
}
