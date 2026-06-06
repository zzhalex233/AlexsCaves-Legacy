package com.zzhalex233.alexscaves.server.block.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class SirenLightTileEntity extends TileEntity implements ITickable {
    private float onProgress;
    private float prevOnProgress;
    private float sirenRotation;
    private float prevSirenRotation;
    private int color = -1;

    @Override
    public void update() {
        prevOnProgress = onProgress;
        prevSirenRotation = sirenRotation;
        boolean powered = world != null && world.getBlockState(pos).getValue(com.zzhalex233.alexscaves.server.block.SirenLightBlock.POWERED);
        if (powered && onProgress < 10.0F) {
            onProgress += 1.0F;
        } else if (!powered && onProgress > 0.0F) {
            onProgress -= 1.0F;
        }
        if (powered) {
            sirenRotation += onProgress * 2.0F + 0.25F;
        }
    }

    public float getOnProgress(float partialTicks) {
        return (prevOnProgress + (onProgress - prevOnProgress) * partialTicks) * 0.1F;
    }

    public float getSirenRotation(float partialTicks) {
        return prevSirenRotation + (sirenRotation - prevSirenRotation) * partialTicks;
    }

    public boolean setColor(int setTo) {
        if (color == setTo) {
            return false;
        }
        color = setTo;
        markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        return true;
    }

    public int getColor() {
        return color < 0 ? 0x00FF00 : color;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-3, -3, -3), pos.add(4, 4, 4));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Color", color);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        color = compound.getInteger("Color");
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
}
