package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.LanternfishModel;
import com.zzhalex233.alexscaves.server.entity.living.LanternfishEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LanternfishRenderer extends RenderLiving<LanternfishEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/lanternfish.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/lanternfish_glow.png");

    public LanternfishRenderer(RenderManager renderManager) {
        super(renderManager, new LanternfishModel(), 0.25F);
        addLayer(new LayerGlow());
    }

    @Override
    protected ResourceLocation getEntityTexture(LanternfishEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<LanternfishEntity> {
        @Override
        public void doRenderLayer(LanternfishEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(GLOW_TEXTURE);
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
