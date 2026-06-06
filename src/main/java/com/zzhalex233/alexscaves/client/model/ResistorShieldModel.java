package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ResistorShieldModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer base;
    private final ModelRenderer rotationBolt;

    public ResistorShieldModel() {
        textureWidth = 128;
        textureHeight = 128;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, 2.0F);

        base = new ModelRenderer(this);
        base.setRotationPoint(4.0F, -20.0F, 0.0F);
        root.addChild(base);
        base.setTextureOffset(16, 36).addBox(-9.0F, -3.0F, -1.0F, 6, 6, 2, 0.0F);
        base.setTextureOffset(28, 28).addBox(-3.0F, -8.0F, -8.0F, 6, 4, 16, 0.0F);
        base.setTextureOffset(40, 60).addBox(-3.0F, -4.0F, -8.0F, 6, 8, 4, 0.0F);
        base.setTextureOffset(56, 22).addBox(-3.0F, -4.0F, 4.0F, 6, 8, 4, 0.0F);
        base.setTextureOffset(16, 20).addBox(-3.0F, -4.0F, -4.0F, 6, 8, 8, 0.0F);
        base.setTextureOffset(0, 44).addBox(-3.0F, 4.0F, -8.0F, 6, 4, 16, 0.0F);

        rotationBolt = new ModelRenderer(this);
        rotationBolt.setRotationPoint(1.0F, 0.0F, 0.0F);
        base.addChild(rotationBolt);
        rotationBolt.setTextureOffset(54, 66).addBox(-3.0F, 4.0F, -3.0F, 6, 13, 6, 0.0F);
        rotationBolt.setTextureOffset(22, 66).addBox(-3.0F, -17.0F, -3.0F, 6, 13, 6, 0.0F);
        rotationBolt.setTextureOffset(1, 0).addBox(-3.0F, -4.0F, -4.0F, 7, 8, 8, 0.0F);
        setupAnim(0.0F, 0.0F);
    }

    public void setupAnim(float useProgress, float switchProgress) {
        root.rotateAngleX = 0.0F;
        root.rotateAngleY = 1.5708F;
        root.rotateAngleZ = 0.0F;
        base.rotateAngleX = 0.0F;
        base.rotateAngleY = -0.0436F;
        base.rotateAngleZ = 0.0F;
        rotationBolt.rotateAngleX = (float) Math.toRadians(360.0F * useProgress + switchProgress * 180.0F);
        rotationBolt.rotateAngleY = 0.0F;
        rotationBolt.rotateAngleZ = 0.0F;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        root.render(scale);
    }
}
