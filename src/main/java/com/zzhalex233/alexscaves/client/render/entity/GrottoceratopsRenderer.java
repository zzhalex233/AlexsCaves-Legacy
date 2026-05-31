package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.GrottoceratopsModel;
import com.zzhalex233.alexscaves.server.entity.living.GrottoceratopsEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class GrottoceratopsRenderer extends RenderLiving<GrottoceratopsEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/grottoceratops.png");
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation(AlexsCaves.MODID, "textures/entity/grottoceratops_baby.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation(AlexsCaves.MODID, "textures/entity/grottoceratops_retro.png");
    private static final ResourceLocation TEXTURE_RETRO_BABY = new ResourceLocation(AlexsCaves.MODID, "textures/entity/grottoceratops_retro_baby.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation(AlexsCaves.MODID, "textures/entity/grottoceratops_tectonic.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BABY = new ResourceLocation(AlexsCaves.MODID, "textures/entity/grottoceratops_tectonic_baby.png");

    public GrottoceratopsRenderer(RenderManager renderManager) {
        super(renderManager, new GrottoceratopsModel(), 1.1F);
    }

    @Override
    protected void preRenderCallback(GrottoceratopsEntity entity, float partialTick) {
        if (entity.isChild()) {
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(GrottoceratopsEntity entity) {
        if (entity.getAltSkin() == 1) {
            return entity.isChild() ? TEXTURE_RETRO_BABY : TEXTURE_RETRO;
        }
        if (entity.getAltSkin() == 2) {
            return entity.isChild() ? TEXTURE_TECTONIC_BABY : TEXTURE_TECTONIC;
        }
        return entity.isChild() ? TEXTURE_BABY : TEXTURE;
    }
}
