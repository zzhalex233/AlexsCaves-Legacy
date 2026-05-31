package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.HullbreakerEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class HullbreakerModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer tail1;
    private final ModelRenderer tail2;
    private final ModelRenderer tail3;
    private final ModelRenderer tail4;
    private final ModelRenderer leftFin;
    private final ModelRenderer rightFin;
    private final ModelRenderer dorsalFin;

    public HullbreakerModel() {
        textureWidth = 256;
        textureHeight = 256;
        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 18.0F, 0.0F);
        body = box(0, 0, -18.0F, -16.0F, -24.0F, 36, 32, 48, false);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);
        root.addChild(body);
        head = box(0, 80, -20.0F, -15.0F, -34.0F, 40, 30, 34, false);
        head.setRotationPoint(0.0F, 0.0F, -22.0F);
        body.addChild(head);
        jaw = box(0, 144, -16.0F, 0.0F, -32.0F, 32, 10, 32, false);
        jaw.setRotationPoint(0.0F, 8.0F, -2.0F);
        head.addChild(jaw);
        tail1 = box(112, 72, -12.0F, -11.0F, 0.0F, 24, 22, 26, false);
        tail1.setRotationPoint(0.0F, 0.0F, 22.0F);
        body.addChild(tail1);
        tail2 = box(112, 120, -9.0F, -8.0F, 0.0F, 18, 16, 24, false);
        tail2.setRotationPoint(0.0F, 0.0F, 23.0F);
        tail1.addChild(tail2);
        tail3 = box(112, 160, -7.0F, -6.0F, 0.0F, 14, 12, 22, false);
        tail3.setRotationPoint(0.0F, 0.0F, 21.0F);
        tail2.addChild(tail3);
        tail4 = box(168, 160, -4.0F, -4.0F, 0.0F, 8, 8, 26, false);
        tail4.setRotationPoint(0.0F, 0.0F, 18.0F);
        tail3.addChild(tail4);
        leftFin = box(0, 200, 0.0F, -2.0F, -10.0F, 28, 4, 26, false);
        leftFin.setRotationPoint(17.0F, 7.0F, -5.0F);
        body.addChild(leftFin);
        rightFin = box(0, 200, -28.0F, -2.0F, -10.0F, 28, 4, 26, true);
        rightFin.setRotationPoint(-17.0F, 7.0F, -5.0F);
        body.addChild(rightFin);
        dorsalFin = box(120, 0, -3.0F, -24.0F, -10.0F, 6, 24, 32, false);
        dorsalFin.setRotationPoint(0.0F, -14.0F, -2.0F);
        body.addChild(dorsalFin);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        float partial = entity instanceof HullbreakerEntity ? MathHelper.clamp(ageInTicks - entity.ticksExisted, 0.0F, 1.0F) : 0.0F;
        HullbreakerEntity hullbreaker = entity instanceof HullbreakerEntity ? (HullbreakerEntity) entity : null;
        float land = hullbreaker == null ? 0.0F : hullbreaker.getLandProgress(partial);
        float swim = ageInTicks * 0.24F;
        float amplitude = 0.08F + limbSwingAmount * 0.35F;
        root.rotateAngleX = hullbreaker == null ? 0.0F : hullbreaker.getFishPitch(partial) * 0.017453292F;
        body.rotateAngleZ = MathHelper.sin(swim) * 0.04F * (1.0F - land);
        head.rotateAngleY = MathHelper.sin(swim - 0.8F) * amplitude * (1.0F - land);
        tail1.rotateAngleY = MathHelper.sin(swim + 0.5F) * amplitude;
        tail2.rotateAngleY = MathHelper.sin(swim + 1.2F) * amplitude * 1.2F;
        tail3.rotateAngleY = MathHelper.sin(swim + 1.9F) * amplitude * 1.4F;
        tail4.rotateAngleY = MathHelper.sin(swim + 2.6F) * amplitude * 1.7F;
        jaw.rotateAngleX = 0.08F + Math.max(0.0F, MathHelper.sin(ageInTicks * 0.18F)) * 0.08F;
        leftFin.rotateAngleZ = -0.35F - MathHelper.sin(swim) * 0.12F;
        rightFin.rotateAngleZ = 0.35F + MathHelper.sin(swim) * 0.12F;
        body.rotateAngleX += land * 0.35F;
    }

    private void resetPose() {
        reset(root);
        reset(body);
        reset(head);
        reset(jaw);
        reset(tail1);
        reset(tail2);
        reset(tail3);
        reset(tail4);
        reset(leftFin);
        reset(rightFin);
        reset(dorsalFin);
    }

    private void reset(ModelRenderer renderer) {
        renderer.rotateAngleX = renderer.rotateAngleY = renderer.rotateAngleZ = 0.0F;
    }

    private ModelRenderer box(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth);
        return renderer;
    }
}
