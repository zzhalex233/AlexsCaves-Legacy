package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.CaramelCubeModel;
import com.zzhalex233.alexscaves.server.entity.living.CaramelCubeEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class CaramelCubeRenderer extends RenderLiving<CaramelCubeEntity> {
    private static final ResourceLocation TEXTURE_SMALL = new ResourceLocation(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_small.png");
    private static final ResourceLocation TEXTURE_MEDIUM = new ResourceLocation(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_medium.png");
    private static final ResourceLocation TEXTURE_LARGE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_large.png");
    private static final ResourceLocation TEXTURE_SMALL_OUTSIDE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_small_outside.png");
    private static final ResourceLocation TEXTURE_MEDIUM_OUTSIDE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_medium_outside.png");
    private static final ResourceLocation TEXTURE_LARGE_OUTSIDE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/caramel_cube/caramel_cube_large_outside.png");

    public CaramelCubeRenderer(RenderManager renderManager) {
        super(renderManager, new CaramelCubeModel(), 0.65F);
        addLayer(new LayerOutside());
    }

    @Override
    protected void preRenderCallback(CaramelCubeEntity entity, float partialTick) {
        int size = entity.getSlimeSize();
        float scale = size == 2 ? 4.0F : size == 1 ? 2.0F : 1.0F;
        float jump = entity.getJumpProgress(partialTick);
        float squish = entity.getSquishProgress(partialTick);
        float jiggle = entity.getJiggleTime(partialTick) * 0.5F;
        float xz = scale * (1.0F - 0.12F * jump + 0.18F * squish + jiggle);
        float y = scale * (1.0F + 0.35F * jump - 0.08F * squish - jiggle);
        GlStateManager.scale(xz, Math.max(0.1F, y), xz);
    }

    @Override
    protected ResourceLocation getEntityTexture(CaramelCubeEntity entity) {
        return getInsideTexture(entity);
    }

    private ResourceLocation getInsideTexture(CaramelCubeEntity entity) {
        return entity.getSlimeSize() == 2 ? TEXTURE_LARGE : entity.getSlimeSize() == 1 ? TEXTURE_MEDIUM : TEXTURE_SMALL;
    }

    private ResourceLocation getOutsideTexture(CaramelCubeEntity entity) {
        return entity.getSlimeSize() == 2 ? TEXTURE_LARGE_OUTSIDE : entity.getSlimeSize() == 1 ? TEXTURE_MEDIUM_OUTSIDE : TEXTURE_SMALL_OUTSIDE;
    }

    private class LayerOutside implements LayerRenderer<CaramelCubeEntity> {
        @Override
        public void doRenderLayer(CaramelCubeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (!entity.isInvisible()) {
                bindTexture(getOutsideTexture(entity));
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
                getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableBlend();
            }
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
