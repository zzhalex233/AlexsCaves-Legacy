package com.zzhalex233.alexscaves.coremod.hooks;

import net.minecraft.client.renderer.GlStateManager;

public final class ItemOverlayStateHooks {
    private ItemOverlayStateHooks() {
    }

    public static void beforeRenderItemOverlayIntoGUI() {
        resetGuiOverlayState();
    }

    public static void afterRenderItemOverlayIntoGUI() {
        resetGuiOverlayState();
    }

    private static void resetGuiOverlayState() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }
}
