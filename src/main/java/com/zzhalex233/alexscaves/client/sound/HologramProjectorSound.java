package com.zzhalex233.alexscaves.client.sound;

import com.zzhalex233.alexscaves.server.block.entity.HologramProjectorTileEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HologramProjectorSound extends MovingSound {
    private final HologramProjectorTileEntity hologramProjector;

    public HologramProjectorSound(HologramProjectorTileEntity hologramProjector) {
        super(ACSoundRegistry.HOLOGRAM_LOOP, SoundCategory.BLOCKS);
        this.hologramProjector = hologramProjector;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1F;
        this.pitch = 1.0F;
        this.xPosF = hologramProjector.getPos().getX() + 0.5F;
        this.yPosF = hologramProjector.getPos().getY() + 0.5F;
        this.zPosF = hologramProjector.getPos().getZ() + 0.5F;
    }

    @Override
    public void update() {
        if (hologramProjector.isInvalid() || hologramProjector.getWorld() == null || hologramProjector.getWorld() != Minecraft.getMinecraft().world || !canPlay()) {
            donePlaying = true;
            return;
        }
        xPosF = hologramProjector.getPos().getX() + 0.5F;
        yPosF = hologramProjector.getPos().getY() + 0.5F;
        zPosF = hologramProjector.getPos().getZ() + 0.5F;
        volume = hologramProjector.getSwitchAmount(1.0F) * 0.5F;
        pitch = 1.0F;
    }

    private boolean canPlay() {
        return hologramProjector.getSwitchAmount(1.0F) > 0.0F && (hologramProjector.isPlayerRender() || hologramProjector.getDisplayEntity() != null);
    }

    public boolean isSameBlockEntity(HologramProjectorTileEntity tileEntity) {
        return hologramProjector == tileEntity || hologramProjector.getPos().equals(tileEntity.getPos()) && hologramProjector.getWorld() == tileEntity.getWorld();
    }
}
