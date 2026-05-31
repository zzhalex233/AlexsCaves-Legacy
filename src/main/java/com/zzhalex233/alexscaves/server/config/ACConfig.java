package com.zzhalex233.alexscaves.server.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ACConfig {
    private static boolean enableCaveGeneration;
    private static boolean onlyOneResearchNeeded;
    private static int darknessCloakChargeTime;
    private static int darknessCloakFlightTime;
    private static int nucleeperFuseTime;
    private static int nukeMaxBlockExplosionResistance;
    private static boolean nukesSpawnItemDrops;
    private static double nukeExplosionSizeModifier;

    public static void load(File file) {
        Configuration config = new Configuration(file);
        config.load();
        enableCaveGeneration = config.getBoolean(
            "enableCaveGeneration",
            Configuration.CATEGORY_GENERAL,
            false,
            "Keep cave world generation disabled until each cave phase is fully ported."
        );
        darknessCloakChargeTime = config.getInt(
            "darknessCloakChargeTime",
            Configuration.CATEGORY_GENERAL,
            1000,
            20,
            Integer.MAX_VALUE,
            "Ticks required for the Cloak of Darkness to charge while the wearer is in darkness."
        );
        onlyOneResearchNeeded = config.getBoolean(
            "onlyOneResearchNeeded",
            Configuration.CATEGORY_GENERAL,
            false,
            "If true, one Cave Codex unlocks all available Cave Compendium pages for its cave."
        );
        darknessCloakFlightTime = config.getInt(
            "darknessCloakFlightTime",
            Configuration.CATEGORY_GENERAL,
            200,
            20,
            Integer.MAX_VALUE,
            "Ticks of Darkness Incarnate granted after the cloak finishes charging."
        );
        nucleeperFuseTime = config.getInt(
            "nucleeperFuseTime",
            Configuration.CATEGORY_GENERAL,
            300,
            20,
            Integer.MAX_VALUE,
            "How long in ticks it takes for a Nucleeper to explode."
        );
        nukeMaxBlockExplosionResistance = config.getInt(
            "nukeMaxBlockExplosionResistance",
            Configuration.CATEGORY_GENERAL,
            1000,
            0,
            Integer.MAX_VALUE,
            "Maximum block explosion resistance that can be destroyed by a nuclear explosion. Set to 0 to disable nuclear block breaking."
        );
        nukesSpawnItemDrops = config.getBoolean(
            "nukesSpawnItemDrops",
            Configuration.CATEGORY_GENERAL,
            true,
            "Whether a small portion of blocks broken by nuclear explosions can drop items."
        );
        nukeExplosionSizeModifier = config.getFloat(
            "nukeExplosionSizeModifier",
            Configuration.CATEGORY_GENERAL,
            3.0F,
            0.0F,
            Float.MAX_VALUE,
            "Scale modifier for nuclear explosion destruction radius."
        );
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static boolean isCaveGenerationEnabled() {
        return enableCaveGeneration;
    }

    public static boolean isOnlyOneResearchNeeded() {
        return onlyOneResearchNeeded;
    }

    public static int getDarknessCloakChargeTime() {
        return darknessCloakChargeTime;
    }

    public static int getDarknessCloakFlightTime() {
        return darknessCloakFlightTime;
    }

    public static int getNucleeperFuseTime() {
        return nucleeperFuseTime;
    }

    public static int getNukeMaxBlockExplosionResistance() {
        return nukeMaxBlockExplosionResistance;
    }

    public static boolean doNukesSpawnItemDrops() {
        return nukesSpawnItemDrops;
    }

    public static double getNukeExplosionSizeModifier() {
        return nukeExplosionSizeModifier;
    }
}
