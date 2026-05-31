package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GammaroachModel;
import com.zzhalex233.alexscaves.server.entity.living.GammaroachEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class GammaroachRenderer extends RenderLiving<GammaroachEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gammaroach.png");
    private static final ResourceLocation EYES = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gammaroach_eyes.png");

    public GammaroachRenderer(RenderManager renderManager) {
        super(renderManager, new GammaroachModel(), 0.5F);
        addLayer(new LayerGlow());
    }

    @Override
    protected ResourceLocation getEntityTexture(GammaroachEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<GammaroachEntity> {
        @Override
        public void doRenderLayer(GammaroachEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(EYES);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
