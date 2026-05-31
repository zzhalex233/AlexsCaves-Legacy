package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.HullbreakerModel;
import com.zzhalex233.alexscaves.server.entity.living.HullbreakerEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class HullbreakerRenderer extends RenderLiving<HullbreakerEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/hullbreaker.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/hullbreaker_glow.png");

    public HullbreakerRenderer(RenderManager renderManager) {
        super(renderManager, new HullbreakerModel(), 1.8F);
        addLayer(new LayerGlow());
    }

    @Override
    protected void applyRotations(HullbreakerEntity entity, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entity, ageInTicks, rotationYaw, partialTicks);
        GlStateManager.translate(0.0F, 1.2F, 0.0F);
        GlStateManager.rotate(-entity.getFishPitch(partialTicks), 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -1.2F, 0.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(HullbreakerEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<HullbreakerEntity> {
        @Override
        public void doRenderLayer(HullbreakerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(GLOW_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.35F + Math.min(entity.getPulseAmount(partialTicks), 1.0F) * 0.45F);
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
