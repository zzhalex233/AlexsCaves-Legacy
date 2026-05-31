package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.FloaterModel;
import com.zzhalex233.alexscaves.server.entity.item.FloaterEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class FloaterRenderer extends Render<FloaterEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/floater.png");
    private final FloaterModel model = new FloaterModel();

    public FloaterRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.35F;
    }

    @Override
    public void doRender(FloaterEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.5F, (float) z);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        bindEntityTexture(entity);
        model.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(FloaterEntity entity) {
        return TEXTURE;
    }
}
