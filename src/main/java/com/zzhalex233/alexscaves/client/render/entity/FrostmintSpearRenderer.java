package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.FrostmintSpearModel;
import com.zzhalex233.alexscaves.server.entity.item.FrostmintSpearEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class FrostmintSpearRenderer extends Render<FrostmintSpearEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/frostmint_spear.png");
    private static final FrostmintSpearModel MODEL = new FrostmintSpearModel();

    public FrostmintSpearRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(FrostmintSpearEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.25F, (float) z);
        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.rotate(yaw - 180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch + 90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.25F, 0.0F);
        bindEntityTexture(entity);
        MODEL.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(FrostmintSpearEntity entity) {
        return TEXTURE;
    }
}
