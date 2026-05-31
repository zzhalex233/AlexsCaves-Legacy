package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class FerrouslimeModel extends ModelBase {
    private final ModelRenderer ferrouslime;

    public FerrouslimeModel() {
        textureWidth = 32;
        textureHeight = 32;
        ferrouslime = new ModelRenderer(this);
        ferrouslime.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
        ferrouslime.setTextureOffset(0, 12).addBox(-3.5F, 0.0F, -3.5F, 2, 2, 2, 0.0F);
        ferrouslime.setTextureOffset(0, 12).addBox(1.5F, 0.0F, -3.5F, 2, 2, 2, 0.0F);
        ferrouslime.setTextureOffset(0, 12).addBox(-1.0F, -3.5F, -3.5F, 2, 2, 2, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ferrouslime.render(scale);
    }
}
