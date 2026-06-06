package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class CandyCaneHookModel extends ModelBase {
    private final ModelRenderer root;

    public CandyCaneHookModel() {
        textureWidth = 32;
        textureHeight = 32;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        ModelRenderer hook = new ModelRenderer(this);
        hook.setRotationPoint(0.0F, -4.0F, 5.0F);
        hook.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2);
        root.addChild(hook);

        ModelRenderer cross1 = new ModelRenderer(this);
        cross1.rotateAngleZ = 0.7854F;
        cross1.setTextureOffset(0, 0).addBox(0.0F, -5.0F, -13.0F, 0, 10, 14);
        hook.addChild(cross1);

        ModelRenderer cross2 = new ModelRenderer(this);
        cross2.rotateAngleZ = -0.7854F;
        cross2.setTextureOffset(0, 0).addBox(0.0F, -5.0F, -13.0F, 0, 10, 14);
        hook.addChild(cross2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        root.render(scale);
    }
}
