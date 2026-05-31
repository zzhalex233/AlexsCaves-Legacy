package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.WatcherModel;
import com.zzhalex233.alexscaves.server.entity.living.WatcherEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class WatcherRenderer extends RenderLiving<WatcherEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/watcher.png");
    private static final ResourceLocation MOTH = new ResourceLocation(AlexsCaves.MODID, "textures/entity/watcher_moth.png");
    private static final ResourceLocation EYESPOTS = new ResourceLocation(AlexsCaves.MODID, "textures/entity/watcher_eyespots.png");

    public WatcherRenderer(RenderManager renderManager) {
        super(renderManager, new WatcherModel(), 0.5F);
        addLayer(new LayerGlow());
    }

    @Override
    protected ResourceLocation getEntityTexture(WatcherEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(WatcherEntity entity, float partialTickTime) {
        float alpha = (1.0F - entity.getShadeAmount(partialTickTime)) * 0.8F + 0.2F;
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
    }

    private class LayerGlow implements LayerRenderer<WatcherEntity> {
        @Override
        public void doRenderLayer(WatcherEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(MOTH);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            bindTexture(EYESPOTS);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.66F);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
