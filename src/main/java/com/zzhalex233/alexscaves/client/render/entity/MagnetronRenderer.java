package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.MagnetronModel;
import com.zzhalex233.alexscaves.server.entity.living.MagnetronEntity;
import com.zzhalex233.alexscaves.server.entity.living.MagnetronPartEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class MagnetronRenderer extends RenderLiving<MagnetronEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/magnetron.png");
    private static final ResourceLocation TEXTURE_GLOW_RED = new ResourceLocation(AlexsCaves.MODID, "textures/entity/magnetron_glow_red.png");
    private static final ResourceLocation TEXTURE_GLOW_BLUE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/magnetron_glow_blue.png");
    private static final ResourceLocation TEXTURE_GLOW_EYES = new ResourceLocation(AlexsCaves.MODID, "textures/entity/magnetron_glow_eyes.png");

    public MagnetronRenderer(RenderManager manager) {
        super(manager, new MagnetronModel(), 0.8F);
        addLayer(new GlowLayer(this, TEXTURE_GLOW_BLUE));
        addLayer(new GlowLayer(this, TEXTURE_GLOW_RED));
        addLayer(new GlowLayer(this, TEXTURE_GLOW_EYES));
    }

    @Override
    public void doRender(MagnetronEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        if (entity.isFormed()) {
            renderParts(entity, x, y, z, partialTicks);
        }
    }

    private void renderParts(MagnetronEntity entity, double x, double y, double z, float partialTicks) {
        for (MagnetronPartEntity part : entity.getPartsArray()) {
            IBlockState state = part.getBlockState();
            if (state == null) {
                continue;
            }
            double px = part.lastTickPosX + (part.posX - part.lastTickPosX) * partialTicks - renderManager.viewerPosX;
            double py = part.lastTickPosY + (part.posY - part.lastTickPosY) * partialTicks - renderManager.viewerPosY;
            double pz = part.lastTickPosZ + (part.posZ - part.lastTickPosZ) * partialTicks - renderManager.viewerPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.translate(px - 0.5D, py, pz - 0.5D);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, entity.getBrightness());
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(MagnetronEntity entity) {
        return TEXTURE;
    }

    private static class GlowLayer implements LayerRenderer<MagnetronEntity> {
        private final MagnetronRenderer renderer;
        private final ResourceLocation texture;

        private GlowLayer(MagnetronRenderer renderer, ResourceLocation texture) {
            this.renderer = renderer;
            this.texture = texture;
        }

        @Override
        public void doRenderLayer(MagnetronEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            renderer.bindTexture(texture);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.disableLighting();
            renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
