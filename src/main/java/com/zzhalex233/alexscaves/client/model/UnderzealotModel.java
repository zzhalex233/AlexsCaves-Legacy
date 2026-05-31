package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.UnderzealotEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class UnderzealotModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;

    public UnderzealotModel() {
        textureWidth = 64;
        textureHeight = 64;
        root = empty(0.0F, 24.0F, 0.0F);
        body = part(0, 17, 0.0F, -13.0F, 0.0F, -5.0F, -8.0F, -3.0F, 10, 11, 6);
        head = part(0, 0, 0.0F, -8.0F, -1.0F, -4.0F, -8.0F, -4.0F, 8, 8, 8);
        leftArm = part(32, 0, -5.0F, -18.0F, 0.0F, -3.0F, 0.0F, -2.0F, 3, 12, 4);
        rightArm = part(32, 0, 5.0F, -18.0F, 0.0F, 0.0F, 0.0F, -2.0F, 3, 12, 4);
        leftLeg = part(24, 35, -2.5F, -10.0F, 0.0F, -2.0F, 0.0F, -2.0F, 3, 10, 4);
        rightLeg = part(24, 35, 2.5F, -10.0F, 0.0F, -1.0F, 0.0F, -2.0F, 3, 10, 4);
        root.addChild(body);
        body.addChild(head);
        body.addChild(leftArm);
        body.addChild(rightArm);
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
        UnderzealotEntity underzealot = entity instanceof UnderzealotEntity ? (UnderzealotEntity) entity : null;
        float partial = underzealot == null ? 0.0F : ageInTicks - underzealot.ticksExisted;
        float buried = underzealot == null ? 0.0F : underzealot.getBuriedProgress(partial);
        float carrying = underzealot == null ? 0.0F : underzealot.getCarryingProgress(partial);
        float praying = underzealot == null ? 0.0F : underzealot.getPrayingProgress(partial);
        root.rotationPointY += buried * 18.0F;
        body.rotateAngleX += praying * 0.35F;
        head.rotateAngleX += headPitch * 0.017453292F - praying * 0.35F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        leftLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount * 0.8F;
        rightLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount * 0.8F;
        leftArm.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount * 0.7F - carrying * 1.2F + praying * 1.7F;
        rightArm.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount * 0.7F - carrying * 1.2F + praying * 1.7F;
        leftArm.rotateAngleZ -= carrying * 0.5F;
        rightArm.rotateAngleZ += carrying * 0.5F;
        if (underzealot != null && underzealot.getAnimation() != UnderzealotEntity.ANIMATION_NONE) {
            float strike = MathHelper.sin(MathHelper.clamp(underzealot.getAnimationTick() / 15.0F, 0.0F, 1.0F) * (float) Math.PI);
            if (underzealot.getAnimation() == UnderzealotEntity.ANIMATION_ATTACK_0) {
                rightArm.rotateAngleX -= strike * 1.8F;
                body.rotateAngleY += strike * 0.4F;
            } else if (underzealot.getAnimation() == UnderzealotEntity.ANIMATION_ATTACK_1) {
                leftArm.rotateAngleX -= strike * 1.8F;
                body.rotateAngleY -= strike * 0.4F;
            } else if (underzealot.getAnimation() == UnderzealotEntity.ANIMATION_BREAKTORCH) {
                rightArm.rotateAngleX -= strike * 2.2F;
                leftArm.rotateAngleX -= strike * 1.4F;
            }
        }
    }

    private void resetPose() {
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        body.setRotationPoint(0.0F, -13.0F, 0.0F);
        head.setRotationPoint(0.0F, -8.0F, -1.0F);
        leftArm.setRotationPoint(-5.0F, -18.0F, 0.0F);
        rightArm.setRotationPoint(5.0F, -18.0F, 0.0F);
        leftLeg.setRotationPoint(-2.5F, -10.0F, 0.0F);
        rightLeg.setRotationPoint(2.5F, -10.0F, 0.0F);
        ModelRenderer[] parts = {root, body, head, leftArm, rightArm, leftLeg, rightLeg};
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
