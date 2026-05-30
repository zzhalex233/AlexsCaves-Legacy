package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.SeaPigModel;
import com.zzhalex233.alexscaves.server.entity.living.SeaPigEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SeaPigRenderer extends RenderLiving<SeaPigEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/sea_pig.png");
    private static final ResourceLocation INNARDS_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/sea_pig_innards.png");

    public SeaPigRenderer(RenderManager renderManager) {
        super(renderManager, new SeaPigModel(), 0.4F);
        addLayer(new LayerInside());
        addLayer(new LayerDigestingItem());
    }

    @Override
    protected ResourceLocation getEntityTexture(SeaPigEntity entity) {
        return INNARDS_TEXTURE;
    }

    private class LayerInside implements LayerRenderer<SeaPigEntity> {
        @Override
        public void doRenderLayer(SeaPigEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }

    private class LayerDigestingItem implements LayerRenderer<SeaPigEntity> {
        @Override
        public void doRenderLayer(SeaPigEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            ItemStack stack = entity.getDigestingItem();
            if (stack.isEmpty()) {
                return;
            }
            float progress = entity.getDigestProgress(partialTicks);
            float invProgress = 1.0F - progress;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 1.18F - invProgress * 0.1F, -0.28F + progress * 0.2F);
            GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate((float) Math.sin(progress * 15.0F) * 4.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(invProgress, invProgress, invProgress);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
            GlStateManager.popMatrix();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
