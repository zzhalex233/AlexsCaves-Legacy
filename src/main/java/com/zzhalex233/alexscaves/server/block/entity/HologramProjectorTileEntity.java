package com.zzhalex233.alexscaves.server.block.entity;

import java.util.UUID;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class HologramProjectorTileEntity extends TileEntity implements ITickable {
    public int tickCount;
    private NBTTagCompound entityTag;
    private Entity displayEntity;
    private UUID displayUUID;
    private UUID prevDisplayUUID;
    private float prevSwitchProgress;
    private float switchProgress;
    private float previousRotation;
    private float rotation;

    @Override
    public void update() {
        tickCount++;
        prevSwitchProgress = switchProgress;
        previousRotation = rotation;
        UUID current = getDisplayUUID();
        if (current != null && !current.equals(displayUUID)) {
            displayUUID = current;
            switchProgress = 0.0F;
        }
        if (displayUUID != null && !displayUUID.equals(prevDisplayUUID)) {
            if (switchProgress < 10.0F) {
                if (switchProgress == 0.0F && !world.isRemote) {
                    world.playSound(null, pos, ACSoundRegistry.HOLOGRAM_STOP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                switchProgress++;
            } else {
                prevDisplayUUID = displayUUID;
                markUpdated();
            }
        } else if (displayUUID != null && switchProgress < 10.0F) {
            switchProgress++;
        }
        int redstoneSignal = world.getStrongPower(pos);
        if (redstoneSignal > 0) {
            rotation = MathHelper.wrapDegrees(rotation + redstoneSignal);
        }
        if (world.isRemote) {
            updateLoopSound();
        }
    }

    private void updateLoopSound() {
        if (getSwitchAmount(1.0F) > 0.0F) {
            com.zzhalex233.alexscaves.AlexsCaves.PROXY.playHologramProjectorSound(this);
        }
    }

    public void setEntity(NBTTagCompound entityTag, float rotation) {
        this.entityTag = entityTag.copy();
        this.rotation = rotation;
        displayEntity = null;
        displayUUID = null;
        prevDisplayUUID = null;
        switchProgress = 0.0F;
        markUpdated();
    }

    public boolean isPlayerRender() {
        return entityTag != null && "minecraft:player".equals(entityTag.getString("id"));
    }

    public UUID getPlayerUUID() {
        return isPlayerRender() && entityTag.hasUniqueId("UUID") ? entityTag.getUniqueId("UUID") : null;
    }

    public Entity getDisplayEntity() {
        if (entityTag == null) {
            return null;
        }
        if (isPlayerRender()) {
            UUID uuid = getPlayerUUID();
            return uuid == null || world == null ? null : world.getPlayerEntityByUUID(uuid);
        }
        if (displayEntity == null && world != null) {
            displayEntity = EntityList.createEntityFromNBT(entityTag.copy(), world);
        }
        return displayEntity;
    }

    private UUID getDisplayUUID() {
        if (isPlayerRender()) {
            return getPlayerUUID();
        }
        Entity entity = getDisplayEntity();
        return entity == null ? null : entity.getUniqueID();
    }

    public float getSwitchAmount(float partialTicks) {
        return (prevSwitchProgress + (switchProgress - prevSwitchProgress) * partialTicks) * 0.1F;
    }

    public float getRotation(float partialTicks) {
        return previousRotation + (rotation - previousRotation) * partialTicks;
    }

    public boolean hasHologram() {
        return entityTag != null;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        Entity entity = getDisplayEntity();
        double size = entity == null ? 2.0D : Math.max(2.0D, Math.max(entity.width, entity.height) + 1.0D);
        return new AxisAlignedBB(pos).grow(size, size, size);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (entityTag != null) {
            compound.setTag("EntityTag", entityTag);
        }
        compound.setFloat("Rotation", rotation);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        entityTag = compound.hasKey("EntityTag", 10) ? compound.getCompoundTag("EntityTag").copy() : null;
        rotation = compound.getFloat("Rotation");
        displayEntity = null;
        displayUUID = null;
        prevDisplayUUID = null;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    private void markUpdated() {
        markDirty();
        if (world != null) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }
}
