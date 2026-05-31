package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.CandicornEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CandicornModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer neck;
    private final ModelRenderer head;
    private final ModelRenderer horn;
    private final ModelRenderer mane;
    private final ModelRenderer tail;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer rightBackLeg;
    private final ModelRenderer leftBackLeg;

    public CandicornModel() {
        textureWidth = 128;
        textureHeight = 128;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 12.0F, 0.0F);
        body.setTextureOffset(0, 32).addBox(-5.0F, -8.0F, -9.0F, 10, 12, 18, 0.0F);

        neck = new ModelRenderer(this);
        neck.setRotationPoint(0.0F, -6.0F, -7.0F);
        neck.setTextureOffset(58, 30).addBox(-3.0F, -9.0F, -3.0F, 6, 10, 6, 0.0F);
        body.addChild(neck);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -8.0F, -2.0F);
        head.setTextureOffset(0, 0).addBox(-4.0F, -5.0F, -8.0F, 8, 7, 8, 0.0F);
        head.setTextureOffset(32, 0).addBox(-2.5F, -2.0F, -13.0F, 5, 4, 6, 0.0F);
        head.setTextureOffset(0, 16).addBox(-5.0F, -8.0F, -2.0F, 2, 4, 2, 0.0F);
        head.setTextureOffset(0, 16).addBox(3.0F, -8.0F, -2.0F, 2, 4, 2, 0.0F);
        neck.addChild(head);

        horn = new ModelRenderer(this);
        horn.setRotationPoint(0.0F, -5.0F, -6.0F);
        horn.setTextureOffset(24, 0).addBox(-1.0F, -6.0F, -1.0F, 2, 6, 2, 0.0F);
        head.addChild(horn);

        mane = new ModelRenderer(this);
        mane.setRotationPoint(0.0F, -7.0F, 1.0F);
        mane.setTextureOffset(86, 0).addBox(0.0F, -1.0F, 0.0F, 0, 14, 8, 0.0F);
        neck.addChild(mane);

        tail = new ModelRenderer(this);
        tail.setRotationPoint(0.0F, -5.0F, 9.0F);
        tail.setTextureOffset(52, 0).addBox(-1.5F, -1.0F, 0.0F, 3, 3, 11, 0.0F);
        body.addChild(tail);

        rightFrontLeg = leg(true);
        rightFrontLeg.setRotationPoint(-3.5F, 3.0F, -6.5F);
        leftFrontLeg = leg(false);
        leftFrontLeg.setRotationPoint(3.5F, 3.0F, -6.5F);
        rightBackLeg = leg(true);
        rightBackLeg.setRotationPoint(-3.5F, 3.0F, 6.5F);
        leftBackLeg = leg(false);
        leftBackLeg.setRotationPoint(3.5F, 3.0F, 6.5F);
        body.addChild(rightFrontLeg);
        body.addChild(leftFrontLeg);
        body.addChild(rightBackLeg);
        body.addChild(leftBackLeg);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        CandicornEntity candicorn = entity instanceof CandicornEntity ? (CandicornEntity) entity : null;
        float partialTicks = candicorn == null ? 0.0F : MathHelper.clamp(ageInTicks - candicorn.ticksExisted, 0.0F, 1.0F);
        float run = candicorn == null ? 0.0F : candicorn.getRunProgress(partialTicks);
        float charge = candicorn == null ? 0.0F : candicorn.getChargeProgress(partialTicks);
        float speed = 0.65F + run * 0.45F;
        float amount = limbSwingAmount * (1.0F + run * 0.4F);

        body.rotateAngleX += charge * -0.12F;
        neck.rotateAngleX += -0.35F - charge * 0.25F + headPitch * 0.008F;
        head.rotateAngleX += 0.25F + headPitch * 0.009F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        horn.rotateAngleX += charge * -0.2F;
        mane.rotateAngleX += MathHelper.sin(ageInTicks * 0.15F) * 0.04F;
        tail.rotateAngleX += 0.4F + MathHelper.cos(limbSwing * speed) * amount * 0.18F;
        tail.rotateAngleY += MathHelper.sin(ageInTicks * 0.12F) * 0.18F;

        rightFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * speed + (float) Math.PI) * amount;
        leftFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * speed) * amount;
        rightBackLeg.rotateAngleX = MathHelper.cos(limbSwing * speed) * amount;
        leftBackLeg.rotateAngleX = MathHelper.cos(limbSwing * speed + (float) Math.PI) * amount;
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, 12.0F, 0.0F);
        neck.setRotationPoint(0.0F, -6.0F, -7.0F);
        head.setRotationPoint(0.0F, -8.0F, -2.0F);
        horn.setRotationPoint(0.0F, -5.0F, -6.0F);
        mane.setRotationPoint(0.0F, -7.0F, 1.0F);
        tail.setRotationPoint(0.0F, -5.0F, 9.0F);
        rightFrontLeg.setRotationPoint(-3.5F, 3.0F, -6.5F);
        leftFrontLeg.setRotationPoint(3.5F, 3.0F, -6.5F);
        rightBackLeg.setRotationPoint(-3.5F, 3.0F, 6.5F);
        leftBackLeg.setRotationPoint(3.5F, 3.0F, 6.5F);
        body.rotateAngleX = body.rotateAngleY = body.rotateAngleZ = 0.0F;
        neck.rotateAngleX = neck.rotateAngleY = neck.rotateAngleZ = 0.0F;
        head.rotateAngleX = head.rotateAngleY = head.rotateAngleZ = 0.0F;
        horn.rotateAngleX = horn.rotateAngleY = horn.rotateAngleZ = 0.0F;
        mane.rotateAngleX = mane.rotateAngleY = mane.rotateAngleZ = 0.0F;
        tail.rotateAngleX = tail.rotateAngleY = tail.rotateAngleZ = 0.0F;
        rightFrontLeg.rotateAngleX = rightFrontLeg.rotateAngleY = rightFrontLeg.rotateAngleZ = 0.0F;
        leftFrontLeg.rotateAngleX = leftFrontLeg.rotateAngleY = leftFrontLeg.rotateAngleZ = 0.0F;
        rightBackLeg.rotateAngleX = rightBackLeg.rotateAngleY = rightBackLeg.rotateAngleZ = 0.0F;
        leftBackLeg.rotateAngleX = leftBackLeg.rotateAngleY = leftBackLeg.rotateAngleZ = 0.0F;
    }

    private ModelRenderer leg(boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(78, 48).addBox(-2.0F, 0.0F, -2.0F, 4, 11, 4, 0.0F);
        renderer.setTextureOffset(96, 48).addBox(-2.5F, 9.0F, -3.0F, 5, 3, 5, 0.0F);
        return renderer;
    }
}
