package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.BrainiacEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BrainiacModel extends ModelBase {
    private final ModelRenderer torso;
    private final ModelRenderer chest;
    private final ModelRenderer leftArm;
    private final ModelRenderer handMaw;
    private final ModelRenderer handMawUpper;
    private final ModelRenderer rightArm;
    private final ModelRenderer head;
    private final ModelRenderer brain;
    private final ModelRenderer tongue;
    private final ModelRenderer tongue2;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;

    public BrainiacModel() {
        textureWidth = 256;
        textureHeight = 256;
        torso = part(0, 68, -5.5F, -4.0F, -4.0F, 11, 14, 8, false);
        torso.setRotationPoint(0.0F, 3.0F, 0.0F);
        torso.setTextureOffset(0, 107).addBox(-5.5F, -3.0F, -4.0F, 11, 13, 8, 0.25F);

        chest = part(0, 42, -8.5F, -14.0F, -11.0F, 17, 14, 12, false);
        chest.setRotationPoint(0.0F, 0.0F, 4.0F);
        torso.addChild(chest);

        leftArm = part(0, 0, 0.3333F, -1.8333F, -4.1667F, 8, 23, 9, false);
        leftArm.setRotationPoint(8.1667F, -8.1667F, -5.0833F);
        leftArm.setTextureOffset(70, 79).addBox(1.3333F, -2.8333F, -6.1667F, 8, 8, 8, 0.0F);
        leftArm.setTextureOffset(35, 98).addBox(1.3333F, -2.8333F, -6.1667F, 8, 8, 8, -0.25F);
        leftArm.setTextureOffset(38, 79).addBox(1.3333F, 9.1667F, -2.1667F, 8, 8, 8, 0.0F);
        leftArm.setTextureOffset(35, 98).addBox(1.3333F, 9.1667F, -2.1667F, 8, 8, 8, -0.25F);
        chest.addChild(leftArm);

        handMaw = part(0, 90, -4.0F, 0.0F, -4.5F, 4, 8, 9, false);
        handMaw.setRotationPoint(8.3333F, 21.1667F, 0.3333F);
        leftArm.addChild(handMaw);
        handMawUpper = part(92, 0, 0.0F, 0.0F, -4.5F, 4, 8, 9, false);
        handMawUpper.setRotationPoint(-8.0F, 0.0F, 0.0F);
        handMaw.addChild(handMawUpper);

        rightArm = part(56, 0, -8.25F, -3.75F, -3.25F, 9, 37, 9, false);
        rightArm.setRotationPoint(-7.25F, -12.25F, -6.0F);
        rightArm.setTextureOffset(84, 38).addBox(-10.25F, 15.25F, -5.25F, 8, 8, 8, 0.0F);
        rightArm.setTextureOffset(35, 98).addBox(-10.25F, 15.25F, -5.25F, 8, 8, 8, -0.25F);
        chest.addChild(rightArm);

        head = part(100, 54, -3.5F, -3.0F, -6.25F, 7, 10, 7, false);
        head.setRotationPoint(0.0F, -7.0F, -11.25F);
        chest.addChild(head);
        brain = part(46, 56, -6.5F, -13.0F, -7.75F, 13, 11, 12, false);
        brain.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.addChild(brain);
        tongue = part(73, 106, -2.5F, -0.5F, -21.0F, 5, 1, 21, false);
        tongue.setRotationPoint(0.0F, 3.5F, -1.75F);
        head.addChild(tongue);
        tongue2 = part(84, 84, -2.5F, -0.5F, -21.0F, 5, 1, 21, false);
        tongue2.setRotationPoint(0.0F, 0.0F, -21.0F);
        tongue.addChild(tongue2);

        leftLeg = part(26, 91, -2.0F, 0.5F, -2.0F, 4, 11, 4, true);
        leftLeg.setRotationPoint(3.5F, 9.5F, 0.0F);
        leftLeg.setTextureOffset(67, 95).addBox(-2.0F, 0.5F, -2.0F, 4, 11, 4, 0.25F);
        torso.addChild(leftLeg);
        rightLeg = part(26, 91, -2.0F, 0.5F, -2.0F, 4, 11, 4, false);
        rightLeg.setRotationPoint(-3.5F, 9.5F, 0.0F);
        rightLeg.setTextureOffset(67, 95).addBox(-2.0F, 0.5F, -2.0F, 4, 11, 4, 0.25F);
        torso.addChild(rightLeg);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        torso.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        BrainiacEntity brainiac = entity instanceof BrainiacEntity ? (BrainiacEntity) entity : null;
        float partialTick = brainiac == null ? 0.0F : ageInTicks - brainiac.ticksExisted;
        float walkSpeed = 0.5F;
        float hunchAmount = limbSwingAmount;
        if (brainiac != null) {
            hunchAmount *= 1.0F - brainiac.getRaiseArmsAmount(partialTick);
        }
        float bodyBob = Math.abs(MathHelper.cos(limbSwing * walkSpeed)) * limbSwingAmount * 1.2F;
        torso.rotationPointY += bodyBob;
        chest.rotateAngleX += Math.min(1.0F, hunchAmount * 3.0F) * 0.35F;
        head.rotateAngleX -= Math.min(1.0F, hunchAmount * 3.0F) * 0.35F;
        rightLeg.rotateAngleX += MathHelper.cos(limbSwing * walkSpeed) * limbSwingAmount;
        leftLeg.rotateAngleX += MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * limbSwingAmount;
        rightArm.rotateAngleX -= MathHelper.cos(limbSwing * walkSpeed + 1.0F) * hunchAmount * 1.3F;
        leftArm.rotateAngleX -= MathHelper.cos(limbSwing * walkSpeed - 1.0F) * hunchAmount * 1.3F * (brainiac == null ? 1.0F : 1.0F - brainiac.getRaiseLeftArmAmount(partialTick));
        chest.rotateAngleZ += MathHelper.sin(limbSwing * walkSpeed + 2.5F) * limbSwingAmount * 0.2F;
        torso.rotateAngleZ += MathHelper.sin(limbSwing * walkSpeed + 3.0F) * limbSwingAmount * 0.1F;
        head.rotateAngleZ += MathHelper.sin(ageInTicks * 0.15F) * 0.1F;
        brain.rotationPointY += MathHelper.sin(ageInTicks * 0.5F) * 0.35F;

        if (brainiac != null) {
            animateAction(brainiac);
            float tongueLaunch = brainiac.getShootTongueAmount(partialTick);
            float tongueLength = brainiac.getLastTongueDistance(partialTick) * tongueLaunch;
            tongue.showModel = tongueLength > 0.0F;
            tongue2.rotationPointZ = -21.0F * Math.max(0.05F, tongueLength);
            tongue.rotateAngleX += MathHelper.sin(ageInTicks * 0.4F) * 0.1F;
            tongue2.rotateAngleX += MathHelper.sin(ageInTicks * 0.4F) * 0.2F;
            tongue.rotateAngleY += MathHelper.sin(ageInTicks * 1.5F) * 0.4F * MathHelper.sin(tongueLaunch * (float) Math.PI);
            head.rotateAngleX += headPitch * 0.004363323F;
            head.rotateAngleY += netHeadYaw * 0.008726646F;
        } else {
            tongue.showModel = false;
        }
    }

    private void animateAction(BrainiacEntity brainiac) {
        float progress = MathHelper.clamp(brainiac.getAnimationTick() / (float) Math.max(1, actionLength(brainiac.getAnimation())), 0.0F, 1.0F);
        float strike = MathHelper.sin(progress * (float) Math.PI);
        if (brainiac.getAnimation() == BrainiacEntity.ANIMATION_THROW_BARREL) {
            leftArm.rotateAngleX -= 4.7F * MathHelper.clamp(progress * 2.0F, 0.0F, 1.0F);
            leftArm.rotateAngleY -= 0.2F;
            handMaw.rotateAngleZ += progress < 0.5F ? 0.35F : -1.6F * strike;
            handMawUpper.rotateAngleZ += progress < 0.5F ? 0.7F : 1.6F * strike;
            torso.rotateAngleY += 0.35F * strike;
        } else if (brainiac.getAnimation() == BrainiacEntity.ANIMATION_DRINK_BARREL) {
            leftArm.rotationPointY += 6.0F * strike;
            leftArm.rotationPointZ += 8.0F * strike;
            leftArm.rotateAngleX -= 1.4F * strike;
            leftArm.rotateAngleY += 0.35F * strike;
            head.rotateAngleX -= 0.35F * strike;
            handMaw.rotateAngleZ -= 1.6F * strike;
            handMawUpper.rotateAngleZ += 1.6F * strike;
        } else if (brainiac.getAnimation() == BrainiacEntity.ANIMATION_BITE) {
            torso.rotateAngleY += 0.2F * strike;
            chest.rotateAngleY += 0.2F * strike;
            leftArm.rotateAngleX -= 1.3F * strike;
            leftArm.rotateAngleY -= 0.35F * strike;
            handMaw.rotateAngleZ -= 1.5F * strike;
            handMawUpper.rotateAngleZ += 1.5F * strike;
        } else if (brainiac.getAnimation() == BrainiacEntity.ANIMATION_SMASH) {
            chest.rotateAngleX += -0.5F + 0.85F * strike;
            rightArm.rotateAngleX -= 2.2F * (1.0F - strike) + 0.35F * strike;
            leftArm.rotateAngleX -= 2.2F * (1.0F - strike) + 0.35F * strike;
            rightArm.rotateAngleY += 0.35F - 0.7F * strike;
            leftArm.rotateAngleY -= 0.35F - 0.7F * strike;
        }
    }

    private int actionLength(int animation) {
        switch (animation) {
            case BrainiacEntity.ANIMATION_THROW_BARREL:
                return 30;
            case BrainiacEntity.ANIMATION_DRINK_BARREL:
                return 75;
            case BrainiacEntity.ANIMATION_BITE:
                return 25;
            case BrainiacEntity.ANIMATION_SMASH:
                return 20;
            default:
                return 1;
        }
    }

    public void translateToArmOrChest(boolean arm) {
        torso.postRender(0.0625F);
        chest.postRender(0.0625F);
        if (arm) {
            leftArm.postRender(0.0625F);
        }
    }

    private void resetPose() {
        torso.setRotationPoint(0.0F, 3.0F, 0.0F);
        chest.setRotationPoint(0.0F, 0.0F, 4.0F);
        leftArm.setRotationPoint(8.1667F, -8.1667F, -5.0833F);
        rightArm.setRotationPoint(-7.25F, -12.25F, -6.0F);
        head.setRotationPoint(0.0F, -7.0F, -11.25F);
        handMaw.setRotationPoint(8.3333F, 21.1667F, 0.3333F);
        handMawUpper.setRotationPoint(-8.0F, 0.0F, 0.0F);
        leftLeg.setRotationPoint(3.5F, 9.5F, 0.0F);
        rightLeg.setRotationPoint(-3.5F, 9.5F, 0.0F);
        zero(torso, chest, leftArm, rightArm, head, brain, tongue, tongue2, leftLeg, rightLeg, handMaw, handMawUpper);
        tongue2.rotationPointZ = -21.0F;
    }

    private void zero(ModelRenderer... parts) {
        for (ModelRenderer part : parts) {
            part.rotateAngleX = 0.0F;
            part.rotateAngleY = 0.0F;
            part.rotateAngleZ = 0.0F;
        }
    }

    private ModelRenderer part(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        return renderer;
    }
}
