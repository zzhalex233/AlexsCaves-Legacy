package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.DeepOneBaseEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class DeepOneModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer headFins;

    public DeepOneModel() {
        textureWidth = 128;
        textureHeight = 128;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.1667F, 2.1667F, 1.5F);
        body.setTextureOffset(0, 12).addBox(0.3333F, -15.1667F, -2.5F, 0, 19, 11, 0.0F);
        body.setTextureOffset(0, 0).addBox(-6.6667F, -10.1667F, -6.5F, 13, 14, 9, 0.0F);
        body.setTextureOffset(45, 15).addBox(-3.6667F, 3.8333F, -3.5F, 7, 10, 5, 0.0F);

        tail = new ModelRenderer(this);
        tail.setRotationPoint(-0.1667F, 12.3333F, 1.5F);
        tail.setTextureOffset(22, 29).addBox(0.0F, -5.5F, 0.0F, 0, 12, 12, 0.0F);
        body.addChild(tail);

        head = new ModelRenderer(this);
        head.setRotationPoint(-0.1667F, -10.1667F, -3.5F);
        head.setTextureOffset(70, 9).addBox(-3.5F, -6.0F, -8.0F, 7, 5, 6, 0.0F);
        head.setTextureOffset(22, 23).addBox(-3.5F, -1.0F, -8.0F, 7, 9, 9, 0.0F);
        head.setTextureOffset(18, 53).addBox(-3.5F, -6.0F, -2.0F, 7, 5, 3, 0.0F);
        head.setTextureOffset(0, 0).addBox(0.5F, -3.0F, -4.0F, 2, 2, 2, 0.0F);
        addMirroredBox(head, 0, 0, -2.5F, -3.0F, -4.0F, 2, 2, 2, 0.0F);
        body.addChild(head);

        headFins = new ModelRenderer(this);
        headFins.setRotationPoint(-3.0F, -1.0F, -2.0F);
        headFins.rotateAngleX = -0.7854F;
        addMirroredBox(headFins, 69, 20, -5.5F, -4.0F, 0.0F, 5, 10, 0, 0.0F);
        headFins.setTextureOffset(69, 20).addBox(6.5F, -4.0F, 0.0F, 5, 10, 0, 0.0F);
        head.addChild(headFins);

        jaw = new ModelRenderer(this);
        jaw.setRotationPoint(0.0F, 3.0F, -0.5F);
        jaw.setTextureOffset(44, 0).addBox(-3.5F, -1.0F, -7.5F, 7, 6, 9, 0.25F);
        head.addChild(jaw);

        rightArm = new ModelRenderer(this);
        rightArm.setRotationPoint(-6.1667F, -4.1667F, -0.75F);
        addMirroredBox(rightArm, 0, 42, -3.5F, -2.0F, -3.75F, 4, 19, 5, 0.0F);
        addMirroredBox(rightArm, 54, 30, -3.5F, 17.0F, -3.75F, 4, 4, 5, 0.0F);
        addMirroredBox(rightArm, 0, 66, -3.5F, -2.0F, -3.75F, 4, 19, 5, 0.25F);
        addMirroredBox(rightArm, 46, 47, -1.5F, -2.0F, 1.25F, 0, 19, 5, 0.0F);
        body.addChild(rightArm);

        leftArm = new ModelRenderer(this);
        leftArm.setRotationPoint(5.8333F, -4.1667F, -2.25F);
        leftArm.setTextureOffset(0, 42).addBox(-0.5F, -2.0F, -2.25F, 4, 19, 5, 0.0F);
        leftArm.setTextureOffset(0, 66).addBox(-0.5F, -2.0F, -2.25F, 4, 19, 5, 0.25F);
        leftArm.setTextureOffset(54, 30).addBox(-0.5F, 17.0F, -2.25F, 4, 4, 5, 0.0F);
        leftArm.setTextureOffset(46, 47).addBox(1.5F, -2.0F, 2.75F, 0, 19, 5, 0.0F);
        body.addChild(leftArm);

        rightLeg = new ModelRenderer(this);
        rightLeg.setRotationPoint(-2.1667F, 13.3333F, -1.5F);
        addMirroredBox(rightLeg, 56, 52, -1.5F, 0.5F, -1.0F, 3, 8, 3, 0.0F);
        addMirroredBox(rightLeg, 65, 43, -1.5F, 0.5F, -1.0F, 3, 8, 3, 0.25F);
        addMirroredBox(rightLeg, 29, 0, -4.5F, 8.5F, -4.0F, 6, 0, 6, 0.0F);
        body.addChild(rightLeg);

        leftLeg = new ModelRenderer(this);
        leftLeg.setRotationPoint(1.8333F, 13.3333F, -1.5F);
        leftLeg.setTextureOffset(56, 52).addBox(-1.5F, 0.5F, -1.0F, 3, 8, 3, 0.0F);
        leftLeg.setTextureOffset(65, 43).addBox(-1.5F, 0.5F, -1.0F, 3, 8, 3, 0.25F);
        leftLeg.setTextureOffset(29, 0).addBox(-1.5F, 8.5F, -4.0F, 6, 0, 6, 0.0F);
        body.addChild(leftLeg);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        DeepOneBaseEntity deepOne = entity instanceof DeepOneBaseEntity ? (DeepOneBaseEntity) entity : null;
        float swim = deepOne != null && deepOne.isDeepOneSwimming() ? 1.0F : 0.0F;
        float walkAmount = Math.min(limbSwingAmount * 2.0F, 1.0F) * (1.0F - swim);
        float swimAmount = limbSwingAmount * swim;
        float fishPitch = deepOne == null ? 0.0F : deepOne.getFishPitch(0.0F) * 0.017453292F;

        body.rotateAngleX += walkAmount * 0.2618F + swim * (1.3963F + fishPitch);
        head.rotateAngleX += headPitch * 0.017453292F - walkAmount * 0.2618F - swim * 1.2217F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        tail.rotateAngleX -= swim * 0.8727F;

        float walkSpeed = 1.0F;
        rightLeg.rotateAngleX += MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkAmount;
        leftLeg.rotateAngleX += MathHelper.cos(limbSwing * walkSpeed) * walkAmount;
        rightArm.rotateAngleX += MathHelper.cos(limbSwing * walkSpeed) * walkAmount * 0.2F - swim * 0.2F;
        leftArm.rotateAngleX += MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkAmount * 0.2F - swim * 0.2F;

        float swimSwing = MathHelper.sin(limbSwing * 0.25F);
        body.rotateAngleZ += swimSwing * swimAmount * 0.5F;
        head.rotateAngleY += swimSwing * swimAmount * 0.5F;
        tail.rotateAngleY += MathHelper.sin(limbSwing * 0.25F - 2.0F) * swimAmount * 0.375F;
        rightArm.rotateAngleZ -= swimAmount * 1.4F;
        leftArm.rotateAngleZ += swimAmount * 1.4F;
        rightLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.375F + 2.0F) * swimAmount * 0.5F;
        leftLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.375F + 2.0F + (float) Math.PI) * swimAmount * 0.5F;
    }

    private void resetPose() {
        body.rotationPointY = 2.1667F;
        zero(body);
        zero(tail);
        zero(head);
        zero(jaw);
        zero(rightArm);
        zero(leftArm);
        zero(rightLeg);
        zero(leftLeg);
        zero(headFins);
        headFins.rotateAngleX = -0.7854F;
    }

    private void zero(ModelRenderer renderer) {
        renderer.rotateAngleX = 0.0F;
        renderer.rotateAngleY = 0.0F;
        renderer.rotateAngleZ = 0.0F;
    }

    private void addMirroredBox(ModelRenderer renderer, int textureX, int textureY, float x, float y, float z, int width, int height, int depth, float scale) {
        renderer.mirror = true;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, scale);
        renderer.mirror = false;
    }
}
