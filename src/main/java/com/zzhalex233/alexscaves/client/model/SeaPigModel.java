package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.SeaPigEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SeaPigModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightLeg1;
    private final ModelRenderer rightLeg2;
    private final ModelRenderer rightLeg3;
    private final ModelRenderer rightLeg4;
    private final ModelRenderer leftLeg1;
    private final ModelRenderer leftLeg2;
    private final ModelRenderer leftLeg3;
    private final ModelRenderer leftLeg4;
    private final ModelRenderer leftFrontAntennae;
    private final ModelRenderer rightFrontAntennae;
    private final ModelRenderer leftBackAntennae;
    private final ModelRenderer rightBackAntennae;

    public SeaPigModel() {
        textureWidth = 48;
        textureHeight = 48;
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 20.0F, 0.5F);
        body.setTextureOffset(0, 0).addBox(-2.5F, -3.0F, -4.5F, 5, 6, 9, 0.0F);
        body.setTextureOffset(0, 29).addBox(-2.5F, -2.0F, -4.5F, 5, 3, 9, -0.25F);

        rightLeg1 = leg(-2.0F, -3.5F, false);
        rightLeg2 = leg(-2.0F, -1.0F, false);
        rightLeg3 = leg(-2.0F, 1.5F, false);
        rightLeg4 = leg(-2.0F, 4.0F, false);
        leftLeg1 = leg(2.0F, -3.5F, true);
        leftLeg2 = leg(2.0F, -1.0F, true);
        leftLeg3 = leg(2.0F, 1.5F, true);
        leftLeg4 = leg(2.0F, 4.0F, true);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -0.5F, -4.5F);
        head.setTextureOffset(16, 6).addBox(-2.0F, 3.0F, -2.0F, 4, 0, 3, 0.0F);
        head.setTextureOffset(0, 15).addBox(-2.0F, 0.0F, -2.0F, 4, 4, 3, 0.0F);
        body.addChild(head);

        leftFrontAntennae = antenna(1.75F, -2.5F, -4.5F, 0.3927F, -0.3927F, 0.0F, 0, 14, -6.5F, -8.0F, 7, 8, false);
        rightFrontAntennae = antenna(-1.75F, -2.5F, -4.5F, 0.3927F, 0.3927F, 0.0F, 0, 14, -6.5F, -8.0F, 7, 8, true);
        leftBackAntennae = antenna(2.0F, -3.0F, 2.0F, -0.3927F, 0.0F, 0.7854F, 20, 9, -10.0F, -0.5F, 11, 6, false);
        rightBackAntennae = antenna(-2.0F, -3.0F, 2.0F, -0.3927F, 0.0F, -0.7854F, 20, 9, -10.0F, -0.5F, 11, 6, true);
    }

    private ModelRenderer leg(float x, float z, boolean mirror) {
        ModelRenderer leg = new ModelRenderer(this);
        leg.mirror = mirror;
        leg.setRotationPoint(x, 2.75F, z);
        leg.setTextureOffset(0, 0).addBox(-0.5F, 0.25F, -0.5F, 1, 1, 1, 0.0F);
        body.addChild(leg);
        return leg;
    }

    private ModelRenderer antenna(float x, float y, float z, float rotX, float rotY, float rotZ, int texX, int texY, float boxY, float boxZ, int height, int depth, boolean mirror) {
        ModelRenderer root = new ModelRenderer(this);
        root.setRotationPoint(x, y, z);
        ModelRenderer strand = new ModelRenderer(this);
        strand.mirror = mirror;
        strand.setRotationPoint(0.0F, 0.0F, 0.0F);
        strand.rotateAngleX = rotX;
        strand.rotateAngleY = rotY;
        strand.rotateAngleZ = rotZ;
        strand.setTextureOffset(texX, texY).addBox(0.0F, boxY, boxZ, 0, height, depth, 0.0F);
        root.addChild(strand);
        body.addChild(root);
        return root;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        float squish = entity instanceof SeaPigEntity ? ((SeaPigEntity) entity).getSquishProgress(0.0F) : 0.0F;
        float walk = MathHelper.sin(limbSwing) * limbSwingAmount;
        body.rotationPointY = 20.0F + squish * 3.0F + MathHelper.sin(ageInTicks * 0.1F) * 0.2F;
        body.rotateAngleX = walk * 0.065F;
        body.rotateAngleY = 0.0F;
        body.rotateAngleZ = 0.0F;
        head.rotateAngleZ = MathHelper.sin(ageInTicks * 0.2F + 3.0F) * 0.03F;
        animateLeg(rightLeg1, limbSwing, limbSwingAmount, 0.0F);
        animateLeg(rightLeg2, limbSwing, limbSwingAmount, 2.0F);
        animateLeg(rightLeg3, limbSwing, limbSwingAmount, 4.0F);
        animateLeg(rightLeg4, limbSwing, limbSwingAmount, 6.0F);
        animateLeg(leftLeg1, limbSwing, limbSwingAmount, 0.0F);
        animateLeg(leftLeg2, limbSwing, limbSwingAmount, 2.0F);
        animateLeg(leftLeg3, limbSwing, limbSwingAmount, 4.0F);
        animateLeg(leftLeg4, limbSwing, limbSwingAmount, 6.0F);
        rightFrontAntennae.rotateAngleX = MathHelper.sin(ageInTicks * 0.1F + 2.0F) * 0.1F;
        leftFrontAntennae.rotateAngleX = -rightFrontAntennae.rotateAngleX;
        rightBackAntennae.rotateAngleX = MathHelper.sin(ageInTicks * 0.1F + 1.0F) * 0.1F;
        leftBackAntennae.rotateAngleX = -rightBackAntennae.rotateAngleX;
    }

    private void animateLeg(ModelRenderer leg, float limbSwing, float limbSwingAmount, float offset) {
        leg.rotateAngleX = MathHelper.sin(limbSwing + offset) * limbSwingAmount * 1.3F;
        leg.rotationPointY = 2.75F - Math.max(0.0F, MathHelper.sin(limbSwing + offset + 1.5F) * limbSwingAmount * 0.2F);
    }
}
