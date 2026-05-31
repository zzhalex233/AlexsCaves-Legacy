package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.SubterranodonModel;
import com.zzhalex233.alexscaves.server.entity.living.SubterranodonEntity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class SubterranodonRenderer extends RenderLiving<SubterranodonEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/entity/subterranodon.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation(AlexsCaves.MODID, "textures/entity/subterranodon_retro.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation(AlexsCaves.MODID, "textures/entity/subterranodon_tectonic.png");

    public SubterranodonRenderer(RenderManager renderManager) {
        super(renderManager, new SubterranodonModel(), 0.5F);
    }

    @Override
    protected void preRenderCallback(SubterranodonEntity entity, float partialTick) {
        if (entity.isChild()) {
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(SubterranodonEntity entity) {
        return entity.getAltSkin() == 1 ? TEXTURE_RETRO : entity.getAltSkin() == 2 ? TEXTURE_TECTONIC : TEXTURE;
    }
}
