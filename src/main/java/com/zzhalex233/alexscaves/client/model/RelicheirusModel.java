package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.RelicheirusEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class RelicheirusModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer chest;
    private final ModelRenderer hips;
    private final ModelRenderer neck;
    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftHand;
    private final ModelRenderer rightHand;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg2;
    private final ModelRenderer rightLeg2;
    private final ModelRenderer leftFoot;
    private final ModelRenderer rightFoot;
    private final ModelRenderer tail;
    private final ModelRenderer tail2;
    private final ModelRenderer chestFeathers;
    private final ModelRenderer tailFeathers;

    public RelicheirusModel() {
        textureWidth = 256;
        textureHeight = 256;
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, -15.0F, 0.0F);
        chest = box(0, 0, -15.0F, -8.5F, -28.0F, 30, 37, 28, false);
        chest.setRotationPoint(0.0F, -10.5F, 3.0F);
        body.addChild(chest);
        chestFeathers = plate(0, 113, 0.0F, -14.5F, -28.0F, 0, 14, 33, false);
        chestFeathers.setRotationPoint(0.0F, -8.5F, -9.5F);
        chest.addChild(chestFeathers);
        hips = box(97, 46, -13.0F, -0.75F, -5.0F, 26, 29, 19, false);
        hips.setRotationPoint(0.0F, -10.5F, 3.0F);
        body.addChild(hips);
        neck = box(116, 0, -8.0F, -28.0F, -11.0F, 16, 30, 16, false);
        neck.setRotationPoint(0.0F, 4.5F, -23.0F);
        chest.addChild(neck);
        head = box(0, 160, -5.0F, -27.0F, -5.0F, 10, 31, 10, false);
        head.setRotationPoint(0.0F, -25.0F, -1.0F);
        head.setTextureOffset(168, 56).addBox(-4.5F, -27.0F, -17.0F, 9, 4, 5);
        neck.addChild(head);
        jaw = box(88, 14, -3.5F, 0.0F, -7.0F, 7, 4, 7, false);
        jaw.setRotationPoint(0.0F, -25.0F, -5.0F);
        head.addChild(jaw);
        leftArm = box(40, 160, -2.5F, 4.0F, 2.5F, 7, 21, 15, false);
        leftArm.setRotationPoint(15.0F, 20.5F, -21.0F);
        chest.addChild(leftArm);
        leftHand = box(84, 170, -2.0F, -0.5F, -7.0F, 6, 8, 14, false);
        leftHand.setRotationPoint(0.0F, 17.5F, 10.0F);
        leftArm.addChild(leftHand);
        rightArm = box(40, 160, -4.5F, 4.0F, 3.0F, 7, 21, 15, true);
        rightArm.setRotationPoint(-15.0F, 20.5F, -21.5F);
        chest.addChild(rightArm);
        rightHand = box(84, 170, -4.0F, -0.5F, -7.0F, 6, 8, 14, true);
        rightHand.setRotationPoint(0.0F, 17.5F, 10.5F);
        rightArm.addChild(rightHand);
        leftLeg = box(124, 166, -4.0F, -3.0F, -7.5F, 8, 18, 11, false);
        leftLeg.setRotationPoint(12.0F, 23.5F, 9.5F);
        hips.addChild(leftLeg);
        leftLeg2 = box(0, 0, -2.5F, -2.0F, -2.5F, 5, 12, 7, false);
        leftLeg2.setRotationPoint(-0.5F, 13.0F, 1.0F);
        leftLeg.addChild(leftLeg2);
        leftFoot = box(88, 0, -3.5F, 0.5F, -7.5F, 7, 3, 11, false);
        leftFoot.setRotationPoint(0.0F, 9.5F, 1.0F);
        leftLeg2.addChild(leftFoot);
        rightLeg = box(124, 166, -4.0F, -3.0F, -7.5F, 8, 18, 11, true);
        rightLeg.setRotationPoint(-12.0F, 23.5F, 9.5F);
        hips.addChild(rightLeg);
        rightLeg2 = box(0, 0, -2.5F, -2.0F, -2.5F, 5, 12, 7, true);
        rightLeg2.setRotationPoint(0.5F, 13.0F, 1.0F);
        rightLeg.addChild(rightLeg2);
        rightFoot = box(88, 0, -3.5F, 0.5F, -7.5F, 7, 3, 11, true);
        rightFoot.setRotationPoint(0.0F, 9.5F, 1.0F);
        rightLeg2.addChild(rightFoot);
        tail = box(165, 100, -8.0F, -5.0F, -2.0F, 16, 16, 22, false);
        tail.setRotationPoint(0.0F, 10.5F, 13.0F);
        hips.addChild(tail);
        tail2 = box(88, 225, -4.0F, -2.0F, -2.5F, 8, 8, 23, false);
        tail2.setRotationPoint(0.0F, 1.0F, 19.5F);
        tail.addChild(tail2);
        tailFeathers = plate(127, 167, -12.0F, 0.0F, -14.0F, 24, 18, 38, false);
        tailFeathers.setRotationPoint(0.0F, 1.0F, 17.5F);
        tail2.addChild(tailFeathers);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        RelicheirusEntity relicheirus = entity instanceof RelicheirusEntity ? (RelicheirusEntity) entity : null;
        float partial = relicheirus == null ? 0.0F : MathHelper.clamp(ageInTicks - relicheirus.ticksExisted, 0.0F, 1.0F);
        float arms = relicheirus == null ? 0.0F : relicheirus.getRaiseArmsProgress(partial);
        float walk = limbSwing * 0.8F;
        float amount = limbSwingAmount;
        body.rotationPointY += MathHelper.cos(walk - 1.5F) * amount * 2.0F;
        neck.rotateAngleX += headPitch * 0.017453292F * 0.45F;
        head.rotateAngleX += headPitch * 0.017453292F * 0.35F;
        neck.rotateAngleY += netHeadYaw * 0.017453292F * 0.4F;
        head.rotateAngleY += netHeadYaw * 0.017453292F * 0.35F;
        tail.rotateAngleY += MathHelper.cos(walk + 2.0F) * amount * 0.25F;
        tail2.rotateAngleY += MathHelper.cos(walk + 1.5F) * amount * 0.35F;
        leftLeg.rotateAngleX = MathHelper.cos(walk) * amount * 0.5F;
        rightLeg.rotateAngleX = MathHelper.cos(walk + (float) Math.PI) * amount * 0.5F;
        leftLeg2.rotateAngleX = MathHelper.cos(walk + 0.7F) * amount * 0.35F;
        rightLeg2.rotateAngleX = MathHelper.cos(walk + (float) Math.PI + 0.7F) * amount * 0.35F;
        leftArm.rotateAngleX = MathHelper.cos(walk + (float) Math.PI) * amount * 0.35F - arms * 1.1F;
        rightArm.rotateAngleX = MathHelper.cos(walk) * amount * 0.35F - arms * 1.1F;
        leftHand.rotateAngleZ -= arms * 0.45F;
        rightHand.rotateAngleZ += arms * 0.45F;
        if (relicheirus != null && relicheirus.getAttackTicks() > 0) {
            float swing = MathHelper.sin((20 - relicheirus.getAttackTicks() + partial) * 0.35F);
            chest.rotateAngleY += swing * 0.25F;
            leftArm.rotateAngleX -= Math.max(0.0F, swing) * 0.9F;
            rightArm.rotateAngleX -= Math.max(0.0F, -swing) * 0.9F;
            jaw.rotateAngleX += 0.35F;
        }
        float idle = ageInTicks * 0.1F;
        neck.rotateAngleX += MathHelper.sin(idle) * 0.03F;
        head.rotateAngleX += MathHelper.sin(idle + 1.0F) * 0.025F;
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, -15.0F, 0.0F);
        reset(body);
        reset(chest);
        reset(hips);
        reset(neck);
        reset(head);
        reset(jaw);
        reset(leftArm);
        reset(rightArm);
        reset(leftHand);
        reset(rightHand);
        reset(leftLeg);
        reset(rightLeg);
        reset(leftLeg2);
        reset(rightLeg2);
        reset(leftFoot);
        reset(rightFoot);
        reset(tail);
        reset(tail2);
    }

    private void reset(ModelRenderer renderer) {
        renderer.rotateAngleX = renderer.rotateAngleY = renderer.rotateAngleZ = 0.0F;
    }

    private ModelRenderer box(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth);
        return renderer;
    }

    private ModelRenderer plate(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        return box(textureX, textureY, x, y, z, width, height, depth, mirror);
    }
}
