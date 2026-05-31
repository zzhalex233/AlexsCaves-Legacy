package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.VesperModel;
import com.zzhalex233.alexscaves.server.entity.living.VesperEntity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class VesperRenderer extends RenderLiving<VesperEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/vesper.png");

    public VesperRenderer(RenderManager renderManager) {
        super(renderManager, new VesperModel(), 0.35F);
    }

    @Override
    protected ResourceLocation getEntityTexture(VesperEntity entity) {
        return TEXTURE;
    }
}
