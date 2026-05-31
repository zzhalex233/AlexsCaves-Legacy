package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.server.entity.item.MagneticWeaponEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class MagneticWeaponRenderer extends Render<MagneticWeaponEntity> {
    public MagneticWeaponRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(MagneticWeaponEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack stack = entity.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        float strike = entity.getStrikeProgress(partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.2F, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks + 90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, -strike * 0.65F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.GROUND);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(MagneticWeaponEntity entity) {
        return null;
    }
}
