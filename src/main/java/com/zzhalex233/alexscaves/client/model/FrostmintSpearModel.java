package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class FrostmintSpearModel extends ModelBase {
    private final ModelRenderer spear;

    public FrostmintSpearModel() {
        textureWidth = 32;
        textureHeight = 32;
        spear = new ModelRenderer(this);
        spear.setRotationPoint(0.0F, 15.0F, 0.0F);
        spear.setTextureOffset(0, 0).addBox(-0.5F, -20.0F, -0.5F, 1, 29, 1, 0.0F);

        ModelRenderer upperWide = plate(7, 8, 0.0F, -23.0F, 0.0F, 12, 12);
        upperWide.rotateAngleX = 0.7854F;
        spear.addChild(upperWide);

        ModelRenderer upperSmall = plate(4, 1, 0.0F, -18.0F, 0.0F, 9, 9);
        upperSmall.rotateAngleX = 0.7854F;
        spear.addChild(upperSmall);

        ModelRenderer lowerSmall = plate(20, 1, 0.0F, 8.0F, 0.0F, 5, 5);
        lowerSmall.rotateAngleX = 0.7854F;
        spear.addChild(lowerSmall);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        spear.render(scale);
    }

    private ModelRenderer plate(int textureX, int textureY, float x, float y, float z, int height, int depth) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(x, y, z);
        renderer.setTextureOffset(textureX, textureY).addBox(0.0F, -height / 2.0F, -depth / 2.0F, 0, height, depth, 0.0F);
        return renderer;
    }
}
