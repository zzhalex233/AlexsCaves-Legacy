package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.CaramelCubeEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class CaramelCubeModel extends ModelBase {
    private final ModelRenderer main;
    private final ModelRenderer core;

    public CaramelCubeModel() {
        textureWidth = 64;
        textureHeight = 64;
        main = new ModelRenderer(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);
        core = new ModelRenderer(this);
        core.setRotationPoint(0.0F, -5.5F, 0.0F);
        main.addChild(core);
        core.setTextureOffset(32, 12).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F);
        core.setTextureOffset(60, 0).addBox(2.5F, -1.0F, -4.5F, 1, 1, 1, 0.0F);
        core.setTextureOffset(60, 0).addBox(-2.5F, 0.0F, -4.5F, 1, 1, 1, 0.0F);
        core.setTextureOffset(0, 20).addBox(-5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F);
        core.setTextureOffset(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10, 10, 10, 0.5F);
        core.setTextureOffset(0, 30).addBox(5.0F, -4.0F, -5.0F, 0, 10, 10, 0.0F);
        core.setTextureOffset(0, 30).addBox(-5.0F, -4.0F, -5.0F, 0, 10, 10, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity instanceof CaramelCubeEntity) {
            CaramelCubeEntity cube = (CaramelCubeEntity) entity;
            float partialTick = ageInTicks - cube.ticksExisted;
            core.rotationPointY = -5.5F - cube.getJumpProgress(partialTick) * 4.0F;
        }
        main.render(scale);
    }
}
