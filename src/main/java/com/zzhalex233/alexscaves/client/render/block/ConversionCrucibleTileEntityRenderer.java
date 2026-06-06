package com.zzhalex233.alexscaves.client.render.block;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.ConversionCrucibleModel;
import com.zzhalex233.alexscaves.server.block.entity.ConversionCrucibleTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ConversionCrucibleTileEntityRenderer extends TileEntitySpecialRenderer<ConversionCrucibleTileEntity> {
    private static final ConversionCrucibleModel MODEL = new ConversionCrucibleModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/conversion_crucible.png");
    private static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation(AlexsCaves.MODID, "textures/entity/conversion_crucible_active.png");
    private static final ResourceLocation TEXTURE_FLUID = new ResourceLocation(AlexsCaves.MODID, "textures/entity/conversion_crucible_fluid.png");
    private static final float HALF_SQRT_3 = 0.8660254F;

    @Override
    public void render(ConversionCrucibleTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        float conversionProgress = te.getConversionProgress(partialTicks);
        float splashProgress = te.getSplashProgress(partialTicks);
        float showItemProgress = te.getItemDisplayProgress(partialTicks) * (1.0F - splashProgress);
        float ageInTicks = te.tickCount + partialTicks;
        int color = te.getConvertingToColor();
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.5D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        MODEL.setup(splashProgress, conversionProgress, ageInTicks, te.getFilledLevel());
        MODEL.hideBeam(true);
        MODEL.hideSauce(true);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        MODEL.render(0.0625F);

        if (te.getFilledLevel() > 0) {
            GlStateManager.enableBlend();
            GlStateManager.disableCull();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_FLUID);
            GlStateManager.color(r, g, b, 0.9F);
            MODEL.hideBeam(true);
            MODEL.hideSauce(false);
            MODEL.render(0.0625F);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
        }

        if (splashProgress > 0.0F || conversionProgress > 0.0F) {
            GlStateManager.enableBlend();
            GlStateManager.disableCull();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_OVERLAY);
            GlStateManager.color(r, g, b, Math.max(splashProgress, conversionProgress));
            MODEL.hideBeam(false);
            MODEL.hideSauce(true);
            MODEL.render(0.0625F);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        if (conversionProgress > 0.0F) {
            renderConversionHexes(x, y, z, ageInTicks, conversionProgress, r, g, b);
        }
        if (showItemProgress > 0.0F) {
            renderDisplayItemAndText(te, x, y, z, ageInTicks, showItemProgress, r, g, b);
        }
    }

    private void renderConversionHexes(double x, double y, double z, float ageInTicks, float progress, float r, float g, float b) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.35D, z + 0.5D);
        GlStateManager.rotate(ageInTicks * 15.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        for (int i = 0; i < 5; i++) {
            float radius = progress * (1.25F + i * 0.25F);
            drawHex(radius, r, g, b, (1.0F - i / 5.0F) * 0.35F);
            GlStateManager.translate(0.0F, -0.035F, 0.0F);
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderDisplayItemAndText(ConversionCrucibleTileEntity te, double x, double y, double z, float ageInTicks, float progress, float r, float g, float b) {
        float bob = ((float) Math.sin(ageInTicks * 0.25F) * 0.25F + 0.75F) * 0.4F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.25D, z + 0.5D);
        renderItemBeam(progress, bob, r, g, b);
        ItemStack stack = te.getDisplayItem();
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.15F, bob - 0.15F, -0.15F);
            GlStateManager.scale(0.35F * progress, 0.35F * progress, 0.35F * progress);
            GlStateManager.translate(0.5F, 0.0F, 0.5F);
            GlStateManager.rotate(ageInTicks * 3.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.translate(-0.5F, 0.0F, -0.5F);
            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
        String text = te.getDisplayText();
        if (!text.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.45F + bob, 0.0F);
            GlStateManager.rotate(180.0F - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-0.02F, -0.02F, 0.02F);
            int alpha = Math.max(4, Math.min(255, (int) (progress * 255.0F)));
            int textColor = alpha << 24 | 0xFFFFFF;
            int width = getFontRenderer().getStringWidth(text);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            getFontRenderer().drawString(text, -width / 2, 0, textColor);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private void renderItemBeam(float progress, float bob, float r, float g, float b) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -1.1F, 0.0F);
        GlStateManager.rotate(180.0F - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        float length = 1.25F + bob;
        float width = 0.35F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0D, 0.0D, 0.0D).color(r, g, b, 0.55F * progress).endVertex();
        buffer.pos(-HALF_SQRT_3 * width, length, 0.0D).color(r, g, b, 0.0F).endVertex();
        buffer.pos(HALF_SQRT_3 * width, length, 0.0D).color(r, g, b, 0.0F).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void drawHex(float radius, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 3.0D * i;
            buffer.pos(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius).color(r, g, b, a).endVertex();
        }
        tessellator.draw();
    }
}
