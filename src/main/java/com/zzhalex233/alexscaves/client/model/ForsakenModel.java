package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.ForsakenEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ForsakenModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer body;
    private final ModelRenderer chest;
    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftSmallArm;
    private final ModelRenderer rightSmallArm;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;

    public ForsakenModel() {
        textureWidth = 128;
        textureHeight = 128;
        root = empty(0.0F, 24.0F, 0.0F);
        body = part(0, 38, 0.0F, -24.0F, 0.0F, -8.0F, -13.0F, -5.0F, 16, 16, 10);
        chest = part(0, 0, 0.0F, -13.0F, -1.0F, -10.0F, -15.0F, -6.0F, 20, 17, 12);
        head = part(64, 0, 0.0F, -15.0F, -3.0F, -6.0F, -10.0F, -7.0F, 12, 10, 10);
        jaw = part(64, 20, 0.0F, -6.0F, -7.0F, -5.0F, 0.0F, -5.0F, 10, 4, 8);
        leftArm = part(40, 44, -10.0F, -13.0F, -1.0F, -5.0F, -2.0F, -4.0F, 5, 22, 8);
        rightArm = part(40, 44, 10.0F, -13.0F, -1.0F, 0.0F, -2.0F, -4.0F, 5, 22, 8);
        leftSmallArm = part(80, 42, -5.5F, -8.0F, -4.0F, -3.0F, -1.0F, -2.0F, 3, 14, 4);
        rightSmallArm = part(80, 42, 5.5F, -8.0F, -4.0F, 0.0F, -1.0F, -2.0F, 3, 14, 4);
        leftLeg = part(0, 70, -4.0F, -12.0F, 1.0F, -3.0F, 0.0F, -3.0F, 5, 12, 6);
        rightLeg = part(0, 70, 4.0F, -12.0F, 1.0F, -2.0F, 0.0F, -3.0F, 5, 12, 6);
        root.addChild(body);
        body.addChild(chest);
        chest.addChild(head);
        head.addChild(jaw);
        chest.addChild(leftArm);
        chest.addChild(rightArm);
        body.addChild(leftSmallArm);
        body.addChild(rightSmallArm);
        root.addChild(leftLeg);
        root.addChild(rightLeg);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        ForsakenEntity forsaken = entity instanceof ForsakenEntity ? (ForsakenEntity) entity : null;
        float partial = forsaken == null ? 0.0F : ageInTicks - forsaken.ticksExisted;
        float run = forsaken == null ? 0.0F : forsaken.getRunProgress(partial);
        float leap = forsaken == null ? 0.0F : forsaken.getLeapProgress(partial);
        float darkness = forsaken == null ? 0.0F : forsaken.getDarknessProgress(partial);
        float speed = 0.45F + run * 0.25F;
        body.rotateAngleX -= run * 0.25F + leap * 0.3F;
        chest.rotateAngleX += run * 0.15F;
        head.rotateAngleX += headPitch * 0.017453292F + leap * 0.2F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        jaw.rotateAngleX += 0.15F + darkness * 0.25F;
        leftLeg.rotateAngleX += MathHelper.cos(limbSwing * speed) * limbSwingAmount * 0.8F;
        rightLeg.rotateAngleX += MathHelper.cos(limbSwing * speed + (float) Math.PI) * limbSwingAmount * 0.8F;
        leftArm.rotateAngleX += MathHelper.cos(limbSwing * speed + (float) Math.PI) * limbSwingAmount * 0.45F - 0.2F;
        rightArm.rotateAngleX += MathHelper.cos(limbSwing * speed) * limbSwingAmount * 0.45F - 0.2F;
        leftSmallArm.rotateAngleX += MathHelper.cos(ageInTicks * 0.1F) * 0.08F - 0.3F;
        rightSmallArm.rotateAngleX += MathHelper.cos(ageInTicks * 0.1F + (float) Math.PI) * 0.08F - 0.3F;
        if (forsaken != null && forsaken.getAnimation() != ForsakenEntity.ANIMATION_NONE) {
            float strike = MathHelper.sin(MathHelper.clamp(forsaken.getAnimationTick() / 18.0F, 0.0F, 1.0F) * (float) Math.PI);
            if (forsaken.getAnimation() == ForsakenEntity.ANIMATION_BITE) {
                head.rotateAngleX += strike * 0.7F;
                jaw.rotateAngleX += strike * 0.9F;
            } else if (forsaken.getAnimation() == ForsakenEntity.ANIMATION_LEFT_SLASH || forsaken.getAnimation() == ForsakenEntity.ANIMATION_LEFT_PICKUP) {
                leftArm.rotateAngleX -= strike * 1.8F;
                leftArm.rotateAngleY += strike * 0.8F;
                chest.rotateAngleY += strike * 0.25F;
            } else if (forsaken.getAnimation() == ForsakenEntity.ANIMATION_RIGHT_SLASH || forsaken.getAnimation() == ForsakenEntity.ANIMATION_RIGHT_PICKUP) {
                rightArm.rotateAngleX -= strike * 1.8F;
                rightArm.rotateAngleY -= strike * 0.8F;
                chest.rotateAngleY -= strike * 0.25F;
            } else if (forsaken.getAnimation() == ForsakenEntity.ANIMATION_GROUND_SMASH) {
                leftArm.rotateAngleX -= strike * 2.2F;
                rightArm.rotateAngleX -= strike * 2.2F;
                chest.rotateAngleX += strike * 0.5F;
            } else if (forsaken.getAnimation() == ForsakenEntity.ANIMATION_SONIC_ATTACK || forsaken.getAnimation() == ForsakenEntity.ANIMATION_SONIC_BLAST) {
                jaw.rotateAngleX += strike * 1.1F;
                chest.rotateAngleX -= strike * 0.2F;
                leftArm.rotateAngleZ -= strike * 0.5F;
                rightArm.rotateAngleZ += strike * 0.5F;
            } else if (forsaken.getAnimation() == ForsakenEntity.ANIMATION_SUMMON) {
                root.rotationPointY += (1.0F - strike) * 12.0F;
                leftArm.rotateAngleX += (1.0F - strike) * 2.0F;
                rightArm.rotateAngleX += (1.0F - strike) * 2.0F;
            }
        }
    }

    private void resetPose() {
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        body.setRotationPoint(0.0F, -24.0F, 0.0F);
        chest.setRotationPoint(0.0F, -13.0F, -1.0F);
        head.setRotationPoint(0.0F, -15.0F, -3.0F);
        jaw.setRotationPoint(0.0F, -6.0F, -7.0F);
        leftArm.setRotationPoint(-10.0F, -13.0F, -1.0F);
        rightArm.setRotationPoint(10.0F, -13.0F, -1.0F);
        leftSmallArm.setRotationPoint(-5.5F, -8.0F, -4.0F);
        rightSmallArm.setRotationPoint(5.5F, -8.0F, -4.0F);
        leftLeg.setRotationPoint(-4.0F, -12.0F, 1.0F);
        rightLeg.setRotationPoint(4.0F, -12.0F, 1.0F);
        ModelRenderer[] parts = {root, body, chest, head, jaw, leftArm, rightArm, leftSmallArm, rightSmallArm, leftLeg, rightLeg};
        for (ModelRenderer part : parts) {
            part.rotateAngleX = 0.0F;
            part.rotateAngleY = 0.0F;
            part.rotateAngleZ = 0.0F;
        }
    }

    private ModelRenderer part(int textureX, int textureY, float pointX, float pointY, float pointZ, float x, float y, float z, int width, int height, int depth) {
        ModelRenderer renderer = empty(pointX, pointY, pointZ);
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        return renderer;
    }

    private ModelRenderer empty(float pointX, float pointY, float pointZ) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(pointX, pointY, pointZ);
        return renderer;
    }
}
