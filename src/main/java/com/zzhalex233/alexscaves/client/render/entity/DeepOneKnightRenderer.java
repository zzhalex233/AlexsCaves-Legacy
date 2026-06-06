package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.DeepOneKnightModel;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneKnightEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class DeepOneKnightRenderer extends RenderLiving<DeepOneKnightEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_knight.png");
    private static final ResourceLocation GLOW = new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_knight_glow.png");

    public DeepOneKnightRenderer(RenderManager renderManager) {
        super(renderManager, new DeepOneKnightModel(), 0.45F);
        addLayer(new LayerGlow());
    }

    @Override
    protected ResourceLocation getEntityTexture(DeepOneKnightEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<DeepOneKnightEntity> {
        @Override
        public void doRenderLayer(DeepOneKnightEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity.isInvisible()) {
                return;
            }
            bindTexture(GLOW);
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
