package com.zzhalex233.alexscaves.server.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ACConfig {
    private static boolean enableCaveGeneration;
    private static boolean onlyOneResearchNeeded;
    private static int darknessCloakChargeTime;
    private static int darknessCloakFlightTime;

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
}
