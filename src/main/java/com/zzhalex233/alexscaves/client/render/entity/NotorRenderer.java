package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.NotorModel;
import com.zzhalex233.alexscaves.server.entity.living.NotorEntity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class NotorRenderer extends RenderLiving<NotorEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/notor.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(AlexsCaves.MODID, "textures/entity/notor_glow.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation(AlexsCaves.MODID, "textures/entity/notor_eyes.png");

    public NotorRenderer(RenderManager renderManager) {
        super(renderManager, new NotorModel(), 0.25F);
        addLayer(new LayerGlow());
    }

    @Override
    public void doRender(NotorEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        if (entity.isEntityAlive() && entity.getBeamProgress(partialTicks) > 0.0F) {
            renderBeam(entity, x, y, z, partialTicks);
        }
    }

    private void renderBeam(NotorEntity entity, double x, double y, double z, float partialTicks) {
        Vec3d start = entity.getPositionVector().add(0.0D, 1.5D, 0.0D);
        Vec3d end = entity.getBeamEndPosition(partialTicks);
        Vec3d diff = end.subtract(start);
        float length = (float) Math.sqrt(diff.x * diff.x + diff.y * diff.y + diff.z * diff.z) * entity.getBeamProgress(partialTicks);
        float yaw = (float) MathHelper.atan2(diff.x, diff.z) * 180.0F / (float) Math.PI;
        float pitch = -(float) MathHelper.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)) * 180.0F / (float) Math.PI;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 1.5F, (float) z);
        GlStateManager.rotate(yaw - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float width = entity.showingHologram() ? 0.45F : 0.25F;
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, -width, 0.0D).color(70, 210, 255, 190).endVertex();
        buffer.pos(length, -width * 0.35F, 0.0D).color(70, 210, 255, 0).endVertex();
        buffer.pos(length, width * 0.35F, 0.0D).color(70, 210, 255, 0).endVertex();
        buffer.pos(0.0D, width, 0.0D).color(70, 210, 255, 190).endVertex();
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(NotorEntity entity) {
        return TEXTURE;
    }

    public static void renderEntityInHologram(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        RenderManager manager = net.minecraft.client.Minecraft.getMinecraft().getRenderManager();
        Render<? super Entity> render = manager.getEntityRenderObject(entity);
        if (render == null) {
            return;
        }
        float prevRotationYaw = entity.prevRotationYaw;
        float rotationYaw = entity.rotationYaw;
        float prevRotationPitch = entity.prevRotationPitch;
        float rotationPitch = entity.rotationPitch;
        float prevRenderYawOffset = 0.0F;
        float renderYawOffset = 0.0F;
        float prevRotationYawHead = 0.0F;
        float rotationYawHead = 0.0F;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            prevRenderYawOffset = living.prevRenderYawOffset;
            renderYawOffset = living.renderYawOffset;
            prevRotationYawHead = living.prevRotationYawHead;
            rotationYawHead = living.rotationYawHead;
            living.prevRenderYawOffset = 0.0F;
            living.renderYawOffset = 0.0F;
            living.prevRotationYawHead = 0.0F;
            living.rotationYawHead = 0.0F;
        }
        entity.prevRotationYaw = 0.0F;
        entity.rotationYaw = 0.0F;
        entity.prevRotationPitch = 0.0F;
        entity.rotationPitch = 0.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.color(0.45F, 0.85F, 1.0F, 0.7F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        render.doRender(entity, x, y, z, yaw, partialTicks);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        entity.prevRotationYaw = prevRotationYaw;
        entity.rotationYaw = rotationYaw;
        entity.prevRotationPitch = prevRotationPitch;
        entity.rotationPitch = rotationPitch;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            living.prevRenderYawOffset = prevRenderYawOffset;
            living.renderYawOffset = renderYawOffset;
            living.prevRotationYawHead = prevRotationYawHead;
            living.rotationYawHead = rotationYawHead;
        }
    }

    private class LayerGlow implements LayerRenderer<NotorEntity> {
        @Override
        public void doRenderLayer(NotorEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
            bindTexture(TEXTURE_GLOW);
            getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            bindTexture(TEXTURE_EYES);
            float alpha = entity.getBeamProgress(partialTicks) > 0.0F ? 1.0F : 0.7F;
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
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
