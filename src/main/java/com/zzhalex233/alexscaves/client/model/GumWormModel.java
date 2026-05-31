package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.GumWormEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GumWormModel extends ModelBase {
    private final ModelRenderer main;
    private final ModelRenderer head;
    private final ModelRenderer bottomJaw;
    private final ModelRenderer topJaw;
    private final ModelRenderer leftEye;
    private final ModelRenderer rightEye;
    private final ModelRenderer topEye;
    private final ModelRenderer bottomEye;
    private final ModelRenderer gumStrand1;
    private final ModelRenderer gumStrand2;
    private final ModelRenderer gumStrand3;

    public GumWormModel() {
        textureWidth = 256;
        textureHeight = 256;
        main = new ModelRenderer(this);
        main.setRotationPoint(0.0F, 24.0F, -6.0F);
        head = box(116, 67, -16.5F, -16.5F, -22.25F, 33, 33, 22, false);
        head.setRotationPoint(0.0F, -16.5F, 35.25F);
        head.setTextureOffset(17, 220).addBox(-16.5F, -16.5F, -22.25F, 33, 33, 3);
        main.addChild(head);
        leftEye = box(0, 0, -1.5F, -6.0F, -6.0F, 3, 12, 12, false);
        leftEye.setRotationPoint(18.0F, 0.0F, -10.75F);
        head.addChild(leftEye);
        rightEye = box(0, 0, -1.5F, -6.0F, -6.0F, 3, 12, 12, true);
        rightEye.setRotationPoint(-18.0F, 0.0F, -10.75F);
        head.addChild(rightEye);
        topEye = box(0, 0, -1.5F, -6.0F, -6.0F, 3, 12, 12, true);
        topEye.setRotationPoint(0.0F, -18.0F, -11.75F);
        topEye.rotateAngleZ = 1.5708F;
        head.addChild(topEye);
        bottomEye = box(0, 0, -1.5F, -6.0F, -6.0F, 3, 12, 12, true);
        bottomEye.setRotationPoint(0.0F, 18.0F, -11.75F);
        bottomEye.rotateAngleZ = -1.5708F;
        head.addChild(bottomEye);
        gumStrand1 = plate(218, 103, 0.0F, -18.5F, -9.5F, 0, 37, 19, false);
        gumStrand1.setRotationPoint(9.0F, 1.0F, -35.75F);
        head.addChild(gumStrand1);
        gumStrand2 = plate(218, 103, 0.0F, -18.5F, -9.5F, 0, 37, 19, false);
        gumStrand2.setRotationPoint(-9.0F, 1.0F, -35.75F);
        head.addChild(gumStrand2);
        gumStrand3 = plate(218, 103, 0.0F, -18.5F, -9.5F, 0, 37, 19, true);
        gumStrand3.setRotationPoint(0.0F, 1.0F, -35.75F);
        gumStrand3.rotateAngleY = 0.7854F;
        head.addChild(gumStrand3);
        bottomJaw = box(102, 9, -16.5F, 0.0F, -36.25F, 33, 10, 36, false);
        bottomJaw.setRotationPoint(0.0F, 6.5F, -22.0F);
        bottomJaw.setTextureOffset(0, 43).addBox(-16.5F, -10.0F, -36.25F, 33, 10, 36);
        bottomJaw.setTextureOffset(118, 210).addBox(-16.5F, 0.0F, -36.25F, 33, 10, 36);
        head.addChild(bottomJaw);
        topJaw = box(118, 165, -16.5F, -10.0F, -36.0F, 33, 10, 36, false);
        topJaw.setRotationPoint(0.0F, -6.5F, -22.25F);
        topJaw.setTextureOffset(0, 144).addBox(-16.5F, 0.0F, -36.0F, 33, 10, 36);
        topJaw.setTextureOffset(0, 89).addBox(-16.5F, -10.0F, -36.0F, 33, 10, 36);
        head.addChild(topJaw);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        main.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        resetPose();
        GumWormEntity worm = entity instanceof GumWormEntity ? (GumWormEntity) entity : null;
        float partial = worm == null ? 0.0F : MathHelper.clamp(ageInTicks - worm.ticksExisted, 0.0F, 1.0F);
        float mouth = worm == null ? 0.0F : worm.getMouthOpenProgress(partial);
        bottomJaw.rotateAngleX += 0.8F * mouth + MathHelper.sin(ageInTicks * 0.2F) * 0.1F * mouth;
        topJaw.rotateAngleX -= 0.8F * mouth + MathHelper.sin(ageInTicks * 0.2F) * 0.1F * mouth;
        head.rotateAngleZ += worm == null ? 0.0F : worm.getBodyZRot(partial) * 0.017453292F;
        float strand = 0.45F + mouth + Math.max(MathHelper.sin(ageInTicks * 0.2F) * 0.15F * mouth, 0.0F);
        gumStrand1.rotationPointY = 1.0F + strand * 4.0F;
        gumStrand2.rotationPointY = 1.0F + strand * 4.0F;
        gumStrand3.rotationPointY = 1.0F + strand * 4.0F;
    }

    private void resetPose() {
        reset(main);
        reset(head);
        reset(bottomJaw);
        reset(topJaw);
        reset(leftEye);
        reset(rightEye);
        reset(topEye);
        reset(bottomEye);
        reset(gumStrand1);
        reset(gumStrand2);
        reset(gumStrand3);
        topEye.rotateAngleZ = 1.5708F;
        bottomEye.rotateAngleZ = -1.5708F;
        gumStrand1.setRotationPoint(9.0F, 1.0F, -35.75F);
        gumStrand2.setRotationPoint(-9.0F, 1.0F, -35.75F);
        gumStrand3.setRotationPoint(0.0F, 1.0F, -35.75F);
        gumStrand3.rotateAngleY = 0.7854F;
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

    private ModelRenderer plate(int textureX, int textureY, float x, float y, float z, int width, int height, int depth, boolean mirror) {
        return box(textureX, textureY, x, y, z, width, height, depth, mirror);
    }
}
