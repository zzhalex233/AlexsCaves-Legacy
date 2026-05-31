package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.BrainiacModel;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.living.BrainiacEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class BrainiacRenderer extends RenderLiving<BrainiacEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/brainiac.png");
    private static final ResourceLocation GLOW = new ResourceLocation(AlexsCaves.MODID, "textures/entity/brainiac_glow.png");

    public BrainiacRenderer(RenderManager renderManager) {
        super(renderManager, new BrainiacModel(), 0.25F);
        addLayer(new LayerGlow());
        addLayer(new LayerWasteDrum());
    }

    @Override
    protected ResourceLocation getEntityTexture(BrainiacEntity entity) {
        return TEXTURE;
    }

    private class LayerWasteDrum implements LayerRenderer<BrainiacEntity> {
        @Override
        public void doRenderLayer(BrainiacEntity brainiac, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (!brainiac.hasBarrel()) {
                return;
            }
            boolean hand = (brainiac.getAnimation() == BrainiacEntity.ANIMATION_THROW_BARREL || brainiac.getAnimation() == BrainiacEntity.ANIMATION_DRINK_BARREL) && brainiac.getAnimationTick() > 10;
            GlStateManager.pushMatrix();
            ((BrainiacModel) getMainModel()).translateToArmOrChest(hand);
            GlStateManager.translate(-0.5F, -0.7F, 1.01F);
            if (hand) {
                GlStateManager.translate(1.25F, 2.1F, -1.5F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            } else {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }
            bindTexture(net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(ACBlockRegistry.WASTE_DRUM.block().getDefaultState(), 1.0F);
            GlStateManager.popMatrix();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }

    private class LayerGlow implements LayerRenderer<BrainiacEntity> {
        @Override
        public void doRenderLayer(BrainiacEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            bindTexture(GLOW);
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
