package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.BoundroidWinchModel;
import com.zzhalex233.alexscaves.server.entity.living.BoundroidWinchEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BoundroidWinchRenderer extends RenderLiving<BoundroidWinchEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/boundroid_winch.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(AlexsCaves.MODID, "textures/entity/boundroid_winch_glow.png");
    private static final ResourceLocation TEXTURE_CHAIN = new ResourceLocation("minecraft", "textures/blocks/chain.png");

    public BoundroidWinchRenderer(RenderManager renderManager) {
        super(renderManager, new BoundroidWinchModel(), 0.3F);
        addLayer(new LayerGlow());
    }

    @Override
    public void doRender(BoundroidWinchEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        Vec3d from = entity.getChainFrom(partialTicks);
        Vec3d to = entity.getChainTo(partialTicks);
        renderChain(to.subtract(from), x, y + 0.2D, z, partialTicks);
    }

    private void renderChain(Vec3d chain, double x, double y, double z, float partialTicks) {
        double horizontal = Math.sqrt(chain.x * chain.x + chain.z * chain.z);
        float length = (float) chain.length();
        if (length <= 0.01F) {
            return;
        }
        float yaw = (float) MathHelper.atan2(chain.x, chain.z) * 180.0F / (float) Math.PI;
        float pitch = -(float) MathHelper.atan2(chain.y, horizontal) * 180.0F / (float) Math.PI;
        bindTexture(TEXTURE_CHAIN);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch - 90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableCull();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float width = 0.1875F;
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-width, 0.0D, 0.0D).tex(0.0D, length).endVertex();
        buffer.pos(width, 0.0D, 0.0D).tex(width, length).endVertex();
        buffer.pos(width, length, 0.0D).tex(width, 0.0D).endVertex();
        buffer.pos(-width, length, 0.0D).tex(0.0D, 0.0D).endVertex();
        buffer.pos(0.0D, 0.0D, -width).tex(0.0D, length).endVertex();
        buffer.pos(0.0D, 0.0D, width).tex(width, length).endVertex();
        buffer.pos(0.0D, length, width).tex(width, 0.0D).endVertex();
        buffer.pos(0.0D, length, -width).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(BoundroidWinchEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<BoundroidWinchEntity> {
        @Override
        public void doRenderLayer(BoundroidWinchEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.55F + (float) Math.sin(ageInTicks * 0.1F + 2.0F) * 0.1F);
            bindTexture(TEXTURE_GLOW);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
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
