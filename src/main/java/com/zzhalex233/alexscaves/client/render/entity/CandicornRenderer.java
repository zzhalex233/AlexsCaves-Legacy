package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.CandicornModel;
import com.zzhalex233.alexscaves.server.entity.living.CandicornEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class CandicornRenderer extends RenderLiving<CandicornEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            texture(0), texture(1), texture(2), texture(3), texture(4)
    };

    public CandicornRenderer(RenderManager renderManager) {
        super(renderManager, new CandicornModel(), 0.75F);
    }

    @Override
    protected void preRenderCallback(CandicornEntity entity, float partialTick) {
        if (entity.isChild()) {
            GlStateManager.scale(0.55F, 0.55F, 0.55F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(CandicornEntity entity) {
        return TEXTURES[MathHelper.clamp(entity.getVariant(), 0, TEXTURES.length - 1)];
    }

    private static ResourceLocation texture(int variant) {
        return new ResourceLocation(AlexsCaves.MODID, "textures/entity/candicorn_" + variant + ".png");
    }
}
