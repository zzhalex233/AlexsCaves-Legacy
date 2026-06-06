package com.zzhalex233.alexscaves.client.sound;

import com.zzhalex233.alexscaves.server.block.entity.NuclearFurnaceTileEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NuclearFurnaceSound extends MovingSound {
    private final NuclearFurnaceTileEntity furnace;
    private final int criticality;
    private int fade;

    public NuclearFurnaceSound(NuclearFurnaceTileEntity furnace) {
        super(soundForCriticality(furnace.getCriticality()), SoundCategory.BLOCKS);
        this.furnace = furnace;
        this.criticality = furnace.getCriticality();
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1F;
        this.pitch = 1.0F;
        updatePosition();
    }

    @Override
    public void update() {
        if (furnace.isInvalid() || furnace.getWorld() == null || furnace.getWorld() != Minecraft.getMinecraft().world) {
            donePlaying = true;
            return;
        }
        if ((furnace.isUndergoingFission() || criticality > 0) && criticality == furnace.getCriticality()) {
            if (fade > 0) {
                fade--;
            }
        } else {
            fade++;
        }
        updatePosition();
        volume = Math.max(0.0F, Math.min(1.0F, 1.0F - fade / 40.0F));
        pitch = 1.0F;
        if (fade > 40) {
            donePlaying = true;
        }
    }

    public boolean isSameBlockEntity(NuclearFurnaceTileEntity other) {
        return !furnace.isInvalid() && furnace.getPos().equals(other.getPos()) && furnace.getWorld() == other.getWorld() && criticality == other.getCriticality();
    }

    private void updatePosition() {
        xPosF = furnace.getPos().getX() + 1.0F;
        yPosF = furnace.getPos().getY() + 1.0F;
        zPosF = furnace.getPos().getZ() + 1.0F;
    }

    private static SoundEvent soundForCriticality(int criticality) {
        switch (criticality) {
            case 1:
                return ACSoundRegistry.NUCLEAR_FURNACE_ACTIVE_SUBCRITICAL;
            case 2:
                return ACSoundRegistry.NUCLEAR_FURNACE_ACTIVE_CRITICAL;
            case 3:
                return ACSoundRegistry.NUCLEAR_FURNACE_ACTIVE_SUPERCRITICAL;
            default:
                return ACSoundRegistry.NUCLEAR_FURNACE_ACTIVE;
        }
    }
}
