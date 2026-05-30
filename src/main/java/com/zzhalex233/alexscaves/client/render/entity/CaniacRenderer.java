package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.CaniacModel;
import com.zzhalex233.alexscaves.server.entity.living.CaniacEntity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class CaniacRenderer extends RenderLiving<CaniacEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/caniac.png");

    public CaniacRenderer(RenderManager renderManager) {
        super(renderManager, new CaniacModel(), 0.65F);
    }

    @Override
    protected ResourceLocation getEntityTexture(CaniacEntity entity) {
        return TEXTURE;
    }
}
