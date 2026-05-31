package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.RadgillModel;
import com.zzhalex233.alexscaves.server.entity.living.RadgillEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RadgillRenderer extends RenderLiving<RadgillEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/radgill.png");
    private static final ResourceLocation EYES = new ResourceLocation(AlexsCaves.MODID, "textures/entity/radgill_eyes.png");

    public RadgillRenderer(RenderManager renderManager) {
        super(renderManager, new RadgillModel(), 0.25F);
        addLayer(new LayerGlow());
    }

    @Override
    protected void preRenderCallback(RadgillEntity entity, float partialTickTime) {
        GlStateManager.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    protected ResourceLocation getEntityTexture(RadgillEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<RadgillEntity> {
        @Override
        public void doRenderLayer(RadgillEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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
