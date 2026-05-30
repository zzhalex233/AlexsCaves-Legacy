package com.zzhalex233.alexscaves.client.render.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.model.SweetishFishModel;
import com.zzhalex233.alexscaves.server.entity.living.SweetishFishEntity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class SweetishFishRenderer extends RenderLiving<SweetishFishEntity> {
    private static final ResourceLocation TEXTURE_RED = texture("sweetish_fish_red");
    private static final ResourceLocation TEXTURE_GREEN = texture("sweetish_fish_green");
    private static final ResourceLocation TEXTURE_YELLOW = texture("sweetish_fish_yellow");
    private static final ResourceLocation TEXTURE_BLUE = texture("sweetish_fish_blue");
    private static final ResourceLocation TEXTURE_PINK = texture("sweetish_fish_pink");

    public SweetishFishRenderer(RenderManager renderManager) {
        super(renderManager, new SweetishFishModel(), 0.35F);
    }

    @Override
    protected ResourceLocation getEntityTexture(SweetishFishEntity entity) {
        switch (entity.getGummyColor()) {
            case GREEN:
                return TEXTURE_GREEN;
            case YELLOW:
                return TEXTURE_YELLOW;
            case BLUE:
                return TEXTURE_BLUE;
            case PINK:
                return TEXTURE_PINK;
            default:
                return TEXTURE_RED;
        }
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(AlexsCaves.MODID, "textures/entity/" + name + ".png");
    }
}
