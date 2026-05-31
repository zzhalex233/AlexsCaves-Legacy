package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.RelicheirusModel;
import com.zzhalex233.alexscaves.server.entity.living.RelicheirusEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RelicheirusRenderer extends RenderLiving<RelicheirusEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/relicheirus.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation(AlexsCaves.MODID, "textures/entity/relicheirus_retro.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation(AlexsCaves.MODID, "textures/entity/relicheirus_tectonic.png");

    public RelicheirusRenderer(RenderManager renderManager) {
        super(renderManager, new RelicheirusModel(), 1.0F);
    }

    @Override
    protected void preRenderCallback(RelicheirusEntity entity, float partialTick) {
        if (entity.isChild()) {
            GlStateManager.scale(0.35F, 0.35F, 0.35F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(RelicheirusEntity entity) {
        if (entity.getAltSkin() == 2) {
            return TEXTURE_TECTONIC;
        }
        return entity.getAltSkin() == 1 ? TEXTURE_RETRO : TEXTURE;
    }
}
