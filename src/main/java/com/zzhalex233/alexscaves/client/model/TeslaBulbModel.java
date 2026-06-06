package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class TeslaBulbModel extends ModelBase {
    private final ModelRenderer bulb;
    private final ModelRenderer baseRing;
    private final ModelRenderer midRing;
    private final ModelRenderer topRing;
    private final ModelRenderer outer;
    private final ModelRenderer center;
    private float outerScale = 1.0F;

    public TeslaBulbModel() {
        textureWidth = 128;
        textureHeight = 128;
        bulb = new ModelRenderer(this);
        bulb.setRotationPoint(0.0F, 20.0F, 0.0F);
        baseRing = new ModelRenderer(this);
        baseRing.setRotationPoint(0.0F, 4.0F, 0.0F);
        baseRing.setTextureOffset(0, 32).addBox(-8.0F, 0.0F, -8.0F, 16, 0, 16);
        bulb.addChild(baseRing);
        midRing = new ModelRenderer(this);
        midRing.setRotationPoint(0.0F, -2.0F, 0.0F);
        midRing.setTextureOffset(0, 0).addBox(-8.0F, 0.0F, -8.0F, 16, 0, 16);
        baseRing.addChild(midRing);
        topRing = new ModelRenderer(this);
        topRing.setRotationPoint(0.0F, -2.0F, 0.0F);
        topRing.setTextureOffset(0, 16).addBox(-8.0F, 0.0F, -8.0F, 16, 0, 16);
        midRing.addChild(topRing);
        outer = new ModelRenderer(this);
        outer.setRotationPoint(0.0F, -8.0F, 0.0F);
        outer.setTextureOffset(40, 40).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
        topRing.addChild(outer);
        center = new ModelRenderer(this);
        center.setRotationPoint(0.0F, 0.0F, 0.0F);
        center.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2);
        outer.addChild(center);
    }

    public void setup(float explode, float ageInTicks) {
        reset();
        float intensity = 1.0F + explode;
        float zoom = (float) ((Math.sin(ageInTicks * 0.5F * intensity) + 1.0F) * 0.5F) * 0.1F + (float) Math.sin(explode * Math.PI);
        outerScale = 1.0F + zoom;
        bulb.rotationPointY += -1.0F + Math.sin(ageInTicks * 0.045F * intensity);
        baseRing.rotationPointY -= -1.0F + Math.sin(ageInTicks * 0.045F * intensity);
        baseRing.rotationPointY += -2.5F + Math.sin(ageInTicks * 0.045F * intensity + 1.0F) * 0.5F;
        midRing.rotationPointY += Math.sin(ageInTicks * 0.045F * intensity + 2.0F) * 0.25F;
        topRing.rotationPointY -= Math.sin(ageInTicks * 0.045F * intensity + 3.0F) * 0.15F;
        bulb.rotationPointY += Math.sin(ageInTicks * 0.045F) * 2.0F;
        center.rotateAngleX += ageInTicks * 0.1F * intensity;
        center.rotateAngleY += ageInTicks * 0.2F * intensity;
        outer.rotateAngleX -= ageInTicks * 0.1F * intensity;
        outer.rotateAngleY -= ageInTicks * 0.2F * intensity;
        baseRing.rotateAngleY += ageInTicks * 0.1F * intensity;
        midRing.rotateAngleY += ageInTicks * 0.1F * intensity;
        topRing.rotateAngleY += ageInTicks * 0.1F * intensity;
    }

    public void render(float scale) {
        bulb.render(scale);
    }

    public float getOuterScale() {
        return outerScale;
    }

    private void reset() {
        bulb.rotationPointY = 20.0F;
        baseRing.rotationPointY = 4.0F;
        midRing.rotationPointY = -2.0F;
        topRing.rotationPointY = -2.0F;
        center.rotateAngleX = 0.0F;
        center.rotateAngleY = 0.0F;
        outer.rotateAngleX = 0.0F;
        outer.rotateAngleY = 0.0F;
        baseRing.rotateAngleY = 0.0F;
        midRing.rotateAngleY = 0.0F;
        topRing.rotateAngleY = 0.0F;
        outerScale = 1.0F;
    }
}
