package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.server.entity.item.SpinningPeppermintEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class SpinningPeppermintRenderer extends Render<SpinningPeppermintEntity> {
    public SpinningPeppermintRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.15F;
    }

    @Override
    public void doRender(SpinningPeppermintEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.5F, (float) z);
        GlStateManager.rotate(entity.ticksExisted * 40.0F + partialTicks * 40.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
        Minecraft.getMinecraft().getRenderItem().renderItem(entity.peppermintRenderStack, ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(SpinningPeppermintEntity entity) {
        return null;
    }
}
