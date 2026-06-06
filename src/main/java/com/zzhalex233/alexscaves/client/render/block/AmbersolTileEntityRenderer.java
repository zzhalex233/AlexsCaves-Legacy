package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.server.block.entity.AmbersolTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;

public class AmbersolTileEntityRenderer extends TileEntitySpecialRenderer<AmbersolTileEntity> {
    private static final float HALF_SQRT_3 = 0.8660254F;
    private static final int SHINE_R = 215;
    private static final int SHINE_G = 89;
    private static final int SHINE_B = 32;
    private static final int SHINE_CENTER_R = 255;
    private static final int SHINE_CENTER_G = 254;
    private static final int SHINE_CENTER_B = 233;

    @Override
    public void render(AmbersolTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (Minecraft.getMinecraft().getRenderViewEntity() == null || te.getWorld() == null) {
            return;
        }
        Vec3d camera = Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(partialTicks);
        float scale = te.calculateShineScale(camera);
        if (scale <= 0.0F) {
            return;
        }
        float time = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + partialTicks;
        float time1 = time * te.getRotSpeed() * 0.3F;
        float time2 = time * 0.1F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F) * Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(te.getRotOffset(), 0.0F, 0.0F, -1.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        int lights = te.getLights();
        for (int i = 0; i < lights; i++) {
            float length = (float) (3.0F + Math.sin(time2 + i * 2.0F)) * scale;
            float width = (float) (1.0F - 0.2F * Math.abs(Math.cos(time2 - i * Math.PI * 0.5F))) * scale;
            GlStateManager.pushMatrix();
            GlStateManager.rotate(time1 - i / (float) lights * 360.0F, 0.0F, 0.0F, -1.0F);
            renderShine(length, width);
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderShine(float length, float width) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 0.0D, 0.0D).color(SHINE_CENTER_R, SHINE_CENTER_G, SHINE_CENTER_B, 230).endVertex();
        buffer.pos(-HALF_SQRT_3 * width, length, 0.0D).color(SHINE_R, SHINE_G, SHINE_B, 0).endVertex();
        buffer.pos(HALF_SQRT_3 * width, length, 0.0D).color(SHINE_R, SHINE_G, SHINE_B, 0).endVertex();
        buffer.pos(-HALF_SQRT_3 * width, length, 0.0D).color(SHINE_R, SHINE_G, SHINE_B, 0).endVertex();
        tessellator.draw();
    }
}
