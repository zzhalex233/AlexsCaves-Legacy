package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.entity.item.MeltedCaramelEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class MeltedCaramelRenderer extends Render<MeltedCaramelEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/caramel_cube/melted_caramel.png");

    public MeltedCaramelRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(MeltedCaramelEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float alpha = Math.min(20.0F, entity.getDespawnTime(partialTicks)) / 20.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + entity.getYRenderOffset(), (float) z);
        GlStateManager.rotate(entity.getEntityId() % 4 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        bindTexture(TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-0.5D, 0.01D, -0.5D).tex(0.0D, 0.0D).endVertex();
        buffer.pos(0.5D, 0.01D, -0.5D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(0.5D, 0.01D, 0.5D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(-0.5D, 0.01D, 0.5D).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(MeltedCaramelEntity entity) {
        return TEXTURE;
    }
}
