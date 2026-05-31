package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.RadgillEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class RadgillModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer tailFin;
    private final ModelRenderer leftFin;
    private final ModelRenderer rightFin;
    private final ModelRenderer jaw;

    public RadgillModel() {
        textureWidth = 64;
        textureHeight = 64;
        body = part(0, 0, -3.0F, -4.0F, -6.0F, 6, 9, 11, false);
        body.setRotationPoint(0.0F, 19.0F, 0.0F);
        body.setTextureOffset(38, 40).addBox(-1.5F, -6.0F, -5.0F, 3, 3, 3, 0.0F);
        body.setTextureOffset(26, 40).addBox(1.75F, -5.0F, -5.99F, 3, 3, 3, 0.0F);
        body.setTextureOffset(34, 17).addBox(-4.75F, -5.0F, -5.99F, 3, 3, 3, 0.0F);
        body.setTextureOffset(23, 0).addBox(-3.0F, -4.0F, -11.0F, 6, 4, 5, 0.0F);

        tail = part(18, 20, -2.0F, -3.0F, 0.5F, 4, 6, 8, false);
        tail.setRotationPoint(0.0F, -1.0F, 4.5F);
        body.addChild(tail);

        tailFin = plate(5, 34, 0.0F, -4.0F, -4.0F, 0, 10, 14, false);
        tailFin.setRotationPoint(0.0F, 0.0F, 8.5F);
        tail.addChild(tailFin);

        ModelRenderer dorsal = plate(32, 28, 0.0F, -6.0F, -0.5F, 0, 6, 6, false);
        dorsal.setRotationPoint(0.0F, -4.0F, -0.5F);
        body.addChild(dorsal);
        ModelRenderer dorsal2 = plate(20, 28, 0.0F, -6.0F, -0.5F, 0, 6, 6, false);
        dorsal2.setRotationPoint(0.0F, -3.0F, 3.0F);
        tail.addChild(dorsal2);
        ModelRenderer bottomFin = plate(0, 22, 0.0F, -1.0F, -1.0F, 0, 6, 6, false);
        bottomFin.setRotationPoint(0.0F, 4.0F, 3.0F);
        body.addChild(bottomFin);

        leftFin = plate(34, 9, 0.0F, -2.5F, 0.0F, 6, 8, 0, false);
        leftFin.setRotationPoint(3.0F, 2.5F, -1.0F);
        leftFin.rotateAngleY = -0.7854F;
        body.addChild(leftFin);

        rightFin = plate(34, 9, -6.0F, -2.5F, 0.0F, 6, 8, 0, true);
        rightFin.setRotationPoint(-3.0F, 2.5F, -1.0F);
        rightFin.rotateAngleY = 0.7854F;
        body.addChild(rightFin);

        jaw = part(0, 34, -3.5F, -6.0F, -5.0F, 7, 6, 6, false);
        jaw.setRotationPoint(0.0F, 5.0F, -6.0F);
        body.addChild(jaw);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        RadgillEntity radgill = entity instanceof RadgillEntity ? (RadgillEntity) entity : null;
        float landProgress = radgill == null ? 0.0F : radgill.getLandProgress(0.0F);
        float fishPitch = radgill == null ? 0.0F : radgill.getFishPitch(0.0F);
        float swim = MathHelper.sin(limbSwing * 0.5F) * limbSwingAmount;
        float idle = MathHelper.sin(ageInTicks * 0.1F);
        body.rotateAngleZ = -landProgress * 1.5708F;
        body.rotateAngleX += fishPitch;
        body.rotateAngleY = swim * 0.2F;
        body.rotationPointY += idle;
        jaw.rotateAngleX = MathHelper.sin(ageInTicks * 0.1F + 1.0F) * 0.2F - 0.2F;
        tail.rotateAngleY = MathHelper.sin(limbSwing * 0.5F - 1.0F) * limbSwingAmount * 0.5F;
        tailFin.rotateAngleY = MathHelper.sin(limbSwing * 0.5F - 2.0F) * limbSwingAmount * 0.5F;
        leftFin.rotateAngleY = -0.7854F + MathHelper.sin(ageInTicks * 0.1F) * 0.2F;
        rightFin.rotateAngleY = 0.7854F - MathHelper.sin(ageInTicks * 0.1F) * 0.2F;
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, 19.0F, 0.0F);
        body.rotateAngleX = 0.0F;
        body.rotateAngleY = 0.0F;
        body.rotateAngleZ = 0.0F;
        tail.rotateAngleY = 0.0F;
        tailFin.rotateAngleY = 0.0F;
        jaw.rotateAngleX = 0.0F;
        leftFin.rotateAngleY = -0.7854F;
        rightFin.rotateAngleY = 0.7854F;
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
