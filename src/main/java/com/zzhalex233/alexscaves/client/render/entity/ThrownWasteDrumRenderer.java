package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.item.ThrownWasteDrumEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ThrownWasteDrumRenderer extends Render<ThrownWasteDrumEntity> {
    public ThrownWasteDrumRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(ThrownWasteDrumEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float progress = (entity.getOnGroundFor() + partialTicks) / ThrownWasteDrumEntity.MAX_TIME;
        if (progress > 1.0F) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.5F, (float) z);
        float expand = 1.0F + MathHelper.sin(progress * progress * (float) Math.PI) * 0.5F;
        GlStateManager.scale(expand, expand - progress * 0.3F, expand);
        if (entity.onGround) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        } else {
            GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((entity.ticksExisted + partialTicks) * 25.0F, 0.0F, 0.0F, 1.0F);
        }
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        bindTexture(net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(ACBlockRegistry.WASTE_DRUM.block().getDefaultState(), 1.0F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(ThrownWasteDrumEntity entity) {
        return net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
