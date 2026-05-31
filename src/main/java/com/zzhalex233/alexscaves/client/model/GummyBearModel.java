package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.GummyBearEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GummyBearModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer belly;

    public GummyBearModel() {
        textureWidth = 128;
        textureHeight = 128;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 11.0F, 0.0F);
        body.setTextureOffset(0, 32).addBox(-6.0F, -9.0F, -4.0F, 12, 14, 8, 0.0F);

        belly = new ModelRenderer(this);
        belly.setRotationPoint(0.0F, -1.0F, -4.5F);
        belly.setTextureOffset(40, 32).addBox(-4.0F, -5.0F, -1.0F, 8, 9, 2, 0.0F);
        body.addChild(belly);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -9.0F, -2.5F);
        head.setTextureOffset(0, 0).addBox(-5.0F, -8.0F, -5.0F, 10, 8, 9, 0.0F);
        head.setTextureOffset(38, 0).addBox(-3.0F, -3.0F, -8.0F, 6, 4, 4, 0.0F);
        head.setTextureOffset(0, 18).addBox(-5.5F, -10.0F, -2.0F, 3, 3, 3, 0.0F);
        head.setTextureOffset(0, 18).addBox(2.5F, -10.0F, -2.0F, 3, 3, 3, 0.0F);
        body.addChild(head);

        rightArm = limb(64, 0, -7.0F, -6.5F, -1.5F, true);
        leftArm = limb(64, 0, 7.0F, -6.5F, -1.5F, false);
        rightLeg = limb(80, 0, -3.5F, 4.0F, 0.0F, true);
        leftLeg = limb(80, 0, 3.5F, 4.0F, 0.0F, false);
        body.addChild(rightArm);
        body.addChild(leftArm);
        body.addChild(rightLeg);
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
        GummyBearEntity bear = entity instanceof GummyBearEntity ? (GummyBearEntity) entity : null;
        float partialTicks = bear == null ? 0.0F : MathHelper.clamp(ageInTicks - bear.ticksExisted, 0.0F, 1.0F);
        float sit = bear == null ? 0.0F : bear.getSitProgress(partialTicks);
        float stand = bear == null ? 0.0F : bear.getStandProgress(partialTicks);
        float sleep = bear == null ? 0.0F : bear.getSleepProgress(partialTicks);

        progressPosition(body, sit, 0.0F, 4.0F, 1.0F);
        progressRotation(body, sit, -0.45F, 0.0F, 0.0F);
        progressRotation(rightLeg, sit, -0.9F, 0.0F, 0.25F);
        progressRotation(leftLeg, sit, -0.9F, 0.0F, -0.25F);
        progressRotation(rightArm, sit, 0.45F, 0.0F, -0.2F);
        progressRotation(leftArm, sit, 0.45F, 0.0F, 0.2F);

        progressPosition(body, stand, 0.0F, -3.0F, 0.0F);
        progressRotation(body, stand, -0.35F, 0.0F, 0.0F);
        progressRotation(rightArm, stand, -0.5F, 0.0F, -0.35F);
        progressRotation(leftArm, stand, -0.5F, 0.0F, 0.35F);
        progressRotation(rightLeg, stand, 0.35F, 0.0F, 0.0F);
        progressRotation(leftLeg, stand, 0.35F, 0.0F, 0.0F);

        progressPosition(body, sleep, 0.0F, 5.0F, 0.0F);
        progressRotation(body, sleep, 0.0F, 0.0F, 1.5708F);
        progressRotation(head, sleep, 0.2F, 0.0F, -0.75F);
        progressRotation(rightArm, sleep, 0.3F, 0.0F, -0.8F);
        progressRotation(leftArm, sleep, -0.2F, 0.0F, 0.4F);
        progressRotation(rightLeg, sleep, -0.4F, 0.0F, -0.4F);
        progressRotation(leftLeg, sleep, 0.3F, 0.0F, 0.2F);

        float walk = limbSwing * 0.65F;
        float amount = limbSwingAmount * (1.0F - sleep) * (1.0F - sit);
        rightArm.rotateAngleX += MathHelper.cos(walk + (float) Math.PI) * amount * 0.65F;
        leftArm.rotateAngleX += MathHelper.cos(walk) * amount * 0.65F;
        rightLeg.rotateAngleX += MathHelper.cos(walk) * amount * 0.7F;
        leftLeg.rotateAngleX += MathHelper.cos(walk + (float) Math.PI) * amount * 0.7F;
        head.rotateAngleX += headPitch * 0.017453292F * (1.0F - sleep);
        head.rotateAngleY += netHeadYaw * 0.017453292F * (1.0F - sleep);
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, 11.0F, 0.0F);
        belly.setRotationPoint(0.0F, -1.0F, -4.5F);
        head.setRotationPoint(0.0F, -9.0F, -2.5F);
        rightArm.setRotationPoint(-7.0F, -6.5F, -1.5F);
        leftArm.setRotationPoint(7.0F, -6.5F, -1.5F);
        rightLeg.setRotationPoint(-3.5F, 4.0F, 0.0F);
        leftLeg.setRotationPoint(3.5F, 4.0F, 0.0F);
        body.rotateAngleX = body.rotateAngleY = body.rotateAngleZ = 0.0F;
        belly.rotateAngleX = belly.rotateAngleY = belly.rotateAngleZ = 0.0F;
        head.rotateAngleX = head.rotateAngleY = head.rotateAngleZ = 0.0F;
        rightArm.rotateAngleX = rightArm.rotateAngleY = rightArm.rotateAngleZ = 0.0F;
        leftArm.rotateAngleX = leftArm.rotateAngleY = leftArm.rotateAngleZ = 0.0F;
        rightLeg.rotateAngleX = rightLeg.rotateAngleY = rightLeg.rotateAngleZ = 0.0F;
        leftLeg.rotateAngleX = leftLeg.rotateAngleY = leftLeg.rotateAngleZ = 0.0F;
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

    private ModelRenderer limb(int textureX, int textureY, float pointX, float pointY, float pointZ, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setRotationPoint(pointX, pointY, pointZ);
        renderer.setTextureOffset(textureX, textureY).addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, 0.0F);
        return renderer;
    }
}
