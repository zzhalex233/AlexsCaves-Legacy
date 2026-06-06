package com.zzhalex233.alexscaves.server.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.entity.item.CinderBrickEntity;
import com.zzhalex233.alexscaves.server.entity.item.BurrowingArrowEntity;
import com.zzhalex233.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.zzhalex233.alexscaves.server.entity.item.DarkArrowEntity;
import com.zzhalex233.alexscaves.server.entity.item.DepthChargeEntity;
import com.zzhalex233.alexscaves.server.entity.item.DesolateDaggerEntity;
import com.zzhalex233.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.zzhalex233.alexscaves.server.entity.item.ExtinctionSpearEntity;
import com.zzhalex233.alexscaves.server.entity.item.FallingFrostmintEntity;
import com.zzhalex233.alexscaves.server.entity.item.FallingGuanoEntity;
import com.zzhalex233.alexscaves.server.entity.item.FloaterEntity;
import com.zzhalex233.alexscaves.server.entity.item.FrostmintSpearEntity;
import com.zzhalex233.alexscaves.server.entity.item.GumballEntity;
import com.zzhalex233.alexscaves.server.entity.item.GuanoEntity;
import com.zzhalex233.alexscaves.server.entity.item.InkBombEntity;
import com.zzhalex233.alexscaves.server.entity.item.LimestoneSpearEntity;
import com.zzhalex233.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.zzhalex233.alexscaves.server.entity.item.AlexsCavesBoatEntity;
import com.zzhalex233.alexscaves.server.entity.item.MeltedCaramelEntity;
import com.zzhalex233.alexscaves.server.entity.item.MineGuardianAnchorEntity;
import com.zzhalex233.alexscaves.server.entity.item.MovingMetalBlockEntity;
import com.zzhalex233.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.zzhalex233.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.zzhalex233.alexscaves.server.entity.item.SeekingArrowEntity;
import com.zzhalex233.alexscaves.server.entity.item.SodaBottleRocketEntity;
import com.zzhalex233.alexscaves.server.entity.item.SpinningPeppermintEntity;
import com.zzhalex233.alexscaves.server.entity.item.SubmarineEntity;
import com.zzhalex233.alexscaves.server.entity.item.SugarStaffHexEntity;
import com.zzhalex233.alexscaves.server.entity.item.ThrownIceCreamScoopEntity;
import com.zzhalex233.alexscaves.server.entity.item.ThrownWasteDrumEntity;
import com.zzhalex233.alexscaves.server.entity.item.WaterBoltEntity;
import com.zzhalex233.alexscaves.server.entity.item.WaveEntity;
import com.zzhalex233.alexscaves.server.entity.living.BrainiacEntity;
import com.zzhalex233.alexscaves.server.entity.living.BoundroidEntity;
import com.zzhalex233.alexscaves.server.entity.living.BoundroidWinchEntity;
import com.zzhalex233.alexscaves.server.entity.living.CaniacEntity;
import com.zzhalex233.alexscaves.server.entity.living.CandicornEntity;
import com.zzhalex233.alexscaves.server.entity.living.CaramelCubeEntity;
import com.zzhalex233.alexscaves.server.entity.living.CorrodentEntity;
import com.zzhalex233.alexscaves.server.entity.living.CorrodentTailEntity;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneEntity;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneKnightEntity;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneMageEntity;
import com.zzhalex233.alexscaves.server.entity.living.FerrouslimeEntity;
import com.zzhalex233.alexscaves.server.entity.living.ForsakenEntity;
import com.zzhalex233.alexscaves.server.entity.living.GammaroachEntity;
import com.zzhalex233.alexscaves.server.entity.living.GloomothEntity;
import com.zzhalex233.alexscaves.server.entity.living.GossamerWormEntity;
import com.zzhalex233.alexscaves.server.entity.living.GossamerWormPartEntity;
import com.zzhalex233.alexscaves.server.entity.living.GingerbreadManEntity;
import com.zzhalex233.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.zzhalex233.alexscaves.server.entity.living.GummyBearEntity;
import com.zzhalex233.alexscaves.server.entity.living.GumbeeperEntity;
import com.zzhalex233.alexscaves.server.entity.living.GumWormEntity;
import com.zzhalex233.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.zzhalex233.alexscaves.server.entity.living.HullbreakerEntity;
import com.zzhalex233.alexscaves.server.entity.living.HullbreakerPartEntity;
import com.zzhalex233.alexscaves.server.entity.living.LanternfishEntity;
import com.zzhalex233.alexscaves.server.entity.living.MagnetronEntity;
import com.zzhalex233.alexscaves.server.entity.living.MagnetronPartEntity;
import com.zzhalex233.alexscaves.server.entity.living.MineGuardianEntity;
import com.zzhalex233.alexscaves.server.entity.living.NucleeperEntity;
import com.zzhalex233.alexscaves.server.entity.living.NotorEntity;
import com.zzhalex233.alexscaves.server.entity.living.RadgillEntity;
import com.zzhalex233.alexscaves.server.entity.living.RaycatEntity;
import com.zzhalex233.alexscaves.server.entity.living.RelicheirusEntity;
import com.zzhalex233.alexscaves.server.entity.living.SeaPigEntity;
import com.zzhalex233.alexscaves.server.entity.living.SubterranodonEntity;
import com.zzhalex233.alexscaves.server.entity.living.SweetishFishEntity;
import com.zzhalex233.alexscaves.server.entity.living.TeletorEntity;
import com.zzhalex233.alexscaves.server.entity.living.TripodfishEntity;
import com.zzhalex233.alexscaves.server.entity.living.TrilocarisEntity;
import com.zzhalex233.alexscaves.server.entity.living.UnderzealotEntity;
import com.zzhalex233.alexscaves.server.entity.living.VallumraptorEntity;
import com.zzhalex233.alexscaves.server.entity.living.VesperEntity;
import com.zzhalex233.alexscaves.server.entity.living.WatcherEntity;

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
    public static final EntityTypeEntry GOSSAMER_WORM = entity("gossamer_worm", GossamerWormEntity.class, 64, 3, true, 0xE7F2FF, 0x7DD5FF);
    public static final EntityTypeEntry GOSSAMER_WORM_PART = entity("gossamer_worm_part", GossamerWormPartEntity.class, 64, 3, true);
    public static final EntityTypeEntry SWEETISH_FISH = entity("sweetish_fish", SweetishFishEntity.class, 64, 3, true, 0xE9132C, 0xFF364D);
    public static final EntityTypeEntry SEEKING_ARROW = entity("seeking_arrow", SeekingArrowEntity.class, 64, 1, true);
    public static final EntityTypeEntry GROTTOCERATOPS = entity("grottoceratops", GrottoceratopsEntity.class, 96, 3, true, 0x6B6F4C, 0xD1B36A);
    public static final EntityTypeEntry RELICHEIRUS = entity("relicheirus", RelicheirusEntity.class, 96, 3, true, 0x776349, 0xD7BA7E);
    public static final EntityTypeEntry SUBTERRANODON = entity("subterranodon", SubterranodonEntity.class, 96, 3, true, 0x3B2B1F, 0xB58E61);
    public static final EntityTypeEntry VALLUMRAPTOR = entity("vallumraptor", VallumraptorEntity.class, 96, 3, true, 0x22389A, 0xEEE5AB);
    public static final EntityTypeEntry BOAT = entity("boat", AlexsCavesBoatEntity.class, 80, 3, true);
    public static final EntityTypeEntry DEEP_ONE = entity("deep_one", DeepOneEntity.class, 64, 3, true, 0x243C4B, 0x77C1BC);
    public static final EntityTypeEntry DEEP_ONE_KNIGHT = entity("deep_one_knight", DeepOneKnightEntity.class, 64, 3, true, 0x1A2D3A, 0xB7C4C6);
    public static final EntityTypeEntry DEEP_ONE_MAGE = entity("deep_one_mage", DeepOneMageEntity.class, 64, 3, true, 0x2B2746, 0x6ED4DF);
    public static final EntityTypeEntry HULLBREAKER = entity("hullbreaker", HullbreakerEntity.class, 128, 3, true, 0x1B3544, 0x7BE8E7);
    public static final EntityTypeEntry HULLBREAKER_PART = entity("hullbreaker_part", HullbreakerPartEntity.class, 128, 3, true);
    public static final EntityTypeEntry MINE_GUARDIAN = entity("mine_guardian", MineGuardianEntity.class, 64, 3, true, 0x4F4B43, 0xFF1A1A);
    public static final EntityTypeEntry MINE_GUARDIAN_ANCHOR = entity("mine_guardian_anchor", MineGuardianAnchorEntity.class, 64, 3, true);
    public static final EntityTypeEntry FLOATER = entity("floater", FloaterEntity.class, 64, 10, true);
    public static final EntityTypeEntry SUBMARINE = entity("submarine", SubmarineEntity.class, 128, 1, true);
    public static final EntityTypeEntry DEPTH_CHARGE = entity("depth_charge", DepthChargeEntity.class, 64, 1, true);
    public static final EntityTypeEntry INK_BOMB = entity("ink_bomb", InkBombEntity.class, 64, 1, true);
    public static final EntityTypeEntry WATER_BOLT = entity("water_bolt", WaterBoltEntity.class, 64, 1, true);
    public static final EntityTypeEntry WAVE = entity("wave", WaveEntity.class, 64, 1, true);
    public static final EntityTypeEntry CANDICORN = entity("candicorn", CandicornEntity.class, 80, 3, true, 0xFFEF57, 0xFFADD2);
    public static final EntityTypeEntry GUMMY_BEAR = entity("gummy_bear", GummyBearEntity.class, 80, 3, true, 0xE9232D, 0xFFEF57);
    public static final EntityTypeEntry CANIAC = entity("caniac", CaniacEntity.class, 64, 3, true, 0xF9F0FF, 0xFF3F56);
    public static final EntityTypeEntry CARAMEL_CUBE = entity("caramel_cube", CaramelCubeEntity.class, 64, 3, true, 0xB65A20, 0xFFBE4A);
    public static final EntityTypeEntry GUMBEEPER = entity("gumbeeper", GumbeeperEntity.class, 64, 3, true, 0x7B2441, 0xFF6BC8);
    public static final EntityTypeEntry GINGERBREAD_MAN = entity("gingerbread_man", GingerbreadManEntity.class, 64, 3, true, 0xA15A2A, 0xFFF0D3);
    public static final EntityTypeEntry GUM_WORM = entity("gum_worm", GumWormEntity.class, 128, 3, true, 0xF07DB5, 0xFFE06F);
    public static final EntityTypeEntry GUM_WORM_SEGMENT = entity("gum_worm_segment", GumWormSegmentEntity.class, 128, 3, true);
    public static final EntityTypeEntry THROWN_ICE_CREAM_SCOOP = entity("thrown_ice_cream_scoop", ThrownIceCreamScoopEntity.class, 64, 1, true);
    public static final EntityTypeEntry CANDY_CANE_HOOK = entity("candy_cane_hook", CandyCaneHookEntity.class, 64, 1, true);
    public static final EntityTypeEntry SPINNING_PEPPERMINT = entity("spinning_peppermint", SpinningPeppermintEntity.class, 64, 3, true);
    public static final EntityTypeEntry SUGAR_STAFF_HEX = entity("sugar_staff_hex", SugarStaffHexEntity.class, 64, 3, true);
    public static final EntityTypeEntry NOTOR = entity("notor", NotorEntity.class, 64, 3, true, 0x2D333E, 0x57D8FF);
    public static final EntityTypeEntry TELETOR = entity("teletor", TeletorEntity.class, 64, 3, true, 0x433B4A, 0x0060EF);
    public static final EntityTypeEntry BOUNDROID = entity("boundroid", BoundroidEntity.class, 64, 3, true, 0x4B4E55, 0xF08C21);
    public static final EntityTypeEntry BOUNDROID_WINCH = entity("boundroid_winch", BoundroidWinchEntity.class, 64, 3, true);
    public static final EntityTypeEntry FERROUSLIME = entity("ferrouslime", FerrouslimeEntity.class, 64, 3, true, 0x26272D, 0x53556C);
    public static final EntityTypeEntry MAGNETRON = entity("magnetron", MagnetronEntity.class, 96, 3, true, 0x3A3A42, 0xE93939);
    public static final EntityTypeEntry MAGNETRON_PART = entity("magnetron_part", MagnetronPartEntity.class, 96, 3, true);
    public static final EntityTypeEntry RADGILL = entity("radgill", RadgillEntity.class, 64, 3, true, 0x73870A, 0xE6FF1F);
    public static final EntityTypeEntry GAMMAROACH = entity("gammaroach", GammaroachEntity.class, 64, 3, true, 0x56682A, 0x2A2B19);
    public static final EntityTypeEntry RAYCAT = entity("raycat", RaycatEntity.class, 64, 3, true, 0x67FF00, 0x030A00);
    public static final EntityTypeEntry NUCLEEPER = entity("nucleeper", NucleeperEntity.class, 64, 3, true, 0x95A1A5, 0x00FF00);
    public static final EntityTypeEntry BRAINIAC = entity("brainiac", BrainiacEntity.class, 64, 3, true, 0x4D714B, 0x90E234);
    public static final EntityTypeEntry CORRODENT = entity("corrodent", CorrodentEntity.class, 64, 3, true, 0x351A14, 0x593B33);
    public static final EntityTypeEntry CORRODENT_TAIL = entity("corrodent_tail", CorrodentTailEntity.class, 64, 3, true);
    public static final EntityTypeEntry VESPER = entity("vesper", VesperEntity.class, 64, 3, true, 0x1A1B2E, 0x4B3A73);
    public static final EntityTypeEntry GLOOMOTH = entity("gloomoth", GloomothEntity.class, 64, 3, true, 0x5E463D, 0xEBD3BE);
    public static final EntityTypeEntry WATCHER = entity("watcher", WatcherEntity.class, 64, 3, true, 0x1A1725, 0xD8C3ED);
    public static final EntityTypeEntry BURROWING_ARROW = entity("burrowing_arrow", BurrowingArrowEntity.class, 64, 1, true);
    public static final EntityTypeEntry DARK_ARROW = entity("dark_arrow", DarkArrowEntity.class, 64, 1, true);
    public static final EntityTypeEntry UNDERZEALOT = entity("underzealot", UnderzealotEntity.class, 64, 3, true, 0x221C22, 0x7F3945);
    public static final EntityTypeEntry FORSAKEN = entity("forsaken", ForsakenEntity.class, 64, 3, true, 0x130B12, 0xDB1A31);
    public static final EntityTypeEntry NUCLEAR_EXPLOSION = entity("nuclear_explosion", NuclearExplosionEntity.class, 256, 1, true);
    public static final EntityTypeEntry THROWN_WASTE_DRUM = entity("thrown_waste_drum", ThrownWasteDrumEntity.class, 64, 1, true);
    public static final EntityTypeEntry CINDER_BRICK = entity("cinder_brick", CinderBrickEntity.class, 64, 1, true);
    public static final EntityTypeEntry GUANO = entity("guano", GuanoEntity.class, 64, 1, true);
    public static final EntityTypeEntry FALLING_GUANO = entity("falling_guano", FallingGuanoEntity.class, 64, 10, true);
    public static final EntityTypeEntry FALLING_FROSTMINT = entity("falling_frostmint", FallingFrostmintEntity.class, 64, 10, true);
    public static final EntityTypeEntry LIMESTONE_SPEAR = entity("limestone_spear", LimestoneSpearEntity.class, 64, 1, true);
    public static final EntityTypeEntry EXTINCTION_SPEAR = entity("extinction_spear", ExtinctionSpearEntity.class, 64, 1, true);
    public static final EntityTypeEntry DINOSAUR_SPIRIT = entity("dinosaur_spirit", DinosaurSpiritEntity.class, 64, 1, true);
    public static final EntityTypeEntry FROSTMINT_SPEAR = entity("frostmint_spear", FrostmintSpearEntity.class, 64, 1, true);
    public static final EntityTypeEntry SODA_BOTTLE_ROCKET = entity("soda_bottle_rocket", SodaBottleRocketEntity.class, 64, 1, true);
    public static final EntityTypeEntry DESOLATE_DAGGER = entity("desolate_dagger", DesolateDaggerEntity.class, 64, 1, true);
    public static final EntityTypeEntry MELTED_CARAMEL = entity("melted_caramel", MeltedCaramelEntity.class, 64, 10, true);
    public static final EntityTypeEntry MAGNETIC_WEAPON = entity("magnetic_weapon", MagneticWeaponEntity.class, 64, 1, true);
    public static final EntityTypeEntry MOVING_METAL_BLOCK = entity("moving_metal_block", MovingMetalBlockEntity.class, 20, 1, true);
    public static final EntityTypeEntry QUARRY_SMASHER = entity("quarry_smasher", QuarrySmasherEntity.class, 128, 1, true);
    public static final EntityTypeEntry GUMBALL = entity("gumball", GumballEntity.class, 64, 10, true);

    public static final List<EntityTypeEntry> ENTITIES = Collections.unmodifiableList(MUTABLE_ENTITIES);

    public static void registerEntities(Object mod) {
        for (EntityTypeEntry entry : ENTITIES) {
            ResourceLocation id = new ResourceLocation(AlexsCaves.MODID, entry.name());
            EntityRegistry.registerModEntity(id, entry.entityClass(), AlexsCaves.MODID + "." + entry.name(), entry.entityId(), mod, entry.trackingRange(), entry.updateFrequency(), entry.sendsVelocityUpdates());
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
