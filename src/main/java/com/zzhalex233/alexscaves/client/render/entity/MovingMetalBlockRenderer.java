package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.server.entity.item.MovingMetalBlockEntity;
import com.zzhalex233.alexscaves.server.entity.util.MovingBlockData;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;

public class MovingMetalBlockRenderer extends Render<MovingMetalBlockEntity> {
    public MovingMetalBlockRenderer(RenderManager renderManager) {
        super(renderManager);
        shadowSize = 0.5F;
    }

    @Override
    public void doRender(MovingMetalBlockEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        for (MovingBlockData data : entity.getBlockData()) {
            IBlockState state = data.state();
            if (state.getBlock().getRenderType(state) == EnumBlockRenderType.INVISIBLE) {
                continue;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(x - 0.5D + data.offset().getX(), y - 0.5D + data.offset().getY(), z - 0.5D + data.offset().getZ());
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, entity.getBrightness());
            GlStateManager.popMatrix();
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(MovingMetalBlockEntity entity) {
        return null;
    }
}
