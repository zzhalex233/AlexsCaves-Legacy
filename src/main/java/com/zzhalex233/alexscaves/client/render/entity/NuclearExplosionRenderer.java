package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.server.entity.item.NuclearExplosionEntity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class NuclearExplosionRenderer extends Render<NuclearExplosionEntity> {
    public NuclearExplosionRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(NuclearExplosionEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }

    @Override
    protected ResourceLocation getEntityTexture(NuclearExplosionEntity entity) {
        return null;
    }
}
