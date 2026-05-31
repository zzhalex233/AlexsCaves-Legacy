package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GloomothModel;
import com.zzhalex233.alexscaves.server.entity.living.GloomothEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class GloomothRenderer extends RenderLiving<GloomothEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gloomoth.png");
    private static final ResourceLocation EYESPOTS = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gloomoth_eyespots.png");

    public GloomothRenderer(RenderManager renderManager) {
        super(renderManager, new GloomothModel(), 0.35F);
        addLayer(new LayerEyespots());
    }

    @Override
    protected ResourceLocation getEntityTexture(GloomothEntity entity) {
        return TEXTURE;
    }

    private class LayerEyespots implements LayerRenderer<GloomothEntity> {
        @Override
        public void doRenderLayer(GloomothEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(EYESPOTS);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.33F);
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
