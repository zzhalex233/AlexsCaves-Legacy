package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.MineGuardianModel;
import com.zzhalex233.alexscaves.server.entity.living.MineGuardianEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class MineGuardianRenderer extends RenderLiving<MineGuardianEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/mine_guardian.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation(AlexsCaves.MODID, "textures/entity/mine_guardian_sleeping.png");
    private static final ResourceLocation TEXTURE_EYE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/mine_guardian_eye.png");
    private static final ResourceLocation TEXTURE_EXPLODE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/mine_guardian_explode.png");

    public MineGuardianRenderer(RenderManager renderManager) {
        super(renderManager, new MineGuardianModel(), 0.8F);
        addLayer(new LayerGlow());
    }

    @Override
    protected void preRenderCallback(MineGuardianEntity entity, float partialTick) {
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(MineGuardianEntity entity) {
        return entity.isEyeClosed() ? TEXTURE_SLEEPING : TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<MineGuardianEntity> {
        @Override
        public void doRenderLayer(MineGuardianEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            if (!entity.isEyeClosed()) {
                bindTexture(TEXTURE_EYE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
            float explode = entity.getExplodeProgress(partialTicks);
            if (explode > 0.0F) {
                bindTexture(TEXTURE_EXPLODE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, explode);
                getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
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
