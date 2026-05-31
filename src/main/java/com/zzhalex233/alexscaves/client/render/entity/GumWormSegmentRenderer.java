package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GumWormSegmentModel;
import com.zzhalex233.alexscaves.server.entity.living.GumWormSegmentEntity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class GumWormSegmentRenderer extends Render<GumWormSegmentEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/gum_worm_segment_0.png"),
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/gum_worm_segment_1.png"),
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/gum_worm_segment_2.png")
    };
    private final GumWormSegmentModel model = new GumWormSegmentModel();

    public GumWormSegmentRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 1.0F;
    }

    @Override
    public void doRender(GumWormSegmentEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        net.minecraft.client.renderer.GlStateManager.pushMatrix();
        net.minecraft.client.renderer.GlStateManager.translate((float) x, (float) y + 1.5F, (float) z);
        net.minecraft.client.renderer.GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        net.minecraft.client.renderer.GlStateManager.rotate(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
        bindEntityTexture(entity);
        model.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
        net.minecraft.client.renderer.GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(GumWormSegmentEntity entity) {
        return TEXTURES[Math.abs(entity.getIndex()) % TEXTURES.length];
    }
}
