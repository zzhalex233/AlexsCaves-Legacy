package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.CorrodentModel;
import com.zzhalex233.alexscaves.server.entity.living.CorrodentEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class CorrodentRenderer extends RenderLiving<CorrodentEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/corrodent.png");
    private static final ResourceLocation EYES = new ResourceLocation(AlexsCaves.MODID, "textures/entity/corrodent_eyes.png");

    public CorrodentRenderer(RenderManager renderManager) {
        super(renderManager, new CorrodentModel(), 0.5F);
        addLayer(new LayerEyes());
    }

    @Override
    protected ResourceLocation getEntityTexture(CorrodentEntity entity) {
        return TEXTURE;
    }

    private class LayerEyes implements LayerRenderer<CorrodentEntity> {
        @Override
        public void doRenderLayer(CorrodentEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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
