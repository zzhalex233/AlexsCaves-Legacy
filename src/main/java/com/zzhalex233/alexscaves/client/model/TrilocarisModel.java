package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.TrilocarisEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class TrilocarisModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer tailFlipper;
    private final ModelRenderer legs;
    private final ModelRenderer legs2;
    private final ModelRenderer legs3;
    private final ModelRenderer legs4;
    private final ModelRenderer leftFlippers;
    private final ModelRenderer rightFlippers;
    private final ModelRenderer leftFlipper;
    private final ModelRenderer leftFlipper2;
    private final ModelRenderer leftFlipper3;
    private final ModelRenderer rightFlipper;
    private final ModelRenderer rightFlipper2;
    private final ModelRenderer rightFlipper3;
    private final ModelRenderer leftAntennae;
    private final ModelRenderer rightAntennae;
    private final ModelRenderer leftMandible;
    private final ModelRenderer rightMandible;

    public TrilocarisModel() {
        textureWidth = 64;
        textureHeight = 64;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 20.0F, 0.0F);
        body.setTextureOffset(21, 18).addBox(-3.0F, -1.0F, 0.0F, 6, 2, 5, 0.0F);
        body.setTextureOffset(16, 8).addBox(0.0F, -2.0F, 0.0F, 0, 1, 5, 0.0F);

        legs3 = plate(0, 5, -3.0F, 0.0F, 0.0F, 6, 3, 0);
        legs3.setRotationPoint(0.0F, 1.0F, 1.0F);
        body.addChild(legs3);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);
        head.setTextureOffset(0, 7).addBox(0.0F, -3.0F, -5.0F, 0, 3, 5, 0.0F);
        head.setTextureOffset(21, 9).addBox(-4.0F, -2.0F, -5.0F, 8, 3, 5, 0.0F);
        body.addChild(head);

        rightAntennae = part(42, 5, -0.5F, -4.0F, 0.0F, 1, 4, 5, false);
        rightAntennae.setRotationPoint(-1.5F, -2.0F, -5.0F);
        head.addChild(rightAntennae);

        leftAntennae = part(42, 5, -0.5F, -4.0F, 0.0F, 1, 4, 5, true);
        leftAntennae.setRotationPoint(1.5F, -2.0F, -5.0F);
        head.addChild(leftAntennae);

        leftMandible = part(0, 22, -2.0F, -0.5F, -6.0F, 3, 1, 7, false);
        leftMandible.setRotationPoint(3.0F, 0.5F, -5.0F);
        head.addChild(leftMandible);

        rightMandible = part(0, 22, -1.0F, -0.5F, -6.0F, 3, 1, 7, true);
        rightMandible.setRotationPoint(-3.0F, 0.5F, -5.0F);
        head.addChild(rightMandible);

        legs = plate(0, 5, -3.0F, 0.0F, 0.0F, 6, 3, 0);
        legs.setRotationPoint(0.0F, 1.0F, -3.0F);
        head.addChild(legs);

        legs2 = plate(0, 5, -3.0F, 0.0F, 0.0F, 6, 3, 0);
        legs2.setRotationPoint(0.0F, 1.0F, -1.0F);
        head.addChild(legs2);

        tailFlipper = plate(0, 0, -6.0F, 0.0F, 0.0F, 12, 0, 5);
        tailFlipper.setRotationPoint(0.0F, 0.0F, 5.0F);
        body.addChild(tailFlipper);

        leftFlippers = new ModelRenderer(this);
        leftFlippers.setRotationPoint(3.0F, 1.0F, 3.0F);
        body.addChild(leftFlippers);

        leftFlipper = plate(0, 8, 0.0F, 0.0F, -1.0F, 4, 0, 2);
        leftFlipper.setRotationPoint(0.0F, 0.0F, -2.0F);
        leftFlippers.addChild(leftFlipper);

        leftFlipper2 = plate(0, 8, 0.0F, 0.0F, -1.0F, 4, 0, 2);
        leftFlippers.addChild(leftFlipper2);

        leftFlipper3 = plate(0, 8, 0.0F, 0.0F, -1.0F, 4, 0, 2);
        leftFlipper3.setRotationPoint(0.0F, 0.0F, 2.0F);
        leftFlippers.addChild(leftFlipper3);

        rightFlippers = new ModelRenderer(this);
        rightFlippers.setRotationPoint(-3.0F, 1.0F, 3.0F);
        body.addChild(rightFlippers);

        rightFlipper = part(0, 8, -4.0F, 0.0F, -1.0F, 4, 0, 2, true);
        rightFlipper.setRotationPoint(0.0F, 0.0F, -2.0F);
        rightFlippers.addChild(rightFlipper);

        rightFlipper2 = part(0, 8, -4.0F, 0.0F, -1.0F, 4, 0, 2, true);
        rightFlippers.addChild(rightFlipper2);

        rightFlipper3 = part(0, 8, -4.0F, 0.0F, -1.0F, 4, 0, 2, true);
        rightFlipper3.setRotationPoint(0.0F, 0.0F, 2.0F);
        rightFlippers.addChild(rightFlipper3);

        legs4 = plate(0, 5, -3.0F, 0.0F, 0.0F, 6, 3, 0);
        legs4.setRotationPoint(0.0F, 1.0F, 3.0F);
        body.addChild(legs4);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetRotations();
        float swimAmount = entity instanceof TrilocarisEntity && entity.isInWater() ? 1.0F : 0.25F;
        float swim = MathHelper.sin(ageInTicks * 0.35F) * swimAmount;
        float walk = MathHelper.cos(limbSwing * 1.2F) * limbSwingAmount;

        body.rotateAngleX = swim * 0.05F;
        body.rotationPointY = 20.0F + MathHelper.abs(MathHelper.sin(ageInTicks * 0.1F)) * swimAmount;
        tailFlipper.rotateAngleY = swim * 0.35F;

        leftFlipper.rotateAngleZ = -0.15F - swim * 0.5F;
        leftFlipper2.rotateAngleZ = -0.15F - MathHelper.sin(ageInTicks * 0.35F + 1.0F) * 0.45F * swimAmount;
        leftFlipper3.rotateAngleZ = -0.15F - MathHelper.sin(ageInTicks * 0.35F + 2.0F) * 0.45F * swimAmount;
        rightFlipper.rotateAngleZ = 0.15F + swim * 0.5F;
        rightFlipper2.rotateAngleZ = 0.15F + MathHelper.sin(ageInTicks * 0.35F + 1.0F) * 0.45F * swimAmount;
        rightFlipper3.rotateAngleZ = 0.15F + MathHelper.sin(ageInTicks * 0.35F + 2.0F) * 0.45F * swimAmount;

        legs.rotateAngleX = walk * 0.3F;
        legs2.rotateAngleX = -walk * 0.3F;
        legs3.rotateAngleX = walk * 0.3F;
        legs4.rotateAngleX = -walk * 0.3F;
        leftAntennae.rotateAngleX = -0.15F + MathHelper.sin(ageInTicks * 0.1F) * 0.08F;
        rightAntennae.rotateAngleX = -0.15F - MathHelper.sin(ageInTicks * 0.1F) * 0.08F;
    }

    private void resetRotations() {
        body.rotateAngleX = 0.0F;
        body.rotationPointY = 20.0F;
        tailFlipper.rotateAngleY = 0.0F;
        leftFlipper.rotateAngleZ = 0.0F;
        leftFlipper2.rotateAngleZ = 0.0F;
        leftFlipper3.rotateAngleZ = 0.0F;
        rightFlipper.rotateAngleZ = 0.0F;
        rightFlipper2.rotateAngleZ = 0.0F;
        rightFlipper3.rotateAngleZ = 0.0F;
        legs.rotateAngleX = 0.0F;
        legs2.rotateAngleX = 0.0F;
        legs3.rotateAngleX = 0.0F;
        legs4.rotateAngleX = 0.0F;
        leftAntennae.rotateAngleX = 0.0F;
        rightAntennae.rotateAngleX = 0.0F;
    }

    private ModelRenderer plate(int textureX, int textureY, float x, float y, float z, int width, int height, int depth) {
        return part(textureX, textureY, x, y, z, width, height, depth, false);
    }

    private ModelRenderer part(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        return renderer;
    }
}
