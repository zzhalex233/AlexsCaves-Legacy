package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.TeletorEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TeletorModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;

    public TeletorModel() {
        textureWidth = 128;
        textureHeight = 128;
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 24.0F, 0.0F);
        body.setTextureOffset(40, 37).addBox(-3.0F, -16.0F, -2.0F, 6, 6, 4, 0.0F);

        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -16.0F, 0.0F);
        body.addChild(head);
        head.setTextureOffset(0, 0).addBox(-6.0F, -18.0F, -6.0F, 12, 17, 12, 0.0F);
        head.setTextureOffset(40, 29).addBox(6.0F, -9.0F, -2.0F, 9, 4, 4, 0.0F);
        head.setTextureOffset(20, 29).addBox(9.0F, -23.0F, -2.0F, 6, 14, 4, 0.0F);
        head.setTextureOffset(40, 29).addBox(-15.0F, -9.0F, -2.0F, 9, 4, 4, 0.0F);
        head.setTextureOffset(20, 29).addBox(-15.0F, -23.0F, -2.0F, 6, 14, 4, 0.0F);

        leftArm = limb(-3.0F, -13.0F, 0.0F, -8.0F);
        rightArm = limb(3.0F, -13.0F, 0.0F, 8.0F);
        leftLeg = limb(-1.5F, -10.0F, 0.0F, -5.0F);
        rightLeg = limb(1.5F, -10.0F, 0.0F, 5.0F);
    }

    private ModelRenderer limb(float x, float y, float z, float width) {
        ModelRenderer limb = new ModelRenderer(this);
        limb.setRotationPoint(x, y, z);
        body.addChild(limb);
        limb.setTextureOffset(0, 48).addBox(width < 0.0F ? width : 0.0F, -1.0F, -1.0F, (int) Math.abs(width), 2, 2, 0.0F);
        return limb;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        float partialTicks = ageInTicks - entity.ticksExisted;
        float control = entity instanceof TeletorEntity ? ((TeletorEntity) entity).getControlProgress(partialTicks) : 0.0F;
        float bob = MathHelper.sin(ageInTicks * 0.16F) * 0.8F;
        body.rotationPointY = 24.0F + bob;
        body.rotateAngleX = MathHelper.sin(ageInTicks * 0.08F) * 0.04F;
        body.rotateAngleZ = MathHelper.sin(ageInTicks * 0.12F) * 0.05F;
        head.rotateAngleY = netHeadYaw * 0.017453292F;
        head.rotateAngleX = headPitch * 0.017453292F - control * 0.15F;
        leftArm.rotateAngleZ = -0.5F - control * 0.95F + MathHelper.sin(ageInTicks * 0.2F) * 0.08F;
        rightArm.rotateAngleZ = 0.5F + control * 0.95F - MathHelper.sin(ageInTicks * 0.2F) * 0.08F;
        leftArm.rotateAngleY = control * 0.45F;
        rightArm.rotateAngleY = -control * 0.45F;
        boolean crossed = entity instanceof TeletorEntity && ((TeletorEntity) entity).areLegsCrossed(limbSwingAmount);
        leftLeg.rotateAngleZ = crossed ? 0.65F : -0.25F;
        rightLeg.rotateAngleZ = crossed ? -0.65F : 0.25F;
        leftLeg.rotateAngleX = crossed ? 0.0F : MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount * 0.6F;
        rightLeg.rotateAngleX = crossed ? 0.0F : MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount * 0.6F;
    }

    public Vec3d getWeaponOrigin() {
        return new Vec3d(0.0D, 1.1D, 0.0D);
    }
}
