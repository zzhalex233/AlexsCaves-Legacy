package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.DeepOneModel;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneMageEntity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class DeepOneMageRenderer extends RenderLiving<DeepOneMageEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_mage.png");

    public DeepOneMageRenderer(RenderManager renderManager) {
        super(renderManager, new DeepOneModel(), 0.6F);
    }

    @Override
    protected ResourceLocation getEntityTexture(DeepOneMageEntity entity) {
        return TEXTURE;
    }
}
