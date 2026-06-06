package com.zzhalex233.alexscaves.client;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.FrostedChocolateBlock;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.BiomeTreatItem;
import com.zzhalex233.alexscaves.server.item.GazingPearlItem;
import com.zzhalex233.alexscaves.server.item.JellyBeanItem;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = AlexsCaves.MODID, value = Side.CLIENT)
public class ACClientRegistryHandler {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (ACBlockRegistry.BlockEntry entry : ACBlockRegistry.BLOCKS) {
            if (entry.hasItem()) {
                ModelLoader.setCustomModelResourceLocation(entry.item(), 0, new ModelResourceLocation(entry.item().getRegistryName(), "inventory"));
            }
        }
        ModelLoader.setCustomStateMapper(ACBlockRegistry.PURPLE_SODA.block(), new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(AlexsCaves.MODID + ":purple_soda", "fluid");
            }
        });
        ModelLoader.setCustomStateMapper(ACBlockRegistry.ACID.block(), new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(AlexsCaves.MODID + ":acid", "fluid");
            }
        });
        for (ACItemRegistry.ItemEntry entry : ACItemRegistry.ITEMS) {
            Item item = entry.item();
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }

    @SubscribeEvent
    public static void stitchTextures(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "particle/fly_0"));
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "particle/fly_1"));
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "particle/sundrop"));
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "particle/trail"));
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "particle/trail_mirrored"));
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "particle/sonar"));
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "block/purple_soda_still"));
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "block/purple_soda_flowing"));
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "block/acid_still"));
        event.getMap().registerSprite(new ResourceLocation(AlexsCaves.MODID, "block/acid_flowing"));
    }

    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> tintIndex == 0 ? FrostedChocolateBlock.calculateFrostingColor(pos) : 0xFFFFFF,
            ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.block(), ACBlockRegistry.BLOCK_OF_FROSTING.block());
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex == 0 ? FrostedChocolateBlock.calculateFrostingColor(null) : 0xFFFFFF,
            ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.item(), ACBlockRegistry.BLOCK_OF_FROSTING.item());
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex == 0 ? JellyBeanItem.getBeanColor(stack) : 0xFFFFFF,
            ACItemRegistry.JELLY_BEAN.item());
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex == 0 ? GazingPearlItem.getPearlColor(stack) : 0xFFFFFF,
            ACItemRegistry.GAZING_PEARL.item());
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex == 1 ? BiomeTreatItem.getBiomeTreatColor(stack) : 0xFFFFFF,
            ACItemRegistry.BIOME_TREAT.item());
    }
}
