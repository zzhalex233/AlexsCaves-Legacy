package com.zzhalex233.alexscaves;

import com.zzhalex233.alexscaves.server.config.ACConfig;
import com.zzhalex233.alexscaves.server.CommonProxy;
import com.zzhalex233.alexscaves.server.entity.ACEntityRegistry;
import com.zzhalex233.alexscaves.server.registry.ACRegistryHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(modid = AlexsCaves.MODID, name = AlexsCaves.NAME, version = AlexsCaves.VERSION)
public class AlexsCaves {
    public static final String MODID = "alexscaves";
    public static final String NAME = "AlexsCaves Legacy";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.Instance(MODID)
    public static AlexsCaves INSTANCE;

    @SidedProxy(clientSide = "com.zzhalex233.alexscaves.client.ClientProxy", serverSide = "com.zzhalex233.alexscaves.server.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ACConfig.load(event.getSuggestedConfigurationFile());
        ACEntityRegistry.registerEntities(this);
        ACRegistryHandler.registerTileEntities();
        PROXY.preInit(event);
        LOGGER.info("Loading {}", NAME);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ACRegistryHandler.registerOreDictionaryEntries();
        ACRegistryHandler.registerBrewingRecipes();
        ACRegistryHandler.registerSmeltingRecipes();
        ACRegistryHandler.registerDispenserBehaviors();
        LOGGER.info("{} initialized", NAME);
    }
}
