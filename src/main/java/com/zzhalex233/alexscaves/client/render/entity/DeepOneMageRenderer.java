package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.DeepOneModel;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneMageEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class DeepOneMageRenderer extends RenderLiving<DeepOneMageEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_mage.png");
    private static final ResourceLocation GLOW = new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_mage_glow.png");

    public DeepOneMageRenderer(RenderManager renderManager) {
        super(renderManager, new DeepOneModel(), 0.45F);
        addLayer(new LayerGlow());
    }

    @Override
    protected ResourceLocation getEntityTexture(DeepOneMageEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<DeepOneMageEntity> {
        @Override
        public void doRenderLayer(DeepOneMageEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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
