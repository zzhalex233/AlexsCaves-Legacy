package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.BoundroidWinchEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BoundroidWinchModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer coil;
    private final ModelRenderer leftFront;
    private final ModelRenderer rightFront;
    private final ModelRenderer leftBack;
    private final ModelRenderer rightBack;

    public BoundroidWinchModel() {
        textureWidth = 64;
        textureHeight = 64;
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 18.0F, 0.0F);
        body.setTextureOffset(20, 41).addBox(4.0F, -1.5F, -3.0F, 2, 7, 6, 0.0F);
        body.setTextureOffset(36, 41).addBox(-6.0F, -1.5F, -3.0F, 2, 7, 6, 0.0F);
        body.setTextureOffset(0, 0).addBox(-6.0F, -10.5F, -6.0F, 12, 9, 12, 0.0F);
        coil = new ModelRenderer(this);
        coil.setRotationPoint(0.0F, 2.5F, 0.0F);
        body.addChild(coil);
        coil.setTextureOffset(0, 21).addBox(-4.5F, -3.5F, -3.5F, 9, 7, 7, 0.0F);
        coil.setTextureOffset(26, 29).addBox(-4.0F, -3.0F, -3.0F, 8, 6, 6, 0.0F);
        leftFront = limb(5.0F, -7.5F, -5.0F, false);
        rightFront = limb(-5.0F, -7.5F, -5.0F, true);
        leftBack = limb(5.0F, -7.5F, 5.0F, false);
        rightBack = limb(-5.0F, -7.5F, 5.0F, true);
    }

    private ModelRenderer limb(float x, float y, float z, boolean mirror) {
        ModelRenderer limb = new ModelRenderer(this);
        limb.setRotationPoint(x, y, z);
        body.addChild(limb);
        limb.mirror = mirror;
        limb.setTextureOffset(0, 35).addBox(mirror ? -4.0F : -1.0F, -9.0F, -4.0F, 5, 10, 5, 0.0F);
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
        float latched = entity instanceof BoundroidWinchEntity ? ((BoundroidWinchEntity) entity).getLatchProgress(partialTicks) : 0.0F;
        float chainLength = entity instanceof BoundroidWinchEntity ? ((BoundroidWinchEntity) entity).getChainLength(partialTicks) : 0.0F;
        float ground = 1.0F - latched;
        coil.rotateAngleX = chainLength * 4.5F;
        body.rotateAngleX = ground * (float) Math.PI;
        body.rotationPointY = 18.0F - ground * 11.0F;
        leftFront.rotateAngleX = MathHelper.cos(ageInTicks * 0.8F) * 0.35F;
        rightFront.rotateAngleX = MathHelper.cos(ageInTicks * 0.8F + (float) Math.PI) * 0.35F;
        leftBack.rotateAngleX = MathHelper.cos(ageInTicks * 0.8F + (float) Math.PI) * 0.35F;
        rightBack.rotateAngleX = MathHelper.cos(ageInTicks * 0.8F) * 0.35F;
    }

    public Vec3d getChainPosition() {
        return new Vec3d(0.0D, 0.35D, 0.0D);
    }
}
