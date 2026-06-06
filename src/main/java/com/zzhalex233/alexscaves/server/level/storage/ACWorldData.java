package com.zzhalex233.alexscaves.server.level.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ACWorldData extends WorldSavedData {
    private static final String IDENTIFIER = "alexscaves_world_data";

    private final Map<UUID, Integer> deepOneReputations = new HashMap<>();
    private boolean primordialBossDefeatedOnce;
    private long firstPrimordialBossDefeatTimestamp = -1L;

    public ACWorldData() {
        super(IDENTIFIER);
    }

    public ACWorldData(String name) {
        super(name);
    }

    @Nullable
    public static ACWorldData get(World world) {
        if (world == null || world.isRemote) {
            return null;
        }
        MapStorage storage = world.getMapStorage();
        ACWorldData data = (ACWorldData) storage.getOrLoadData(ACWorldData.class, IDENTIFIER);
        if (data == null) {
            data = new ACWorldData();
            storage.setData(IDENTIFIER, data);
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        deepOneReputations.clear();
        primordialBossDefeatedOnce = compound.getBoolean("PrimordialBossDefeatedOnce");
        firstPrimordialBossDefeatTimestamp = compound.getLong("FirstPrimordialBossDefeatTimestamp");
        NBTTagList list = compound.getTagList("DeepOneReputations", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            if (tag.hasUniqueId("UUID")) {
                deepOneReputations.put(tag.getUniqueId("UUID"), tag.getInteger("Reputation"));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("PrimordialBossDefeatedOnce", primordialBossDefeatedOnce);
        compound.setLong("FirstPrimordialBossDefeatTimestamp", firstPrimordialBossDefeatTimestamp);
        if (!deepOneReputations.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (Map.Entry<UUID, Integer> entry : deepOneReputations.entrySet()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setUniqueId("UUID", entry.getKey());
                tag.setInteger("Reputation", entry.getValue());
                list.appendTag(tag);
            }
            compound.setTag("DeepOneReputations", list);
        }
        return compound;
    }

    public int getDeepOneReputation(@Nullable UUID uuid) {
        return uuid == null ? 0 : deepOneReputations.getOrDefault(uuid, 0);
    }

    public void setDeepOneReputation(UUID uuid, int reputation) {
        deepOneReputations.put(uuid, MathHelper.clamp(reputation, -100, 100));
        markDirty();
    }

    public boolean isPrimordialBossDefeatedOnce() {
        return primordialBossDefeatedOnce;
    }

    public void setPrimordialBossDefeatedOnce(boolean defeatedOnce) {
        this.primordialBossDefeatedOnce = defeatedOnce;
        markDirty();
    }

    public long getFirstPrimordialBossDefeatTimestamp() {
        return firstPrimordialBossDefeatTimestamp;
    }

    public void setFirstPrimordialBossDefeatTimestamp(long time) {
        this.firstPrimordialBossDefeatTimestamp = time;
        markDirty();
    }
}
