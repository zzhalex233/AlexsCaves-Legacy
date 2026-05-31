package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.GloomothEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GloomothModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightAntennae;
    private final ModelRenderer leftAntennae;
    private final ModelRenderer leftWing;
    private final ModelRenderer leftWingSmall;
    private final ModelRenderer rightWing;
    private final ModelRenderer rightWingSmall;
    private final ModelRenderer legs;
    private final ModelRenderer leftLeg1;
    private final ModelRenderer leftLeg2;
    private final ModelRenderer leftLeg3;
    private final ModelRenderer rightLeg1;
    private final ModelRenderer rightLeg2;
    private final ModelRenderer rightLeg3;

    public GloomothModel() {
        textureWidth = 64;
        textureHeight = 64;
        root = empty(0.0F, 17.5F, 0.0F);
        body = empty(0.0F, 0.0F, 0.0F);
        body.setTextureOffset(31, 25).addBox(-2.5F, -2.5F, -1.0F, 5, 5, 7, 0.0F);
        body.setTextureOffset(0, 32).addBox(-3.0F, -4.0F, -6.0F, 6, 7, 5, 0.0F);
        root.addChild(body);

        head = part(0, 0, 0.0F, -1.0F, -5.5F, -2.5F, -2.0F, -2.5F, 5, 4, 3, false);
        body.addChild(head);
        rightAntennae = part(22, 38, -1.0F, -2.0F, -2.5F, -7.5F, -7.0F, 0.0F, 8, 7, 0, false);
        leftAntennae = part(22, 38, 1.0F, -2.0F, -2.5F, -0.5F, -7.0F, 0.0F, 8, 7, 0, true);
        head.addChild(rightAntennae);
        head.addChild(leftAntennae);

        leftWing = empty(2.5F, -2.0F, -2.0F);
        leftWing.setTextureOffset(0, 0).addBox(-0.5F, 0.0F, -12.0F, 13, 0, 16, 0.0F);
        leftWingSmall = part(0, 16, 0.0F, 0.25F, 0.0F, 0.0F, 0.0F, -2.0F, 11, 0, 16, false);
        leftWing.addChild(leftWingSmall);
        body.addChild(leftWing);

        rightWing = empty(-2.5F, -2.0F, -2.0F);
        rightWing.mirror = true;
        rightWing.setTextureOffset(0, 0).addBox(-12.5F, 0.0F, -12.0F, 13, 0, 16, 0.0F);
        rightWingSmall = part(0, 16, 0.0F, 0.25F, 0.0F, -11.0F, 0.0F, -2.0F, 11, 0, 16, true);
        rightWing.addChild(rightWingSmall);
        body.addChild(rightWing);

        legs = empty(0.0F, 2.5F, -2.0F);
        root.addChild(legs);
        leftLeg1 = leg(1.0F, 0.0F, -2.0F, true);
        leftLeg2 = leg(1.0F, 0.0F, 0.0F, true);
        leftLeg3 = leg(1.0F, 0.0F, 2.0F, true);
        rightLeg1 = leg(-1.0F, 0.0F, -2.0F, false);
        rightLeg2 = leg(-1.0F, 0.0F, 0.0F, false);
        rightLeg3 = leg(-1.0F, 0.0F, 2.0F, false);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        GloomothEntity gloomoth = entity instanceof GloomothEntity ? (GloomothEntity) entity : null;
        float partialTick = gloomoth == null ? 0.0F : ageInTicks - gloomoth.ticksExisted;
        float fly = gloomoth == null ? 0.0F : gloomoth.getFlyProgress(partialTick);
        float flap = gloomoth == null ? 0.0F : Math.max(fly, gloomoth.getFlapAmount(partialTick));
        float grounded = 1.0F - fly;

        rightAntennae.rotateAngleX += 0.15F + fly * 0.7F + MathHelper.sin(ageInTicks * 0.1F + 1.0F) * 0.15F;
        leftAntennae.rotateAngleX += 0.15F + fly * 0.7F + MathHelper.sin(ageInTicks * 0.1F + 1.0F) * 0.15F;
        rightAntennae.rotateAngleZ -= 0.1F + MathHelper.sin(ageInTicks * 0.1F) * 0.15F;
        leftAntennae.rotateAngleZ += 0.1F + MathHelper.sin(ageInTicks * 0.1F) * 0.15F;
        body.rotateAngleX -= fly * 0.17F;
        legs.rotationPointY -= fly;
        animateLeg(leftLeg1, limbSwing, limbSwingAmount * grounded, 4.0F, false);
        animateLeg(leftLeg2, limbSwing, limbSwingAmount * grounded, 2.5F, false);
        animateLeg(leftLeg3, limbSwing, limbSwingAmount * grounded, 1.0F, false);
        animateLeg(rightLeg1, limbSwing, limbSwingAmount * grounded, 4.0F, true);
        animateLeg(rightLeg2, limbSwing, limbSwingAmount * grounded, 2.5F, true);
        animateLeg(rightLeg3, limbSwing, limbSwingAmount * grounded, 1.0F, true);

        float wing = MathHelper.sin(ageInTicks * 0.9F + 1.5F) * 0.9F * flap;
        rightWing.rotateAngleZ -= 0.1F + wing;
        leftWing.rotateAngleZ += 0.1F + wing;
        rightWingSmall.rotateAngleZ -= 0.1F + wing * 0.45F;
        leftWingSmall.rotateAngleZ += 0.1F + wing * 0.45F;
        root.rotationPointY += MathHelper.sin(ageInTicks * 0.6F) * fly * 1.8F;
        if (gloomoth != null) {
            root.rotateAngleX += gloomoth.getFlightPitch(partialTick) * 0.017453292F * fly;
            root.rotateAngleZ += gloomoth.getFlightRoll(partialTick) * 0.017453292F * fly;
        }
    }

    private void animateLeg(ModelRenderer leg, float limbSwing, float amount, float offset, boolean invert) {
        leg.rotateAngleX += MathHelper.cos(limbSwing * 2.0F + offset) * amount * 0.7F * (invert ? -1.0F : 1.0F);
    }

    private void resetPose() {
        root.setRotationPoint(0.0F, 17.5F, 0.0F);
        legs.setRotationPoint(0.0F, 2.5F, -2.0F);
        ModelRenderer[] parts = {root, body, head, rightAntennae, leftAntennae, leftWing, leftWingSmall, rightWing, rightWingSmall, legs, leftLeg1, leftLeg2, leftLeg3, rightLeg1, rightLeg2, rightLeg3};
        for (ModelRenderer part : parts) {
            part.rotateAngleX = 0.0F;
            part.rotateAngleY = 0.0F;
            part.rotateAngleZ = 0.0F;
        }
    }

    private ModelRenderer leg(float pointX, float pointY, float pointZ, boolean mirror) {
        ModelRenderer leg = part(2, 11, pointX, pointY, pointZ, -0.5F, 0.0F, 0.0F, 1, 4, 0, mirror);
        legs.addChild(leg);
        return leg;
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
