package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.entity.living.GumWormSegmentEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class GumWormSegmentModel extends ModelBase {
    private final ModelRenderer main;
    private final ModelRenderer segment;
    private final ModelRenderer gumBack;
    private final ModelRenderer gumFront;

    public GumWormSegmentModel() {
        textureWidth = 128;
        textureHeight = 128;
        main = new ModelRenderer(this);
        main.setRotationPoint(0.0F, 24.0F, 0.0F);
        segment = box(0, 0, -16.0F, -16.0F, -16.0F, 32, 32, 32, false);
        segment.setRotationPoint(0.0F, -16.0F, 0.0F);
        main.addChild(segment);
        gumBack = box(0, 64, -16.0F, -16.0F, 0.0F, 32, 32, 0, false);
        gumBack.setRotationPoint(0.0F, 0.0F, 16.5F);
        segment.addChild(gumBack);
        gumFront = box(64, 64, -16.0F, -16.0F, 0.0F, 32, 32, 0, false);
        gumFront.setRotationPoint(0.0F, 0.0F, -16.5F);
        segment.addChild(gumFront);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        main.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        reset(segment);
        GumWormSegmentEntity segmentEntity = entity instanceof GumWormSegmentEntity ? (GumWormSegmentEntity) entity : null;
        float partial = segmentEntity == null ? 0.0F : MathHelper.clamp(ageInTicks - segmentEntity.ticksExisted, 0.0F, 1.0F);
        segment.rotateAngleZ += segmentEntity == null ? 0.0F : segmentEntity.getBodyZRot(partial) * 0.017453292F;
        gumFront.showModel = segmentEntity == null || segmentEntity.getFrontEntity() != null;
        gumBack.showModel = segmentEntity == null || segmentEntity.getBackEntity() != null;
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
