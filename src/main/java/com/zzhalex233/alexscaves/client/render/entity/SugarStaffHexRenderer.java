package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.entity.item.SugarStaffHexEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class SugarStaffHexRenderer extends Render<SugarStaffHexEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/sugar_staff_hex.png");

    public SugarStaffHexRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(SugarStaffHexEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float alpha = Math.min(1.0F, entity.getDespawnTime(partialTicks) / 20.0F);
        float size = 2.0F * entity.getHexScale();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + entity.getYRenderOffset(), (float) z);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bindTexture(TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-size, 0.01D, -size).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.pos(-size, 0.01D, size).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.pos(size, 0.01D, size).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buffer.pos(size, 0.01D, -size).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(SugarStaffHexEntity entity) {
        return TEXTURE;
    }
}
