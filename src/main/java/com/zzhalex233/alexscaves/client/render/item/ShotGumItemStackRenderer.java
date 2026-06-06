package com.zzhalex233.alexscaves.client.render.item;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.ShotGumModel;
import com.zzhalex233.alexscaves.server.item.ShotGumItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ShotGumItemStackRenderer extends TileEntityItemStackRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/shot_gum.png");
    private static final ResourceLocation GLASS_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/shot_gum_glass.png");
    private static final ShotGumModel MODEL = new ShotGumModel();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        float shootProgress = ShotGumItem.getLerpedShootTime(stack, partialTicks) / 5.0F;
        MODEL.setup(shootProgress, ShotGumItem.getGumballsLeft(stack), ShotGumItem.getLerpedCrankAngle(stack, partialTicks));

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.8F, 0.8F, 0.8F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GLASS_TEXTURE);
        MODEL.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }
}
