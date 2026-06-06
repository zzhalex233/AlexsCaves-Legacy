package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.entity.util.ACBoatType;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class AlexsCavesBoatEntity extends EntityBoat {
    private static final DataParameter<Integer> AC_BOAT_TYPE = EntityDataManager.createKey(AlexsCavesBoatEntity.class, DataSerializers.VARINT);

    public AlexsCavesBoatEntity(World world) {
        super(world);
    }

    public AlexsCavesBoatEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(AC_BOAT_TYPE, ACBoatType.PEWEN.ordinal());
    }

    public ACBoatType getACBoatType() {
        return ACBoatType.byId(dataManager.get(AC_BOAT_TYPE));
    }

    public void setACBoatType(ACBoatType type) {
        dataManager.set(AC_BOAT_TYPE, (type == null ? ACBoatType.PEWEN : type).ordinal());
    }

    @Override
    public Item getItemBoat() {
        return getACBoatType().getDrop();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setString("ACBoatType", getACBoatType().getName());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("ACBoatType")) {
            setACBoatType(ACBoatType.byName(compound.getString("ACBoatType")));
        }
    }
}
