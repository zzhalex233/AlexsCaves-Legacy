package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.TripodfishEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class TripodfishModel extends ModelBase {
    private final ModelRenderer mainBody;
    private final ModelRenderer dorsalFin;
    private final ModelRenderer rightPectoralFin;
    private final ModelRenderer leftPectoralFin;
    private final ModelRenderer leftPelvicFin;
    private final ModelRenderer rightPelvicFin;
    private final ModelRenderer tail;
    private final ModelRenderer topTailFin;
    private final ModelRenderer bottomTailFin;

    public TripodfishModel() {
        textureWidth = 64;
        textureHeight = 64;

        mainBody = part(0, 0, -1.5F, -3.0F, -3.75F, 3, 5, 11, false);
        mainBody.setRotationPoint(0.0F, 22.0F, -2.25F);
        mainBody.setTextureOffset(26, 24).addBox(-1.5F, -1.0F, -7.75F, 3, 3, 4, 0.0F);

        dorsalFin = plate(0, 7, 0.0F, -5.0F, -1.0F, 0, 6, 9, false);
        dorsalFin.setRotationPoint(0.0F, -3.0F, 1.25F);
        mainBody.addChild(dorsalFin);

        ModelRenderer bottomFin = plate(22, 10, 0.0F, -1.0F, -1.5F, 0, 5, 6, false);
        bottomFin.setRotationPoint(0.0F, 2.0F, 3.75F);
        mainBody.addChild(bottomFin);

        rightPectoralFin = plate(17, 0, -7.0F, -9.0F, 0.0F, 7, 11, 0, true);
        rightPectoralFin.setRotationPoint(-1.5F, -2.0F, -0.75F);
        mainBody.addChild(rightPectoralFin);

        leftPectoralFin = plate(17, 0, 0.0F, -9.0F, 0.0F, 7, 11, 0, false);
        leftPectoralFin.setRotationPoint(1.5F, -2.0F, -0.75F);
        mainBody.addChild(leftPectoralFin);

        leftPelvicFin = new ModelRenderer(this);
        leftPelvicFin.setRotationPoint(1.5F, 2.0F, -0.25F);
        mainBody.addChild(leftPelvicFin);
        ModelRenderer leftPelvicLeg = plate(0, 18, 0.0F, -2.0F, -1.75F, 0, 19, 4, false);
        leftPelvicLeg.setRotationPoint(0.0F, 0.0F, 1.25F);
        leftPelvicLeg.rotateAngleZ = -0.4363F;
        leftPelvicFin.addChild(leftPelvicLeg);

        rightPelvicFin = new ModelRenderer(this);
        rightPelvicFin.setRotationPoint(-1.5F, 2.0F, -0.25F);
        mainBody.addChild(rightPelvicFin);
        ModelRenderer rightPelvicLeg = plate(0, 18, 0.0F, -2.0F, -1.75F, 0, 19, 4, true);
        rightPelvicLeg.setRotationPoint(0.0F, 0.0F, 1.25F);
        rightPelvicLeg.rotateAngleZ = 0.4363F;
        rightPelvicFin.addChild(rightPelvicLeg);

        tail = part(10, 16, -1.0F, -2.0F, 0.5F, 2, 4, 8, false);
        tail.setRotationPoint(0.0F, -0.5F, 6.75F);
        mainBody.addChild(tail);

        topTailFin = plate(16, 24, 0.0F, -10.0F, 0.0F, 0, 12, 4, false);
        topTailFin.setRotationPoint(0.0F, -1.5F, 8.5F);
        tail.addChild(topTailFin);

        bottomTailFin = plate(8, 24, -0.01F, -1.0F, 0.0F, 0, 17, 4, false);
        bottomTailFin.setRotationPoint(0.0F, 1.5F, 8.5F);
        tail.addChild(bottomTailFin);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        mainBody.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        TripodfishEntity tripodfish = entity instanceof TripodfishEntity ? (TripodfishEntity) entity : null;
        float landProgress = tripodfish == null ? 0.0F : tripodfish.getLandProgress(0.0F);
        float standProgress = tripodfish == null ? 0.0F : tripodfish.getStandProgress(0.0F) * (1.0F - landProgress);
        float swimProgress = 1.0F - standProgress;
        float fishPitch = tripodfish == null ? 0.0F : tripodfish.getFishPitch(0.0F);
        float swim = MathHelper.sin(limbSwing * 0.8F) * limbSwingAmount * swimProgress;
        float idle = MathHelper.sin(ageInTicks * 0.08F);

        mainBody.rotateAngleZ = landProgress * 1.4835F;
        mainBody.rotateAngleX += fishPitch * swimProgress;
        mainBody.rotationPointY -= standProgress * 15.0F;
        mainBody.rotationPointZ -= standProgress;
        mainBody.rotationPointY += MathHelper.sin(ageInTicks * 0.1F) * 0.5F * swimProgress;

        rightPelvicFin.rotationPointZ += swimProgress;
        leftPelvicFin.rotationPointZ += swimProgress;
        rightPelvicFin.rotateAngleX = 1.2217F * swimProgress;
        rightPelvicFin.rotateAngleZ = -0.5236F * swimProgress;
        leftPelvicFin.rotateAngleX = 1.2217F * swimProgress;
        leftPelvicFin.rotateAngleZ = 0.5236F * swimProgress;
        topTailFin.rotateAngleX = -0.1745F * swimProgress;
        bottomTailFin.rotateAngleX = 0.6981F * swimProgress;
        rightPectoralFin.rotateAngleX = -0.5236F * swimProgress + idle * 0.05F;
        rightPectoralFin.rotateAngleY = -0.1745F * swimProgress;
        rightPectoralFin.rotateAngleZ = -1.2217F * swimProgress;
        leftPectoralFin.rotateAngleX = -0.5236F * swimProgress - idle * 0.05F;
        leftPectoralFin.rotateAngleY = 0.1745F * swimProgress;
        leftPectoralFin.rotateAngleZ = 1.2217F * swimProgress;

        mainBody.rotateAngleY = swim * 0.5F;
        tail.rotateAngleY = MathHelper.sin(limbSwing * 0.8F - 1.0F) * limbSwingAmount * 0.75F * swimProgress;
        topTailFin.rotateAngleY = MathHelper.sin(limbSwing * 0.8F - 2.0F) * limbSwingAmount * 0.75F * swimProgress;
        bottomTailFin.rotateAngleY = topTailFin.rotateAngleY;
    }

    private void resetPose() {
        mainBody.setRotationPoint(0.0F, 22.0F, -2.25F);
        mainBody.rotateAngleX = 0.0F;
        mainBody.rotateAngleY = 0.0F;
        mainBody.rotateAngleZ = 0.0F;
        tail.rotateAngleY = 0.0F;
        topTailFin.rotateAngleX = 0.0F;
        topTailFin.rotateAngleY = 0.0F;
        bottomTailFin.rotateAngleX = 0.0F;
        bottomTailFin.rotateAngleY = 0.0F;
        rightPectoralFin.rotateAngleX = 0.0F;
        rightPectoralFin.rotateAngleY = 0.0F;
        rightPectoralFin.rotateAngleZ = 0.0F;
        leftPectoralFin.rotateAngleX = 0.0F;
        leftPectoralFin.rotateAngleY = 0.0F;
        leftPectoralFin.rotateAngleZ = 0.0F;
        rightPelvicFin.setRotationPoint(-1.5F, 2.0F, -0.25F);
        rightPelvicFin.rotateAngleX = 0.0F;
        rightPelvicFin.rotateAngleZ = 0.0F;
        leftPelvicFin.setRotationPoint(1.5F, 2.0F, -0.25F);
        leftPelvicFin.rotateAngleX = 0.0F;
        leftPelvicFin.rotateAngleZ = 0.0F;
    }

    private ModelRenderer plate(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        return part(textureX, textureY, x, y, z, width, height, depth, mirror);
    }

    private ModelRenderer part(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        return renderer;
    }
}
