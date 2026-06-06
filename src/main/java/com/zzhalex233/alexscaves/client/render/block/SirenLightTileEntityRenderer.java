package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.SirenLightModel;
import com.zzhalex233.alexscaves.server.block.SirenLightBlock;
import com.zzhalex233.alexscaves.server.block.entity.SirenLightTileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class SirenLightTileEntityRenderer extends TileEntitySpecialRenderer<SirenLightTileEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/siren_light.png");
    private static final ResourceLocation COLOR_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/siren_light_color.png");
    private static final SirenLightModel MODEL = new SirenLightModel();
    private static final float HALF_SQRT_3 = 0.8660254F;

    @Override
    public void render(SirenLightTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        renderLight(x, y, z, state.getValue(SirenLightBlock.FACING), te.getSirenRotation(partialTicks), te.getOnProgress(partialTicks), te.getColor(), state.getValue(SirenLightBlock.POWERED));
    }

    public static void renderItem(float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        renderModel(0.0F, 0x00FF00, false);
        GlStateManager.popMatrix();
    }

    private static void renderLight(double x, double y, double z, EnumFacing facing, float rotation, float onProgress, int color, boolean powered) {
        GlStateManager.pushMatrix();
        translateForFacing(x, y, z, facing);
        MODEL.setup(rotation);
        renderModel(rotation, color, powered);
        if (onProgress > 0.0F) {
            renderBeam(rotation, onProgress, color);
        }
        GlStateManager.popMatrix();
    }

    private static void renderModel(float rotation, int color, boolean powered) {
        MODEL.setup(rotation);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        MODEL.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(r, g, b, powered ? 1.0F : 0.65F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(COLOR_TEXTURE);
        MODEL.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    private static void renderBeam(float rotation, float onProgress, int color) {
        float length = onProgress * 1.25F;
        float width = onProgress * onProgress * 0.5F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 1.125F, 0.0F);
        GlStateManager.rotate(rotation + 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        drawTriangle(length, width, r, g, b);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        drawTriangle(length, width, r, g, b);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static void drawTriangle(float length, float width, float r, float g, float b) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 0.0D, 0.0D).color(r, g, b, 0.8F).endVertex();
        buffer.pos(-HALF_SQRT_3 * width, length, 0.0D).color(r, g, b, 0.0F).endVertex();
        buffer.pos(HALF_SQRT_3 * width, length, 0.0D).color(r, g, b, 0.0F).endVertex();
        tessellator.draw();
    }

    private static void translateForFacing(double x, double y, double z, EnumFacing facing) {
        switch (facing) {
            case DOWN:
                GlStateManager.translate(x + 0.5D, y - 0.5D, z + 0.5D);
                break;
            case NORTH:
                GlStateManager.translate(x + 0.5D, y + 0.5D, z - 0.5D);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.translate(x + 0.5D, y + 0.5D, z + 1.5D);
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.translate(x - 0.5D, y + 0.5D, z + 0.5D);
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case EAST:
                GlStateManager.translate(x + 1.5D, y + 0.5D, z + 0.5D);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case UP:
            default:
                GlStateManager.translate(x + 0.5D, y + 1.5D, z + 0.5D);
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                break;
        }
    }
}
