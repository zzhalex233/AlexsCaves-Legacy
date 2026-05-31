package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.CorrodentEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CorrodentModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer chest;
    private final ModelRenderer head;
    private final ModelRenderer rightWhiskerSmall;
    private final ModelRenderer rightWhisker;
    private final ModelRenderer leftWhiskerSmall;
    private final ModelRenderer leftWhisker;
    private final ModelRenderer snout;
    private final ModelRenderer nose;
    private final ModelRenderer jaw;
    private final ModelRenderer rightArmPivot;
    private final ModelRenderer leftArmPivot;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer hipsPivot;
    private final ModelRenderer hips;
    private final ModelRenderer tail;
    private final ModelRenderer rightLegPivot;
    private final ModelRenderer leftLegPivot;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;

    public CorrodentModel() {
        textureWidth = 128;
        textureHeight = 128;
        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        chest = empty(0.0F, -7.0F, 5.0F);
        chest.setTextureOffset(0, 46).addBox(-1.0F, -10.0F, -7.0F, 2, 5, 14, 0.0F);
        chest.setTextureOffset(0, 23).addBox(-4.0F, -5.0F, -7.0F, 8, 9, 14, 0.0F);
        root.addChild(chest);

        head = empty(0.0F, 0.0F, -6.5F);
        head.setTextureOffset(30, 28).addBox(-5.0F, -4.0F, -3.5F, 1, 1, 2, 0.0F);
        head.setTextureOffset(50, 0).addBox(-5.0F, -3.0F, -7.5F, 10, 6, 7, 0.0F);
        head.setTextureOffset(30, 28).addBox(4.0F, -4.0F, -3.5F, 1, 1, 2, 0.0F);
        chest.addChild(head);

        rightWhiskerSmall = part(0, 62, 4.5F, -8.0F, -2.5F, 0.0F, -5.0F, -3.0F, 0, 10, 6, false);
        rightWhisker = part(0, 17, 4.0F, -3.0F, -7.0F, 0.0F, -10.0F, -0.5F, 0, 10, 6, false);
        leftWhiskerSmall = part(0, 62, -4.5F, -8.0F, -2.5F, 0.0F, -5.0F, -3.0F, 0, 10, 6, true);
        leftWhisker = part(0, 17, -4.0F, -3.0F, -7.0F, 0.0F, -10.0F, -0.5F, 0, 10, 6, true);
        head.addChild(rightWhiskerSmall);
        head.addChild(rightWhisker);
        head.addChild(leftWhiskerSmall);
        head.addChild(leftWhisker);

        snout = empty(0.0F, -3.0F, -6.0F);
        snout.setTextureOffset(30, 7).addBox(-2.0F, 3.0F, -9.5F, 4, 7, 0, 0.0F);
        snout.setTextureOffset(48, 28).addBox(-3.0F, -2.0F, -9.5F, 6, 5, 10, 0.0F);
        head.addChild(snout);

        nose = part(0, 9, 0.0F, -1.5F, -9.25F, -2.0F, -1.0F, -1.0F, 4, 2, 2, false);
        snout.addChild(nose);

        jaw = empty(0.0F, 0.0F, -7.5F);
        jaw.setTextureOffset(53, 43).addBox(-3.0F, 0.0F, -8.0F, 6, 3, 9, -0.01F);
        jaw.setTextureOffset(32, 51).addBox(-3.0F, -2.99F, -8.0F, 6, 3, 9, -0.01F);
        head.addChild(jaw);

        rightArmPivot = empty(4.0F, 2.25F, -3.5F);
        rightArm = limb(true);
        rightArmPivot.addChild(rightArm);
        chest.addChild(rightArmPivot);
        leftArmPivot = empty(-4.0F, 2.25F, -3.5F);
        leftArm = limb(false);
        leftArmPivot.addChild(leftArm);
        chest.addChild(leftArmPivot);

        hipsPivot = empty(0.0F, 0.0F, 7.0F);
        hips = empty(0.0F, 0.0F, 0.0F);
        hips.setTextureOffset(30, 32).addBox(-1.0F, -10.0F, 0.0F, 2, 5, 14, -0.01F);
        hips.setTextureOffset(0, 0).addBox(-4.0F, -5.0F, 0.0F, 8, 9, 14, -0.01F);
        hipsPivot.addChild(hips);
        chest.addChild(hipsPivot);

        tail = part(70, 8, 0.0F, -5.0F, 14.0F, -2.0F, 0.0F, 0.0F, 4, 5, 14, false);
        hips.addChild(tail);

        rightLegPivot = empty(4.0F, 2.25F, 11.5F);
        rightLeg = limb(true);
        rightLegPivot.addChild(rightLeg);
        hips.addChild(rightLegPivot);
        leftLegPivot = empty(-4.0F, 2.25F, 11.5F);
        leftLeg = limb(false);
        leftLegPivot.addChild(leftLeg);
        hips.addChild(leftLegPivot);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        CorrodentEntity corrodent = entity instanceof CorrodentEntity ? (CorrodentEntity) entity : null;
        float partialTick = corrodent == null ? 0.0F : ageInTicks - corrodent.ticksExisted;
        float digAmount = corrodent == null ? 0.0F : corrodent.getDigAmount(partialTick);
        float afraidAmount = corrodent == null ? 0.0F : corrodent.getAfraidAmount(partialTick);
        float walkAmount = limbSwingAmount * (1.0F - digAmount) * (1.0F + afraidAmount);
        float digLimbAmount = limbSwingAmount * digAmount;
        float idle = ageInTicks * 0.1F;

        head.rotateAngleX += headPitch * 0.017453292F + MathHelper.sin(idle) * 0.05F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        snout.rotateAngleX += MathHelper.sin(ageInTicks) * 0.06F;
        jaw.rotateAngleX += 0.1F + MathHelper.sin(ageInTicks) * 0.05F;
        nose.rotateAngleX += MathHelper.sin(ageInTicks * 2.5F) * 0.08F;
        leftWhisker.rotateAngleZ -= 0.4F + MathHelper.sin(ageInTicks * 0.15F) * 0.2F;
        rightWhisker.rotateAngleZ += 0.4F + MathHelper.sin(ageInTicks * 0.15F) * 0.2F;
        tail.rotateAngleX -= 0.25F;
        tail.rotateAngleY += MathHelper.sin(ageInTicks * 0.1F) * 0.05F;

        progressFear(afraidAmount);
        progressDig(digAmount);
        walkLegs(limbSwing, walkAmount);
        digLegs(limbSwing, digLimbAmount);

        if (corrodent != null && corrodent.getAnimation() == CorrodentEntity.ANIMATION_BITE) {
            float progress = MathHelper.clamp(corrodent.getAnimationTick() / 15.0F, 0.0F, 1.0F);
            float bite = MathHelper.sin(progress * (float) Math.PI);
            chest.rotationPointZ += bite < 0.5F ? bite * 4.0F : -bite * 2.0F;
            head.rotationPointZ += bite * 4.0F;
            snout.rotateAngleX -= bite * 0.7F;
            jaw.rotateAngleX += bite * 0.9F;
            tail.rotateAngleX += bite * 0.6F;
        }
        if (corrodent != null) {
            chest.rotateAngleX += corrodent.getDigPitch(partialTick) * 0.017453292F;
        }
    }

    private void progressFear(float amount) {
        chest.rotationPointY -= amount * 2.5F;
        hips.rotationPointY -= amount * 2.45F;
        hips.rotationPointZ -= amount * 3.0F;
        head.rotationPointY -= amount;
        chest.rotateAngleX += amount * 0.7F;
        head.rotateAngleX -= amount * 0.7F;
        rightArm.rotateAngleX -= amount * 0.7F;
        leftArm.rotateAngleX -= amount * 0.7F;
        hips.rotateAngleX -= amount * 1.4F;
        rightLeg.rotateAngleX += amount * 0.7F;
        leftLeg.rotateAngleX += amount * 0.7F;
        tail.rotateAngleX += amount * 1.2F;
        snout.rotateAngleX -= amount * 0.17F;
        jaw.rotateAngleX += amount * 0.7F;
    }

    private void progressDig(float amount) {
        rightArm.rotateAngleX -= amount * 0.6F;
        rightArm.rotateAngleY -= amount * 0.8F;
        rightArm.rotateAngleZ -= amount * 0.8F;
        leftArm.rotateAngleX -= amount * 0.6F;
        leftArm.rotateAngleY += amount * 0.8F;
        leftArm.rotateAngleZ += amount * 0.8F;
        rightLeg.rotateAngleX -= amount * 0.6F;
        rightLeg.rotateAngleY -= amount * 0.8F;
        rightLeg.rotateAngleZ -= amount * 0.8F;
        leftLeg.rotateAngleX -= amount * 0.6F;
        leftLeg.rotateAngleY += amount * 0.8F;
        leftLeg.rotateAngleZ += amount * 0.8F;
        rightArm.rotationPointY -= amount * 2.0F;
        leftArm.rotationPointY -= amount * 2.0F;
        rightLeg.rotationPointY -= amount * 2.0F;
        leftLeg.rotationPointY -= amount * 2.0F;
    }

    private void walkLegs(float limbSwing, float amount) {
        float speed = limbSwing;
        chest.rotateAngleZ += MathHelper.cos(speed + 1.0F) * amount * 0.1F;
        hips.rotateAngleZ += MathHelper.cos(speed + 2.0F) * amount * 0.1F;
        tail.rotateAngleY += MathHelper.cos(speed - 1.0F) * amount * 0.4F;
        animateLimb(rightArm, speed, amount, -1.5F, false);
        animateLimb(leftArm, speed, amount, -1.5F, true);
        animateLimb(rightLeg, speed, amount, -2.5F, true);
        animateLimb(leftLeg, speed, amount, -2.5F, false);
        chest.rotationPointY += Math.abs(MathHelper.cos(speed * 1.5F + 0.5F) * amount);
    }

    private void digLegs(float limbSwing, float amount) {
        rightArmPivot.rotateAngleY += MathHelper.cos(limbSwing + 1.0F) * amount;
        leftArmPivot.rotateAngleY += MathHelper.cos(limbSwing + 1.3F) * amount;
        rightLegPivot.rotateAngleY += MathHelper.cos(limbSwing - 1.0F) * amount;
        leftLegPivot.rotateAngleY += MathHelper.cos(limbSwing - 0.7F) * amount;
        rightArmPivot.rotateAngleZ += MathHelper.sin(limbSwing + 3.0F) * amount;
        leftArmPivot.rotateAngleZ += MathHelper.sin(limbSwing + 3.0F) * amount;
        rightLegPivot.rotateAngleZ += MathHelper.sin(limbSwing + 3.0F) * amount;
        leftLegPivot.rotateAngleZ += MathHelper.sin(limbSwing - 2.0F) * amount;
        jaw.rotateAngleX += MathHelper.cos(limbSwing * 4.0F + 4.0F) * amount * 0.15F;
    }

    private void animateLimb(ModelRenderer limb, float speed, float amount, float offset, boolean invert) {
        float direction = invert ? -1.0F : 1.0F;
        limb.rotateAngleX += MathHelper.cos(speed + offset) * amount * direction;
        limb.rotationPointY -= Math.min(0.0F, MathHelper.sin(speed + offset) * amount * 4.0F) + amount;
    }

    private void resetPose() {
        chest.setRotationPoint(0.0F, -7.0F, 5.0F);
        head.setRotationPoint(0.0F, 0.0F, -6.5F);
        hips.setRotationPoint(0.0F, 0.0F, 0.0F);
        rightArm.setRotationPoint(0.0F, 0.0F, 0.0F);
        leftArm.setRotationPoint(0.0F, 0.0F, 0.0F);
        rightLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        leftLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
        ModelRenderer[] parts = {root, chest, head, rightWhiskerSmall, rightWhisker, leftWhiskerSmall, leftWhisker, snout, nose, jaw, rightArmPivot, leftArmPivot, rightArm, leftArm, hipsPivot, hips, tail, rightLegPivot, leftLegPivot, rightLeg, leftLeg};
        for (ModelRenderer part : parts) {
            part.rotateAngleX = 0.0F;
            part.rotateAngleY = 0.0F;
            part.rotateAngleZ = 0.0F;
        }
    }

    private ModelRenderer limb(boolean mirror) {
        ModelRenderer limb = empty(0.0F, 0.0F, 0.0F);
        limb.mirror = mirror;
        limb.setTextureOffset(0, 0).addBox(mirror ? -1.0F : -2.0F, -1.25F, -1.5F, 3, 6, 3, 0.0F);
        limb.setTextureOffset(23, 0).addBox(mirror ? -1.0F : -6.0F, 4.75F, -5.5F, 7, 0, 7, 0.0F);
        return limb;
    }

    private ModelRenderer part(int textureX, int textureY, float pointX, float pointY, float pointZ, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = empty(pointX, pointY, pointZ);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        return renderer;
    }

    private ModelRenderer empty(float pointX, float pointY, float pointZ) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(pointX, pointY, pointZ);
        return renderer;
    }
}
