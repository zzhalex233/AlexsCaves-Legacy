package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.server.entity.living.GossamerWormPartEntity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class GossamerWormPartRenderer extends Render<GossamerWormPartEntity> {
    public GossamerWormPartRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(GossamerWormPartEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }

    @Override
    protected ResourceLocation getEntityTexture(GossamerWormPartEntity entity) {
        return null;
    }
}
