package com.zzhalex233.alexscaves.client;

import com.zzhalex233.alexscaves.client.gui.book.CaveBookScreen;
import com.zzhalex233.alexscaves.client.render.entity.CaniacRenderer;
import com.zzhalex233.alexscaves.client.render.entity.LanternfishRenderer;
import com.zzhalex233.alexscaves.client.render.entity.FrostmintSpearRenderer;
import com.zzhalex233.alexscaves.client.render.entity.SeaPigRenderer;
import com.zzhalex233.alexscaves.client.render.entity.SweetishFishRenderer;
import com.zzhalex233.alexscaves.client.render.entity.TripodfishRenderer;
import com.zzhalex233.alexscaves.client.render.entity.TrilocarisRenderer;
import com.zzhalex233.alexscaves.server.CommonProxy;
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
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(TrilocarisEntity.class, TrilocarisRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(LanternfishEntity.class, LanternfishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TripodfishEntity.class, TripodfishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SeaPigEntity.class, SeaPigRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SweetishFishEntity.class, SweetishFishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CaniacEntity.class, CaniacRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CinderBrickEntity.class, manager -> new RenderSnowball<>(manager, ACItemRegistry.CINDER_BRICK.item(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(GuanoEntity.class, manager -> new RenderSnowball<>(manager, ACItemRegistry.GUANO.item(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(SodaBottleRocketEntity.class, manager -> new RenderSnowball<>(manager, ACItemRegistry.PURPLE_SODA_BOTTLE_ROCKET.item(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(FallingGuanoEntity.class, RenderFallingBlock::new);
        RenderingRegistry.registerEntityRenderingHandler(FallingFrostmintEntity.class, RenderFallingBlock::new);
        RenderingRegistry.registerEntityRenderingHandler(FrostmintSpearEntity.class, FrostmintSpearRenderer::new);
    }

    @Override
    public void openBookGUI(ItemStack stack) {
        Minecraft.getMinecraft().player.playSound(com.zzhalex233.alexscaves.server.misc.ACSoundRegistry.CAVE_BOOK_OPEN, 0.7F, 1.0F);
        Minecraft.getMinecraft().displayGuiScreen(new CaveBookScreen(stack));
    }
}
