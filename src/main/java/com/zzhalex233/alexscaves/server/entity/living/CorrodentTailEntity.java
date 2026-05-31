package com.zzhalex233.alexscaves.server.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CorrodentTailEntity extends Entity {
    private static final DataParameter<Integer> PARENT_ID = EntityDataManager.createKey(CorrodentTailEntity.class, DataSerializers.VARINT);

    public CorrodentTailEntity(World world) {
        super(world);
        setSize(0.9F, 0.9F);
        noClip = true;
    }

    public CorrodentTailEntity(World world, CorrodentEntity parent) {
        this(world);
        setParentId(parent.getEntityId());
    }

    @Override
    protected void entityInit() {
        dataManager.register(PARENT_ID, -1);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        noClip = true;
        CorrodentEntity parent = getParent();
        if (!world.isRemote && ticksExisted > 5 && (parent == null || parent.isDead)) {
            setDead();
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        CorrodentEntity parent = getParent();
        return parent != null && !parent.isDead && parent.attackEntityFrom(source, amount);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        setParentId(compound.getInteger("ParentId"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("ParentId", getParentId());
    }

    public void setToTransformation(CorrodentEntity parent, Vec3d offset, float pitch, float yaw) {
        Vec3d center = parent.getPositionVector().add(0.0D, parent.height * 0.5D, 0.0D);
        Vec3d transformed = rotate(offset, pitch, yaw).add(center);
        setPosition(transformed.x, transformed.y - height * 0.5D, transformed.z);
    }

    private Vec3d rotate(Vec3d offset, float pitch, float yaw) {
        double yawRad = -yaw * 0.017453292D;
        double pitchRad = -pitch * 0.017453292D;
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);
        double cosPitch = Math.cos(pitchRad);
        double sinPitch = Math.sin(pitchRad);
        double zPitch = offset.z * cosPitch - offset.y * sinPitch;
        double yPitch = offset.y * cosPitch + offset.z * sinPitch;
        return new Vec3d(offset.x * cosYaw - zPitch * sinYaw, yPitch, zPitch * cosYaw + offset.x * sinYaw);
    }

    public CorrodentEntity getParent() {
        Entity entity = getParentId() == -1 ? null : world.getEntityByID(getParentId());
        return entity instanceof CorrodentEntity ? (CorrodentEntity) entity : null;
    }

    public int getParentId() {
        return dataManager.get(PARENT_ID);
    }

    public void setParentId(int id) {
        dataManager.set(PARENT_ID, id);
    }
}
