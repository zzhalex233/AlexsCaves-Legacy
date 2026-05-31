package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GumbeeperModel;
import com.zzhalex233.alexscaves.server.entity.living.GumbeeperEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GumbeeperRenderer extends RenderLiving<GumbeeperEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gumbeeper.png");
    private static final ResourceLocation TEXTURE_GLASS = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gumbeeper_glass.png");
    private static final ResourceLocation TEXTURE_EXPLODE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gumbeeper_explode.png");

    public GumbeeperRenderer(RenderManager renderManager) {
        super(renderManager, new GumbeeperModel(), 0.8F);
        addLayer(new LayerGlow());
    }

    @Override
    protected void preRenderCallback(GumbeeperEntity entity, float partialTick) {
        float explode = entity.getExplodeProgress(partialTick);
        GlStateManager.scale(1.0F + explode * 0.15F, 1.0F - explode * 0.2F, 1.0F + explode * 0.15F);
    }

    @Override
    protected ResourceLocation getEntityTexture(GumbeeperEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<GumbeeperEntity> {
        @Override
        public void doRenderLayer(GumbeeperEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity.isInvisible()) {
                return;
            }
            bindTexture(TEXTURE_GLASS);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.65F);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            float explode = entity.getExplodeProgress(partialTicks);
            if (explode > 0.0F) {
                bindTexture(TEXTURE_EXPLODE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, (MathHelper.sin(ageInTicks * 1.2F) + 1.0F) * 0.4F * explode);
                getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
