package com.zzhalex233.alexscaves.server.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GossamerWormPartEntity extends Entity {
    private static final DataParameter<Integer> PARENT_ID = EntityDataManager.createKey(GossamerWormPartEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CONNECTED_ID = EntityDataManager.createKey(GossamerWormPartEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> PART_INDEX = EntityDataManager.createKey(GossamerWormPartEntity.class, DataSerializers.VARINT);

    public GossamerWormPartEntity(World world) {
        super(world);
        setSize(1.0F, 0.5F);
        noClip = true;
    }

    public GossamerWormPartEntity(World world, GossamerWormEntity parent, Entity connectedTo, int index, float sizeXZ, float sizeY) {
        this(world);
        setParentId(parent.getEntityId());
        setConnectedId(connectedTo.getEntityId());
        setPartIndex(index);
        setSize(sizeXZ, sizeY);
    }

    @Override
    protected void entityInit() {
        dataManager.register(PARENT_ID, -1);
        dataManager.register(CONNECTED_ID, -1);
        dataManager.register(PART_INDEX, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        noClip = true;
        GossamerWormEntity parent = getParent();
        Entity connected = getConnectedTo();
        if (!world.isRemote && ticksExisted > 5 && (parent == null || parent.isDead || connected == null || connected.isDead)) {
            setDead();
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        GossamerWormEntity parent = getParent();
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
        setConnectedId(compound.getInteger("ConnectedId"));
        setPartIndex(compound.getInteger("PartIndex"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("ParentId", getParentId());
        compound.setInteger("ConnectedId", getConnectedId());
        compound.setInteger("PartIndex", getPartIndex());
    }

    public void setToTransformation(Entity connectedTo, Vec3d offset, float pitch, float yaw) {
        Vec3d center = connectedTo.getPositionVector().add(0.0D, connectedTo.height * 0.5D, 0.0D);
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

    public GossamerWormEntity getParent() {
        Entity entity = getParentId() == -1 ? null : world.getEntityByID(getParentId());
        return entity instanceof GossamerWormEntity ? (GossamerWormEntity) entity : null;
    }

    public Entity getConnectedTo() {
        return getConnectedId() == -1 ? null : world.getEntityByID(getConnectedId());
    }

    public int getParentId() {
        return dataManager.get(PARENT_ID);
    }

    public void setParentId(int id) {
        dataManager.set(PARENT_ID, id);
    }

    public int getConnectedId() {
        return dataManager.get(CONNECTED_ID);
    }

    public void setConnectedId(int id) {
        dataManager.set(CONNECTED_ID, id);
    }

    public int getPartIndex() {
        return dataManager.get(PART_INDEX);
    }

    public void setPartIndex(int index) {
        dataManager.set(PART_INDEX, index);
    }
}
