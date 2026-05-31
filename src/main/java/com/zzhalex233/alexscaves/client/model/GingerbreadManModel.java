package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.GingerbreadManEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GingerbreadManModel extends ModelBase {
    private final ModelRenderer main;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;

    public GingerbreadManModel() {
        textureWidth = 32;
        textureHeight = 32;
        main = new ModelRenderer(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);
        body = box(14, 14, -2.5F, -2.5F, -1.0F, 5, 5, 2, false);
        body.setRotationPoint(0.0F, -6.5F, -1.0F);
        body.setTextureOffset(0, 16).addBox(-2.5F, -2.5F, -1.0F, 5, 5, 2);
        main.addChild(body);
        head = box(0, 8, -3.0F, -6.0F, -1.0F, 6, 6, 2, false);
        head.setRotationPoint(0.0F, -2.5F, 0.0F);
        head.setTextureOffset(0, 23).addBox(3.0F, -5.0F, -1.0F, 2, 4, 2);
        head.setTextureOffset(0, 23).addBox(-5.0F, -5.0F, -1.0F, 2, 4, 2);
        head.setTextureOffset(13, 28).addBox(-5.0F, -5.0F, -1.0F, 2, 2, 2);
        head.setTextureOffset(13, 28).addBox(3.0F, -5.0F, -1.0F, 2, 2, 2);
        head.setTextureOffset(0, 0).addBox(-3.0F, -6.0F, -1.0F, 6, 6, 2);
        body.addChild(head);
        leftLeg = box(20, 21, -1.5F, 1.5F, -1.0F, 2, 5, 2, false);
        leftLeg.setRotationPoint(1.0F, -0.5F, 0.0F);
        leftLeg.setTextureOffset(12, 21).addBox(-1.5F, 1.5F, -1.0F, 2, 5, 2);
        body.addChild(leftLeg);
        rightLeg = box(20, 21, -0.5F, 1.5F, -1.0F, 2, 5, 2, true);
        rightLeg.setRotationPoint(-1.0F, -0.5F, 0.0F);
        rightLeg.setTextureOffset(12, 21).addBox(-0.5F, 1.5F, -1.0F, 2, 5, 2);
        body.addChild(rightLeg);
        rightArm = box(14, 6, -3.75F, -1.0F, -1.0F, 4, 2, 2, true);
        rightArm.setRotationPoint(-2.75F, -1.5F, 0.0F);
        rightArm.setTextureOffset(16, 0).addBox(-3.75F, -1.0F, -1.0F, 4, 2, 2);
        body.addChild(rightArm);
        leftArm = box(14, 6, -0.25F, -1.0F, -1.0F, 4, 2, 2, false);
        leftArm.setRotationPoint(2.75F, -1.5F, 0.0F);
        leftArm.setTextureOffset(16, 0).addBox(-0.25F, -1.0F, -1.0F, 4, 2, 2);
        body.addChild(leftArm);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        main.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        GingerbreadManEntity gingerbread = entity instanceof GingerbreadManEntity ? (GingerbreadManEntity) entity : null;
        float partial = gingerbread == null ? 0.0F : MathHelper.clamp(ageInTicks - gingerbread.ticksExisted, 0.0F, 1.0F);
        leftArm.showModel = gingerbread == null || !gingerbread.hasLostLimb(true, true);
        rightArm.showModel = gingerbread == null || !gingerbread.hasLostLimb(false, true);
        leftLeg.showModel = gingerbread == null || !gingerbread.hasLostLimb(true, false);
        rightLeg.showModel = gingerbread == null || !gingerbread.hasLostLimb(false, false);
        float sit = gingerbread == null ? 0.0F : gingerbread.getSitProgress(partial);
        float dance = gingerbread == null ? 0.0F : gingerbread.getDanceProgress(partial);
        float carry = gingerbread == null ? 0.0F : gingerbread.getCarryItemProgress(partial);
        body.rotateAngleY += MathHelper.cos(limbSwing * 0.6F) * limbSwingAmount * 0.2F;
        head.rotateAngleY += netHeadYaw * 0.017453292F + MathHelper.cos(limbSwing * 0.6F) * limbSwingAmount * 0.2F;
        head.rotateAngleX += headPitch * 0.017453292F + MathHelper.sin(limbSwing * 0.6F) * limbSwingAmount * 0.2F;
        leftLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.6F) * limbSwingAmount;
        rightLeg.rotateAngleX += MathHelper.cos(limbSwing * 0.6F + (float) Math.PI) * limbSwingAmount;
        leftArm.rotateAngleY += MathHelper.cos(limbSwing * 0.6F + 1.0F) * limbSwingAmount;
        rightArm.rotateAngleY += MathHelper.cos(limbSwing * 0.6F + 1.0F) * limbSwingAmount;
        leftArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.1F) * 0.1F;
        rightArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.1F) * 0.1F;
        leftLeg.rotateAngleX += sit * -1.35F;
        rightLeg.rotateAngleX += sit * -1.35F;
        body.rotationPointY += sit * 4.0F - Math.abs(MathHelper.cos(limbSwing * 0.6F - 0.5F) * 4.0F * limbSwingAmount);
        head.rotateAngleX -= carry * 0.35F;
        rightArm.rotateAngleY -= carry * 1.75F;
        leftArm.rotateAngleY += carry * 1.75F;
        leftArm.rotateAngleZ += MathHelper.sin(ageInTicks) * dance * 0.7F + dance * 0.3F;
        rightArm.rotateAngleZ -= MathHelper.sin(ageInTicks) * dance * 0.7F + dance * 0.3F;
        head.rotateAngleX += MathHelper.cos(ageInTicks - 2.0F) * dance * 0.5F;
        head.rotateAngleY += MathHelper.cos(ageInTicks - 1.0F) * dance * 0.5F;
        body.rotateAngleY += MathHelper.cos(ageInTicks - 3.0F) * dance * 0.15F;
        if (gingerbread != null) {
            animate(gingerbread, ageInTicks, partial);
        }
    }

    private void animate(GingerbreadManEntity gingerbread, float ageInTicks, float partial) {
        int animation = gingerbread.getAnimation();
        float tick = gingerbread.getAnimationTick() + partial;
        if (animation == GingerbreadManEntity.ANIMATION_IDLE_WAVE_LEFT) {
            leftArm.rotateAngleZ += MathHelper.sin(tick * 0.45F) * 0.5F + 0.45F;
            head.rotateAngleY += MathHelper.sin(tick * 0.2F) * 0.5F;
        } else if (animation == GingerbreadManEntity.ANIMATION_IDLE_WAVE_RIGHT) {
            rightArm.rotateAngleZ -= MathHelper.sin(tick * 0.45F) * 0.5F + 0.45F;
            head.rotateAngleY -= MathHelper.sin(tick * 0.2F) * 0.5F;
        } else if (animation == GingerbreadManEntity.ANIMATION_IDLE_FALL_OVER) {
            float fall = MathHelper.clamp(tick / 5.0F, 0.0F, 1.0F);
            body.rotateAngleX -= fall * 1.5707964F;
            body.rotationPointY += fall * 5.0F;
            body.rotationPointZ += fall * 7.0F;
            leftArm.rotateAngleY += 0.5F;
            rightArm.rotateAngleY -= 0.5F;
        } else if (animation == GingerbreadManEntity.ANIMATION_IDLE_JUMP) {
            float jump = MathHelper.sin(MathHelper.clamp(tick / 20.0F, 0.0F, 1.0F) * (float) Math.PI);
            body.rotationPointY -= jump * 3.0F;
            leftArm.rotateAngleZ += jump * 0.7F;
            rightArm.rotateAngleZ -= jump * 0.7F;
        } else if (animation == GingerbreadManEntity.ANIMATION_SWING_RIGHT) {
            float swing = MathHelper.sin(MathHelper.clamp(tick / 12.0F, 0.0F, 1.0F) * (float) Math.PI);
            body.rotateAngleY += swing * 0.5F;
            rightArm.rotateAngleX -= swing * 0.9F;
            rightArm.rotateAngleZ -= swing * 0.8F;
        } else if (animation == GingerbreadManEntity.ANIMATION_SWING_LEFT) {
            float swing = MathHelper.sin(MathHelper.clamp(tick / 12.0F, 0.0F, 1.0F) * (float) Math.PI);
            body.rotateAngleY -= swing * 0.5F;
            leftArm.rotateAngleX -= swing * 0.9F;
            leftArm.rotateAngleZ += swing * 0.8F;
        }
    }

    public void translateToHand(boolean left) {
        body.postRender(0.0625F);
        (left ? leftArm : rightArm).postRender(0.0625F);
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, -6.5F, -1.0F);
        reset(body);
        reset(head);
        reset(leftLeg);
        reset(rightLeg);
        reset(leftArm);
        reset(rightArm);
        leftLeg.rotateAngleZ = -0.3927F;
        rightLeg.rotateAngleZ = 0.3927F;
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
