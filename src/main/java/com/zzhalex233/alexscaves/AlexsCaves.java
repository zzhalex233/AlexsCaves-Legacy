package com.zzhalex233.alexscaves;

import com.zzhalex233.alexscaves.server.config.ACConfig;
import com.zzhalex233.alexscaves.server.CommonProxy;
import com.zzhalex233.alexscaves.server.entity.ACEntityRegistry;
import com.zzhalex233.alexscaves.server.inventory.ACGuiHandler;
import com.zzhalex233.alexscaves.server.level.feature.TeslaBulbWorldGenerator;
import com.zzhalex233.alexscaves.server.message.AmberMonolithMessage;
import com.zzhalex233.alexscaves.server.message.MountedEntityKeyMessage;
import com.zzhalex233.alexscaves.server.message.SpelunkeryTableChangeMessage;
import com.zzhalex233.alexscaves.server.message.TeslaBulbLightningMessage;
import com.zzhalex233.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.zzhalex233.alexscaves.server.message.UpdateItemTagMessage;
import com.zzhalex233.alexscaves.server.registry.ACRegistryHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = AlexsCaves.MODID, name = AlexsCaves.NAME, version = AlexsCaves.VERSION)
public class AlexsCaves {
    public static final String MODID = "alexscaves";
    public static final String NAME = "AlexsCaves Legacy";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @Mod.Instance(MODID)
    public static AlexsCaves INSTANCE;

    @SidedProxy(clientSide = "com.zzhalex233.alexscaves.client.ClientProxy", serverSide = "com.zzhalex233.alexscaves.server.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ACConfig.load(event.getSuggestedConfigurationFile());
        NETWORK_WRAPPER.registerMessage(SpelunkeryTableChangeMessage.Handler.class, SpelunkeryTableChangeMessage.class, 0, Side.SERVER);
        NETWORK_WRAPPER.registerMessage(TeslaBulbLightningMessage.Handler.class, TeslaBulbLightningMessage.class, 1, Side.CLIENT);
        NETWORK_WRAPPER.registerMessage(AmberMonolithMessage.Handler.class, AmberMonolithMessage.class, 2, Side.CLIENT);
        NETWORK_WRAPPER.registerMessage(UpdateItemTagMessage.ServerHandler.class, UpdateItemTagMessage.class, 3, Side.SERVER);
        NETWORK_WRAPPER.registerMessage(UpdateItemTagMessage.ClientHandler.class, UpdateItemTagMessage.class, 4, Side.CLIENT);
        NETWORK_WRAPPER.registerMessage(UpdateEffectVisualityEntityMessage.Handler.class, UpdateEffectVisualityEntityMessage.class, 5, Side.CLIENT);
        NETWORK_WRAPPER.registerMessage(MountedEntityKeyMessage.Handler.class, MountedEntityKeyMessage.class, 6, Side.SERVER);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ACGuiHandler());
        ACEntityRegistry.registerEntities(this);
        ACRegistryHandler.registerTileEntities();
        PROXY.preInit(event);
        LOGGER.info("Loading {}", NAME);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        TeslaBulbWorldGenerator.register();
        ACRegistryHandler.registerOreDictionaryEntries();
        ACRegistryHandler.registerBrewingRecipes();
        ACRegistryHandler.registerSmeltingRecipes();
        ACRegistryHandler.registerDispenserBehaviors();
        LOGGER.info("{} initialized", NAME);
    }
}
