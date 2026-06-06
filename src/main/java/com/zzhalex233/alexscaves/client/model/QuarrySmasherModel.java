package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.item.QuarrySmasherEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class QuarrySmasherModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer armLeft;
    private final ModelRenderer armRight;
    private final ModelRenderer armFront;
    private final ModelRenderer armBack;
    private final ModelRenderer coil;

    public QuarrySmasherModel() {
        textureWidth = 128;
        textureHeight = 128;
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 18.0F, 0.0F);
        body.setTextureOffset(20, 41).addBox(4.0F, -1.5F, -3.0F, 2, 7, 6);
        body.setTextureOffset(36, 41).addBox(-6.0F, -1.5F, -3.0F, 2, 7, 6);
        body.setTextureOffset(0, 0).addBox(-6.0F, -10.5F, -6.0F, 12, 9, 12);

        armLeft = diagonalArm(0.7854F, 1.5708F, false);
        armRight = diagonalArm(0.7854F, -1.5708F, true);
        armFront = diagonalArm(-0.7854F, 1.5708F, false);
        armBack = diagonalArm(-0.7854F, -1.5708F, true);

        coil = new ModelRenderer(this);
        coil.setRotationPoint(0.0F, 2.5F, 0.0F);
        coil.setTextureOffset(0, 21).addBox(-4.5F, -3.5F, -3.5F, 9, 7, 7);
        coil.setTextureOffset(26, 29).addBox(-4.0F, -3.0F, -3.0F, 8, 6, 6);
        body.addChild(coil);
    }

    private ModelRenderer diagonalArm(float xRot, float zRot, boolean mirror) {
        ModelRenderer arm = new ModelRenderer(this);
        arm.mirror = mirror;
        arm.setRotationPoint(0.0F, -6.0F, 0.0F);
        arm.rotateAngleX = xRot;
        arm.rotateAngleZ = zRot;
        arm.setTextureOffset(54, 4).addBox(mirror ? -2.5F : -1.5F, -17.5F, -2.0F, 4, 4, 4);
        arm.setTextureOffset(48, 26).addBox(mirror ? -1.5F : -0.5F, -13.5F, -1.0F, 2, 7, 2);
        body.addChild(arm);
        return arm;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        body.rotateAngleX = 0.0F;
        body.rotateAngleY = 0.0F;
        body.rotateAngleZ = 0.0F;
        coil.rotateAngleX = 0.0F;
        if (entity instanceof QuarrySmasherEntity) {
            QuarrySmasherEntity smasher = (QuarrySmasherEntity) entity;
            float partialTicks = ageInTicks - smasher.ticksExisted;
            float inactive = smasher.getInactiveProgress(partialTicks);
            float active = 1.0F - inactive;
            float wiggle = smasher.isBeingActivated() ? MathHelper.sin(active * (float) Math.PI) : 0.0F;
            float shake = Math.max(smasher.shakeTime - partialTicks, 0.0F) * 0.1F;
            body.rotateAngleX = inactive * 0.7854F + MathHelper.sin(ageInTicks * 0.7F + 1.0F) * shake * 0.05F;
            body.rotateAngleZ = inactive * 0.7854F + MathHelper.sin(ageInTicks * 0.7F) * shake * 0.1F;
            body.rotateAngleY = MathHelper.sin(ageInTicks) * wiggle * 0.35F;
            coil.rotateAngleX = smasher.getChainLength(partialTicks) * 4.5379F;
        }
    }
}
