package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.DeepOneBaseEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class DeepOneModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer tail;

    public DeepOneModel() {
        textureWidth = 128;
        textureHeight = 128;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, -18.0F, 0.0F);
        body.setTextureOffset(0, 28).addBox(-5.0F, 0.0F, -3.0F, 10, 14, 6, 0.0F);
        root.addChild(body);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -1.0F, 0.0F);
        head.setTextureOffset(0, 0).addBox(-5.0F, -9.0F, -5.0F, 10, 9, 10, 0.0F);
        body.addChild(head);

        rightArm = limb(32, 0, -8.0F, 1.0F, 0.0F);
        leftArm = limb(32, 0, 8.0F, 1.0F, 0.0F);
        leftArm.mirror = true;
        body.addChild(rightArm);
        body.addChild(leftArm);

        rightLeg = limb(56, 0, -3.0F, 13.0F, 0.0F);
        leftLeg = limb(56, 0, 3.0F, 13.0F, 0.0F);
        leftLeg.mirror = true;
        body.addChild(rightLeg);
        body.addChild(leftLeg);

        tail = new ModelRenderer(this);
        tail.setRotationPoint(0.0F, 10.0F, 3.0F);
        tail.setTextureOffset(34, 28).addBox(-3.0F, 0.0F, 0.0F, 6, 4, 10, 0.0F);
        body.addChild(tail);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        DeepOneBaseEntity deepOne = entity instanceof DeepOneBaseEntity ? (DeepOneBaseEntity) entity : null;
        float swim = deepOne != null && deepOne.isDeepOneSwimming() ? 1.0F : 0.0F;
        float walkSpeed = swim > 0.0F ? 0.4F : 0.7F;
        float walk = Math.min(limbSwingAmount, 1.0F);
        body.rotateAngleX += swim * ((deepOne == null ? 0.0F : deepOne.getFishPitch(0.0F)) * 0.017453292F - 0.45F);
        head.rotateAngleX += headPitch * 0.017453292F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        rightArm.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walk * 0.8F - swim * 0.7F;
        leftArm.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walk * 0.8F - swim * 0.7F;
        rightLeg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walk * 0.9F;
        leftLeg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walk * 0.9F;
        tail.rotateAngleY = MathHelper.sin(ageInTicks * (swim > 0.0F ? 0.35F : 0.15F)) * (0.2F + swim * 0.35F);
    }

    private void resetPose() {
        body.rotateAngleX = body.rotateAngleY = body.rotateAngleZ = 0.0F;
        head.rotateAngleX = head.rotateAngleY = head.rotateAngleZ = 0.0F;
        rightArm.rotateAngleX = rightArm.rotateAngleY = rightArm.rotateAngleZ = 0.0F;
        leftArm.rotateAngleX = leftArm.rotateAngleY = leftArm.rotateAngleZ = 0.0F;
        rightLeg.rotateAngleX = rightLeg.rotateAngleY = rightLeg.rotateAngleZ = 0.0F;
        leftLeg.rotateAngleX = leftLeg.rotateAngleY = leftLeg.rotateAngleZ = 0.0F;
        tail.rotateAngleX = tail.rotateAngleY = tail.rotateAngleZ = 0.0F;
    }

    private ModelRenderer limb(int textureX, int textureY, float pointX, float pointY, float pointZ) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(pointX, pointY, pointZ);
        renderer.setTextureOffset(textureX, textureY).addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        return renderer;
    }
}
