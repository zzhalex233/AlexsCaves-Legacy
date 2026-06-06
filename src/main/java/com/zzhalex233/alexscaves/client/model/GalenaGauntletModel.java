package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GalenaGauntletModel extends ModelBase {
    private final ModelRenderer base;
    private final ModelRenderer thumb;
    private final ModelRenderer finger;
    private final ModelRenderer finger2;
    private final ModelRenderer finger3;
    private final boolean left;

    public GalenaGauntletModel(boolean left) {
        textureWidth = 128;
        textureHeight = 128;
        this.left = left;

        base = new ModelRenderer(this);
        base.setRotationPoint(0.0F, 6.75F, -0.5F);
        base.setTextureOffset(27, 31).addBox(-4.5F, 0.25F, -4.5F, 9, 11, 9, 0.01F);
        base.setTextureOffset(0, 0).addBox(-4.5F, 0.25F, -4.5F, 9, 11, 9, 0.26F);

        ModelRenderer fingers = new ModelRenderer(this);
        fingers.setRotationPoint(0.0F, 9.25F, 0.5F);
        base.addChild(fingers);

        thumb = new ModelRenderer(this);
        thumb.setRotationPoint(left ? -5.0F : 4.0F, 0.5F, 2.0F);
        thumb.rotateAngleY = left ? 0.0F : (float) Math.PI;
        thumb.setTextureOffset(27, 0).addBox(-4.5F, -1.5F, -2.0F, 5, 3, 4, 0.25F);
        thumb.setTextureOffset(36, 23).addBox(-4.5F, -1.5F, -2.0F, 5, 3, 4, 0.0F);
        fingers.addChild(thumb);

        finger = finger(-4.5F, 0.5F, false);
        finger2 = finger(0.0F, 0.25F, true);
        finger3 = finger(4.5F, 0.25F, true);
        fingers.addChild(finger);
        fingers.addChild(finger2);
        fingers.addChild(finger3);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        base.render(scale);
    }

    public void setup(float openAmount, float ageInTicks) {
        resetPose();
        float closeAmount = 1.0F - openAmount;
        float leftOff = left ? -1.0F : 1.0F;
        base.rotateAngleY += closeAmount * (left ? -90.0F : 90.0F) * ((float) Math.PI / 180.0F);
        thumb.rotateAngleZ += closeAmount * (left ? -90.0F : 90.0F) * ((float) Math.PI / 180.0F);
        closeFinger(finger, closeAmount, 1.0F, 2.0F, 0.0F);
        closeFinger(finger2, closeAmount, 0.0F, 3.0F, -1.0F);
        closeFinger(finger3, closeAmount, -1.0F, 2.5F, 0.0F);
        thumb.rotationPointY -= closeAmount * 2.5F;
        finger.rotateAngleX += MathHelper.cos(ageInTicks * 0.1F + 3.0F * leftOff) * 0.3F * openAmount - 0.4F * openAmount;
        finger2.rotateAngleX += MathHelper.cos(ageInTicks * 0.1F + 2.0F * leftOff) * 0.3F * openAmount - 0.4F * openAmount;
        finger3.rotateAngleX += MathHelper.cos(ageInTicks * 0.1F + leftOff) * 0.3F * openAmount - 0.4F * openAmount;
        thumb.rotateAngleZ += MathHelper.cos(ageInTicks * 0.1F - 1.0F) * 0.3F * leftOff * openAmount - 0.2F * leftOff * openAmount;
    }

    private void closeFinger(ModelRenderer renderer, float closeAmount, float x, float y, float z) {
        renderer.rotateAngleX += closeAmount * (float) Math.PI;
        renderer.rotationPointX += closeAmount * x;
        renderer.rotationPointY += closeAmount * y;
        renderer.rotationPointZ += closeAmount * z;
    }

    private void resetPose() {
        base.setRotationPoint(0.0F, 6.75F, -0.5F);
        base.rotateAngleX = base.rotateAngleY = base.rotateAngleZ = 0.0F;
        thumb.setRotationPoint(left ? -5.0F : 4.0F, 0.5F, 2.0F);
        thumb.rotateAngleX = 0.0F;
        thumb.rotateAngleY = left ? 0.0F : (float) Math.PI;
        thumb.rotateAngleZ = 0.0F;
        resetFinger(finger, -4.5F, 0.5F);
        resetFinger(finger2, 0.0F, 0.25F);
        resetFinger(finger3, 4.5F, 0.25F);
    }

    private void resetFinger(ModelRenderer renderer, float x, float y) {
        renderer.setRotationPoint(x, y, -5.0F);
        renderer.rotateAngleX = renderer.rotateAngleY = renderer.rotateAngleZ = 0.0F;
    }

    private ModelRenderer finger(float x, float y, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(x, y, -5.0F);
        renderer.mirror = true;
        renderer.setTextureOffset(30, 54).addBox(-2.0F, y == 0.5F ? -1.75F : -1.5F, -6.0F, 4, 3, 6, 0.25F);
        renderer.mirror = mirror;
        renderer.setTextureOffset(56, 54).addBox(-2.0F, y == 0.5F ? -1.75F : -1.5F, -6.0F, 4, 3, 6, 0.0F);
        return renderer;
    }
}
