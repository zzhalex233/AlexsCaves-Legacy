package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.MagnetronEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class MagnetronModel extends ModelBase {
    private final ModelRenderer wheel;
    private final ModelRenderer headPivot;
    private final ModelRenderer head;
    private final ModelRenderer headExtra;

    public MagnetronModel() {
        textureWidth = 128;
        textureHeight = 128;
        wheel = box(28, 28, -3.0F, -8.0F, -8.0F, 6, 4, 16);
        wheel.setRotationPoint(0.0F, 16.0F, 0.0F);
        wheel.setTextureOffset(40, 60).addBox(-3.0F, -4.0F, -8.0F, 6, 8, 4);
        wheel.setTextureOffset(56, 22).addBox(-3.0F, -4.0F, 4.0F, 6, 8, 4);
        wheel.setTextureOffset(0, 44).addBox(-3.0F, 4.0F, -8.0F, 6, 4, 16);
        headPivot = new ModelRenderer(this);
        headPivot.setRotationPoint(0.0F, 0.0F, -2.0F);
        head = new ModelRenderer(this);
        head.rotateAngleX = 0.3927F;
        headPivot.addChild(head);
        headExtra = box(52, 48, -2.0F, -5.0F, 18.0F, 6, 6, 6);
        headExtra.setRotationPoint(0.0F, 2.0F, 0.0F);
        headExtra.rotateAngleY = -0.7854F;
        head.addChild(headExtra);
        headExtra.setTextureOffset(0, 22).addBox(-8.0F, -5.0F, 8.0F, 6, 6, 16);
        headExtra.setTextureOffset(28, 48).addBox(18.0F, -5.0F, -2.0F, 6, 6, 6);
        headExtra.setTextureOffset(48, 0).addBox(8.0F, -5.0F, -8.0F, 16, 6, 6);
        headExtra.setTextureOffset(0, 0).addBox(-8.0F, -5.0F, -8.0F, 16, 6, 16);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        wheel.render(scale);
        headPivot.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        if (entity instanceof MagnetronEntity) {
            MagnetronEntity magnetron = (MagnetronEntity) entity;
            float partial = MathHelper.clamp(ageInTicks - magnetron.ticksExisted, 0.0F, 1.0F);
            float form = Math.min(1.0F, magnetron.getFormProgress(partial) * 10.0F);
            float roll = 1.0F - form;
            float wheelYaw = (magnetron.getWheelYaw(partial) - magnetron.renderYawOffset) * roll + 90.0F * form;
            wheel.rotateAngleY = wheelYaw * 0.017453292F;
            wheel.rotateAngleX = magnetron.getRollPosition(partial) * 0.017453292F * roll;
            headPivot.rotationPointY -= magnetron.getRollLeanProgress(partial) * 2.0F;
            headPivot.rotationPointZ -= magnetron.getRollLeanProgress(partial) * 4.0F;
            headPivot.rotateAngleY = netHeadYaw * 0.017453292F * 0.25F;
            headPivot.rotateAngleX = headPitch * 0.017453292F * 0.25F;
            if (!magnetron.isEntityAlive()) {
                wheel.isHidden = true;
            }
        }
    }

    private void resetPose() {
        wheel.isHidden = false;
        wheel.rotationPointX = 0.0F;
        wheel.rotationPointY = 16.0F;
        wheel.rotationPointZ = 0.0F;
        wheel.rotateAngleX = 0.0F;
        wheel.rotateAngleY = 0.0F;
        wheel.rotateAngleZ = 0.0F;
        headPivot.rotationPointX = 0.0F;
        headPivot.rotationPointY = 0.0F;
        headPivot.rotationPointZ = -2.0F;
        headPivot.rotateAngleX = 0.0F;
        headPivot.rotateAngleY = 0.0F;
        headPivot.rotateAngleZ = 0.0F;
    }

    private ModelRenderer box(int u, int v, float x, float y, float z, int dx, int dy, int dz) {
        ModelRenderer renderer = new ModelRenderer(this, u, v);
        renderer.addBox(x, y, z, dx, dy, dz);
        return renderer;
    }
}
