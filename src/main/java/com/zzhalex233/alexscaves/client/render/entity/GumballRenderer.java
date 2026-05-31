package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.entity.item.GumballEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GumballRenderer extends Render<GumballEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[11];
    private static final ResourceLocation TEXTURE_EXPLODING = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gumball/gumball_exploding.png");

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gumball/gumball_" + i + ".png");
        }
    }

    public GumballRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.15F;
    }

    @Override
    public void doRender(GumballEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float explode = entity.getExplodeProgress(partialTicks);
        float scale = entity.isExplosive() ? 0.5F + explode * 0.2F : 0.25F;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.25F, (float) z);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1.0F : 1.0F) * renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bindTexture(getEntityTexture(entity));
        drawQuad(1.0F);
        if (entity.isExplosive() && entity.getBounces() >= entity.getMaximumBounces()) {
            bindTexture(TEXTURE_EXPLODING);
            drawQuad(1.0F - 0.5F * (1.0F + (float) Math.sin((entity.ticksExisted + partialTicks) * 0.9F)));
        }
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void drawQuad(float alpha) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-0.5D, -0.25D, 0.0D).tex(0.0D, 1.0D).endVertex();
        buffer.pos(0.5D, -0.25D, 0.0D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(0.5D, 0.75D, 0.0D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(-0.5D, 0.75D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(GumballEntity entity) {
        int color = Math.max(0, Math.min(TEXTURES.length - 1, entity.getColor()));
        return TEXTURES[color];
    }
}
