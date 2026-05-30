package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.TrilocarisModel;
import com.zzhalex233.alexscaves.server.entity.living.TrilocarisEntity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class TrilocarisRenderer extends RenderLiving<TrilocarisEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/trilocaris.png");

    public TrilocarisRenderer(RenderManager renderManager) {
        super(renderManager, new TrilocarisModel(), 0.3F);
    }

    @Override
    protected ResourceLocation getEntityTexture(TrilocarisEntity entity) {
        return TEXTURE;
    }
}
