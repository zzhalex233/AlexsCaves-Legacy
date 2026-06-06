package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.WaterBoltModel;
import com.zzhalex233.alexscaves.server.entity.item.WaterBoltEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class WaterBoltRenderer extends Render<WaterBoltEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/water_bolt.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/water_bolt_overlay.png");
    private final WaterBoltModel model = new WaterBoltModel();

    public WaterBoltRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.15F;
    }

    @Override
    public void doRender(WaterBoltEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.25F, (float) z);
        GlStateManager.rotate(interpolate(entity.prevRotationYaw, entity.rotationYaw, partialTicks) - 180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(interpolate(entity.prevRotationPitch, entity.rotationPitch, partialTicks), 1.0F, 0.0F, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bindTexture(TEXTURE);
        model.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
        bindTexture(OVERLAY_TEXTURE);
        model.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (entity.hasTrail()) {
            renderTrail(entity, x, y, z, partialTicks);
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderTrail(WaterBoltEntity entity, double x, double y, double z, float partialTicks) {
        Vec3d now = entity.getPositionVector();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - now.x, y - now.y, z - now.z);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(3, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 10; i++) {
            Vec3d pos = entity.getTrailPosition(i + 2, partialTicks);
            float alpha = 0.6F * (1.0F - i / 10.0F);
            buffer.pos(pos.x, pos.y + 0.25D, pos.z).color(0.35F, 0.8F, 1.0F, alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private float interpolate(float prev, float current, float partialTicks) {
        return prev + (current - prev) * partialTicks;
    }

    @Override
    protected ResourceLocation getEntityTexture(WaterBoltEntity entity) {
        return TEXTURE;
    }
}
