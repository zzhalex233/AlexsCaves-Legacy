package com.zzhalex233.alexscaves.client.render.item;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.RaygunModel;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.item.RaygunItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RaygunItemStackRenderer extends TileEntityItemStackRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/raygun/raygun.png");
    private static final ResourceLocation ACTIVE_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/raygun/raygun_active.png");
    private static final ResourceLocation BLUE_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/raygun/raygun_blue.png");
    private static final ResourceLocation BLUE_ACTIVE_TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/raygun/raygun_blue_active.png");
    private static final RaygunModel MODEL = new RaygunModel();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        boolean gamma = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.GAMMA_RAY, stack) > 0;
        float ageInTicks = Minecraft.getMinecraft().player == null ? 0.0F : Minecraft.getMinecraft().player.ticksExisted + partialTicks;
        float useAmount = RaygunItem.getLerpedUseTime(stack, partialTicks) / 5.0F;
        float pulseAlpha = useAmount * (0.25F + 0.25F * (float) (1.0F + Math.sin(ageInTicks * 0.8F)));
        ResourceLocation texture = gamma ? BLUE_TEXTURE : TEXTURE;
        ResourceLocation textureActive = gamma ? BLUE_ACTIVE_TEXTURE : ACTIVE_TEXTURE;
        MODEL.setupAnim(useAmount, ageInTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(0.9F, 0.9F, 0.9F);
        GlStateManager.disableCull();
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        MODEL.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        renderActiveOverlay(textureActive, pulseAlpha);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private void renderActiveOverlay(ResourceLocation texture, float alpha) {
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
