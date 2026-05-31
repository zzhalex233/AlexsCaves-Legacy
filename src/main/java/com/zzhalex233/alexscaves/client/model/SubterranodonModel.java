package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.SubterranodonEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SubterranodonModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer chest;
    private final ModelRenderer neck;
    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer leftWing;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWingTip;
    private final ModelRenderer rightWingTip;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer tail;
    private final ModelRenderer tailTip;

    public SubterranodonModel() {
        textureWidth = 128;
        textureHeight = 128;
        body = box(0, 0, -5.0F, -5.0F, -7.0F, 10, 10, 16, false);
        body.setRotationPoint(0.0F, 15.0F, 0.0F);
        chest = box(0, 26, -4.0F, -4.0F, -7.0F, 8, 8, 9, false);
        chest.setRotationPoint(0.0F, -1.0F, -6.0F);
        body.addChild(chest);
        neck = box(34, 0, -2.5F, -2.5F, -8.0F, 5, 5, 10, false);
        neck.setRotationPoint(0.0F, -1.0F, -6.5F);
        chest.addChild(neck);
        head = box(60, 0, -3.0F, -3.0F, -8.0F, 6, 6, 8, false);
        head.setRotationPoint(0.0F, 0.0F, -7.5F);
        head.setTextureOffset(58, 15).addBox(-2.0F, -1.0F, -15.0F, 4, 3, 8);
        neck.addChild(head);
        jaw = box(60, 27, -2.0F, 0.0F, -8.0F, 4, 2, 8, false);
        jaw.setRotationPoint(0.0F, 1.8F, -7.0F);
        head.addChild(jaw);
        leftWing = box(0, 48, 0.0F, -1.0F, -2.0F, 22, 2, 6, false);
        leftWing.setRotationPoint(4.5F, -1.0F, -3.5F);
        chest.addChild(leftWing);
        leftWingTip = box(0, 58, 0.0F, -0.5F, -1.5F, 25, 1, 5, false);
        leftWingTip.setRotationPoint(21.0F, 0.0F, 0.0F);
        leftWing.addChild(leftWingTip);
        rightWing = box(0, 48, -22.0F, -1.0F, -2.0F, 22, 2, 6, true);
        rightWing.setRotationPoint(-4.5F, -1.0F, -3.5F);
        chest.addChild(rightWing);
        rightWingTip = box(0, 58, -25.0F, -0.5F, -1.5F, 25, 1, 5, true);
        rightWingTip.setRotationPoint(-21.0F, 0.0F, 0.0F);
        rightWing.addChild(rightWingTip);
        leftLeg = box(88, 0, -1.5F, 0.0F, -1.5F, 3, 8, 3, false);
        leftLeg.setRotationPoint(3.0F, 4.0F, 5.0F);
        body.addChild(leftLeg);
        rightLeg = box(88, 0, -1.5F, 0.0F, -1.5F, 3, 8, 3, true);
        rightLeg.setRotationPoint(-3.0F, 4.0F, 5.0F);
        body.addChild(rightLeg);
        tail = box(34, 18, -2.5F, -2.5F, 0.0F, 5, 5, 12, false);
        tail.setRotationPoint(0.0F, -0.5F, 8.0F);
        body.addChild(tail);
        tailTip = box(34, 36, -1.5F, -1.5F, 0.0F, 3, 3, 10, false);
        tailTip.setRotationPoint(0.0F, 0.0F, 11.0F);
        tail.addChild(tailTip);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        SubterranodonEntity subterranodon = entity instanceof SubterranodonEntity ? (SubterranodonEntity) entity : null;
        float partial = subterranodon == null ? 0.0F : MathHelper.clamp(ageInTicks - subterranodon.ticksExisted, 0.0F, 1.0F);
        float fly = subterranodon == null ? 0.0F : subterranodon.getFlyProgress(partial);
        float hover = subterranodon == null ? 0.0F : subterranodon.getHoverProgress(partial);
        float flap = MathHelper.sin(ageInTicks * (0.55F + hover * 0.25F));
        body.rotateAngleX = -fly * 0.25F + (subterranodon == null ? 0.0F : subterranodon.getFlightPitch(partial) * 0.017453292F * 0.2F);
        body.rotateAngleZ = subterranodon == null ? 0.0F : subterranodon.getFlightRoll(partial) * 0.017453292F * 0.35F;
        body.rotationPointY += fly * -3.0F + hover * MathHelper.sin(ageInTicks * 0.15F);
        neck.rotateAngleX = headPitch * 0.017453292F * 0.35F - fly * 0.15F;
        head.rotateAngleX = headPitch * 0.017453292F * 0.35F;
        neck.rotateAngleY = netHeadYaw * 0.017453292F * 0.35F;
        head.rotateAngleY = netHeadYaw * 0.017453292F * 0.25F;
        leftWing.rotateAngleZ = -0.35F - fly * 0.45F - flap * fly * 0.45F;
        rightWing.rotateAngleZ = 0.35F + fly * 0.45F + flap * fly * 0.45F;
        leftWingTip.rotateAngleZ = -0.2F - fly * 0.5F - flap * fly * 0.35F;
        rightWingTip.rotateAngleZ = 0.2F + fly * 0.5F + flap * fly * 0.35F;
        tail.rotateAngleY = MathHelper.cos(ageInTicks * 0.1F) * 0.08F + MathHelper.cos(limbSwing * 0.6F) * limbSwingAmount * 0.2F;
        tailTip.rotateAngleY = MathHelper.cos(ageInTicks * 0.1F + 1.0F) * 0.1F + MathHelper.cos(limbSwing * 0.6F + 0.5F) * limbSwingAmount * 0.25F;
        leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.7F) * limbSwingAmount * 0.8F + fly * 0.75F;
        rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.7F + (float) Math.PI) * limbSwingAmount * 0.8F + fly * 0.75F;
        if (subterranodon != null && subterranodon.getAttackTicks() > 0) {
            float attack = MathHelper.sin((15 - subterranodon.getAttackTicks() + partial) * 0.3F);
            neck.rotateAngleX -= attack * 0.35F;
            jaw.rotateAngleX += 0.45F;
        }
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, 15.0F, 0.0F);
        reset(body);
        reset(chest);
        reset(neck);
        reset(head);
        reset(jaw);
        reset(leftWing);
        reset(rightWing);
        reset(leftWingTip);
        reset(rightWingTip);
        reset(leftLeg);
        reset(rightLeg);
        reset(tail);
        reset(tailTip);
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
