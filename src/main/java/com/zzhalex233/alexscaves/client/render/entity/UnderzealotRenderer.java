package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.UnderzealotModel;
import com.zzhalex233.alexscaves.server.entity.living.UnderzealotEntity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class UnderzealotRenderer extends RenderLiving<UnderzealotEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/underzealot.png");

    public UnderzealotRenderer(RenderManager renderManager) {
        super(renderManager, new UnderzealotModel(), 0.45F);
    }

    @Override
    protected ResourceLocation getEntityTexture(UnderzealotEntity entity) {
        return TEXTURE;
    }
}
