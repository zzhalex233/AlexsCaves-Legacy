package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.WaveModel;
import com.zzhalex233.alexscaves.server.entity.item.WaveEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class WaveRenderer extends Render<WaveEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/wave_0.png"),
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/wave_1.png"),
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/wave_2.png"),
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/wave_3.png")
    };
    private static final ResourceLocation[] OVERLAYS = new ResourceLocation[] {
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/wave_overlay_0.png"),
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/wave_overlay_1.png"),
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/wave_overlay_2.png"),
        new ResourceLocation(AlexsCaves.MODID, "textures/entity/deep_one/wave_overlay_3.png")
    };
    private final WaveModel model = new WaveModel();

    public WaveRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.0F;
    }

    @Override
    public void doRender(WaveEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity.isInvisible()) {
            return;
        }
        float age = entity.activeWaveTicks + partialTicks;
        float rise = age / 10.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.5F, (float) z);
        GlStateManager.rotate(-interpolate(entity.prevRotationYaw, entity.rotationYaw, partialTicks) + 180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.1F + (1.0F - rise) * -1.0F, -0.5F);
        GlStateManager.scale(entity.getWaveScale(), -(0.2F + rise * 0.9F) * entity.getWaveScale(), entity.getWaveScale());
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bindTexture(getWaveTexture(entity));
        model.render(entity, 0.0F, 0.0F, age, 0.0F, 0.0F, 0.0625F);
        bindTexture(getOverlayTexture(entity));
        model.render(entity, 0.0F, 0.0F, age, 0.0F, 0.0F, 0.0625F);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private float interpolate(float prev, float current, float partialTicks) {
        return prev + (current - prev) * partialTicks;
    }

    private ResourceLocation getWaveTexture(WaveEntity entity) {
        return TEXTURES[entity.activeWaveTicks % 12 / 3];
    }

    private ResourceLocation getOverlayTexture(WaveEntity entity) {
        return OVERLAYS[entity.activeWaveTicks % 12 / 3];
    }

    @Override
    protected ResourceLocation getEntityTexture(WaveEntity entity) {
        return getWaveTexture(entity);
    }
}
