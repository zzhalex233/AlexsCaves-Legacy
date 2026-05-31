package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.GammaroachEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GammaroachModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer carapace;
    private final ModelRenderer head;
    private final ModelRenderer leftAntenna;
    private final ModelRenderer rightAntenna;
    private final ModelRenderer leftLeg1;
    private final ModelRenderer leftLeg2;
    private final ModelRenderer leftLeg3;
    private final ModelRenderer rightLeg1;
    private final ModelRenderer rightLeg2;
    private final ModelRenderer rightLeg3;
    private final ModelRenderer middleTail;
    private final ModelRenderer leftTail;
    private final ModelRenderer rightTail;

    public GammaroachModel() {
        textureWidth = 128;
        textureHeight = 128;
        body = part(0, 0, -5.0F, -2.5F, -6.0F, 10, 5, 19, false);
        body.setRotationPoint(0.0F, 14.5F, 0.0F);
        body.setTextureOffset(33, 22).addBox(0.0F, -6.5F, 2.0F, 0, 5, 11, 0.0F);

        ModelRenderer leftBodyWing = part(22, 36, 0.0F, -4.0F, -5.5F, 0, 5, 11, true);
        leftBodyWing.setRotationPoint(-5.0F, -2.5F, 7.5F);
        leftBodyWing.rotateAngleZ = -0.7854F;
        body.addChild(leftBodyWing);
        ModelRenderer rightBodyWing = part(22, 36, 0.0F, -4.0F, -5.5F, 0, 5, 11, false);
        rightBodyWing.setRotationPoint(5.0F, -2.5F, 7.5F);
        rightBodyWing.rotateAngleZ = 0.7854F;
        body.addChild(rightBodyWing);

        middleTail = part(30, 7, -3.0F, 0.0F, 0.0F, 6, 0, 9, false);
        middleTail.setRotationPoint(0.0F, 0.5F, 13.0F);
        body.addChild(middleTail);
        leftTail = part(39, 24, -3.5F, 0.0F, 0.0F, 8, 0, 4, false);
        leftTail.setRotationPoint(3.5F, 1.5F, 13.0F);
        body.addChild(leftTail);
        rightTail = part(39, 24, -4.5F, 0.0F, 0.0F, 8, 0, 4, true);
        rightTail.setRotationPoint(-3.5F, 1.5F, 13.0F);
        body.addChild(rightTail);

        carapace = part(0, 33, -6.0F, -1.0F, -0.5F, 12, 5, 9, false);
        carapace.setRotationPoint(0.0F, -2.5F, -6.0F);
        carapace.setTextureOffset(0, 41).addBox(0.0F, -4.0F, -2.5F, 0, 5, 11, 0.0F);
        carapace.setTextureOffset(6, 8).addBox(4.0F, -3.0F, -0.5F, 2, 2, 2, 0.0F);
        carapace.setTextureOffset(0, 6).addBox(-6.0F, -3.0F, -0.5F, 2, 2, 2, 0.0F);
        body.addChild(carapace);

        head = part(42, 38, -3.5F, -1.5F, -4.0F, 7, 4, 4, false);
        head.setRotationPoint(0.0F, 2.0F, 0.5F);
        head.setTextureOffset(0, 0).addBox(-3.0F, 0.0F, -3.5F, 6, 3, 3, 0.0F);
        carapace.addChild(head);

        leftAntenna = part(0, 24, -2.0F, 0.0F, -7.5F, 17, 0, 9, false);
        leftAntenna.setRotationPoint(2.0F, 0.75F, -3.0F);
        leftAntenna.rotateAngleZ = -0.7854F;
        head.addChild(leftAntenna);
        rightAntenna = part(0, 24, -15.0F, 0.0F, -7.5F, 17, 0, 9, true);
        rightAntenna.setRotationPoint(-2.0F, 0.75F, -3.0F);
        rightAntenna.rotateAngleZ = 0.7854F;
        head.addChild(rightAntenna);

        leftLeg1 = leg(58, 12, 1.5F, 2.0F, -5.0F, 0.7854F, false);
        leftLeg2 = leg(49, 0, 1.5F, 2.0F, -1.5F, 0.0F, false);
        leftLeg3 = leg(0, 57, 1.5F, 2.0F, 2.0F, -0.7854F, false);
        rightLeg1 = leg(58, 12, -1.5F, 2.0F, -5.0F, -0.7854F, true);
        rightLeg2 = leg(49, 0, -1.5F, 2.0F, -1.5F, 0.0F, true);
        rightLeg3 = leg(0, 57, -1.5F, 2.0F, 2.0F, 0.7854F, true);
    }

    private ModelRenderer leg(int textureX, int textureY, float pointX, float pointY, float pointZ, float yaw, boolean mirror) {
        ModelRenderer leg = part(textureX, textureY, mirror ? -11.5F : -0.5F, -3.5F, 0.0F, textureX == 0 ? 21 : 12, textureX == 58 ? 8 : 11, 0, mirror);
        leg.setRotationPoint(pointX, pointY, pointZ);
        leg.rotateAngleY = yaw;
        body.addChild(leg);
        return leg;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        GammaroachEntity roach = entity instanceof GammaroachEntity ? (GammaroachEntity) entity : null;
        float walk = limbSwing * 1.0F;
        float amount = limbSwingAmount * 0.9F;
        float idle = MathHelper.sin(ageInTicks * 0.1F);
        head.rotateAngleX += idle * 0.1F;
        leftAntenna.rotateAngleX -= 0.2F + idle * 0.15F;
        rightAntenna.rotateAngleX -= 0.2F + idle * 0.15F;
        middleTail.rotateAngleX += idle * 0.05F;
        leftTail.rotateAngleY += idle * 0.05F;
        rightTail.rotateAngleY -= idle * 0.05F;

        animateLeg(leftLeg1, walk, amount, 0.0F, 1.0F);
        animateLeg(rightLeg1, walk, amount, (float) Math.PI, -1.0F);
        animateLeg(leftLeg2, walk, amount, (float) Math.PI, -1.0F);
        animateLeg(rightLeg2, walk, amount, 0.0F, 1.0F);
        animateLeg(leftLeg3, walk, amount, 0.0F, 0.6F);
        animateLeg(rightLeg3, walk, amount, (float) Math.PI, -0.6F);
        body.rotationPointY -= Math.abs(MathHelper.cos(walk) * amount * 2.0F);

        if (roach != null && roach.getAnimation() != GammaroachEntity.ANIMATION_NONE) {
            float progress = MathHelper.clamp(roach.getAnimationTick() / (roach.getAnimation() == GammaroachEntity.ANIMATION_SPRAY ? 40.0F : 25.0F), 0.0F, 1.0F);
            if (roach.getAnimation() == GammaroachEntity.ANIMATION_SPRAY) {
                body.rotateAngleX += MathHelper.sin(progress * (float) Math.PI) * 0.55F;
                carapace.rotateAngleX -= MathHelper.sin(progress * (float) Math.PI) * 0.55F;
                leftLeg1.rotateAngleY -= 0.5F;
                rightLeg1.rotateAngleY += 0.5F;
                leftLeg3.rotateAngleZ -= 0.35F;
                rightLeg3.rotateAngleZ += 0.35F;
            } else {
                float strike = MathHelper.sin(progress * (float) Math.PI);
                body.rotationPointZ += strike < 0.5F ? 6.0F * strike : -9.0F * strike;
                carapace.rotateAngleX += strike * 0.35F;
                head.rotateAngleX += strike * 0.35F;
            }
        }
    }

    private void animateLeg(ModelRenderer leg, float walk, float amount, float offset, float direction) {
        leg.rotateAngleY += MathHelper.cos(walk + offset) * amount * 0.6F * direction;
        leg.rotateAngleZ += MathHelper.sin(walk + offset) * amount * 0.25F * direction;
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, 14.5F, 0.0F);
        body.rotateAngleX = 0.0F;
        body.rotateAngleY = 0.0F;
        body.rotateAngleZ = 0.0F;
        carapace.rotateAngleX = 0.0F;
        head.rotateAngleX = 0.0F;
        leftAntenna.rotateAngleX = 0.0F;
        rightAntenna.rotateAngleX = 0.0F;
        leftAntenna.rotateAngleZ = -0.7854F;
        rightAntenna.rotateAngleZ = 0.7854F;
        middleTail.rotateAngleX = 0.0F;
        leftTail.rotateAngleY = 0.0F;
        rightTail.rotateAngleY = 0.0F;
        leftLeg1.rotateAngleY = 0.7854F;
        leftLeg2.rotateAngleY = 0.0F;
        leftLeg3.rotateAngleY = -0.7854F;
        rightLeg1.rotateAngleY = -0.7854F;
        rightLeg2.rotateAngleY = 0.0F;
        rightLeg3.rotateAngleY = 0.7854F;
        leftLeg1.rotateAngleZ = leftLeg2.rotateAngleZ = leftLeg3.rotateAngleZ = 0.0F;
        rightLeg1.rotateAngleZ = rightLeg2.rotateAngleZ = rightLeg3.rotateAngleZ = 0.0F;
    }

    private ModelRenderer part(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        return renderer;
    }
}
