package com.zzhalex233.alexscaves.server.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.entity.item.CinderBrickEntity;
import com.zzhalex233.alexscaves.server.entity.item.FallingFrostmintEntity;
import com.zzhalex233.alexscaves.server.entity.item.FallingGuanoEntity;
import com.zzhalex233.alexscaves.server.entity.item.FrostmintSpearEntity;
import com.zzhalex233.alexscaves.server.entity.item.GuanoEntity;
import com.zzhalex233.alexscaves.server.entity.item.SodaBottleRocketEntity;
import com.zzhalex233.alexscaves.server.entity.living.CaniacEntity;
import com.zzhalex233.alexscaves.server.entity.living.LanternfishEntity;
import com.zzhalex233.alexscaves.server.entity.living.SeaPigEntity;
import com.zzhalex233.alexscaves.server.entity.living.SweetishFishEntity;
import com.zzhalex233.alexscaves.server.entity.living.TripodfishEntity;
import com.zzhalex233.alexscaves.server.entity.living.TrilocarisEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ACEntityRegistry {
    private static final List<EntityTypeEntry> MUTABLE_ENTITIES = new ArrayList<>();
    private static int nextEntityId;

    public static final EntityTypeEntry TRILOCARIS = entity("trilocaris", TrilocarisEntity.class, 64, 3, true, 0x713E0D, 0x8B2010);
    public static final EntityTypeEntry LANTERNFISH = entity("lanternfish", LanternfishEntity.class, 64, 3, true, 0x182538, 0xECA500);
    public static final EntityTypeEntry TRIPODFISH = entity("tripodfish", TripodfishEntity.class, 64, 3, true, 0x584842, 0x9CC7BD);
    public static final EntityTypeEntry SEA_PIG = entity("sea_pig", SeaPigEntity.class, 64, 3, true, 0xFFA3B9, 0xF88672);
    public static final EntityTypeEntry SWEETISH_FISH = entity("sweetish_fish", SweetishFishEntity.class, 64, 3, true, 0xE9132C, 0xFF364D);
    public static final EntityTypeEntry CANIAC = entity("caniac", CaniacEntity.class, 64, 3, true, 0xF9F0FF, 0xFF3F56);
    public static final EntityTypeEntry CINDER_BRICK = entity("cinder_brick", CinderBrickEntity.class, 64, 1, true);
    public static final EntityTypeEntry GUANO = entity("guano", GuanoEntity.class, 64, 1, true);
    public static final EntityTypeEntry FALLING_GUANO = entity("falling_guano", FallingGuanoEntity.class, 64, 10, true);
    public static final EntityTypeEntry FALLING_FROSTMINT = entity("falling_frostmint", FallingFrostmintEntity.class, 64, 10, true);
    public static final EntityTypeEntry FROSTMINT_SPEAR = entity("frostmint_spear", FrostmintSpearEntity.class, 64, 1, true);
    public static final EntityTypeEntry SODA_BOTTLE_ROCKET = entity("soda_bottle_rocket", SodaBottleRocketEntity.class, 64, 1, true);

    public static final List<EntityTypeEntry> ENTITIES = Collections.unmodifiableList(MUTABLE_ENTITIES);

    public static void registerEntities(Object mod) {
        for (EntityTypeEntry entry : ENTITIES) {
            ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, entry.name());
            EntityRegistry.registerModEntity(id, entry.entityClass(), entry.name(), entry.entityId(), mod, entry.trackingRange(), entry.updateFrequency(), entry.sendsVelocityUpdates());
            if (entry.hasEgg()) {
                EntityRegistry.registerEgg(id, entry.primaryEggColor(), entry.secondaryEggColor());
            }
        }
    }

    private static EntityTypeEntry entity(String name, Class<? extends Entity> entityClass, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int primaryEggColor, int secondaryEggColor) {
        EntityTypeEntry entry = new EntityTypeEntry(name, entityClass, nextEntityId++, trackingRange, updateFrequency, sendsVelocityUpdates, primaryEggColor, secondaryEggColor);
        MUTABLE_ENTITIES.add(entry);
        return entry;
    }

    private static EntityTypeEntry entity(String name, Class<? extends Entity> entityClass, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
        EntityTypeEntry entry = new EntityTypeEntry(name, entityClass, nextEntityId++, trackingRange, updateFrequency, sendsVelocityUpdates, -1, -1);
        MUTABLE_ENTITIES.add(entry);
        return entry;
    }

    public static class EntityTypeEntry {
        private final String name;
        private final Class<? extends Entity> entityClass;
        private final int entityId;
        private final int trackingRange;
        private final int updateFrequency;
        private final boolean sendsVelocityUpdates;
        private final int primaryEggColor;
        private final int secondaryEggColor;

        private EntityTypeEntry(String name, Class<? extends Entity> entityClass, int entityId, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int primaryEggColor, int secondaryEggColor) {
            this.name = name;
            this.entityClass = entityClass;
            this.entityId = entityId;
            this.trackingRange = trackingRange;
            this.updateFrequency = updateFrequency;
            this.sendsVelocityUpdates = sendsVelocityUpdates;
            this.primaryEggColor = primaryEggColor;
            this.secondaryEggColor = secondaryEggColor;
        }

        public String name() {
            return name;
        }

        public Class<? extends Entity> entityClass() {
            return entityClass;
        }

        public int entityId() {
            return entityId;
        }

        public int trackingRange() {
            return trackingRange;
        }

        public int updateFrequency() {
            return updateFrequency;
        }

        public boolean sendsVelocityUpdates() {
            return sendsVelocityUpdates;
        }

        public boolean hasEgg() {
            return primaryEggColor >= 0 && secondaryEggColor >= 0;
        }

        public int primaryEggColor() {
            return primaryEggColor;
        }

        public int secondaryEggColor() {
            return secondaryEggColor;
        }
    }
}
