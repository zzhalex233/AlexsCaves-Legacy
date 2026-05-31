package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.NucleeperEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class NucleeperModel extends ModelBase {
    private final ModelRenderer base;
    private final ModelRenderer coreTop;
    private final ModelRenderer head;
    private final ModelRenderer rightPupil;
    private final ModelRenderer leftPupil;
    private final ModelRenderer rightBackLeg;
    private final ModelRenderer rightBackFoot;
    private final ModelRenderer leftBackLeg;
    private final ModelRenderer leftBackFoot;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer leftFrontFoot;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer rightFrontFoot;
    private final ModelRenderer coreBottom;

    public NucleeperModel() {
        textureWidth = 128;
        textureHeight = 128;
        base = part(80, 21, -7.0F, -36.5F, -5.0F, 14, 38, 10, false);
        base.setRotationPoint(0.0F, 15.5F, 0.0F);

        coreTop = part(0, 0, -8.0F, 3.0F, -8.0F, 16, 8, 16, false);
        coreTop.setRotationPoint(0.0F, -62.5F, 0.0F);
        base.addChild(coreTop);

        head = part(0, 48, -7.0F, -14.0F, -7.0F, 14, 14, 14, false);
        head.setRotationPoint(0.0F, -37.5F, 0.0F);
        head.setTextureOffset(26, 86).addBox(-6.0F, -13.5F, -6.0F, 12, 12, 12, 0.0F);
        base.addChild(head);

        rightPupil = part(26, 86, -0.5F, -0.5F, -0.5F, 1, 1, 1, false);
        rightPupil.setRotationPoint(-3.5F, -9.0F, -5.6F);
        head.addChild(rightPupil);
        leftPupil = part(26, 86, -0.5F, -0.5F, -0.5F, 1, 1, 1, true);
        leftPupil.setRotationPoint(3.5F, -9.0F, -5.6F);
        head.addChild(leftPupil);

        rightBackLeg = leg(80, 69, -7.0F, -1.0F, 3.5F, -2.0F, -2.5F, -3.5F, false);
        rightBackFoot = foot(0, 93, 1.0F, -2.5F, 4.5F, 0.0F, 6.0F, 3.0F, false, true);
        rightBackLeg.addChild(rightBackFoot);

        leftBackLeg = leg(80, 69, 7.0F, -1.0F, 3.5F, -4.0F, -2.5F, -3.5F, true);
        leftBackFoot = foot(0, 93, -1.0F, -2.5F, 4.5F, 0.0F, 6.0F, 3.0F, true, true);
        leftBackLeg.addChild(leftBackFoot);

        leftFrontLeg = leg(44, 69, 7.0F, -1.0F, -3.5F, -4.0F, -2.5F, -8.5F, true);
        leftFrontFoot = foot(0, 76, -1.0F, -2.5F, -4.5F, 0.0F, 6.0F, -8.0F, true, false);
        leftFrontLeg.addChild(leftFrontFoot);

        rightFrontLeg = leg(44, 69, -7.0F, -1.0F, -3.5F, -2.0F, -2.5F, -8.5F, false);
        rightFrontFoot = foot(0, 76, 1.0F, -2.5F, -4.5F, 0.0F, 6.0F, -8.0F, false, false);
        rightFrontLeg.addChild(rightFrontFoot);

        coreBottom = part(0, 24, -8.0F, -11.0F, -8.0F, 16, 8, 16, false);
        coreBottom.setRotationPoint(0.0F, -27.5F, 0.0F);
        base.addChild(coreBottom);
    }

    private ModelRenderer leg(int textureX, int textureY, float pointX, float pointY, float pointZ, float boxX, float boxY, float boxZ, boolean mirror) {
        ModelRenderer leg = part(textureX, textureY, boxX, boxY, boxZ, 6, 5, 12, mirror);
        leg.setRotationPoint(pointX, pointY, pointZ);
        base.addChild(leg);
        return leg;
    }

    private ModelRenderer foot(int textureX, int textureY, float pointX, float pointY, float pointZ, float toeX, float toeY, float toeZ, boolean mirror, boolean back) {
        ModelRenderer foot = part(textureX, textureY, -4.0F, 0.0F, back ? 0.0F : -5.0F, 8, 12, 5, mirror);
        foot.setRotationPoint(pointX, pointY, pointZ);
        foot.setTextureOffset(back ? 1 : 0, back ? 28 : 46).addBox(toeX, 0.0F, toeZ, 0, (int) toeY, back ? 3 : 5, 0.0F);
        return foot;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        base.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        NucleeperEntity nucleeper = entity instanceof NucleeperEntity ? (NucleeperEntity) entity : null;
        float partialTick = nucleeper == null ? 0.0F : ageInTicks - nucleeper.ticksExisted;
        float closeProgress = nucleeper == null ? 0.0F : nucleeper.getCloseProgress(partialTick);
        float explodeProgress = nucleeper == null ? 0.0F : nucleeper.getExplodeProgress(partialTick);
        float stillProgress = 1.0F - MathHelper.clamp(limbSwingAmount, 0.0F, 1.0F);

        progressPosition(head, closeProgress, 0.0F, 8.0F, 0.0F);
        progressPosition(coreTop, closeProgress, 0.0F, 14.0F, 0.0F);
        progressPosition(coreBottom, closeProgress, 0.0F, 1.0F, 0.0F);
        progressRotation(leftFrontLeg, stillProgress, 0.0F, -0.4363F, 0.0F);
        progressRotation(leftBackLeg, stillProgress, 0.0F, 0.4363F, 0.0F);
        progressRotation(rightFrontLeg, stillProgress, 0.0F, 0.4363F, 0.0F);
        progressRotation(rightBackLeg, stillProgress, 0.0F, -0.4363F, 0.0F);

        float squishXz = 1.0F - explodeProgress * 0.15F;
        base.rotateAngleZ += MathHelper.sin(ageInTicks * 3.0F) * 0.3F * explodeProgress;
        base.offsetY += explodeProgress * 0.12F;
        base.offsetX += (1.0F - squishXz) * 0.03F;

        float walk = limbSwing * 0.8F;
        float bob = walkValue(limbSwing, limbSwingAmount, 1.2F, 0.5F, 2.4F, true);
        base.rotationPointY += bob;
        animateLeg(leftFrontLeg, leftFrontFoot, walk, limbSwingAmount, true, 1.0F);
        animateLeg(rightFrontLeg, rightFrontFoot, walk, limbSwingAmount, false, 1.0F);
        animateLeg(rightBackLeg, rightBackFoot, walk, limbSwingAmount, true, 0.0F);
        animateLeg(leftBackLeg, leftBackFoot, walk, limbSwingAmount, false, 0.0F);

        Entity look = Minecraft.getMinecraft().getRenderViewEntity();
        if (look != null) {
            Vec3d lookEyes = look.getPositionEyes(0.0F);
            Vec3d eyes = entity.getPositionEyes(0.0F);
            double yDiff = MathHelper.clamp(-(lookEyes.y - eyes.y), -1.0D, 1.0D);
            Vec3d view = entity.getLook(0.0F);
            view = new Vec3d(view.x, 0.0D, view.z);
            Vec3d lateral = new Vec3d(eyes.x - lookEyes.x, 0.0D, eyes.z - lookEyes.z).normalize().rotateYaw(1.5708F);
            double dot = view.dotProduct(lateral);
            float xShift = (float) (Math.sqrt(Math.abs(dot)) * Math.signum(dot));
            leftPupil.rotationPointX += xShift - base.rotateAngleZ;
            leftPupil.rotationPointY += yDiff;
            rightPupil.rotationPointX += xShift - base.rotateAngleZ;
            rightPupil.rotationPointY += yDiff;
        }
    }

    private void animateLeg(ModelRenderer leg, ModelRenderer foot, float walk, float amount, boolean inverse, float phase) {
        float sign = inverse ? -1.0F : 1.0F;
        leg.rotateAngleX += MathHelper.cos(walk + phase) * amount * 0.36F * sign;
        foot.rotateAngleX += MathHelper.cos(walk + phase + 2.0F) * amount * 0.24F * sign;
        leg.rotationPointY += Math.min(0.0F, MathHelper.cos(walk + phase - 0.5F) * amount * 5.0F * sign);
        leg.rotationPointZ += MathHelper.cos(walk + phase - 0.5F) * amount * 2.5F * sign;
    }

    private float walkValue(float limbSwing, float limbSwingAmount, float speed, float offset, float degree, boolean inverse) {
        return MathHelper.cos(limbSwing * speed + offset) * degree * limbSwingAmount * (inverse ? -1.0F : 1.0F);
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
        base.setRotationPoint(0.0F, 15.5F, 0.0F);
        coreTop.setRotationPoint(0.0F, -62.5F, 0.0F);
        head.setRotationPoint(0.0F, -37.5F, 0.0F);
        rightPupil.setRotationPoint(-3.5F, -9.0F, -5.6F);
        leftPupil.setRotationPoint(3.5F, -9.0F, -5.6F);
        rightBackLeg.setRotationPoint(-7.0F, -1.0F, 3.5F);
        rightBackFoot.setRotationPoint(1.0F, -2.5F, 4.5F);
        leftBackLeg.setRotationPoint(7.0F, -1.0F, 3.5F);
        leftBackFoot.setRotationPoint(-1.0F, -2.5F, 4.5F);
        leftFrontLeg.setRotationPoint(7.0F, -1.0F, -3.5F);
        leftFrontFoot.setRotationPoint(-1.0F, -2.5F, -4.5F);
        rightFrontLeg.setRotationPoint(-7.0F, -1.0F, -3.5F);
        rightFrontFoot.setRotationPoint(1.0F, -2.5F, -4.5F);
        coreBottom.setRotationPoint(0.0F, -27.5F, 0.0F);
        ModelRenderer[] parts = {base, coreTop, head, rightPupil, leftPupil, rightBackLeg, rightBackFoot, leftBackLeg, leftBackFoot, leftFrontLeg, leftFrontFoot, rightFrontLeg, rightFrontFoot, coreBottom};
        for (ModelRenderer part : parts) {
            part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = 0.0F;
            part.offsetX = part.offsetY = part.offsetZ = 0.0F;
        }
    }

    private ModelRenderer part(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        return renderer;
    }
}
