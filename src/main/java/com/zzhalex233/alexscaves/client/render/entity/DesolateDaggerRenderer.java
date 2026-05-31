package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.server.entity.item.DesolateDaggerEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DesolateDaggerRenderer extends Render<DesolateDaggerEntity> {
    public DesolateDaggerRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(DesolateDaggerEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.2F, (float) z);
        float stab = entity.getStab(partialTicks);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks + 90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, -stab * 0.7F);
        GlStateManager.color(1.0F, 0.15F, 0.15F, 0.85F);
        ItemStack stack = entity.getItemStack().isEmpty() ? new ItemStack(ACItemRegistry.DESOLATE_DAGGER.item()) : entity.getItemStack();
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.GROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(DesolateDaggerEntity entity) {
        return null;
    }
}
