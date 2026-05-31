package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.RaycatModel;
import com.zzhalex233.alexscaves.server.entity.living.RaycatEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RaycatRenderer extends RenderLiving<RaycatEntity> {
    private static final ResourceLocation BODY = new ResourceLocation(AlexsCaves.MODID, "textures/entity/raycat_body.png");
    private static final ResourceLocation BONES = new ResourceLocation(AlexsCaves.MODID, "textures/entity/raycat.png");
    private static final ResourceLocation EYES = new ResourceLocation(AlexsCaves.MODID, "textures/entity/raycat_eyes.png");

    public RaycatRenderer(RenderManager renderManager) {
        super(renderManager, new RaycatModel(), 0.4F);
        addLayer(new LayerBonesAndGlow());
    }

    @Override
    protected ResourceLocation getEntityTexture(RaycatEntity entity) {
        return BODY;
    }

    private class LayerBonesAndGlow implements LayerRenderer<RaycatEntity> {
        @Override
        public void doRenderLayer(RaycatEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(BONES);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
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
