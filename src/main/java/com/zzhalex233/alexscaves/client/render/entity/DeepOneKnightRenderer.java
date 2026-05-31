package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.DeepOneModel;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneKnightEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class DeepOneKnightRenderer extends RenderLiving<DeepOneKnightEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/deep_one_knight.png");

    public DeepOneKnightRenderer(RenderManager renderManager) {
        super(renderManager, new DeepOneModel(), 0.75F);
    }

    @Override
    protected void preRenderCallback(DeepOneKnightEntity entity, float partialTick) {
        GlStateManager.scale(1.12F, 1.12F, 1.12F);
    }

    @Override
    protected ResourceLocation getEntityTexture(DeepOneKnightEntity entity) {
        return TEXTURE;
    }
}
