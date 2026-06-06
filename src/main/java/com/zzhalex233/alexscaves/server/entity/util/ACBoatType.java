package com.zzhalex233.alexscaves.server.entity.util;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public enum ACBoatType {
    PEWEN("pewen"),
    THORNWOOD("thornwood");

    private final String name;

    ACBoatType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Block getPlanks() {
        return this == THORNWOOD ? ACBlockRegistry.THORNWOOD_PLANKS.block() : ACBlockRegistry.PEWEN_PLANKS.block();
    }

    public Item getDrop() {
        return this == THORNWOOD ? ACItemRegistry.THORNWOOD_BOAT.item() : ACItemRegistry.PEWEN_BOAT.item();
    }

    public static ACBoatType byName(String name) {
        for (ACBoatType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return PEWEN;
    }

    public static ACBoatType byId(int id) {
        ACBoatType[] values = values();
        return values[id < 0 || id >= values.length ? 0 : id];
    }
}
