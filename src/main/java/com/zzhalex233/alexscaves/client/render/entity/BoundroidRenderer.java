package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.BoundroidModel;
import com.zzhalex233.alexscaves.server.entity.living.BoundroidEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class BoundroidRenderer extends RenderLiving<BoundroidEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/boundroid.png");
    private static final ResourceLocation TEXTURE_SCARED = new ResourceLocation(AlexsCaves.MODID, "textures/entity/boundroid_scared.png");

    public BoundroidRenderer(RenderManager renderManager) {
        super(renderManager, new BoundroidModel(), 0.8F);
        addLayer(new LayerGlow());
    }

    @Override
    protected ResourceLocation getEntityTexture(BoundroidEntity entity) {
        return entity.isScared() ? TEXTURE_SCARED : TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<BoundroidEntity> {
        @Override
        public void doRenderLayer(BoundroidEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.55F + (float) Math.sin(ageInTicks * 0.1F + 2.0F) * 0.1F);
            bindTexture(getEntityTexture(entity));
            ((BoundroidModel) getMainModel()).showChains(false);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            ((BoundroidModel) getMainModel()).showChains(true);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
