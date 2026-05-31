package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.VallumraptorModel;
import com.zzhalex233.alexscaves.server.entity.living.VallumraptorEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class VallumraptorRenderer extends RenderLiving<VallumraptorEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/vallumraptor.png");
    private static final ResourceLocation TEXTURE_ELDER = new ResourceLocation(AlexsCaves.MODID, "textures/entity/vallumraptor_elder.png");
    private static final ResourceLocation TEXTURE_ALAN = new ResourceLocation(AlexsCaves.MODID, "textures/entity/vallumraptor_alan.png");
    private static final ResourceLocation TEXTURE_ALAN_ELDER = new ResourceLocation(AlexsCaves.MODID, "textures/entity/vallumraptor_alan_elder.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation(AlexsCaves.MODID, "textures/entity/vallumraptor_retro.png");
    private static final ResourceLocation TEXTURE_RETRO_ELDER = new ResourceLocation(AlexsCaves.MODID, "textures/entity/vallumraptor_retro_elder.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation(AlexsCaves.MODID, "textures/entity/vallumraptor_tectonic.png");
    private static final ResourceLocation TEXTURE_TECTONIC_ELDER = new ResourceLocation(AlexsCaves.MODID, "textures/entity/vallumraptor_tectonic_elder.png");

    public VallumraptorRenderer(RenderManager manager) {
        super(manager, new VallumraptorModel(), 0.3F);
        addLayer(new ItemLayer(this));
    }

    @Override
    protected void preRenderCallback(VallumraptorEntity entity, float partialTickTime) {
        if (entity.isElder()) {
            GlStateManager.scale(1.1F, 1.1F, 1.1F);
        }
        float alpha = 1.0F - 0.75F * entity.getHideProgress(partialTickTime);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
    }

    @Override
    protected ResourceLocation getEntityTexture(VallumraptorEntity entity) {
        if (entity.hasCustomName() && "alan".equalsIgnoreCase(entity.getCustomNameTag())) {
            return entity.isElder() ? TEXTURE_ALAN_ELDER : TEXTURE_ALAN;
        }
        if (entity.getAltSkin() == 1) {
            return entity.isElder() ? TEXTURE_RETRO_ELDER : TEXTURE_RETRO;
        }
        if (entity.getAltSkin() == 2) {
            return entity.isElder() ? TEXTURE_TECTONIC_ELDER : TEXTURE_TECTONIC;
        }
        return entity.isElder() ? TEXTURE_ELDER : TEXTURE;
    }

    private static class ItemLayer implements LayerRenderer<VallumraptorEntity> {
        private final VallumraptorRenderer renderer;

        private ItemLayer(VallumraptorRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public void doRenderLayer(VallumraptorEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            ItemStack stack = entity.getHeldItemMainhand();
            if (stack.isEmpty()) {
                return;
            }
            GlStateManager.pushMatrix();
            ((VallumraptorModel) renderer.getMainModel()).translateToHand(entity.isLeftHanded());
            GlStateManager.translate(entity.isLeftHanded() ? -0.12F : 0.12F, 0.15F, -0.25F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.isLeftHanded() ? 12.0F : -12.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
            GlStateManager.popMatrix();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
