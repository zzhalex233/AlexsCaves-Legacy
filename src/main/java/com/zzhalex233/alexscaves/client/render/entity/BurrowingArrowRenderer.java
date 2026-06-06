package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.BurrowingArrowModel;
import com.zzhalex233.alexscaves.server.entity.item.BurrowingArrowEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class BurrowingArrowRenderer extends Render<BurrowingArrowEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/burrowing_arrow.png");
    private final BurrowingArrowModel model = new BurrowingArrowModel();

    public BurrowingArrowRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(BurrowingArrowEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(interpolate(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-interpolate(entity.prevRotationPitch, entity.rotationPitch, partialTicks), 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, -0.35F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        float shake = entity.arrowShake - partialTicks;
        if (shake > 0.0F) {
            GlStateManager.rotate(-MathHelper.sin(shake * 3.0F) * shake, 0.0F, 0.0F, 1.0F);
        }
        bindEntityTexture(entity);
        model.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(BurrowingArrowEntity entity) {
        return TEXTURE;
    }

    private float interpolate(float prev, float current, float partialTicks) {
        return prev + (current - prev) * partialTicks;
    }
}
