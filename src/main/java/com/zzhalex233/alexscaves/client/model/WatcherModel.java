package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.WatcherEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class WatcherModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer body;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer head;
    private final ModelRenderer brow;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer leftHorn;
    private final ModelRenderer rightHorn;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;

    public WatcherModel() {
        textureWidth = 128;
        textureHeight = 128;
        root = empty(0.0F, -2.0F, 0.0F);
        root.rotateAngleY = (float) Math.PI;
        body = empty(0.0F, 5.125F, -0.5F);
        body.setTextureOffset(67, 61).addBox(-4.0F, -5.125F, -2.5F, 8, 12, 5, 0.0F);
        body.setTextureOffset(27, 22).addBox(-5.0F, -5.875F, -3.5F, 10, 22, 7, 0.25F);
        root.addChild(body);

        leftArm = empty(-5.1667F, -4.4583F, 0.0F);
        leftArm.setTextureOffset(16, 61).addBox(-4.3333F, -0.6667F, -3.0F, 4, 5, 6, 0.25F);
        leftArm.setTextureOffset(56, 46).addBox(-3.3333F, 0.3333F, -2.5F, 3, 15, 5, 0.24F);
        leftArm.setTextureOffset(36, 65).addBox(-3.3333F, 15.3333F, -2.5F, 3, 4, 5, 0.0F);
        body.addChild(leftArm);
        rightArm = empty(5.1667F, -4.4583F, 0.0F);
        rightArm.mirror = true;
        rightArm.setTextureOffset(16, 61).addBox(0.3333F, -0.6667F, -3.0F, 4, 5, 6, 0.25F);
        rightArm.setTextureOffset(56, 46).addBox(0.3333F, 0.3333F, -2.5F, 3, 15, 5, 0.24F);
        rightArm.setTextureOffset(36, 65).addBox(0.3333F, 15.3333F, -2.5F, 3, 4, 5, 0.0F);
        body.addChild(rightArm);

        head = empty(0.0F, -5.125F, 0.5F);
        head.setTextureOffset(72, 45).addBox(-4.0F, -9.0F, -4.0F, 8, 9, 7, 0.0F);
        head.setTextureOffset(34, 0).addBox(-5.0F, -10.0F, -4.0F, 10, 9, 7, 0.26F);
        body.addChild(head);
        brow = part(54, 16, 0.0F, -10.25F, -4.25F, -2.0F, 0.25F, -7.25F, 4, 4, 7, false);
        brow.rotateAngleX = 0.7854F;
        head.addChild(brow);
        rightWing = part(0, 18, 0.0F, -5.0F, 3.0F, 0.0F, -9.0F, 0.0F, 0, 17, 11, false);
        rightWing.rotateAngleY = 1.1781F;
        leftWing = part(0, 18, 0.0F, -5.0F, 3.0F, 0.0F, -9.0F, 0.0F, 0, 17, 11, true);
        leftWing.rotateAngleY = -1.1781F;
        head.addChild(rightWing);
        head.addChild(leftWing);
        leftHorn = part(0, 61, 5.25F, -6.0F, 0.0F, 0.0F, -11.0F, 0.0F, 8, 15, 0, true);
        rightHorn = part(0, 61, -5.25F, -6.0F, 0.0F, -8.0F, -11.0F, 0.0F, 8, 15, 0, false);
        head.addChild(leftHorn);
        head.addChild(rightHorn);

        rightLeg = part(61, 27, 2.5F, 11.5F, -0.5F, -1.5F, -0.5F, -1.5F, 3, 15, 3, true);
        leftLeg = part(61, 27, -2.5F, 11.5F, -0.5F, -1.5F, -0.5F, -1.5F, 3, 15, 3, false);
        root.addChild(rightLeg);
        root.addChild(leftLeg);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        WatcherEntity watcher = entity instanceof WatcherEntity ? (WatcherEntity) entity : null;
        float partialTick = watcher == null ? 0.0F : ageInTicks - watcher.ticksExisted;
        float run = watcher == null ? 0.0F : watcher.getRunAmount(partialTick);
        float shade = watcher == null ? 0.0F : watcher.getShadeAmount(partialTick);
        float ground = 1.0F - shade;
        float walk = limbSwingAmount * (1.0F - run);
        float runAmount = limbSwingAmount * run;
        body.rotateAngleX -= walk * 0.09F + runAmount * 0.26F;
        head.rotateAngleX += walk * 0.09F + runAmount * 0.26F - headPitch * 0.017453292F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        leftWing.rotateAngleY = -1.1781F + MathHelper.sin(ageInTicks * 0.2F + 1.0F) * 0.25F;
        rightWing.rotateAngleY = 1.1781F - MathHelper.sin(ageInTicks * 0.2F + 1.0F) * 0.25F;
        leftArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.1F - 0.5F) * 0.1F;
        rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.1F - 0.5F) * 0.1F;
        leftArm.rotateAngleZ += MathHelper.sin(ageInTicks * 0.1F + 2.0F) * 0.1F;
        rightArm.rotateAngleZ -= MathHelper.sin(ageInTicks * 0.1F + 2.0F) * 0.1F;
        leftLeg.rotateAngleX += MathHelper.cos(limbSwing * (run > 0.5F ? 0.5F : 0.25F) + 1.0F) * limbSwingAmount * ground * 0.5F;
        rightLeg.rotateAngleX += MathHelper.cos(limbSwing * (run > 0.5F ? 0.5F : 0.25F) + (float) Math.PI + 1.0F) * limbSwingAmount * ground * 0.5F;
        if (run > 0.0F) {
            leftArm.rotateAngleX += run * 1.3F;
            leftArm.rotateAngleY -= run * 0.6F;
            rightArm.rotateAngleX += run * 1.3F;
            rightArm.rotateAngleY += run * 0.6F;
            body.rotationPointY -= runAmount * ground * 6.5F;
            body.rotationPointZ += runAmount * ground * 4.0F;
            head.rotationPointY += runAmount * 1.5F;
            head.rotationPointZ += runAmount * 2.0F;
            root.rotateAngleY += MathHelper.sin(limbSwing * 0.5F + 3.0F) * runAmount * ground * 0.35F;
        }
        if (shade > 0.0F) {
            leftLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.5F - 1.0F) * limbSwingAmount * shade * 0.3F;
            rightLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.5F - 2.0F) * limbSwingAmount * shade * 0.3F;
            body.rotateAngleX -= shade * 0.2F;
        }
        if (watcher != null && watcher.getAnimation() != WatcherEntity.ANIMATION_NONE) {
            float strike = MathHelper.sin(MathHelper.clamp(watcher.getAnimationTick() / 15.0F, 0.0F, 1.0F) * (float) Math.PI);
            if (watcher.getAnimation() == WatcherEntity.ANIMATION_ATTACK_0) {
                body.rotateAngleY += strike * 0.5F;
                rightArm.rotateAngleX -= strike * 0.6F;
                rightArm.rotateAngleY -= strike;
                leftArm.rotateAngleX += strike * 0.4F;
                leftArm.rotateAngleZ += strike * 0.7F;
            } else {
                body.rotationPointZ -= strike * 5.0F;
                body.rotateAngleX += strike * 0.35F;
                rightArm.rotateAngleY += strike * 1.3F;
                leftArm.rotateAngleY -= strike * 1.3F;
            }
        }
    }

    private void resetPose() {
        root.setRotationPoint(0.0F, -2.0F, 0.0F);
        body.setRotationPoint(0.0F, 5.125F, -0.5F);
        head.setRotationPoint(0.0F, -5.125F, 0.5F);
        ModelRenderer[] parts = {root, body, leftArm, rightArm, head, leftHorn, rightHorn, rightLeg, leftLeg};
        for (ModelRenderer part : parts) {
            part.rotateAngleX = 0.0F;
            part.rotateAngleY = 0.0F;
            part.rotateAngleZ = 0.0F;
        }
        root.rotateAngleY = (float) Math.PI;
        brow.rotateAngleX = 0.7854F;
        brow.rotateAngleY = 0.0F;
        brow.rotateAngleZ = 0.0F;
        rightWing.rotateAngleX = 0.0F;
        rightWing.rotateAngleY = 1.1781F;
        rightWing.rotateAngleZ = 0.0F;
        leftWing.rotateAngleX = 0.0F;
        leftWing.rotateAngleY = -1.1781F;
        leftWing.rotateAngleZ = 0.0F;
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
