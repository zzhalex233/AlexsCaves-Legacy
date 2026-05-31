package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GumWormModel;
import com.zzhalex233.alexscaves.server.entity.living.GumWormEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class GumWormRenderer extends RenderLiving<GumWormEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gum_worm.png");

    public GumWormRenderer(RenderManager renderManager) {
        super(renderManager, new GumWormModel(), 1.2F);
    }

    @Override
    protected void applyRotations(GumWormEntity entity, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entity, ageInTicks, rotationYaw, partialTicks);
        GlStateManager.translate(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-entity.getViewXRot(partialTicks), 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -1.0F, 0.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(GumWormEntity entity) {
        return TEXTURE;
    }
}
