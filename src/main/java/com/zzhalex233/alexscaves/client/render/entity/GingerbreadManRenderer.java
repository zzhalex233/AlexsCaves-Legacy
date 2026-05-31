package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GingerbreadManModel;
import com.zzhalex233.alexscaves.server.entity.living.GingerbreadManEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GingerbreadManRenderer extends RenderLiving<GingerbreadManEntity> {
    private static final ResourceLocation[] TEXTURES_FOR_VARIANT = new ResourceLocation[GingerbreadManEntity.MAX_VARIANTS + 1];
    private static final ResourceLocation TEXTURE_ALEX = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_alex.png");
    private static final ResourceLocation TEXTURE_CARRO = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_carro.png");
    private static final ResourceLocation TEXTURE_DENO = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_deno.png");
    private static final ResourceLocation TEXTURE_GATETOH = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_gatetoh.png");
    private static final ResourceLocation TEXTURE_HOLIDAY = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_holiday.png");
    private static final ResourceLocation TEXTURE_PINKY = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_pinky.png");
    private static final ResourceLocation TEXTURE_PLUMMET = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_plummet.png");
    private static final ResourceLocation TEXTURE_VAKY = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_vaky.png");
    private static final ResourceLocation TEXTURE_TEAM_OVERLAY = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_team_overlay.png");

    static {
        for (int i = 0; i <= GingerbreadManEntity.MAX_VARIANTS; i++) {
            TEXTURES_FOR_VARIANT[i] = new ResourceLocation(AlexsCaves.MODID, "textures/entity/gingerbread_man/gingerbread_man_" + i + ".png");
        }
    }

    public GingerbreadManRenderer(RenderManager renderManager) {
        super(renderManager, new GingerbreadManModel(), 0.25F);
        addLayer(new LayerHeldItem());
        addLayer(new LayerTeamOverlay());
    }

    @Override
    protected ResourceLocation getEntityTexture(GingerbreadManEntity entity) {
        if (entity.hasCustomName()) {
            String name = entity.getCustomNameTag().toLowerCase();
            if (name.contains("alex")) {
                return TEXTURE_ALEX;
            } else if (name.contains("carro")) {
                return TEXTURE_CARRO;
            } else if (name.contains("deno")) {
                return TEXTURE_DENO;
            } else if (name.contains("gatetoh")) {
                return TEXTURE_GATETOH;
            } else if (name.contains("holiday")) {
                return TEXTURE_HOLIDAY;
            } else if (name.contains("pinky")) {
                return TEXTURE_PINKY;
            } else if (name.contains("plummet")) {
                return TEXTURE_PLUMMET;
            } else if (name.contains("vaky")) {
                return TEXTURE_VAKY;
            }
        }
        return TEXTURES_FOR_VARIANT[Math.max(0, Math.min(GingerbreadManEntity.MAX_VARIANTS, entity.getVariant()))];
    }

    private class LayerHeldItem implements LayerRenderer<GingerbreadManEntity> {
        @Override
        public void doRenderLayer(GingerbreadManEntity gingerbread, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            ItemStack stack = gingerbread.getHeldItemOffhand();
            if (stack.isEmpty()) {
                return;
            }
            boolean left = !gingerbread.isLeftHanded();
            GlStateManager.pushMatrix();
            ((GingerbreadManModel) getMainModel()).translateToHand(left);
            GlStateManager.translate(left ? 0.12F : -0.12F, -0.05F + 0.1F * gingerbread.getCarryItemProgress(partialTicks), -0.15F);
            GlStateManager.rotate(-160.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(left ? -20.0F : 20.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.35F, 0.35F, 0.35F);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
            GlStateManager.popMatrix();
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }

    private class LayerTeamOverlay implements LayerRenderer<GingerbreadManEntity> {
        @Override
        public void doRenderLayer(GingerbreadManEntity gingerbread, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (!gingerbread.isOvenSpawned() || gingerbread.getGingerbreadTeamColor() < 0) {
                return;
            }
            int color = gingerbread.getGingerbreadTeamColor();
            float r = ((color & 0xFF0000) >> 16) / 255.0F;
            float g = ((color & 0x00FF00) >> 8) / 255.0F;
            float b = (color & 0x0000FF) / 255.0F;
            bindTexture(TEXTURE_TEAM_OVERLAY);
            GlStateManager.color(r, g, b, 1.0F);
            getMainModel().render(gingerbread, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
