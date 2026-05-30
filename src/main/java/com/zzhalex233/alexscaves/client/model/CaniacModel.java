package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.CaniacEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CaniacModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer body;
    private final ModelRenderer pelvis;
    private final ModelRenderer spine;
    private final ModelRenderer chest;
    private final ModelRenderer neck;
    private final ModelRenderer head;
    private final ModelRenderer rightEye;
    private final ModelRenderer leftEye;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer spinePlate;
    private final ModelRenderer chestBox;

    public CaniacModel() {
        textureWidth = 128;
        textureHeight = 128;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, -19.0F, -3.0F);
        root.addChild(body);

        pelvis = part(12, 40, -5.0F, -1.5F, -2.0F, 10, 3, 4, false);
        pelvis.setRotationPoint(0.0F, 3.5F, 3.0F);
        body.addChild(pelvis);

        spine = new ModelRenderer(this);
        spine.setRotationPoint(0.0F, -1.5F, 2.0F);
        pelvis.addChild(spine);

        spinePlate = part(0, 6, -2.0F, -4.0F, 0.0F, 4, 4, 0, false);
        spinePlate.rotateAngleX = 0.3927F;
        spine.addChild(spinePlate);

        chest = new ModelRenderer(this);
        chest.setRotationPoint(0.0F, -3.7F, -1.54F);
        spine.addChild(chest);

        chestBox = part(0, 0, -8.0F, -11.9959F, -9.9898F, 16, 12, 10, false);
        chestBox.rotateAngleX = 0.3927F;
        chest.addChild(chestBox);

        neck = part(32, 0, -2.0F, 0.0F, -10.0F, 4, 0, 10, false);
        neck.setRotationPoint(0.0F, -11.05F, -4.56F);
        chest.addChild(neck);

        head = part(36, 22, -7.0F, -7.25F, -4.25F, 14, 14, 4, false);
        head.setRotationPoint(0.0F, 0.0F, -9.4F);
        head.setTextureOffset(0, 22).addBox(-7.0F, -7.25F, -4.25F, 14, 14, 4, 0.25F);
        neck.addChild(head);

        rightEye = part(0, 78, -0.5F, -0.5F, 0.0F, 1, 1, 0, true);
        rightEye.setRotationPoint(-2.5F, -0.75F, -4.3F);
        head.addChild(rightEye);

        leftEye = part(0, 78, -0.5F, -0.5F, 0.0F, 1, 1, 0, false);
        leftEye.setRotationPoint(2.5F, -0.75F, -4.3F);
        head.addChild(leftEye);

        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(7.75F, -8.8F, -6.96F);
        leftArm.setTextureOffset(20, 47).addBox(0.0F, 22.5F, 8.5F, 3, 7, 3, 0.0F);
        leftArm.setTextureOffset(33, 40).addBox(0.0F, 26.5F, 1.5F, 3, 3, 7, 0.0F);
        leftArm.setTextureOffset(0, 40).addBox(0.0F, -5.5F, -1.5F, 3, 35, 3, 0.0F);
        chest.addChild(leftArm);

        rightArm = new ModelRenderer(this);
        rightArm.mirror = true;
        rightArm.setRotationPoint(-7.75F, -8.8F, -6.96F);
        rightArm.setTextureOffset(20, 47).addBox(-3.0F, 22.5F, 8.5F, 3, 7, 3, 0.0F);
        rightArm.setTextureOffset(33, 40).addBox(-3.0F, 26.5F, 1.5F, 3, 3, 7, 0.0F);
        rightArm.setTextureOffset(0, 40).addBox(-3.0F, -5.5F, -1.5F, 3, 35, 3, 0.0F);
        chest.addChild(rightArm);

        leftLeg = part(12, 47, -1.0F, 0.0F, -1.0F, 2, 14, 2, false);
        leftLeg.setRotationPoint(3.0F, 1.5F, 0.0F);
        leftLeg.setTextureOffset(32, 22).addBox(-1.0F, 12.0F, 1.0F, 2, 2, 2, 0.0F);
        leftLeg.setTextureOffset(0, 0).addBox(-1.0F, 10.0F, 3.0F, 2, 4, 2, 0.0F);
        pelvis.addChild(leftLeg);

        rightLeg = part(12, 47, -1.0F, 0.0F, -1.0F, 2, 14, 2, true);
        rightLeg.setRotationPoint(-3.0F, 1.5F, 0.0F);
        rightLeg.setTextureOffset(32, 22).addBox(-1.0F, 12.0F, 1.0F, 2, 2, 2, 0.0F);
        rightLeg.setTextureOffset(0, 0).addBox(-1.0F, 10.0F, 3.0F, 2, 4, 2, 0.0F);
        pelvis.addChild(rightLeg);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        CaniacEntity caniac = entity instanceof CaniacEntity ? (CaniacEntity) entity : null;
        float partialTicks = caniac == null ? 0.0F : MathHelper.clamp(ageInTicks - caniac.ticksExisted, 0.0F, 1.0F);
        float runProgress = caniac == null ? 0.0F : caniac.getRunProgress(partialTicks);
        float lungeProgress = caniac == null ? 0.0F : caniac.getLungeProgress(partialTicks);
        float walkProgress = 1.0F - runProgress;
        float walkAmount = limbSwingAmount * walkProgress;
        float runAmount = limbSwingAmount * runProgress;
        float walk = limbSwing * 0.7F;
        float run = limbSwing * 0.5F;
        float idle = MathHelper.sin(ageInTicks * 0.06F);

        body.rotationPointY += -Math.abs(MathHelper.cos(walk) * walkAmount * 1.5F) - Math.abs(MathHelper.cos(run) * runAmount * 3.0F);
        spine.rotateAngleX += runProgress * 0.2618F - walkAmount * 0.4363F;
        chest.rotateAngleX += idle * 0.05F + MathHelper.cos(walk + 2.0F) * walkAmount * 0.1F + MathHelper.cos(run + 1.0F) * runAmount * 0.15F;
        neck.rotateAngleX += idle * 0.1F;
        head.rotateAngleX += headPitch * 0.017453292F * 0.3F - idle * 0.08F;
        neck.rotateAngleY += netHeadYaw * 0.017453292F * 0.35F;
        head.rotateAngleY += netHeadYaw * 0.017453292F * 0.35F;

        leftLeg.rotateAngleX = MathHelper.cos(walk) * walkAmount + MathHelper.cos(run) * runAmount;
        rightLeg.rotateAngleX = MathHelper.cos(walk + (float) Math.PI) * walkAmount + MathHelper.cos(run + (float) Math.PI) * runAmount;

        if (lungeProgress > 0.0F) {
            float windup = MathHelper.clamp(lungeProgress * 2.0F, 0.0F, 1.0F);
            float strike = MathHelper.clamp((lungeProgress - 0.45F) * 2.2F, 0.0F, 1.0F);
            body.rotationPointZ += 4.0F * windup - 15.0F * strike;
            body.rotationPointY += 4.0F * strike;
            body.rotateAngleX += 0.5236F * strike;
            spine.rotateAngleX -= 0.4363F * windup;
            head.rotateAngleX -= 0.4363F * windup;
            leftArm.rotateAngleX -= 3.2289F * windup + 1.9199F * strike;
            rightArm.rotateAngleX -= 3.2289F * windup + 1.9199F * strike;
            leftArm.rotateAngleY -= 0.2618F * strike;
            rightArm.rotateAngleY += 0.2618F * strike;
            leftArm.rotateAngleZ += 0.7854F * windup + 1.0472F * strike;
            rightArm.rotateAngleZ -= 0.7854F * windup + 1.0472F * strike;
        }

        if (caniac != null) {
            leftArm.rotateAngleX += caniac.getArmAngle(true, partialTicks) * 0.017453292F;
            rightArm.rotateAngleX += caniac.getArmAngle(false, partialTicks) * 0.017453292F;
        }
        leftArm.rotationPointY += walkAmount * 4.0F;
        rightArm.rotationPointY += walkAmount * 4.0F;
        leftArm.rotateAngleX += MathHelper.cos(walk + 3.0F) * walkAmount * 0.05F;
        rightArm.rotateAngleX += MathHelper.cos(walk + 3.0F + (float) Math.PI) * walkAmount * 0.05F;
        leftArm.rotateAngleZ += runProgress * 0.35F;
        rightArm.rotateAngleZ -= runProgress * 0.35F;
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, -19.0F, -3.0F);
        body.rotateAngleX = 0.0F;
        body.rotateAngleY = 0.0F;
        body.rotateAngleZ = 0.0F;
        pelvis.setRotationPoint(0.0F, 3.5F, 3.0F);
        pelvis.rotateAngleX = 0.0F;
        pelvis.rotateAngleY = 0.0F;
        pelvis.rotateAngleZ = 0.0F;
        spine.setRotationPoint(0.0F, -1.5F, 2.0F);
        spine.rotateAngleX = 0.0F;
        spine.rotateAngleY = 0.0F;
        spine.rotateAngleZ = 0.0F;
        chest.setRotationPoint(0.0F, -3.7F, -1.54F);
        chest.rotateAngleX = 0.0F;
        chest.rotateAngleY = 0.0F;
        chest.rotateAngleZ = 0.0F;
        neck.setRotationPoint(0.0F, -11.05F, -4.56F);
        neck.rotateAngleX = 0.0F;
        neck.rotateAngleY = 0.0F;
        neck.rotateAngleZ = 0.0F;
        head.setRotationPoint(0.0F, 0.0F, -9.4F);
        head.rotateAngleX = 0.0F;
        head.rotateAngleY = 0.0F;
        head.rotateAngleZ = 0.0F;
        rightEye.setRotationPoint(-2.5F, -0.75F, -4.3F);
        leftEye.setRotationPoint(2.5F, -0.75F, -4.3F);
        leftArm.setRotationPoint(7.75F, -8.8F, -6.96F);
        leftArm.rotateAngleX = 0.0F;
        leftArm.rotateAngleY = 0.0F;
        leftArm.rotateAngleZ = 0.0F;
        rightArm.setRotationPoint(-7.75F, -8.8F, -6.96F);
        rightArm.rotateAngleX = 0.0F;
        rightArm.rotateAngleY = 0.0F;
        rightArm.rotateAngleZ = 0.0F;
        leftLeg.setRotationPoint(3.0F, 1.5F, 0.0F);
        leftLeg.rotateAngleX = 0.0F;
        leftLeg.rotateAngleY = 0.0F;
        leftLeg.rotateAngleZ = 0.0F;
        rightLeg.setRotationPoint(-3.0F, 1.5F, 0.0F);
        rightLeg.rotateAngleX = 0.0F;
        rightLeg.rotateAngleY = 0.0F;
        rightLeg.rotateAngleZ = 0.0F;
        spinePlate.rotateAngleX = 0.3927F;
        chestBox.rotateAngleX = 0.3927F;
    }

    private ModelRenderer part(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        return renderer;
    }
}
