package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.FerrouslimeModel;
import com.zzhalex233.alexscaves.server.entity.living.FerrouslimeEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class FerrouslimeRenderer extends Render<FerrouslimeEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/ferrouslime.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation(AlexsCaves.MODID, "textures/entity/ferrouslime_eyes.png");
    private static final ResourceLocation TEXTURE_GEL = new ResourceLocation(AlexsCaves.MODID, "textures/entity/ferrouslime_gel.png");
    private static final FerrouslimeModel MODEL = new FerrouslimeModel();

    public FerrouslimeRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.5F;
    }

    @Override
    public void doRender(FerrouslimeEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.2F + entity.height * 0.5F, (float) z);
        float age = entity.ticksExisted + partialTicks;
        float orbitDist = entity.getSlimeSize(partialTicks) * 0.1F;
        for (int i = 1; i <= entity.getHeadCount(); i++) {
            Vec3d offset = entity.getHeadOffsetPos(i);
            if (i > entity.prevHeadCount) {
                offset = offset.scale(entity.getMergeProgress(partialTicks));
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(offset.x + orbitDist * Math.sin(i + age * 0.05F), offset.y + orbitDist * Math.sin(2 + i + age * 0.1F), offset.z + orbitDist * Math.cos(i + age * 0.035F));
            GlStateManager.rotate(180.0F - entity.renderYawOffset, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            bindTexture(TEXTURE);
            MODEL.render(entity, 0.0F, 0.0F, age, 0.0F, 0.0F, 0.0625F);
            bindTexture(TEXTURE_EYES);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            MODEL.render(entity, 0.0F, 0.0F, age, 0.0F, 0.0F, 0.0625F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        bindTexture(TEXTURE_GEL);
        renderGel(entity.getSlimeSize(partialTicks) - 0.2F, entity.getAttackProgress(partialTicks));
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void renderGel(float size, float attack) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.55F);
        float half = size * 0.5F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        face(buffer, -half, -half, half, half, half, half);
        face(buffer, half, -half, -half, -half, half, -half);
        face(buffer, half, -half, half, half, half, -half);
        face(buffer, -half, -half, -half, -half, half, half);
        face(buffer, -half, half, -half, half, half, half);
        face(buffer, -half, -half, half, half, -half, -half);
        tessellator.draw();
        if (attack > 0.0F) {
            GlStateManager.scale(1.0F + attack * 0.2F, 1.0F + attack * 0.2F, 1.0F + attack * 0.2F);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    private void face(BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2) {
        buffer.pos(x1, y1, z1).tex(0.0D, 0.0D).endVertex();
        buffer.pos(x2, y1, z1).tex(1.0D, 0.0D).endVertex();
        buffer.pos(x2, y2, z2).tex(1.0D, 1.0D).endVertex();
        buffer.pos(x1, y2, z2).tex(0.0D, 1.0D).endVertex();
    }

    @Override
    protected ResourceLocation getEntityTexture(FerrouslimeEntity entity) {
        return TEXTURE;
    }
}
