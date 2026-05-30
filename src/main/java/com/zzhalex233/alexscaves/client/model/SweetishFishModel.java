package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.SweetishFishEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SweetishFishModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer bodySeg1;
    private final ModelRenderer bodySeg2;
    private final ModelRenderer tail;

    public SweetishFishModel() {
        textureWidth = 32;
        textureHeight = 32;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, -2.0F);

        bodySeg1 = new ModelRenderer(this);
        bodySeg1.setRotationPoint(0.0F, -2.5F, 0.0F);
        root.addChild(bodySeg1);
        bodySeg1.setTextureOffset(0, 0).addBox(-1.5F, -2.5F, -6.0F, 3, 5, 6, 0.0F);
        bodySeg1.setTextureOffset(0, 0).addBox(0.0F, -4.5F, -2.0F, 0, 2, 2, 0.0F);

        bodySeg2 = new ModelRenderer(this);
        bodySeg2.setRotationPoint(0.0F, 0.0F, 0.0F);
        bodySeg1.addChild(bodySeg2);
        bodySeg2.setTextureOffset(12, 6).addBox(0.0F, -4.5F, 0.0F, 0, 2, 5, 0.0F);
        bodySeg2.setTextureOffset(0, 11).addBox(-1.5F, -2.5F, 0.0F, 3, 5, 6, 0.0F);

        tail = new ModelRenderer(this);
        tail.setRotationPoint(0.0F, 0.0F, 6.0F);
        bodySeg2.addChild(tail);
        tail.mirror = true;
        tail.setTextureOffset(0, 18).addBox(0.0F, -3.5F, 0.0F, 0, 7, 4, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        root.rotateAngleX = 0.0F;
        root.rotateAngleY = 0.0F;
        root.rotateAngleZ = 0.0F;
        root.rotationPointY = 24.0F;
        bodySeg1.rotateAngleX = 0.0F;
        bodySeg1.rotateAngleY = 0.0F;
        bodySeg1.rotateAngleZ = 0.0F;
        bodySeg1.rotationPointY = -2.5F;
        bodySeg2.rotateAngleX = 0.0F;
        bodySeg2.rotateAngleY = 0.0F;
        bodySeg2.rotateAngleZ = 0.0F;
        tail.rotateAngleX = 0.0F;
        tail.rotateAngleY = 0.0F;
        tail.rotateAngleZ = 0.0F;

        SweetishFishEntity fish = entity instanceof SweetishFishEntity ? (SweetishFishEntity) entity : null;
        float partialTicks = fish == null ? 0.0F : MathHelper.clamp(ageInTicks - fish.ticksExisted, 0.0F, 1.0F);
        float landProgress = fish == null ? 0.0F : fish.getLandProgress(partialTicks);
        float swimProgress = 1.0F - landProgress;
        float fishPitch = fish == null ? 0.0F : fish.getFishPitch(partialTicks) * swimProgress;
        float swim = MathHelper.sin(limbSwing * 0.8F) * limbSwingAmount * swimProgress;
        float flop = MathHelper.sin(ageInTicks * 0.7F) * landProgress;

        bodySeg1.rotateAngleZ = landProgress * (float) Math.toRadians(-85.0D);
        bodySeg1.rotateAngleX += fishPitch * 0.9F;
        bodySeg1.rotateAngleY += swim * 0.25F + flop * 0.15F;
        bodySeg2.rotateAngleY += -swim * 0.45F - flop * 0.25F;
        tail.rotateAngleY += swim * 0.85F + flop * 0.5F;
        bodySeg1.rotationPointY += MathHelper.sin(ageInTicks * 0.35F) * landProgress;
    }
}
