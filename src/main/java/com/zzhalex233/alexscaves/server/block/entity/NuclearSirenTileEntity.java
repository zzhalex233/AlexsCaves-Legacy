package com.zzhalex233.alexscaves.server.block.entity;

import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.NuclearSirenBlock;
import com.zzhalex233.alexscaves.server.entity.living.NucleeperEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class NuclearSirenTileEntity extends TileEntity implements ITickable {
    private float volumeProgress;
    private float prevVolumeProgress;
    private int age;
    private int trackedBombId = -1;
    private BlockPos trackedFurnace;

    @Override
    public void update() {
        if (world == null) {
            return;
        }
        prevVolumeProgress = volumeProgress;
        age++;
        IBlockState state = world.getBlockState(pos);
        if (!world.isRemote && age % 20 == 0) {
            refreshTrackedThreats();
        }
        boolean active = isActivated(state);
        if (active && volumeProgress < 10.0F) {
            volumeProgress += 0.5F;
        } else if (!active && volumeProgress > 0.0F) {
            volumeProgress -= 0.5F;
        }
        if (world.isRemote && active) {
            AlexsCaves.PROXY.playNuclearSirenSound(this);
            int pulse = age % 18;
            if (pulse >= 9 && pulse % 3 == 0) {
                spawnSonarParticles();
            }
        }
    }

    private void spawnSonarParticles() {
        Vec3d center = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D);
        AlexsCaves.PROXY.spawnNuclearSirenSonar(world, center.add(0.5D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D));
        AlexsCaves.PROXY.spawnNuclearSirenSonar(world, center.add(-0.5D, 0.0D, 0.0D), new Vec3d(-1.0D, 0.0D, 0.0D));
        AlexsCaves.PROXY.spawnNuclearSirenSonar(world, center.add(0.0D, 0.0D, 0.5D), new Vec3d(0.0D, 0.0D, 1.0D));
        AlexsCaves.PROXY.spawnNuclearSirenSonar(world, center.add(0.0D, 0.0D, -0.5D), new Vec3d(0.0D, 0.0D, -1.0D));
    }

    private void refreshTrackedThreats() {
        int previousBombId = trackedBombId;
        BlockPos previousFurnace = trackedFurnace;
        trackedBombId = findNearestTriggeredNucleeperId();
        trackedFurnace = findNearestCriticalFurnace();
        if (previousBombId != trackedBombId || (previousFurnace == null ? trackedFurnace != null : !previousFurnace.equals(trackedFurnace))) {
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            world.updateComparatorOutputLevel(pos, world.getBlockState(pos).getBlock());
        }
    }

    private int findNearestTriggeredNucleeperId() {
        List<NucleeperEntity> nucleepers = world.getEntitiesWithinAABB(NucleeperEntity.class, new AxisAlignedBB(pos).grow(128.0D), nucleeper -> nucleeper != null && nucleeper.isEntityAlive() && (nucleeper.isTriggered() || nucleeper.isExploding()));
        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (NucleeperEntity nucleeper : nucleepers) {
            double distance = nucleeper.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            if (distance < nearestDistance) {
                nearest = nucleeper;
                nearestDistance = distance;
            }
        }
        return nearest == null ? -1 : nearest.getEntityId();
    }

    private BlockPos findNearestCriticalFurnace() {
        BlockPos nearest = null;
        double nearestDistance = 128.0D * 128.0D;
        for (TileEntity tile : world.loadedTileEntityList) {
            if (tile instanceof NuclearFurnaceTileEntity && ((NuclearFurnaceTileEntity) tile).getCriticality() >= 2) {
                double distance = tile.getPos().distanceSq(pos);
                if (distance <= nearestDistance) {
                    nearest = tile.getPos();
                    nearestDistance = distance;
                }
            }
        }
        return nearest;
    }

    public boolean isActivated(IBlockState state) {
        return state.getBlock() instanceof NuclearSirenBlock && state.getValue(NuclearSirenBlock.POWERED) || trackedBombId != -1 || isTrackedFurnaceCritical();
    }

    private boolean isTrackedFurnaceCritical() {
        TileEntity tile = trackedFurnace == null ? null : world.getTileEntity(trackedFurnace);
        return tile instanceof NuclearFurnaceTileEntity && ((NuclearFurnaceTileEntity) tile).getCriticality() >= 2;
    }

    public float getVolume(float partialTicks) {
        return (prevVolumeProgress + (volumeProgress - prevVolumeProgress) * partialTicks) * 0.1F;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("BombID", trackedBombId);
        if (trackedFurnace != null) {
            compound.setInteger("NearestFurnaceX", trackedFurnace.getX());
            compound.setInteger("NearestFurnaceY", trackedFurnace.getY());
            compound.setInteger("NearestFurnaceZ", trackedFurnace.getZ());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        trackedBombId = compound.getInteger("BombID");
        trackedFurnace = compound.hasKey("NearestFurnaceX") ? new BlockPos(compound.getInteger("NearestFurnaceX"), compound.getInteger("NearestFurnaceY"), compound.getInteger("NearestFurnaceZ")) : null;
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
