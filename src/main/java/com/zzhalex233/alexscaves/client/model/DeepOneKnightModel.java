package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.DeepOneBaseEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class DeepOneKnightModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer lfin;
    private final ModelRenderer rfin;
    private final ModelRenderer lure;
    private final ModelRenderer lleg;
    private final ModelRenderer rleg;
    private final ModelRenderer rarm;
    private final ModelRenderer larm;
    private final ModelRenderer tail;

    public DeepOneKnightModel() {
        textureWidth = 128;
        textureHeight = 128;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 24.0F, 0.0F);
        body.setTextureOffset(86, 105).addBox(-6.5F, -20.0F, -4.0F, 13, 11, 8, 0.0F);
        body.setTextureOffset(0, 0).addBox(-9.5F, -36.0F, -6.0F, 19, 18, 14, 0.0F);
        body.setTextureOffset(0, 82).addBox(-9.5F, -36.0F, -6.0F, 19, 18, 14, 0.25F);
        body.setTextureOffset(50, 41).addBox(0.0F, -41.0F, 0.0F, 0, 23, 16, 0.0F);
        child(body, -9.5F, -27.0F, 8.0F, 0.0F, 0.7854F, 0.0F).setTextureOffset(68, 105).addBox(-2.0F, -9.0F, 0.0F, 2, 18, 0, 0.0F);
        child(body, 9.5F, -27.0F, 8.0F, 0.0F, -0.7854F, 0.0F, true).setTextureOffset(68, 105).addBox(0.0F, -9.0F, 0.0F, 2, 18, 0, 0.0F);
        child(body, 9.5F, -27.0F, -6.0F, 0.0F, 0.7854F, 0.0F, true).setTextureOffset(68, 105).addBox(0.0F, -9.0F, 0.0F, 2, 18, 0, 0.0F);
        child(body, -9.5F, -27.0F, -6.0F, 0.0F, -0.7854F, 0.0F).setTextureOffset(68, 105).addBox(-2.0F, -9.0F, 0.0F, 2, 18, 0, 0.0F);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -36.0F, -1.0F);
        body.addChild(head);
        head.setTextureOffset(0, 57).addBox(-6.5F, -2.0F, -12.0F, 13, 13, 12, 0.0F);
        head.setTextureOffset(68, 88).addBox(-6.5F, -5.0F, -12.0F, 13, 3, 6, 0.0F);
        head.setTextureOffset(98, 96).addBox(-6.5F, 0.0F, -12.0F, 13, 6, 2, 0.25F);
        child(head, 0.0F, 11.0F, -12.0F, -0.7854F, 0.0F, 0.0F).setTextureOffset(29, 123).addBox(-6.5F, 0.0F, 0.0F, 13, 2, 0, 0.0F);
        child(head, 0.0F, -2.0F, 0.0F, -0.7854F, 0.0F, 0.0F).setTextureOffset(29, 126).addBox(-6.5F, -2.0F, 0.0F, 13, 2, 0, 0.0F);
        child(head, 0.0F, -2.0F, -12.0F, 0.7854F, 0.0F, 0.0F).setTextureOffset(29, 126).addBox(-6.5F, -2.0F, 0.0F, 13, 2, 0, 0.0F);
        child(head, -6.5F, 3.5F, 0.0F, 0.0F, 0.7854F, 0.0F).setTextureOffset(0, 113).addBox(-2.0F, -7.5F, 0.0F, 2, 15, 0, 0.0F);
        child(head, 6.5F, 3.5F, 0.0F, 0.0F, -0.7854F, 0.0F, true).setTextureOffset(68, 108).addBox(0.0F, -7.5F, 0.0F, 2, 15, 0, 0.0F);
        child(head, 6.5F, 3.5F, -12.0F, 0.0F, 0.7854F, 0.0F, true).setTextureOffset(68, 108).addBox(0.0F, -7.5F, 0.0F, 2, 15, 0, 0.0F);
        child(head, -6.5F, 3.5F, -12.0F, 0.0F, -0.7854F, 0.0F).setTextureOffset(0, 113).addBox(-2.0F, -7.5F, 0.0F, 2, 15, 0, 0.0F);
        child(head, -6.5F, 11.0F, -6.0F, 0.0F, 0.0F, 0.7854F, true).setTextureOffset(56, 111).addBox(0.0F, 0.0F, -6.0F, 0, 2, 12, 0.0F);
        child(head, 6.5F, 11.0F, -6.0F, 0.0F, 0.0F, -0.7854F).setTextureOffset(56, 111).addBox(0.0F, 0.0F, -6.0F, 0, 2, 12, 0.0F);
        child(head, 6.5F, -2.0F, -6.0F, 0.0F, 0.0F, 0.7854F).setTextureOffset(56, 114).addBox(0.0F, -2.0F, -6.0F, 0, 2, 12, 0.0F);
        child(head, -6.5F, -2.0F, -6.0F, 0.0F, 0.0F, -0.7854F, true).setTextureOffset(56, 114).addBox(0.0F, -2.0F, -6.0F, 0, 2, 12, 0.0F);

        jaw = new ModelRenderer(this);
        jaw.setRotationPoint(0.0F, 1.75F, 2.0F);
        jaw.setTextureOffset(30, 36).addBox(-6.5F, 0.25F, -15.0F, 13, 9, 12, 0.25F);
        head.addChild(jaw);

        lfin = new ModelRenderer(this);
        lfin.mirror = true;
        lfin.setRotationPoint(6.5F, 0.5F, -7.0F);
        lfin.setTextureOffset(0, 0).addBox(0.0F, -4.5F, 0.0F, 7, 11, 0, 0.0F);
        head.addChild(lfin);

        rfin = new ModelRenderer(this);
        rfin.setRotationPoint(-6.5F, 0.5F, -7.0F);
        rfin.setTextureOffset(0, 0).addBox(-7.0F, -4.5F, 0.0F, 7, 11, 0, 0.0F);
        head.addChild(rfin);

        lure = new ModelRenderer(this);
        lure.setRotationPoint(-0.25F, -2.0F, -11.5F);
        lure.setTextureOffset(81, 54).addBox(0.0F, -11.0F, -0.5F, 0, 13, 5, 0.0F);
        lure.setTextureOffset(86, 19).addBox(-1.0F, -10.0F, 0.5F, 2, 2, 2, 0.0F);
        head.addChild(lure);

        lleg = new ModelRenderer(this);
        lleg.mirror = true;
        lleg.setRotationPoint(4.5F, -9.5F, -1.0F);
        lleg.setTextureOffset(58, 80).addBox(-2.0F, 0.5F, -1.0F, 4, 9, 4, -0.001F);
        lleg.setTextureOffset(44, 0).addBox(-2.0F, 9.5F, -5.0F, 8, 0, 8, -0.001F);
        body.addChild(lleg);

        rleg = new ModelRenderer(this);
        rleg.setRotationPoint(-4.5F, -9.5F, -1.0F);
        rleg.setTextureOffset(58, 80).addBox(-2.0F, 0.5F, -1.0F, 4, 9, 4, -0.001F);
        rleg.setTextureOffset(44, 0).addBox(-6.0F, 9.5F, -5.0F, 8, 0, 8, -0.001F);
        body.addChild(rleg);

        rarm = arm(false);
        rarm.setRotationPoint(-9.0F, -27.0F, 4.5F);
        body.addChild(rarm);

        larm = arm(true);
        larm.setRotationPoint(9.0F, -27.0F, 4.5F);
        body.addChild(larm);

        tail = new ModelRenderer(this);
        tail.setRotationPoint(0.0F, -11.0F, 2.5F);
        tail.setTextureOffset(0, 11).addBox(0.0F, -7.0F, -4.5F, 0, 16, 21, 0.0F);
        body.addChild(tail);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        DeepOneBaseEntity deepOne = entity instanceof DeepOneBaseEntity ? (DeepOneBaseEntity) entity : null;
        float swim = deepOne != null && deepOne.isDeepOneSwimming() ? 1.0F : 0.0F;
        float walkAmount = Math.min(limbSwingAmount * 2.0F, 1.0F) * (1.0F - swim);
        float swimAmount = limbSwingAmount * swim;
        float walk = limbSwing * 1.0F;
        float idle = ageInTicks * 0.1F;

        head.rotateAngleX += -0.2618F * walkAmount + headPitch * 0.017453292F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        jaw.rotateAngleX += 0.1F + MathHelper.cos(idle) * 0.05F;
        lure.rotateAngleX += MathHelper.cos(idle + 1.0F) * 0.2F - 0.2F;
        tail.rotateAngleY += MathHelper.sin(idle - 2.5F) * 0.05F;
        body.rotateAngleX += 0.2618F * walkAmount;
        body.rotationPointY += -Math.abs(MathHelper.sin(walk - 1.5F)) * walkAmount;

        lleg.rotateAngleX += -0.2618F * walkAmount + MathHelper.cos(walk - 1.0F) * walkAmount;
        rleg.rotateAngleX += -0.2618F * walkAmount + MathHelper.cos(walk + (float) Math.PI - 1.0F) * walkAmount;
        larm.rotateAngleX += MathHelper.cos(walk - 3.0F) * walkAmount * 0.2F - 0.4F * walkAmount;
        rarm.rotateAngleX += MathHelper.cos(walk + (float) Math.PI - 3.0F) * walkAmount * 0.2F + 0.4F * walkAmount;
        larm.rotateAngleZ += MathHelper.cos(idle - 1.0F) * 0.05F;
        rarm.rotateAngleZ -= MathHelper.cos(idle - 1.0F) * 0.05F;

        if (swim > 0.0F) {
            float swimWave = limbSwing * 0.25F;
            body.rotateAngleX += 1.3963F * swim;
            head.rotateAngleX -= 1.2217F * swim;
            tail.rotateAngleX -= 0.8727F * swim;
            tail.rotateAngleZ += MathHelper.sin(swimWave - 2.0F) * 0.375F * swimAmount;
            larm.rotateAngleZ += MathHelper.cos(swimWave - 1.5F) * 1.075F * swimAmount + swim;
            rarm.rotateAngleZ -= MathHelper.cos(swimWave - 3.0F) * 1.075F * swimAmount + swim;
            larm.rotateAngleX += MathHelper.cos(swimWave - 2.0F) * 0.5F * swimAmount - 0.2F * swim;
            rarm.rotateAngleX += MathHelper.cos(swimWave - 4.5F) * 0.5F * swimAmount - 0.2F * swim;
            lleg.rotateAngleX += MathHelper.cos(swimWave + 2.0F) * swimAmount;
            rleg.rotateAngleX += MathHelper.cos(swimWave + 2.0F + (float) Math.PI) * swimAmount;
            body.rotationPointY -= 6.0F * swim;
            body.rotationPointZ += 25.0F * swim;
            if (deepOne != null) {
                body.rotateAngleX += deepOne.getFishPitch(0.0F) * 0.017453292F;
            }
        } else if (deepOne != null) {
            body.rotateAngleX += deepOne.getFishPitch(0.0F) * 0.017453292F;
        }
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, 24.0F, 0.0F);
        reset(body);
        reset(head);
        reset(jaw);
        reset(lfin);
        reset(rfin);
        reset(lure);
        reset(lleg);
        reset(rleg);
        reset(rarm);
        reset(larm);
        reset(tail);
    }

    private static void reset(ModelRenderer renderer) {
        renderer.rotateAngleX = 0.0F;
        renderer.rotateAngleY = 0.0F;
        renderer.rotateAngleZ = 0.0F;
    }

    private ModelRenderer arm(boolean left) {
        ModelRenderer arm = new ModelRenderer(this);
        arm.mirror = left;
        arm.setTextureOffset(80, 42).addBox(left ? -0.5F : -5.5F, 19.0F, -4.5F, 6, 4, 6, 0.0F);
        arm.setTextureOffset(104, 11).addBox(left ? -0.5F : -5.5F, -2.0F, -4.5F, 6, 21, 6, 0.25F);
        arm.setTextureOffset(74, 72).addBox(left ? 0.5F : -8.5F, -4.0F, -5.5F, 8, 8, 8, 0.0F);
        arm.setTextureOffset(68, 17).addBox(left ? -0.5F : -5.5F, -2.0F, -4.5F, 6, 21, 6, 0.0F);
        arm.setTextureOffset(0, 42).addBox(left ? 2.5F : -2.5F, 4.0F, 1.5F, 0, 13, 6, 0.0F);
        arm.setTextureOffset(122, 98).addBox(left ? 5.5F : -8.5F, 4.0F, -1.5F, 3, 15, 0, 0.0F);
        return arm;
    }

    private ModelRenderer child(ModelRenderer parent, float x, float y, float z, float rx, float ry, float rz) {
        return child(parent, x, y, z, rx, ry, rz, false);
    }

    private ModelRenderer child(ModelRenderer parent, float x, float y, float z, float rx, float ry, float rz, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setRotationPoint(x, y, z);
        renderer.rotateAngleX = rx;
        renderer.rotateAngleY = ry;
        renderer.rotateAngleZ = rz;
        parent.addChild(renderer);
        return renderer;
    }
}
