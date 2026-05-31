package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class FloaterModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer propellor;

    public FloaterModel() {
        textureWidth = 64;
        textureHeight = 64;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 15.0F, 0.0F);
        root.setTextureOffset(0, 16).addBox(-6.0F, -5.0F, -6.0F, 12, 10, 12);

        propellor = new ModelRenderer(this);
        propellor.setRotationPoint(0.0F, 5.0F, 0.0F);
        root.addChild(propellor);
        propellor.setTextureOffset(0, 0).addBox(-8.0F, 4.0F, -8.0F, 16, 0, 16);
        propellor.setTextureOffset(36, 18).addBox(-6.0F, -6.0F, 0.0F, 12, 10, 0);

        ModelRenderer crossedBlade = new ModelRenderer(this);
        crossedBlade.setRotationPoint(0.0F, 12.0F, 0.0F);
        crossedBlade.rotateAngleY = -1.5708F;
        crossedBlade.setTextureOffset(36, 18).addBox(-6.0F, -18.0F, 0.0F, 12, 10, 0);
        propellor.addChild(crossedBlade);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        propellor.rotateAngleY = ageInTicks * 0.5F;
        root.render(scale);
    }
}
