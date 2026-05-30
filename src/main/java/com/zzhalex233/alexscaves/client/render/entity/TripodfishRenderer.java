package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.TripodfishModel;
import com.zzhalex233.alexscaves.server.entity.living.TripodfishEntity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class TripodfishRenderer extends RenderLiving<TripodfishEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/tripodfish.png");

    public TripodfishRenderer(RenderManager renderManager) {
        super(renderManager, new TripodfishModel(), 0.45F);
    }

    @Override
    protected ResourceLocation getEntityTexture(TripodfishEntity entity) {
        return TEXTURE;
    }
}
