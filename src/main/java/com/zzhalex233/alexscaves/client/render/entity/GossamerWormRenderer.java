package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GossamerWormModel;
import com.zzhalex233.alexscaves.server.entity.living.GossamerWormEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class GossamerWormRenderer extends RenderLiving<GossamerWormEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gossamer_worm.png");

    public GossamerWormRenderer(RenderManager renderManager) {
        super(renderManager, new GossamerWormModel(), 0.4F);
    }

    @Override
    public void doRender(GossamerWormEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
    }

    @Override
    protected void preRenderCallback(GossamerWormEntity entity, float partialTickTime) {
        GlStateManager.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    protected ResourceLocation getEntityTexture(GossamerWormEntity entity) {
        return TEXTURE;
    }
}
