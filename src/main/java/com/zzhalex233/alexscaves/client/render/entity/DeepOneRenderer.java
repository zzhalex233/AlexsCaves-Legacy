package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.DeepOneModel;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneEntity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class DeepOneRenderer extends RenderLiving<DeepOneEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/deep_one.png");

    public DeepOneRenderer(RenderManager renderManager) {
        super(renderManager, new DeepOneModel(), 0.6F);
    }

    @Override
    protected ResourceLocation getEntityTexture(DeepOneEntity entity) {
        return TEXTURE;
    }
}
