package com.zzhalex233.alexscaves.server.entity.item;

import java.util.List;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.AcidBlock;
import com.zzhalex233.alexscaves.server.entity.living.BrainiacEntity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThrownWasteDrumEntity extends Entity {
    private static final DataParameter<Integer> ON_GROUND_FOR = EntityDataManager.createKey(ThrownWasteDrumEntity.class, DataSerializers.VARINT);
    public static final int MAX_TIME = 20;
    private BlockPos removeWasteAt;

    public ThrownWasteDrumEntity(World world) {
        super(world);
        setSize(0.98F, 0.98F);
    }

    public ThrownWasteDrumEntity(World world, double x, double y, double z) {
        this(world);
        setPosition(x, y, z);
    }

    @Override
    protected void entityInit() {
        dataManager.register(ON_GROUND_FOR, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (!hasNoGravity()) {
            motionY -= 0.08D;
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.9D;
        motionY *= 0.9D;
        motionZ *= 0.9D;
        if (onGround) {
            setOnGroundFor(getOnGroundFor() + 1);
            motionX *= 0.7D;
            motionY *= -0.7D;
            motionZ *= 0.7D;
        }
        if (motionX * motionX + motionY * motionY + motionZ * motionZ > 0.0009D) {
            boolean hit = false;
            AxisAlignedBB box = getEntityBoundingBox();
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, box, entity -> !(entity instanceof BrainiacEntity));
            for (EntityLivingBase entity : entities) {
                hit |= entity.attackEntityFrom(AcidBlock.ACID, 2.0F);
            }
            if (hit) {
                motionX *= 0.2D;
                motionZ *= 0.2D;
            }
        }
        if (!world.isRemote && getOnGroundFor() >= MAX_TIME) {
            if (getOnGroundFor() == MAX_TIME) {
                removeWasteAt = findAcidPlacement();
                if (world.isAirBlock(removeWasteAt)) {
                    world.setBlockState(removeWasteAt, ACBlockRegistry.ACID.block().getDefaultState(), 3);
                }
            }
            if (getOnGroundFor() >= MAX_TIME + 15) {
                if (removeWasteAt != null && world.getBlockState(removeWasteAt).getBlock() == ACBlockRegistry.ACID.block()) {
                    world.setBlockState(removeWasteAt, Blocks.AIR.getDefaultState(), 3);
                }
                setDead();
            }
        }
    }

    private BlockPos findAcidPlacement() {
        BlockPos landed = new BlockPos(this);
        while (landed.getY() < world.getHeight() - 1) {
            Material material = world.getBlockState(landed).getMaterial();
            if (material == Material.AIR || world.getBlockState(landed).getBlock() == ACBlockRegistry.ACID.block()) {
                return landed;
            }
            landed = landed.up();
        }
        return landed;
    }

    public int getOnGroundFor() {
        return dataManager.get(ON_GROUND_FOR);
    }

    public void setOnGroundFor(int time) {
        dataManager.set(ON_GROUND_FOR, time);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        setOnGroundFor(compound.getInteger("OnGroundFor"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("OnGroundFor", getOnGroundFor());
    }
}
