package com.zzhalex233.alexscaves.client.render.item;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.ResistorShieldModel;
import com.zzhalex233.alexscaves.server.item.ResistorShieldItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ResistorShieldItemStackRenderer extends TileEntityItemStackRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/resistor_shield.png");
    private static final ResourceLocation RED_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/resistor_shield_red.png");
    private static final ResourceLocation BLUE_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/resistor_shield_blue.png");
    private static final ResistorShieldModel MODEL = new ResistorShieldModel();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        float useTime = ResistorShieldItem.getLerpedUseTime(stack, partialTicks);
        float useProgress = Math.min(10.0F, useTime) / 10.0F;
        float switchAmount = ResistorShieldItem.getLerpedSwitchTime(stack, partialTicks) / 5.0F;
        float ageInTicks = Minecraft.getMinecraft().player == null ? 0.0F : Minecraft.getMinecraft().player.ticksExisted + partialTicks;
        MODEL.setupAnim(useProgress, switchAmount);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.25F, 0.125F);
        GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        MODEL.render(null, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
        renderOverlay(BLUE_TEXTURE, 1.0F - switchAmount, ageInTicks);
        renderOverlay(RED_TEXTURE, switchAmount, ageInTicks);
        GlStateManager.popMatrix();
    }

    private void renderOverlay(ResourceLocation texture, float alpha, float ageInTicks) {
        if (alpha <= 0.0F) {
            return;
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        MODEL.render(null, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }
}
