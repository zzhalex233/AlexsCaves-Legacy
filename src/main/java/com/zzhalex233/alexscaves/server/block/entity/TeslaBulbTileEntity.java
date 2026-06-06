package com.zzhalex233.alexscaves.server.block.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.message.TeslaBulbLightningMessage;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class TeslaBulbTileEntity extends TileEntity implements ITickable {
    public int age;
    private Vec3d lightningPos;
    private int strikeTime;
    private boolean exploding;
    private float explodeProgress;
    private float prevExplodeProgress;

    @Override
    public void update() {
        age++;
        prevExplodeProgress = explodeProgress;
        if (exploding && explodeProgress < 10.0F) {
            explodeProgress += 0.5F;
        } else if (!exploding && explodeProgress > 0.0F) {
            explodeProgress -= 0.5F;
        }
        if (exploding) {
            tickExploding();
            return;
        }
        if (!world.isRemote) {
            tickStriking();
        }
    }

    private void tickExploding() {
        if (explodeProgress >= 10.0F) {
            world.setBlockToAir(pos);
            if (!world.isRemote) {
                world.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 7.0F, false);
            }
        } else if (world.isRemote) {
            Vec3d from = center();
            for (int i = 0; i < 3 + world.rand.nextInt(3); i++) {
                AlexsCaves.PROXY.spawnTeslaBulbLightning(world, from, findStrikePos());
            }
        } else if (explodeProgress % 1.0F == 0.0F) {
            shock(new AxisAlignedBB(pos.add(-5, -5, -5), pos.add(6, 6, 6)));
        }
    }

    private void tickStriking() {
        if (strikeTime > 0) {
            strikeTime--;
            lightningPos = null;
        } else if (strikeTime < 0) {
            strikeTime++;
            if (lightningPos != null && strikeTime == -1) {
                shock(new AxisAlignedBB(lightningPos.x - 1.0D, lightningPos.y - 1.0D, lightningPos.z - 1.0D, lightningPos.x + 1.0D, lightningPos.y + 1.0D, lightningPos.z + 1.0D));
            }
        } else {
            strikeTime = 15;
            if (world.rand.nextFloat() < 0.4F) {
                lightningPos = findStrikePos();
                strikeTime = -5;
                Vec3d from = center();
                AlexsCaves.NETWORK_WRAPPER.sendToAllAround(new TeslaBulbLightningMessage(from, lightningPos), new TargetPoint(world.provider.getDimension(), from.x, from.y, from.z, 64.0D));
            }
        }
    }

    private void shock(AxisAlignedBB box) {
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
            entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 1.0F);
            entity.extinguish();
        }
    }

    private Vec3d findStrikePos() {
        return center().add(5 - world.rand.nextInt(10), 5 - world.rand.nextInt(10), 5 - world.rand.nextInt(10));
    }

    private Vec3d center() {
        return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }

    public float getExplodeProgress(float partialTicks) {
        return prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTicks * 0.1F;
    }

    public void explode() {
        if (exploding) {
            return;
        }
        exploding = true;
        markDirty();
        if (world != null) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        BlockPos blockPos = getPos();
        return new AxisAlignedBB(blockPos.add(-1, -1, -1), blockPos.add(2, 2, 2));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("Exploding", exploding);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        exploding = compound.getBoolean("Exploding");
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
