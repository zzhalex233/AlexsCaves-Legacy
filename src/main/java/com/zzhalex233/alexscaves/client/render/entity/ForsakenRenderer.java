package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.ForsakenModel;
import com.zzhalex233.alexscaves.server.entity.living.ForsakenEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class ForsakenRenderer extends RenderLiving<ForsakenEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/forsaken.png");
    private static final ResourceLocation EYES = new ResourceLocation(AlexsCaves.MODID, "textures/entity/forsaken_eyes.png");
    private static final ResourceLocation DARKNESS = new ResourceLocation(AlexsCaves.MODID, "textures/entity/forsaken_darkness.png");

    public ForsakenRenderer(RenderManager renderManager) {
        super(renderManager, new ForsakenModel(), 1.15F);
        addLayer(new LayerGlow());
    }

    @Override
    protected ResourceLocation getEntityTexture(ForsakenEntity entity) {
        return entity.getDarknessProgress(1.0F) > 0.4F ? DARKNESS : TEXTURE;
    }

    @Override
    protected void preRenderCallback(ForsakenEntity entity, float partialTickTime) {
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
    }

    private class LayerGlow implements LayerRenderer<ForsakenEntity> {
        @Override
        public void doRenderLayer(ForsakenEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(EYES);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.85F);
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
