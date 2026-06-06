package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class QuarryGrinderModel extends ModelBase {
    private final ModelRenderer spinner;

    public QuarryGrinderModel() {
        textureWidth = 64;
        textureHeight = 64;
        spinner = new ModelRenderer(this);
        spinner.setRotationPoint(0.0F, 16.0F, -1.0F);
        spinner.setTextureOffset(0, 16).addBox(-6.0F, -4.0F, -4.0F, 12, 8, 8);
        spinner.setTextureOffset(0, 0).addBox(-6.0F, -4.0F, -4.0F, 12, 8, 8, 0.5F);
    }

    public void setup(float spin) {
        spinner.rotateAngleX = -spin * 0.5F;
    }

    public void render(float scale) {
        spinner.render(scale);
    }
}
