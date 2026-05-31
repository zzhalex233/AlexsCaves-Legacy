package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.GumbeeperEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GumbeeperModel extends ModelBase {
    private final ModelRenderer root;
    private final ModelRenderer body;
    private final ModelRenderer coinWheel;
    private final ModelRenderer rightBackLeg;
    private final ModelRenderer leftBackLeg;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer head;
    private final ModelRenderer gumLayer;
    private final ModelRenderer gumLayer2;
    private final ModelRenderer gumLayer3;
    private final ModelRenderer gumLayer4;
    private final ModelRenderer gumLayer5;
    private final ModelRenderer gumLayer6;
    private final ModelRenderer gumLayerFinal;

    public GumbeeperModel() {
        textureWidth = 128;
        textureHeight = 128;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new ModelRenderer(this);
        body.setRotationPoint(1.0F, -5.0F, 0.0F);
        body.setTextureOffset(16, 49).addBox(-5.0F, -7.0F, -4.0F, 8, 9, 8, 0.25F);
        body.setTextureOffset(40, 40).addBox(-5.0F, -7.0F, -4.0F, 8, 9, 8, 0.0F);
        root.addChild(body);

        coinWheel = new ModelRenderer(this);
        coinWheel.setRotationPoint(-1.0F, -4.0F, -5.0F);
        coinWheel.setTextureOffset(0, 0).addBox(0.0F, -2.0F, -1.0F, 0, 4, 2, 0.0F);
        body.addChild(coinWheel);

        rightBackLeg = part(52, 26, -3.0F, -1.0F, -1.0F, 4, 5, 4, true);
        rightBackLeg.setRotationPoint(-4.0F, 1.0F, 3.0F);
        body.addChild(rightBackLeg);

        leftBackLeg = part(52, 26, -1.0F, -1.0F, -1.0F, 4, 5, 4, false);
        leftBackLeg.setRotationPoint(2.0F, 1.0F, 3.0F);
        body.addChild(leftBackLeg);

        rightFrontLeg = part(36, 26, -3.0F, -1.0F, -3.0F, 4, 5, 4, true);
        rightFrontLeg.setRotationPoint(-4.0F, 1.0F, -3.0F);
        body.addChild(rightFrontLeg);

        leftFrontLeg = part(36, 26, -1.0F, -1.0F, -3.0F, 4, 5, 4, false);
        leftFrontLeg.setRotationPoint(2.0F, 1.0F, -3.0F);
        body.addChild(leftFrontLeg);

        head = new ModelRenderer(this);
        head.setRotationPoint(-1.0F, -7.0F, 0.0F);
        head.setTextureOffset(0, 24).addBox(-6.0F, -14.0F, -6.0F, 12, 12, 12, 0.0F);
        head.setTextureOffset(36, 12).addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12, 0.0F);
        head.setTextureOffset(36, 0).addBox(-4.0F, -17.0F, -4.0F, 8, 3, 8, 0.0F);
        body.addChild(head);

        gumLayerFinal = gum(2, 7, 0.0F, 0.25F, -5.0F, 11, 6, 11);
        gumLayer6 = gum(2, 6, 0.0F, -0.75F, -5.0F, 11, 1, 11);
        gumLayer5 = gum(2, 5, 0.0F, -1.75F, -5.0F, 11, 1, 11);
        gumLayer4 = gum(2, 4, 0.0F, -2.75F, -5.0F, 11, 1, 11);
        gumLayer3 = gum(2, 3, 0.0F, -3.75F, -5.0F, 11, 1, 11);
        gumLayer2 = gum(2, 2, 0.0F, -4.75F, -5.0F, 11, 1, 11);
        gumLayer = gum(2, 1, 0.0F, -5.75F, -5.0F, 11, 1, 11);
        head.addChild(gumLayerFinal);
        head.addChild(gumLayer6);
        head.addChild(gumLayer5);
        head.addChild(gumLayer4);
        head.addChild(gumLayer3);
        head.addChild(gumLayer2);
        head.addChild(gumLayer);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        root.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        GumbeeperEntity gumbeeper = entity instanceof GumbeeperEntity ? (GumbeeperEntity) entity : null;
        float partialTick = gumbeeper == null ? 0.0F : MathHelper.clamp(ageInTicks - gumbeeper.ticksExisted, 0.0F, 1.0F);
        float shootProgress = gumbeeper == null ? 0.0F : gumbeeper.getShootProgress(partialTick);
        float explodeProgress = gumbeeper == null ? 0.0F : gumbeeper.getExplodeProgress(partialTick);
        int gumballs = gumbeeper == null ? 6 : gumbeeper.getGumballsLeft();

        gumLayer6.showModel = gumballs > 0;
        gumLayer5.showModel = gumballs > 1;
        gumLayer4.showModel = gumballs > 2;
        gumLayer3.showModel = gumballs > 3;
        gumLayer2.showModel = gumballs > 4;
        gumLayer.showModel = gumballs > 5;

        float walkSpeed = 0.8F;
        float walkAmount = limbSwingAmount;
        float bodyBob = Math.abs(MathHelper.sin(limbSwing * walkSpeed) * walkAmount * 2.0F);
        body.rotationPointY += bodyBob + explodeProgress * 1.5F - shootProgress;
        body.rotateAngleX -= shootProgress * 0.2618F;
        head.rotateAngleX += headPitch * 0.017453292F - shootProgress * 0.0873F;
        head.rotateAngleY += netHeadYaw * 0.017453292F;
        head.rotateAngleZ += MathHelper.sin(ageInTicks * 1.3F) * shootProgress * 0.1F;
        coinWheel.rotateAngleZ += gumbeeper == null ? 0.0F : (float) Math.toRadians(gumbeeper.getDialRot(partialTick));
        coinWheel.rotationPointZ += Math.max(0.0F, (gumbeeper == null ? 0.0F : (float) gumbeeper.getDialRot(partialTick)) - 400.0F) / 50.0F * 1.5F;

        rightFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walkAmount * 0.5F + shootProgress * 0.2618F;
        leftBackLeg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed) * walkAmount * 0.5F + shootProgress * 0.2618F;
        leftFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkAmount * 0.5F + shootProgress * 0.2618F;
        rightBackLeg.rotateAngleX = MathHelper.cos(limbSwing * walkSpeed + (float) Math.PI) * walkAmount * 0.5F + shootProgress * 0.2618F;
        body.rotateAngleZ += MathHelper.sin(ageInTicks * 3.0F) * explodeProgress * 0.2F;
    }

    private void resetPose() {
        body.setRotationPoint(1.0F, -5.0F, 0.0F);
        body.rotateAngleX = body.rotateAngleY = body.rotateAngleZ = 0.0F;
        coinWheel.setRotationPoint(-1.0F, -4.0F, -5.0F);
        coinWheel.rotateAngleX = coinWheel.rotateAngleY = coinWheel.rotateAngleZ = 0.0F;
        head.setRotationPoint(-1.0F, -7.0F, 0.0F);
        head.rotateAngleX = head.rotateAngleY = head.rotateAngleZ = 0.0F;
        resetLeg(rightBackLeg, -4.0F, 1.0F, 3.0F);
        resetLeg(leftBackLeg, 2.0F, 1.0F, 3.0F);
        resetLeg(rightFrontLeg, -4.0F, 1.0F, -3.0F);
        resetLeg(leftFrontLeg, 2.0F, 1.0F, -3.0F);
    }

    private void resetLeg(ModelRenderer leg, float x, float y, float z) {
        leg.setRotationPoint(x, y, z);
        leg.rotateAngleX = leg.rotateAngleY = leg.rotateAngleZ = 0.0F;
    }

    private ModelRenderer gum(int textureX, int textureY, float x, float y, float z, int width, int height, int depth) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.setRotationPoint(-0.5F, -7.75F, -0.5F);
        renderer.setTextureOffset(textureX, textureY).addBox(x - 5.0F, y, z, width, height, depth, 0.0F);
        return renderer;
    }

    private ModelRenderer part(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this);
        renderer.mirror = mirror;
        renderer.setTextureOffset(textureX, textureY).addBox(x, y, z, width, height, depth, 0.0F);
        return renderer;
    }
}
