package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.RaycatEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class RaycatModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer tail;
    private final ModelRenderer tail2;

    public RaycatModel() {
        textureWidth = 64;
        textureHeight = 64;
        body = part(28, 44, -2.5F, -3.5F, -7.0F, 5, 7, 13, 0.25F, false);
        body.setRotationPoint(0.0F, 17.5F, 0.0F);
        body.setTextureOffset(0, 34).addBox(-2.5F, -3.5F, -7.0F, 5, 7, 13, 0.0F);

        head = part(20, 19, -2.5F, -1.5F, -5.0F, 5, 5, 5, 0.0F, false);
        head.setRotationPoint(0.0F, -3.0F, -7.0F);
        head.setTextureOffset(40, 20).addBox(-2.5F, -0.5F, -5.0F, 5, 2, 2, 0.0F);
        head.setTextureOffset(38, 16).addBox(0.0F, -0.5F, -5.0F, 0, 2, 2, 0.0F);
        head.setTextureOffset(21, 0).addBox(-1.5F, 1.5F, -7.0F, 3, 3, 2, 0.0F);
        head.setTextureOffset(42, 16).addBox(-0.5F, 1.5F, -7.0F, 1, 1, 2, 0.0F);
        head.setTextureOffset(44, 54).addBox(-2.5F, -1.5F, -5.0F, 5, 5, 5, 0.25F);
        head.setTextureOffset(44, 54).addBox(-2.75F, -2.75F, -1.75F, 1, 1, 2, 0.0F);
        head.setTextureOffset(44, 54).addBox(1.75F, -2.75F, -1.75F, 1, 1, 2, 0.0F);
        head.setTextureOffset(56, 60).addBox(-1.5F, 1.5F, -7.0F, 3, 3, 2, 0.25F);
        body.addChild(head);

        rightArm = part(56, 51, -1.0F, -2.5F, -1.0F, 2, 11, 2, 0.25F, true);
        rightArm.setRotationPoint(-2.0F, -2.0F, -5.0F);
        rightArm.setTextureOffset(0, 34).addBox(-1.0F, -2.5F, -1.0F, 2, 11, 2, 0.0F);
        body.addChild(rightArm);

        leftArm = part(56, 51, -1.0F, -2.5F, -1.0F, 2, 11, 2, 0.25F, false);
        leftArm.setRotationPoint(2.0F, -2.0F, -5.0F);
        leftArm.setTextureOffset(0, 34).addBox(-1.0F, -2.5F, -1.0F, 2, 11, 2, 0.0F);
        body.addChild(leftArm);

        rightLeg = part(34, 0, -1.0F, 0.5F, -1.0F, 2, 4, 2, 0.0F, true);
        rightLeg.setRotationPoint(-1.5F, 2.0F, 5.0F);
        rightLeg.setTextureOffset(56, 58).addBox(-1.0F, 0.5F, -1.0F, 2, 4, 2, 0.25F);
        body.addChild(rightLeg);

        leftLeg = part(56, 58, -1.0F, 0.5F, -1.0F, 2, 4, 2, 0.25F, false);
        leftLeg.setRotationPoint(1.5F, 2.0F, 5.0F);
        leftLeg.setTextureOffset(34, 0).addBox(-1.0F, 0.5F, -1.0F, 2, 4, 2, 0.0F);
        body.addChild(leftLeg);

        tail = part(48, 55, -1.0F, -1.0F, 0.0F, 2, 2, 8, 0.0F, false);
        tail.setRotationPoint(0.0F, -2.5F, 6.0F);
        tail.setTextureOffset(22, 0).addBox(0.0F, -0.5F, 0.0F, 0, 1, 8, 0.0F);
        body.addChild(tail);

        tail2 = part(48, 55, -1.0F, -1.0F, 0.0F, 2, 2, 8, 0.0F, false);
        tail2.setRotationPoint(0.0F, 0.0F, 8.0F);
        tail2.setTextureOffset(42, 0).addBox(0.0F, -0.5F, 0.0F, 0, 1, 8, 0.0F);
        tail.addChild(tail2);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        RaycatEntity raycat = entity instanceof RaycatEntity ? (RaycatEntity) entity : null;
        float partialTicks = raycat == null ? 0.0F : ageInTicks - raycat.ticksExisted;
        float layProgress = raycat == null ? 0.0F : raycat.getLayProgress(partialTicks);
        float sitProgress = raycat == null ? 0.0F : raycat.getSitProgress(partialTicks) * (1.0F - layProgress);
        progressRotation(body, sitProgress, -0.6109F, 0.0F, 0.0F);
        progressRotation(rightLeg, sitProgress, -0.7854F, 0.0F, 0.0F);
        progressRotation(leftLeg, sitProgress, -0.7854F, 0.0F, 0.0F);
        progressPosition(rightArm, sitProgress, 0.0F, 4.0F, -1.0F);
        progressPosition(leftArm, sitProgress, 0.0F, 4.0F, -1.0F);
        progressRotation(rightArm, sitProgress, 0.6109F, 0.0F, 0.0F);
        progressRotation(leftArm, sitProgress, 0.6109F, 0.0F, 0.0F);
        progressRotation(tail, sitProgress, 0.6109F, 0.0F, 0.0F);
        progressPosition(head, sitProgress, 0.0F, -1.0F, -2.0F);
        progressRotation(head, sitProgress, 0.6109F, 0.0F, 0.0F);

        progressPosition(body, layProgress, 0.0F, 4.0F, -1.0F);
        progressRotation(body, layProgress, -0.5236F, 0.0F, 1.5708F);
        progressPosition(head, layProgress, -2.0F, 2.0F, 0.0F);
        progressRotation(head, layProgress, -0.1745F, 0.0F, -1.309F);
        progressRotation(rightLeg, layProgress, -0.7854F, 0.0F, -0.6981F);
        progressRotation(leftLeg, layProgress, 0.4363F, 0.0F, 0.0F);
        progressPosition(rightArm, layProgress, -1.0F, 1.0F, 0.0F);
        progressRotation(rightArm, layProgress, 0.2618F, 0.0F, -0.4363F);
        progressRotation(leftArm, layProgress, -0.2618F, 0.0F, 0.0F);

        float walk = limbSwing * 0.7F;
        float idle = ageInTicks * 0.1F;
        tail.rotateAngleX += MathHelper.cos(idle) * 0.05F + 0.15F;
        tail.rotateAngleY += MathHelper.sin(idle - 1.0F) * 0.2F;
        tail2.rotateAngleY += MathHelper.sin(idle - 2.5F) * 0.3F;
        head.rotateAngleX += MathHelper.cos(idle) * 0.05F;
        tail.rotateAngleX += MathHelper.cos(walk) * limbSwingAmount * 0.1F - 0.2F * limbSwingAmount;
        tail2.rotateAngleX += MathHelper.cos(walk) * limbSwingAmount * 0.2F - 0.2F * limbSwingAmount;
        rightLeg.rotateAngleX += MathHelper.cos(walk) * limbSwingAmount;
        leftLeg.rotateAngleX -= MathHelper.cos(walk) * limbSwingAmount;
        rightArm.rotateAngleX -= MathHelper.cos(walk) * limbSwingAmount * 0.8F;
        leftArm.rotateAngleX += MathHelper.cos(walk) * limbSwingAmount * 0.8F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        head.rotateAngleX += headPitch * 0.017453292F;
    }

    private void progressRotation(ModelRenderer part, float progress, float x, float y, float z) {
        part.rotateAngleX += x * progress;
        part.rotateAngleY += y * progress;
        part.rotateAngleZ += z * progress;
    }

    private void progressPosition(ModelRenderer part, float progress, float x, float y, float z) {
        part.rotationPointX += x * progress;
        part.rotationPointY += y * progress;
        part.rotationPointZ += z * progress;
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, 17.5F, 0.0F);
        head.setRotationPoint(0.0F, -3.0F, -7.0F);
        rightArm.setRotationPoint(-2.0F, -2.0F, -5.0F);
        leftArm.setRotationPoint(2.0F, -2.0F, -5.0F);
        rightLeg.setRotationPoint(-1.5F, 2.0F, 5.0F);
        leftLeg.setRotationPoint(1.5F, 2.0F, 5.0F);
        tail.setRotationPoint(0.0F, -2.5F, 6.0F);
        tail2.setRotationPoint(0.0F, 0.0F, 8.0F);
        body.rotateAngleX = body.rotateAngleY = body.rotateAngleZ = 0.0F;
        head.rotateAngleX = head.rotateAngleY = head.rotateAngleZ = 0.0F;
        rightArm.rotateAngleX = rightArm.rotateAngleY = rightArm.rotateAngleZ = 0.0F;
        leftArm.rotateAngleX = leftArm.rotateAngleY = leftArm.rotateAngleZ = 0.0F;
        rightLeg.rotateAngleX = rightLeg.rotateAngleY = rightLeg.rotateAngleZ = 0.0F;
        leftLeg.rotateAngleX = leftLeg.rotateAngleY = leftLeg.rotateAngleZ = 0.0F;
        tail.rotateAngleX = tail.rotateAngleY = tail.rotateAngleZ = 0.0F;
        tail2.rotateAngleX = tail2.rotateAngleY = tail2.rotateAngleZ = 0.0F;
    }

    private ModelRenderer part(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, float inflate, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, inflate);
        return renderer;
    }
}
