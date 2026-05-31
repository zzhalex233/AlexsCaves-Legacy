package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.GrottoceratopsEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GrottoceratopsModel extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer bodySpikes;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer tail;
    private final ModelRenderer tail2;
    private final ModelRenderer neck;
    private final ModelRenderer head;
    private final ModelRenderer jaw;

    public GrottoceratopsModel() {
        textureWidth = 256;
        textureHeight = 256;

        body = part(0, 0, -11.0F, -14.0F, -17.5F, 22, 30, 35, false);
        body.setRotationPoint(0.0F, -3.0F, 0.0F);
        bodySpikes = plate(0, 22, 0.0F, -15.5F, -21.5F, 0, 31, 43, false);
        bodySpikes.setRotationPoint(0.0F, -2.5F, 0.0F);
        body.addChild(bodySpikes);

        rightLeg = part(117, 17, -5.0F, -2.0F, -8.0F, 9, 15, 12, true);
        rightLeg.setRotationPoint(-11.0F, 5.0F, 9.5F);
        body.addChild(rightLeg);
        leftLeg = part(117, 17, -4.0F, -2.0F, -8.0F, 9, 15, 12, false);
        leftLeg.setRotationPoint(11.0F, 5.0F, 9.5F);
        body.addChild(leftLeg);

        rightArm = part(0, 0, -4.0F, -2.0F, -4.5F, 6, 25, 9, true);
        rightArm.setRotationPoint(-10.0F, 4.0F, -12.0F);
        body.addChild(rightArm);
        leftArm = part(0, 0, -2.0F, -2.0F, -4.5F, 6, 25, 9, false);
        leftArm.setRotationPoint(10.0F, 4.0F, -12.0F);
        body.addChild(leftArm);

        tail = part(65, 75, -6.0F, -5.0F, -1.0F, 12, 12, 21, false);
        tail.setRotationPoint(0.0F, -1.0F, 16.5F);
        body.addChild(tail);
        tail2 = new ModelRenderer(this);
        tail2.setRotationPoint(0.0F, 0.0F, 19.0F);
        tail2.setTextureOffset(95, 46).addBox(-3.0F, -3.0F, -1.0F, 6, 7, 19, 0.0F);
        tail2.setTextureOffset(26, 129).addBox(-10.0F, -3.0F, 8.0F, 7, 3, 3, 0.0F);
        tail2.setTextureOffset(26, 129).addBox(3.0F, -3.0F, 8.0F, 7, 3, 3, 0.0F);
        tail.addChild(tail2);

        neck = part(100, 129, -5.0F, -7.0F, -16.0F, 10, 14, 20, false);
        neck.setRotationPoint(0.0F, 5.0F, -16.5F);
        body.addChild(neck);
        head = new ModelRenderer(this);
        head.setRotationPoint(0.0F, -6.0F, -14.0F);
        head.setTextureOffset(110, 72).addBox(-8.0F, -17.0F, -17.0F, 16, 12, 8, 0.0F);
        head.setTextureOffset(46, 108).addBox(-7.0F, -5.0F, -7.0F, 14, 15, 10, 0.0F);
        head.setTextureOffset(94, 108).addBox(-11.0F, -17.0F, 0.0F, 22, 18, 3, 0.0F);
        head.setTextureOffset(0, 96).addBox(-14.0F, -20.0F, 0.0F, 28, 21, 0, 0.0F);
        head.setTextureOffset(0, 129).addBox(-4.0F, -5.0F, -17.0F, 8, 7, 10, 0.0F);
        neck.addChild(head);
        jaw = part(65, 140, -4.0F, 0.0F, -7.0F, 8, 8, 6, false);
        jaw.setRotationPoint(0.0F, 2.0F, -6.0F);
        head.addChild(jaw);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        GrottoceratopsEntity grotto = entity instanceof GrottoceratopsEntity ? (GrottoceratopsEntity) entity : null;
        float partialTicks = grotto == null ? 0.0F : MathHelper.clamp(ageInTicks - grotto.ticksExisted, 0.0F, 1.0F);
        float chew = grotto == null ? 0.0F : grotto.getChewProgress(partialTicks);
        float tailSwing = grotto == null ? 0.0F : grotto.getTailSwingRot(partialTicks) * 0.017453292F;
        float walk = limbSwing * 0.55F;
        float amount = limbSwingAmount;
        body.rotationPointY += Math.abs(MathHelper.cos(walk) * amount) * 2.0F;
        rightLeg.rotateAngleX = MathHelper.cos(walk) * amount * 0.55F;
        leftArm.rotateAngleX = MathHelper.cos(walk) * amount * 0.55F;
        leftLeg.rotateAngleX = MathHelper.cos(walk + (float) Math.PI) * amount * 0.55F;
        rightArm.rotateAngleX = MathHelper.cos(walk + (float) Math.PI) * amount * 0.55F;
        neck.rotateAngleX += headPitch * 0.017453292F * 0.35F + chew * 0.35F;
        head.rotateAngleY += netHeadYaw * 0.017453292F * 0.35F;
        jaw.rotateAngleX += chew * 0.7F;
        tail.rotateAngleY += MathHelper.cos(walk + 1.0F) * amount * 0.25F + tailSwing * 0.6F;
        tail2.rotateAngleY += MathHelper.cos(walk + 2.0F) * amount * 0.25F + tailSwing * 0.4F;
    }

    private void resetPose() {
        body.setRotationPoint(0.0F, -3.0F, 0.0F);
        body.rotateAngleX = body.rotateAngleY = body.rotateAngleZ = 0.0F;
        reset(rightLeg, -11.0F, 5.0F, 9.5F);
        reset(leftLeg, 11.0F, 5.0F, 9.5F);
        reset(rightArm, -10.0F, 4.0F, -12.0F);
        reset(leftArm, 10.0F, 4.0F, -12.0F);
        reset(tail, 0.0F, -1.0F, 16.5F);
        reset(tail2, 0.0F, 0.0F, 19.0F);
        reset(neck, 0.0F, 5.0F, -16.5F);
        reset(head, 0.0F, -6.0F, -14.0F);
        reset(jaw, 0.0F, 2.0F, -6.0F);
    }

    private void reset(ModelRenderer renderer, float x, float y, float z) {
        renderer.setRotationPoint(x, y, z);
        renderer.rotateAngleX = renderer.rotateAngleY = renderer.rotateAngleZ = 0.0F;
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
