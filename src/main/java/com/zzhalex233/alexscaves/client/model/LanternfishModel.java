package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.LanternfishEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class LanternfishModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer rightFin;
    private final ModelRenderer leftFin;

    public LanternfishModel() {
        textureWidth = 16;
        textureHeight = 16;
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 22.0F, -1.5F);
        body.setTextureOffset(2, 9).addBox(-1.0F, -1.0F, -2.5F, 2, 2, 5, 0.0F);
        body.setTextureOffset(8, 3).addBox(0.0F, 1.0F, -1.25F, 0, 2, 4, 0.0F);
        body.setTextureOffset(7, -1).addBox(0.0F, -2.5F, -1.0F, 0, 2, 3, 0.0F);

        tail = new ModelRenderer(this);
        tail.setRotationPoint(0.0F, 0.0F, 2.5F);
        tail.setTextureOffset(0, 3).addBox(0.0F, -1.5F, 0.0F, 0, 3, 3, 0.0F);
        body.addChild(tail);

        rightFin = new ModelRenderer(this);
        rightFin.setRotationPoint(1.0F, 0.5F, -0.5F);
        rightFin.setTextureOffset(0, 0).addBox(0.0F, -0.5F, 0.0F, 1, 1, 0, 0.0F);
        body.addChild(rightFin);

        leftFin = new ModelRenderer(this);
        leftFin.mirror = true;
        leftFin.setRotationPoint(-1.0F, 0.5F, -0.5F);
        leftFin.setTextureOffset(0, 0).addBox(-1.0F, -0.5F, 0.0F, 1, 1, 0, 0.0F);
        body.addChild(leftFin);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        body.rotateAngleX = 0.0F;
        body.rotateAngleY = 0.0F;
        body.rotateAngleZ = 0.0F;
        tail.rotateAngleY = 0.0F;
        rightFin.rotateAngleY = 0.0F;
        leftFin.rotateAngleY = 0.0F;
        body.rotationPointY = 22.0F;

        float landProgress = entity instanceof LanternfishEntity ? ((LanternfishEntity) entity).getLandProgress(0.0F) : 0.0F;
        float fishPitch = entity instanceof LanternfishEntity ? ((LanternfishEntity) entity).getFishPitch(0.0F) : 0.0F;
        float swim = MathHelper.sin(limbSwing * 0.9F) * limbSwingAmount;

        body.rotateAngleZ = -landProgress * ((float) Math.PI / 2.0F);
        body.rotateAngleX += fishPitch;
        body.rotationPointY += MathHelper.sin(ageInTicks * 0.1F) * 0.3F;
        tail.rotateAngleY = swim * 0.8F;
        rightFin.rotateAngleY = -0.4F - swim;
        leftFin.rotateAngleY = 0.4F + swim;
    }
}
