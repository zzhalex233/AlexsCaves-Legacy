package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.VallumraptorEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class VallumraptorModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer neck;
    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer tail;
    private final ModelRenderer tailTip;
    private final ModelRenderer leftLeg;
    private final ModelRenderer leftLowerLeg;
    private final ModelRenderer leftFoot;
    private final ModelRenderer rightLeg;
    private final ModelRenderer rightLowerLeg;
    private final ModelRenderer rightFoot;
    private final ModelRenderer leftArm;
    private final ModelRenderer leftHand;
    private final ModelRenderer rightArm;
    private final ModelRenderer rightHand;
    private final ModelRenderer headQuill;
    private final ModelRenderer tailQuill;

    public VallumraptorModel() {
        textureWidth = 64;
        textureHeight = 64;
        body = box(0, 0, -3.5F, -3.5F, -6.0F, 7, 7, 12);
        body.setRotationPoint(0.0F, 10.5F, -1.0F);
        leftLeg = box(34, 14, -1.5F, -2.0F, -3.5F, 4, 8, 5);
        leftLeg.setRotationPoint(3.0F, 0.5F, 2.5F);
        body.addChild(leftLeg);
        leftLowerLeg = box(0, 48, -1.0F, -0.5F, -0.5F, 2, 9, 2);
        leftLowerLeg.setRotationPoint(0.5F, 4.5F, 1.0F);
        leftLeg.addChild(leftLowerLeg);
        leftFoot = box(20, 0, -2.5F, 0.0F, -5.0F, 5, 1, 6);
        leftFoot.setRotationPoint(0.0F, 8.5F, 0.5F);
        leftLowerLeg.addChild(leftFoot);
        rightLeg = box(34, 14, -2.5F, -2.0F, -3.5F, 4, 8, 5);
        rightLeg.setRotationPoint(-3.0F, 0.5F, 2.5F);
        body.addChild(rightLeg);
        rightLowerLeg = box(0, 48, -1.0F, -0.75F, -0.5F, 2, 9, 2);
        rightLowerLeg.setRotationPoint(-0.5F, 4.75F, 1.0F);
        rightLeg.addChild(rightLowerLeg);
        rightFoot = box(20, 0, -2.5F, 0.0F, -5.0F, 5, 1, 6);
        rightFoot.setRotationPoint(0.0F, 8.25F, 0.5F);
        rightLowerLeg.addChild(rightFoot);
        leftArm = box(8, 48, -0.5F, -1.0F, -1.0F, 2, 6, 2);
        leftArm.setRotationPoint(3.0F, 2.5F, -3.0F);
        body.addChild(leftArm);
        leftHand = box(0, 28, -4.0F, -1.5F, -3.0F, 4, 3, 3);
        leftHand.setRotationPoint(1.5F, 3.5F, -4.0F);
        leftArm.addChild(leftHand);
        rightArm = box(8, 48, -1.5F, -1.0F, -1.0F, 2, 6, 2);
        rightArm.setRotationPoint(-3.0F, 2.5F, -3.0F);
        body.addChild(rightArm);
        rightHand = box(0, 28, 0.0F, -1.5F, -3.0F, 4, 3, 3);
        rightHand.setRotationPoint(-1.5F, 3.5F, -4.0F);
        rightArm.addChild(rightHand);
        neck = box(47, 22, -1.5F, -8.0F, -3.0F, 3, 9, 5);
        neck.setRotationPoint(0.0F, -1.5F, -5.0F);
        body.addChild(neck);
        head = box(26, 54, -2.0F, -1.0F, -8.0F, 4, 5, 7);
        head.setRotationPoint(0.0F, -8.0F, -1.0F);
        head.setTextureOffset(50, 8).addBox(-1.0F, -6.0F, -8.0F, 2, 6, 5);
        neck.addChild(head);
        jaw = box(30, 4, -1.5F, 0.0F, -6.0F, 3, 2, 6);
        jaw.setRotationPoint(0.0F, 2.0F, -1.5F);
        head.addChild(jaw);
        headQuill = box(46, 27, 0.0F, -5.0F, -5.0F, 0, 14, 9);
        headQuill.setRotationPoint(0.0F, -8.0F, 2.0F);
        neck.addChild(headQuill);
        tail = box(16, 19, -1.5F, -1.25F, 1.0F, 3, 3, 12);
        tail.setRotationPoint(0.0F, -2.25F, 5.0F);
        body.addChild(tail);
        tailTip = box(0, 5, 0.0F, -7.5F, 0.0F, 0, 9, 14);
        tailTip.setRotationPoint(0.0F, 0.25F, 13.0F);
        tail.addChild(tailTip);
        tailQuill = box(18, 36, 0.0F, -5.0F, -6.0F, 0, 6, 12);
        tailQuill.setRotationPoint(0.0F, -1.25F, 7.0F);
        tail.addChild(tailQuill);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        VallumraptorEntity raptor = entity instanceof VallumraptorEntity ? (VallumraptorEntity) entity : null;
        float partial = raptor == null ? 0.0F : MathHelper.clamp(ageInTicks - raptor.ticksExisted, 0.0F, 1.0F);
        float run = raptor == null ? 0.0F : raptor.getRunProgress(partial);
        float leap = raptor == null ? 0.0F : raptor.getLeapProgress(partial);
        float sit = raptor == null ? 0.0F : Math.max(raptor.getSitProgress(partial), raptor.getRelaxedProgress(partial));
        float dance = raptor == null ? 0.0F : raptor.getDanceProgress(partial);
        float puzzle = raptor == null ? 0.0F : raptor.getPuzzledHeadRot(partial) * 0.017453292F;
        float speed = run > 0.0F ? 0.7F : 0.45F;
        float degree = run > 0.0F ? 0.85F : 0.65F;
        body.rotateAngleX = -0.08F * limbSwingAmount - 0.35F * leap;
        neck.rotateAngleX = headPitch * 0.006F + 0.2F * leap;
        head.rotateAngleX = headPitch * 0.011F + Math.abs(puzzle) * 0.3F;
        head.rotateAngleY = netHeadYaw * 0.017453292F * 0.35F;
        head.rotateAngleZ = puzzle;
        jaw.rotateAngleX = 0.2F * leap;
        leftLeg.rotateAngleX = MathHelper.cos(limbSwing * speed) * degree * limbSwingAmount - 0.2F * sit;
        rightLeg.rotateAngleX = MathHelper.cos(limbSwing * speed + (float) Math.PI) * degree * limbSwingAmount - 0.2F * sit;
        leftLowerLeg.rotateAngleX = MathHelper.cos(limbSwing * speed + 1.0F) * degree * 0.45F * limbSwingAmount - 0.8F * sit - 0.5F * leap;
        rightLowerLeg.rotateAngleX = MathHelper.cos(limbSwing * speed + (float) Math.PI + 1.0F) * degree * 0.45F * limbSwingAmount - 0.8F * sit - 0.5F * leap;
        leftFoot.rotateAngleX = -leftLowerLeg.rotateAngleX * 0.6F + 1.0F * sit;
        rightFoot.rotateAngleX = -rightLowerLeg.rotateAngleX * 0.6F + 1.0F * sit;
        leftArm.rotateAngleX = -0.25F + MathHelper.cos(ageInTicks * 0.1F) * 0.08F - 0.7F * leap;
        rightArm.rotateAngleX = -0.25F + MathHelper.cos(ageInTicks * 0.1F + 1.0F) * 0.08F - 0.7F * leap;
        leftHand.rotateAngleY = -0.2F;
        rightHand.rotateAngleY = 0.2F;
        tail.rotateAngleY = MathHelper.cos(limbSwing * speed + 1.0F) * 0.35F * limbSwingAmount;
        tailTip.rotateAngleY = tail.rotateAngleY * 0.5F;
        if (raptor != null) {
            float tailYaw = MathHelper.wrapDegrees(raptor.getTailYaw(partial) - raptor.renderYawOffset) * 0.017453292F;
            tail.rotateAngleY += tailYaw * 0.8F;
            tailTip.rotateAngleY += tailYaw * 0.2F;
            if (raptor.getAnimation() == VallumraptorEntity.ANIMATION_CALL_1 || raptor.getAnimation() == VallumraptorEntity.ANIMATION_CALL_2) {
                jaw.rotateAngleX = 0.75F;
                neck.rotateAngleX -= 0.25F;
            } else if (raptor.getAnimation() == VallumraptorEntity.ANIMATION_GRAB) {
                leftArm.rotateAngleX = -1.2F;
                rightArm.rotateAngleX = -1.2F;
                head.rotateAngleX = 0.35F;
            } else if (raptor.getAnimation() == VallumraptorEntity.ANIMATION_MELEE_BITE) {
                jaw.rotateAngleX = 0.9F;
            } else if (raptor.getAnimation() == VallumraptorEntity.ANIMATION_SCRATCH_1) {
                rightArm.rotateAngleX = -1.6F + MathHelper.sin(ageInTicks * 0.8F) * 0.4F;
            } else if (raptor.getAnimation() == VallumraptorEntity.ANIMATION_SCRATCH_2) {
                leftArm.rotateAngleX = -1.6F + MathHelper.sin(ageInTicks * 0.8F) * 0.4F;
            }
        }
        if (sit > 0.0F) {
            body.rotationPointY += sit * 5.0F;
            body.rotationPointZ -= sit;
            tail.rotateAngleX -= sit * 0.35F;
        }
        if (dance > 0.0F) {
            body.rotateAngleZ += MathHelper.sin(ageInTicks * 0.5F) * 0.1F * dance;
            leftArm.rotateAngleZ -= 0.7F * dance;
            rightArm.rotateAngleZ += 0.7F * dance;
        }
    }

    public void translateToHand(boolean left) {
        body.postRender(0.0625F);
        (left ? leftArm : rightArm).postRender(0.0625F);
    }

    private ModelRenderer box(int u, int v, float x, float y, float z, int dx, int dy, int dz) {
        ModelRenderer renderer = new ModelRenderer(this, u, v);
        renderer.addBox(x, y, z, dx, dy, dz);
        return renderer;
    }

    private void resetPose() {
        reset(body, 0.0F, 10.5F, -1.0F);
        reset(leftLeg, 3.0F, 0.5F, 2.5F);
        reset(leftLowerLeg, 0.5F, 4.5F, 1.0F);
        reset(leftFoot, 0.0F, 8.5F, 0.5F);
        reset(rightLeg, -3.0F, 0.5F, 2.5F);
        reset(rightLowerLeg, -0.5F, 4.75F, 1.0F);
        reset(rightFoot, 0.0F, 8.25F, 0.5F);
        reset(leftArm, 3.0F, 2.5F, -3.0F);
        reset(leftHand, 1.5F, 3.5F, -4.0F);
        reset(rightArm, -3.0F, 2.5F, -3.0F);
        reset(rightHand, -1.5F, 3.5F, -4.0F);
        reset(neck, 0.0F, -1.5F, -5.0F);
        reset(head, 0.0F, -8.0F, -1.0F);
        reset(jaw, 0.0F, 2.0F, -1.5F);
        reset(headQuill, 0.0F, -8.0F, 2.0F);
        reset(tail, 0.0F, -2.25F, 5.0F);
        reset(tailTip, 0.0F, 0.25F, 13.0F);
        reset(tailQuill, 0.0F, -1.25F, 7.0F);
    }

    private void reset(ModelRenderer renderer, float x, float y, float z) {
        renderer.rotationPointX = x;
        renderer.rotationPointY = y;
        renderer.rotationPointZ = z;
        renderer.rotateAngleX = 0.0F;
        renderer.rotateAngleY = 0.0F;
        renderer.rotateAngleZ = 0.0F;
    }
}
