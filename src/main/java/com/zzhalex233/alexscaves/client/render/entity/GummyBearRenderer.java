package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GummyBearModel;
import com.zzhalex233.alexscaves.server.entity.living.GummyBearEntity;
import com.zzhalex233.alexscaves.server.entity.util.GummyColors;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class GummyBearRenderer extends RenderLiving<GummyBearEntity> {
    private static final ResourceLocation RED = texture("red");
    private static final ResourceLocation GREEN = texture("green");
    private static final ResourceLocation YELLOW = texture("yellow");
    private static final ResourceLocation BLUE = texture("blue");
    private static final ResourceLocation PINK = texture("pink");
    private static final ResourceLocation INNARDS = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gummy_bear_innards.png");

    public GummyBearRenderer(RenderManager renderManager) {
        super(renderManager, new GummyBearModel(), 0.75F);
        addLayer(new LayerInnards());
    }

    @Override
    protected ResourceLocation getEntityTexture(GummyBearEntity entity) {
        GummyColors color = entity.getGummyColor();
        switch (color) {
            case GREEN:
                return GREEN;
            case YELLOW:
                return YELLOW;
            case BLUE:
                return BLUE;
            case PINK:
                return PINK;
            default:
                return RED;
        }
    }

    private static ResourceLocation texture(String color) {
        return new ResourceLocation(AlexsCaves.MODID, "textures/entity/gummy_bear_" + color + ".png");
    }

    private class LayerInnards implements LayerRenderer<GummyBearEntity> {
        @Override
        public void doRenderLayer(GummyBearEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity.isInvisible()) {
                return;
            }
            bindTexture(INNARDS);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.55F);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
