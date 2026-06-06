package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.server.block.entity.AmberMonolithTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class AmberMonolithTileEntityRenderer extends TileEntitySpecialRenderer<AmberMonolithTileEntity> {
    @Override
    public void render(AmberMonolithTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        EntityLivingBase entity = te.getDisplayEntity(Minecraft.getMinecraft().world);
        if (entity == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.65D, z + 0.5D);
        float scale = 0.45F;
        float size = Math.max(entity.width, entity.height);
        if (size > 1.0F) {
            scale /= size * 1.5F;
        }
        GlStateManager.translate(0.0D, scale * 1.5F - 1.25F, 0.0D);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(te.getRotation(partialTicks), 0.0F, 1.0F, 0.0F);
        renderEntity(entity);
        GlStateManager.popMatrix();
    }

    private void renderEntity(Entity entity) {
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        Render<? super Entity> render = manager.getEntityRenderObject(entity);
        if (render != null) {
            GlStateManager.enableBlend();
            render.doRender(entity, 0.0D, 0.0D, 0.0D, entity.rotationYaw, 0.0F);
            GlStateManager.disableBlend();
        }
    }
}
