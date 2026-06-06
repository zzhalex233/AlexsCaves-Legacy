package com.zzhalex233.alexscaves.client.render.item;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GalenaGauntletModel;
import com.zzhalex233.alexscaves.server.item.GalenaGauntletItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GalenaGauntletItemStackRenderer extends TileEntityItemStackRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/galena_gauntlet.png");
    private static final ResourceLocation RED_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/galena_gauntlet_red.png");
    private static final ResourceLocation BLUE_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/galena_gauntlet_blue.png");
    private static final GalenaGauntletModel MODEL = new GalenaGauntletModel(false);

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        float openAmount = GalenaGauntletItem.getLerpedUseTime(stack, partialTicks) / 5.0F;
        float closeAmount = 1.0F - openAmount;
        float ageInTicks = Minecraft.getMinecraft().player == null ? 0.0F : Minecraft.getMinecraft().player.ticksExisted + partialTicks;
        MODEL.setup(openAmount, ageInTicks);

        GlStateManager.pushMatrix();
        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-180.0F, 0.0F, 1.0F, 0.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        renderOverlay(BLUE_TEXTURE, openAmount);
        renderOverlay(RED_TEXTURE, closeAmount);
        GlStateManager.popMatrix();
    }

    private void renderOverlay(ResourceLocation texture, float alpha) {
        if (alpha <= 0.0F) {
            return;
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        MODEL.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }
}
