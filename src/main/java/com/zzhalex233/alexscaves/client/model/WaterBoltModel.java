package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class WaterBoltModel extends ModelBase {
    private final ModelRenderer main;
    private final ModelRenderer crossA;
    private final ModelRenderer crossB;

    public WaterBoltModel() {
        textureWidth = 64;
        textureHeight = 64;

        main = new ModelRenderer(this);
        main.setTextureOffset(0, 24).addBox(-3.0F, -3.0F, -8.0F, 6, 6, 11);
        main.setTextureOffset(0, 0).addBox(0.0F, -3.0F, -5.0F, 0, 6, 18);

        crossA = new ModelRenderer(this);
        crossA.setTextureOffset(0, 0).addBox(0.0F, -3.0F, -9.0F, 0, 6, 18);
        crossA.rotateAngleZ = 1.5708F;
        main.addChild(crossA);

        crossB = new ModelRenderer(this);
        crossB.setTextureOffset(38, -11).addBox(0.0F, -3.0F, -5.5F, 0, 6, 11);
        crossB.rotateAngleX = 3.1416F;
        main.addChild(crossB);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        main.rotateAngleZ = ageInTicks * 0.2F;
        main.render(scale);
    }
}
