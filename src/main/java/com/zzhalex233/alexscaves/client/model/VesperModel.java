package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.VesperEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class VesperModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer torso;
    private final ModelRenderer head;
    private final ModelRenderer nose;
    private final ModelRenderer jaw;
    private final ModelRenderer leftEar;
    private final ModelRenderer rightEar;
    private final ModelRenderer leftWing;
    private final ModelRenderer leftWingTip;
    private final ModelRenderer rightWing;
    private final ModelRenderer rightWingTip;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftFoot;
    private final ModelRenderer rightFoot;
    private final ModelRenderer tail;

    public VesperModel() {
        textureWidth = 128;
        textureHeight = 128;
        root = empty(0.0F, 24.0F, 0.0F);
        torso = part(30, 44, 0.0F, -10.0F, 0.5F, -3.5F, -4.0F, -2.5F, 7, 8, 5, false);
        root.addChild(torso);

        head = empty(0.0F, -12.5F, 1.0F);
        head.setTextureOffset(0, 0).addBox(-6.5F, -12.5F, -10.0F, 13, 13, 13, 0.0F);
        head.setTextureOffset(0, 44).addBox(-6.5F, -12.5F, 3.0F, 13, 13, 2, 0.0F);
        root.addChild(head);

        nose = empty(0.0F, -4.0F, -9.5F);
        nose.setTextureOffset(49, 52).addBox(-3.5F, -2.75F, -2.5F, 7, 5, 5, 0.0F);
        nose.setTextureOffset(66, 62).addBox(-3.5F, -13.75F, -2.5F, 7, 11, 0, 0.0F);
        nose.setTextureOffset(66, 8).addBox(-2.5F, 2.25F, -2.24F, 5, 2, 5, 0.0F);
        head.addChild(nose);
        jaw = part(14, 67, 0.0F, 2.25F, -0.245F, -3.0F, -1.0F, -2.005F, 6, 3, 3, false);
        nose.addChild(jaw);

        leftEar = part(52, 62, 5.5F, -9.5F, -10.0F, -3.0F, -13.0F, -1.0F, 6, 15, 1, true);
        rightEar = part(52, 62, -5.5F, -9.5F, -10.0F, -3.0F, -13.0F, -1.0F, 6, 15, 1, false);
        head.addChild(leftEar);
        head.addChild(rightEar);

        leftWing = empty(3.0F, -10.75F, 0.5F);
        leftWing.setTextureOffset(0, 26).addBox(-2.5F, 0.75F, 0.0F, 25, 18, 0, 0.0F);
        leftWing.setTextureOffset(39, 4).addBox(0.5F, -1.25F, -1.0F, 17, 2, 2, 0.0F);
        root.addChild(leftWing);
        leftWingTip = empty(17.5F, -0.25F, 0.005F);
        leftWingTip.setTextureOffset(39, 0).addBox(0.0F, -1.0F, -1.005F, 17, 2, 2, 0.0F);
        leftWingTip.setTextureOffset(0, 80).addBox(-11.0F, -1.0F, 0.005F, 43, 28, 0, 0.0F);
        leftWing.addChild(leftWingTip);

        rightWing = empty(-3.0F, -10.75F, 0.5F);
        rightWing.mirror = true;
        rightWing.setTextureOffset(0, 26).addBox(-22.5F, 0.75F, 0.0F, 25, 18, 0, 0.0F);
        rightWing.setTextureOffset(39, 4).addBox(-17.5F, -1.25F, -1.0F, 17, 2, 2, 0.0F);
        root.addChild(rightWing);
        rightWingTip = empty(-17.5F, -0.25F, 0.005F);
        rightWingTip.mirror = true;
        rightWingTip.setTextureOffset(39, 0).addBox(-17.0F, -1.0F, -1.005F, 17, 2, 2, 0.0F);
        rightWingTip.setTextureOffset(0, 80).addBox(-32.0F, -1.0F, 0.005F, 43, 28, 0, 0.0F);
        rightWing.addChild(rightWingTip);

        leftLeg = part(68, 32, 2.5F, -6.0F, -2.0F, -2.0F, 0.0F, -4.0F, 3, 4, 4, true);
        rightLeg = part(68, 32, -2.5F, -6.0F, -2.0F, -1.0F, 0.0F, -4.0F, 3, 4, 4, false);
        leftFoot = part(68, 26, 0.0F, 6.0F, 0.0F, -1.5F, 0.0F, -5.0F, 3, 1, 5, true);
        rightFoot = part(68, 26, 0.0F, 6.0F, 0.0F, -1.5F, 0.0F, -5.0F, 3, 1, 5, false);
        leftLeg.addChild(leftFoot);
        rightLeg.addChild(rightFoot);
        root.addChild(leftLeg);
        root.addChild(rightLeg);

        tail = part(43, 8, 0.0F, -6.0F, 3.0F, -3.5F, 0.0F, 0.0F, 7, 0, 9, false);
        root.addChild(tail);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        VesperEntity vesper = entity instanceof VesperEntity ? (VesperEntity) entity : null;
        float partialTick = vesper == null ? 0.0F : ageInTicks - vesper.ticksExisted;
        float sleep = vesper == null ? 0.0F : vesper.getSleepProgress(partialTick);
        float fly = vesper == null ? 0.0F : vesper.getFlyProgress(partialTick);
        float ground = vesper == null ? 1.0F : (1.0F - Math.max(fly, sleep));
        float flap = MathHelper.sin(ageInTicks * (0.65F + fly * 0.65F));

        head.rotateAngleX += headPitch * 0.017453292F * ground;
        head.rotateAngleY += netHeadYaw * 0.017453292F * ground;
        jaw.rotateAngleX += 0.15F + MathHelper.sin(ageInTicks * 0.15F) * 0.05F;
        leftEar.rotateAngleZ -= 0.08F + MathHelper.sin(ageInTicks * 0.12F) * 0.04F;
        rightEar.rotateAngleZ += 0.08F + MathHelper.sin(ageInTicks * 0.12F) * 0.04F;

        leftWing.rotateAngleZ += -0.15F - flap * 0.7F * fly;
        rightWing.rotateAngleZ += 0.15F + flap * 0.7F * fly;
        leftWingTip.rotateAngleZ += -0.2F - flap * 0.35F * fly;
        rightWingTip.rotateAngleZ += 0.2F + flap * 0.35F * fly;
        leftLeg.rotateAngleX += MathHelper.cos(limbSwing) * limbSwingAmount * ground;
        rightLeg.rotateAngleX += MathHelper.cos(limbSwing + (float) Math.PI) * limbSwingAmount * ground;
        tail.rotateAngleX -= 0.25F + MathHelper.sin(ageInTicks * 0.12F) * 0.05F;

        progressGround(ground);
        progressSleep(sleep, entity.getEntityId());
        if (vesper != null) {
            root.rotateAngleX += vesper.getFlightPitch(partialTick) * 0.017453292F * fly;
            root.rotateAngleZ += vesper.getFlightRoll(partialTick) * 0.017453292F * fly;
            if (vesper.getAnimation() == VesperEntity.ANIMATION_BITE) {
                float bite = MathHelper.sin(MathHelper.clamp(vesper.getAnimationTick() / 15.0F, 0.0F, 1.0F) * (float) Math.PI);
                head.rotationPointZ += bite;
                jaw.rotationPointY += bite;
                jaw.rotationPointZ += bite;
                head.rotateAngleX += bite * 0.2F;
                nose.rotateAngleX -= bite * 0.1F;
                jaw.rotateAngleX += bite * 1.1F;
            }
        }
    }

    private void progressGround(float amount) {
        head.rotationPointY += amount * 3.0F;
        head.rotationPointZ -= amount;
        leftWing.rotationPointY -= amount;
        leftWing.rotationPointZ -= amount;
        rightWing.rotationPointY -= amount;
        rightWing.rotationPointZ -= amount;
        leftLeg.rotationPointZ += amount * 3.0F;
        rightLeg.rotationPointZ += amount * 3.0F;
        leftWing.rotateAngleZ -= amount * 0.9F;
        rightWing.rotateAngleZ += amount * 0.9F;
        leftWingTip.rotateAngleZ -= amount * 1.7F;
        rightWingTip.rotateAngleZ += amount * 1.7F;
    }

    private void progressSleep(float amount, int id) {
        root.rotationPointY -= amount * 24.0F;
        head.rotationPointY -= amount * 2.0F;
        head.rotationPointZ -= amount;
        leftWing.rotationPointY -= amount * 3.0F;
        rightWing.rotationPointY -= amount * 3.0F;
        leftWing.rotationPointZ += amount;
        rightWing.rotationPointZ += amount;
        leftLeg.rotationPointY -= amount;
        rightLeg.rotationPointY -= amount;
        leftWing.rotateAngleX -= amount * 0.6F;
        leftWing.rotateAngleY += amount * 0.5F;
        leftWing.rotateAngleZ -= amount * 0.65F;
        rightWing.rotateAngleX -= amount * 0.6F;
        rightWing.rotateAngleY -= amount * 0.5F;
        rightWing.rotateAngleZ += amount * 0.65F;
        leftWingTip.rotateAngleY += amount * 2.5F;
        rightWingTip.rotateAngleY -= amount * 2.5F;
        leftLeg.rotateAngleY -= amount * 0.8F;
        rightLeg.rotateAngleY += amount * 0.8F;
        head.rotateAngleX -= amount * 1.1F;
        tail.rotateAngleX -= amount * 0.8F;
        root.rotateAngleZ += amount * (float) Math.PI * (id % 2 == 0 ? -1.0F : 1.0F);
    }

    private void resetPose() {
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        head.setRotationPoint(0.0F, -12.5F, 1.0F);
        leftWing.setRotationPoint(3.0F, -10.75F, 0.5F);
        rightWing.setRotationPoint(-3.0F, -10.75F, 0.5F);
        leftLeg.setRotationPoint(2.5F, -6.0F, -2.0F);
        rightLeg.setRotationPoint(-2.5F, -6.0F, -2.0F);
        ModelRenderer[] parts = {root, torso, head, nose, jaw, leftEar, rightEar, leftWing, leftWingTip, rightWing, rightWingTip, leftLeg, rightLeg, leftFoot, rightFoot, tail};
        for (ModelRenderer part : parts) {
            part.rotateAngleX = 0.0F;
            part.rotateAngleY = 0.0F;
            part.rotateAngleZ = 0.0F;
        }
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
