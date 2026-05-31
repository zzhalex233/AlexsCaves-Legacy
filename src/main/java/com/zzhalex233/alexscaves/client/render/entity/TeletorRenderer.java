package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.TeletorModel;
import com.zzhalex233.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.zzhalex233.alexscaves.server.entity.living.TeletorEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TeletorRenderer extends RenderLiving<TeletorEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/teletor.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(AlexsCaves.MODID, "textures/entity/teletor_glow.png");

    public TeletorRenderer(RenderManager renderManager) {
        super(renderManager, new TeletorModel(), 0.5F);
        addLayer(new LayerGlow());
    }

    @Override
    public void doRender(TeletorEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        if (entity.getWeapon() instanceof MagneticWeaponEntity && entity.getControlProgress(partialTicks) > 0.0F) {
            renderMagneticLine(entity, (MagneticWeaponEntity) entity.getWeapon(), x, y, z, partialTicks);
        }
    }

    private void renderMagneticLine(TeletorEntity entity, MagneticWeaponEntity weapon, double x, double y, double z, float partialTicks) {
        Vec3d start = entity.getPositionVector().add(0.0D, 1.15D, 0.0D);
        Vec3d end = weapon.getPositionVector().add(0.0D, 0.2D, 0.0D);
        Vec3d diff = end.subtract(start);
        float length = (float) diff.length();
        if (length <= 0.01F) {
            return;
        }
        float yaw = (float) MathHelper.atan2(diff.x, diff.z) * 180.0F / (float) Math.PI;
        float pitch = -(float) MathHelper.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)) * 180.0F / (float) Math.PI;
        float alpha = entity.getControlProgress(partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.15F, (float) z);
        GlStateManager.rotate(yaw - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, -0.06D, 0.0D).color(36, 122, 255, (int) (190.0F * alpha)).endVertex();
        buffer.pos(length, -0.02D, 0.0D).color(255, 42, 80, 0).endVertex();
        buffer.pos(length, 0.02D, 0.0D).color(255, 42, 80, 0).endVertex();
        buffer.pos(0.0D, 0.06D, 0.0D).color(36, 122, 255, (int) (190.0F * alpha)).endVertex();
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(TeletorEntity entity) {
        return TEXTURE;
    }

    private class LayerGlow implements LayerRenderer<TeletorEntity> {
        @Override
        public void doRenderLayer(TeletorEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.65F + entity.getControlProgress(partialTicks) * 0.35F);
            bindTexture(TEXTURE_GLOW);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
