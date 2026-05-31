package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.server.entity.living.HullbreakerPartEntity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class HullbreakerPartRenderer extends Render<HullbreakerPartEntity> {
    public HullbreakerPartRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(HullbreakerPartEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }

    @Override
    protected ResourceLocation getEntityTexture(HullbreakerPartEntity entity) {
        return null;
    }
}
