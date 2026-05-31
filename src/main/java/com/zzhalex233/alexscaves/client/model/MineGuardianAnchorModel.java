package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class MineGuardianAnchorModel extends ModelBase {
    private final ModelRenderer anchor;

    public MineGuardianAnchorModel() {
        textureWidth = 64;
        textureHeight = 64;
        anchor = new ModelRenderer(this);
        anchor.setRotationPoint(0.0F, 22.25F, 0.0F);
        anchor.setTextureOffset(0, 0).addBox(-6.0F, -4.0F, 0.0F, 12, 14, 0);
        anchor.setTextureOffset(0, 14).addBox(-2.0F, 4.0F, -2.0F, 4, 16, 4);
        ModelRenderer left = new ModelRenderer(this);
        left.setRotationPoint(0.0F, 23.0F, 0.0F);
        left.rotateAngleZ = 0.7854F;
        left.setTextureOffset(24, 0).addBox(-4.0F, -15.0F, -2.0F, 3, 3, 4);
        left.setTextureOffset(16, 26).addBox(-1.0F, -15.0F, -2.0F, 3, 11, 4);
        left.setTextureOffset(16, 14).addBox(-4.0F, -4.0F, -3.0F, 6, 6, 6);
        anchor.addChild(left);
        ModelRenderer right = new ModelRenderer(this);
        right.setRotationPoint(0.0F, 23.0F, 0.0F);
        right.rotateAngleZ = -0.7854F;
        right.setTextureOffset(24, 0).addBox(1.0F, -15.0F, -2.0F, 3, 3, 4);
        right.setTextureOffset(16, 26).addBox(-2.0F, -15.0F, -2.0F, 3, 11, 4);
        anchor.addChild(right);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        anchor.render(scale);
    }
}
