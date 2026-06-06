package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SirenLightModel extends ModelBase {
    private final ModelRenderer main;
    private final ModelRenderer siren;

    public SirenLightModel() {
        textureWidth = 64;
        textureHeight = 64;
        main = new ModelRenderer(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);
        main.setTextureOffset(0, 12).addBox(-4.0F, -10.0F, -4.0F, 8, 8, 8);
        main.setTextureOffset(0, 0).addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10);
        siren = new ModelRenderer(this);
        siren.setRotationPoint(0.0F, -2.0F, 0.0F);
        siren.setTextureOffset(0, 28).addBox(-2.0F, -6.0F, -2.0F, 4, 6, 4);
        main.addChild(siren);
    }

    public void setup(float rotation) {
        siren.rotateAngleY = (float) Math.toRadians(rotation);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        main.render(scale);
    }
}
