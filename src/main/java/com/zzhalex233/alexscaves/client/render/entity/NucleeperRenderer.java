package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.NucleeperModel;
import com.zzhalex233.alexscaves.server.entity.living.NucleeperEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class NucleeperRenderer extends RenderLiving<NucleeperEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_glow.png");
    private static final ResourceLocation TEXTURE_GLASS = new ResourceLocation(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_glass.png");
    private static final ResourceLocation TEXTURE_BUTTONS_0 = new ResourceLocation(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_buttons_0.png");
    private static final ResourceLocation TEXTURE_BUTTONS_1 = new ResourceLocation(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_buttons_1.png");
    private static final ResourceLocation TEXTURE_BUTTONS_2 = new ResourceLocation(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_buttons_2.png");
    private static final ResourceLocation TEXTURE_EXPLODE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_explode.png");
    private static final ResourceLocation TEXTURE_CHARGED = new ResourceLocation(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper_charged.png");

    public NucleeperRenderer(RenderManager renderManager) {
        super(renderManager, new NucleeperModel(), 0.8F);
        addLayer(new LayerGlow());
    }

    @Override
    protected ResourceLocation getEntityTexture(NucleeperEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<NucleeperEntity> {
        @Override
        public void doRenderLayer(NucleeperEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            float alpha = (float) (1.0F + Math.sin(ageInTicks * 0.3F)) * 0.25F + 0.5F;
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
            bindTexture(TEXTURE_GLOW);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            bindTexture(entity.isCharged() ? TEXTURE_CHARGED : buttons(entity));
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (entity.getExplodeProgress(partialTicks) > 0.0F) {
                bindTexture(TEXTURE_EXPLODE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, entity.getExplodeProgress(partialTicks));
                getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            bindTexture(TEXTURE_GLASS);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.55F);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }

        private ResourceLocation buttons(NucleeperEntity entity) {
            int buttonDiv = entity.ticksExisted / (entity.isTriggered() ? 2 : 5) % 6;
            return buttonDiv < 2 ? TEXTURE_BUTTONS_0 : buttonDiv < 4 ? TEXTURE_BUTTONS_1 : TEXTURE_BUTTONS_2;
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
