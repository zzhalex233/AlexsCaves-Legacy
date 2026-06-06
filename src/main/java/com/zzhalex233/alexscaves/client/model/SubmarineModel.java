package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.item.SubmarineEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class SubmarineModel extends ModelBase {
    private final ModelRenderer hull;
    private final ModelRenderer rarm;
    private final ModelRenderer leftpropeller;
    private final ModelRenderer larm;
    private final ModelRenderer rightpropeller;
    private final ModelRenderer watermask;
    private final ModelRenderer backpropeller;
    private final ModelRenderer periscope;

    public SubmarineModel() {
        textureWidth = 512;
        textureHeight = 512;

        hull = new ModelRenderer(this);
        hull.setRotationPoint(0.0F, -10.0F, 0.0F);
        hull.setTextureOffset(103, 0).addBox(-8.0F, -26.0F, 7.0F, 16, 6, 16, 0.0F);
        hull.setTextureOffset(0, 66).addBox(-20.0F, -22.0F, -7.0F, 40, 46, 32, 0.0F);
        hull.setTextureOffset(152, 161).addBox(13.0F, 8.0F, -30.0F, 7, 16, 23, 0.0F);
        hull.setTextureOffset(92, 161).addBox(-20.0F, 8.0F, -30.0F, 7, 16, 23, 0.0F);
        hull.setTextureOffset(154, 0).addBox(-18.0F, -20.0F, -35.0F, 36, 28, 28, 0.0F);
        hull.setTextureOffset(112, 66).addBox(-20.0F, 8.0F, -37.0F, 40, 16, 7, 0.0F);
        hull.setTextureOffset(251, 189).addBox(-6.0F, 8.0F, -30.0F, 12, 16, 6, 0.0F);
        hull.setTextureOffset(0, 0).addBox(-13.0F, 15.0F, -30.0F, 26, 15, 51, 0.0F);

        rarm = new ModelRenderer(this);
        rarm.setRotationPoint(22.5F, 25.5F, -7.5F);
        hull.addChild(rarm);
        rarm.setTextureOffset(255, 91).addBox(-9.5F, -9.5F, -43.5F, 19, 19, 51, 0.0F);

        leftpropeller = new ModelRenderer(this);
        leftpropeller.setRotationPoint(0.0F, 0.0F, -43.5F);
        rarm.addChild(leftpropeller);
        leftpropeller.setTextureOffset(189, 165).addBox(-9.5F, -9.5F, -3.0F, 19, 19, 0, 0.0F);
        leftpropeller.setTextureOffset(103, 22).addBox(-2.5F, -2.5F, -6.0F, 5, 5, 6, 0.0F);

        larm = new ModelRenderer(this);
        larm.setRotationPoint(-22.5F, 25.5F, -25.5F);
        hull.addChild(larm);
        larm.mirror = true;
        larm.setTextureOffset(255, 91).addBox(-9.5F, -9.5F, -25.5F, 19, 19, 51, 0.0F);
        larm.mirror = false;

        rightpropeller = new ModelRenderer(this);
        rightpropeller.setRotationPoint(0.0F, 0.0F, -25.5F);
        larm.addChild(rightpropeller);
        rightpropeller.setTextureOffset(189, 165).addBox(-9.5F, -9.5F, -3.0F, 19, 19, 0, 0.0F);
        rightpropeller.setTextureOffset(103, 22).addBox(-2.5F, -2.5F, -6.0F, 5, 5, 6, 0.0F);

        ModelRenderer llever = new ModelRenderer(this);
        llever.setRotationPoint(-7.0F, 15.25F, -16.0F);
        hull.addChild(llever);
        llever.mirror = true;
        llever.setTextureOffset(265, 72).addBox(-1.0F, -11.25F, -1.0F, 2, 16, 2, 0.0F);
        llever.mirror = false;

        ModelRenderer rlever = new ModelRenderer(this);
        rlever.setRotationPoint(7.0F, 15.0F, -16.0F);
        hull.addChild(rlever);
        rlever.mirror = true;
        rlever.setTextureOffset(265, 72).addBox(-1.0F, -11.0F, -1.0F, 2, 16, 2, 0.0F);
        rlever.mirror = false;

        watermask = new ModelRenderer(this);
        watermask.setRotationPoint(0.0F, 5.0F, 0.0F);
        hull.addChild(watermask);
        watermask.setTextureOffset(0, 214).addBox(-18.0F, 0.0F, -35.0F, 36, 52, 28, 0.0F);

        ModelRenderer motor = new ModelRenderer(this);
        motor.setRotationPoint(0.0F, 1.0F, 25.0F);
        hull.addChild(motor);
        motor.setTextureOffset(0, 0).addBox(-9.0F, -8.0F, 0.0F, 18, 18, 7, 0.0F);
        motor.setTextureOffset(153, 211).addBox(-14.0F, -13.0F, 7.0F, 28, 28, 18, 0.0F);

        backpropeller = new ModelRenderer(this);
        backpropeller.setRotationPoint(0.0F, 1.0F, 7.0F);
        motor.addChild(backpropeller);
        backpropeller.setTextureOffset(0, 25).addBox(-4.0F, -4.0F, 0.0F, 8, 8, 12, 0.0F);
        backpropeller.setTextureOffset(0, 188).addBox(-13.0F, -13.0F, 9.0F, 26, 26, 0, 0.0F);

        periscope = new ModelRenderer(this);
        periscope.setRotationPoint(0.0F, -22.0F, 3.0F);
        hull.addChild(periscope);
        periscope.setTextureOffset(0, 66).addBox(-3.0F, -20.0F, -3.0F, 6, 20, 6, 0.0F);
        periscope.setTextureOffset(199, 59).addBox(-3.0F, -20.0F, -10.0F, 6, 6, 7, 0.25F);
        periscope.setTextureOffset(103, 35).addBox(-3.0F, -20.0F, -10.0F, 6, 6, 7, 0.0F);

        ModelRenderer seat = new ModelRenderer(this);
        seat.setRotationPoint(0.0F, 13.5F, -11.0F);
        hull.addChild(seat);
        seat.setTextureOffset(165, 93).addBox(-4.0F, -1.5F, -4.0F, 8, 3, 8, 0.0F);
    }

    public void setupAnim(SubmarineEntity entity, float ageInTicks) {
        resetPose();
        float partialTicks = ageInTicks - entity.ticksExisted;
        float leftPropellerRot = MathHelper.wrapDegrees(entity.getLeftPropellerRot(partialTicks));
        float rightPropellerRot = MathHelper.wrapDegrees(entity.getRightPropellerRot(partialTicks));
        float backPropellerRot = MathHelper.wrapDegrees(entity.getBackPropellerRot(partialTicks));
        float shake = Math.max(entity.shakeTime - partialTicks, 0.0F) / 10.0F;
        Entity controllingPlayer = entity.getPassengers().isEmpty() ? null : entity.getPassengers().get(0);
        rightpropeller.rotateAngleZ += (float) Math.toRadians(leftPropellerRot);
        leftpropeller.rotateAngleZ += (float) Math.toRadians(rightPropellerRot);
        backpropeller.rotateAngleZ += (float) Math.toRadians(backPropellerRot);
        if (controllingPlayer instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) controllingPlayer;
            float subYaw = 180.0F - interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
            float headYaw = 180.0F + interpolateRotation(living.prevRotationYawHead, living.rotationYawHead, partialTicks);
            periscope.rotateAngleY += (float) Math.toRadians(subYaw + headYaw);
        }
        hull.rotateAngleX += (float) Math.sin(ageInTicks * 0.7F + 1.0F) * shake * 0.05F;
        hull.rotateAngleZ += (float) Math.sin(ageInTicks * 0.7F) * shake * 0.1F;
        if (entity.getDamageLevel() < 4) {
            rarm.showModel = true;
        } else {
            hull.rotateAngleZ += (float) Math.toRadians(10.0F);
            rarm.showModel = false;
        }
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity instanceof SubmarineEntity) {
            setupAnim((SubmarineEntity) entity, ageInTicks);
        } else {
            resetPose();
        }
        hull.render(scale);
    }

    private void resetPose() {
        hull.rotateAngleX = 0.0F;
        hull.rotateAngleY = 0.0F;
        hull.rotateAngleZ = 0.0F;
        rarm.rotateAngleX = 0.0F;
        rarm.rotateAngleY = 0.0F;
        rarm.rotateAngleZ = 0.0F;
        rarm.showModel = true;
        larm.rotateAngleX = 0.0F;
        larm.rotateAngleY = 0.0F;
        larm.rotateAngleZ = 0.0F;
        leftpropeller.rotateAngleX = 0.0F;
        leftpropeller.rotateAngleY = 0.0F;
        leftpropeller.rotateAngleZ = 0.0F;
        rightpropeller.rotateAngleX = 0.0F;
        rightpropeller.rotateAngleY = 0.0F;
        rightpropeller.rotateAngleZ = 0.0F;
        backpropeller.rotateAngleX = 0.0F;
        backpropeller.rotateAngleY = 0.0F;
        backpropeller.rotateAngleZ = 0.0F;
        periscope.rotateAngleX = 0.0F;
        periscope.rotateAngleY = 0.0F;
        periscope.rotateAngleZ = 0.0F;
        watermask.rotateAngleX = 0.0F;
        watermask.rotateAngleY = 0.0F;
        watermask.rotateAngleZ = 0.0F;
        watermask.rotationPointX = 0.0F;
        watermask.rotationPointY = 5.0F;
        watermask.rotationPointZ = 0.0F;
        watermask.showModel = false;
    }

    private static float interpolateRotation(float prevYaw, float yaw, float partialTicks) {
        float delta = yaw - prevYaw;
        while (delta < -180.0F) {
            delta += 360.0F;
        }
        while (delta >= 180.0F) {
            delta -= 360.0F;
        }
        return prevYaw + partialTicks * delta;
    }
}
