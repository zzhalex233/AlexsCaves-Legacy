package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.item.BurrowingArrowEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BurrowingArrowModel extends ModelBase {
    private final ModelRenderer main;
    private final ModelRenderer lowerJaw;
    private final ModelRenderer upperJaw;

    public BurrowingArrowModel() {
        textureWidth = 32;
        textureHeight = 32;

        main = new ModelRenderer(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);

        ModelRenderer shaft = new ModelRenderer(this);
        shaft.setRotationPoint(0.0F, -2.5F, 2.0F);
        main.addChild(shaft);
        shaft.setTextureOffset(0, 5).addBox(0.0F, -2.5F, -6.0F, 0, 5, 12);

        ModelRenderer shaftCross = new ModelRenderer(this);
        shaftCross.rotateAngleZ = 1.5708F;
        shaftCross.setTextureOffset(0, 0).addBox(0.0F, -2.5F, -6.0F, 0, 5, 12);
        shaft.addChild(shaftCross);

        ModelRenderer head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 0.0F, -6.0F);
        shaft.addChild(head);

        lowerJaw = new ModelRenderer(this);
        lowerJaw.setTextureOffset(0, 7).addBox(-2.0F, 0.0F, 0.5F, 4, 3, 2);
        lowerJaw.setTextureOffset(0, 0).addBox(-2.0F, 0.0F, -3.5F, 4, 3, 4);
        head.addChild(lowerJaw);

        upperJaw = new ModelRenderer(this);
        upperJaw.setTextureOffset(12, 3).addBox(-2.0F, -3.0F, -3.5F, 4, 3, 4, 0.01F);
        upperJaw.setTextureOffset(0, 22).addBox(-2.0F, -3.0F, 0.5F, 4, 3, 2, 0.01F);
        head.addChild(upperJaw);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        float burrow = entity instanceof BurrowingArrowEntity ? ((BurrowingArrowEntity) entity).getDiggingAmount(ageInTicks - entity.ticksExisted) : 0.0F;
        main.rotationPointZ = Math.abs(MathHelper.cos(ageInTicks * 0.5F) * 3.0F * burrow);
        lowerJaw.rotateAngleX = -0.6981F + MathHelper.cos(ageInTicks + 1.0F) * 0.6F * burrow - 0.6F * burrow;
        upperJaw.rotateAngleX = 0.6981F - MathHelper.cos(ageInTicks + 1.0F) * 0.6F * burrow + 0.6F * burrow;
        main.render(scale);
    }
}
