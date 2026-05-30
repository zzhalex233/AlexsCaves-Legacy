package com.zzhalex233.alexscaves.server.block.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;

public class MusselTileEntity extends TileEntity {
    private int mussels = 1;

    public int getMussels() {
        return mussels;
    }

    public void setMussels(int mussels) {
        this.mussels = MathHelper.clamp(mussels, 1, 5);
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Mussels", mussels);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        mussels = MathHelper.clamp(compound.getInteger("Mussels"), 1, 5);
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
        if (world != null) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}
