package com.zzhalex233.alexscaves.server.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class HullbreakerPartEntity extends Entity {
    private static final DataParameter<Integer> PARENT_ID = EntityDataManager.createKey(HullbreakerPartEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> PART_INDEX = EntityDataManager.createKey(HullbreakerPartEntity.class, DataSerializers.VARINT);

    public HullbreakerPartEntity(World world) {
        super(world);
        setSize(2.0F, 1.5F);
        noClip = true;
    }

    public HullbreakerPartEntity(World world, HullbreakerEntity parent, int index, float width, float height) {
        this(world);
        setParentId(parent.getEntityId());
        setPartIndex(index);
        setSize(width, height);
    }

    @Override
    protected void entityInit() {
        dataManager.register(PARENT_ID, -1);
        dataManager.register(PART_INDEX, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        noClip = true;
        HullbreakerEntity parent = getParent();
        if (!world.isRemote && ticksExisted > 5 && (parent == null || parent.isDead)) {
            setDead();
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        HullbreakerEntity parent = getParent();
        return parent != null && !parent.isDead && parent.attackEntityFrom(source, source.isProjectile() ? amount * 0.65F : amount);
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
        setPartIndex(compound.getInteger("PartIndex"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("ParentId", getParentId());
        compound.setInteger("PartIndex", getPartIndex());
    }

    public HullbreakerEntity getParent() {
        Entity entity = getParentId() == -1 ? null : world.getEntityByID(getParentId());
        return entity instanceof HullbreakerEntity ? (HullbreakerEntity) entity : null;
    }

    public int getParentId() {
        return dataManager.get(PARENT_ID);
    }

    public void setParentId(int id) {
        dataManager.set(PARENT_ID, id);
    }

    public int getPartIndex() {
        return dataManager.get(PART_INDEX);
    }

    public void setPartIndex(int index) {
        dataManager.set(PART_INDEX, index);
    }
}
