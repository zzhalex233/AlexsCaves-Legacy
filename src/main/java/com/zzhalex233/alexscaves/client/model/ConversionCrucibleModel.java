package com.zzhalex233.alexscaves.client.model;

import com.zzhalex233.alexscaves.server.block.entity.ConversionCrucibleTileEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ConversionCrucibleModel extends ModelBase {
    private final ModelRenderer crucible;
    private final ModelRenderer sideWest;
    private final ModelRenderer sideEast;
    private final ModelRenderer sauce;
    private final ModelRenderer beam;

    public ConversionCrucibleModel() {
        textureWidth = 64;
        textureHeight = 64;
        crucible = new ModelRenderer(this);
        crucible.setRotationPoint(0.0F, 24.0F, 0.0F);
        crucible.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16, 16, 0);
        crucible.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -5.0F, 16, 16, 0);
        crucible.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, 5.0F, 16, 16, 0);
        crucible.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, 8.0F, 16, 16, 0);
        crucible.setTextureOffset(52, 0).addBox(-8.0F, -16.0F, -3.0F, 3, 16, 0);
        crucible.setTextureOffset(52, 0).addBox(-8.0F, -16.0F, 3.0F, 3, 16, 0);
        crucible.setTextureOffset(52, 0).addBox(5.0F, -16.0F, -3.0F, 3, 16, 0);
        crucible.setTextureOffset(52, 0).addBox(5.0F, -16.0F, 3.0F, 3, 16, 0);
        crucible.setTextureOffset(-16, 32).addBox(-8.0F, 0.0F, -8.0F, 16, 0, 16);
        crucible.setTextureOffset(-16, 16).addBox(-8.0F, -2.0F, -8.0F, 16, 0, 16);
        crucible.setTextureOffset(-16, 48).addBox(-8.0F, -16.0F, -8.0F, 16, 0, 16);
        crucible.setTextureOffset(52, -3).addBox(-3.0F, -16.0F, -8.0F, 0, 16, 3);
        crucible.setTextureOffset(52, -3).addBox(3.0F, -16.0F, -8.0F, 0, 16, 3);

        sideWest = new ModelRenderer(this);
        sideWest.setRotationPoint(-5.0F, -8.0F, -0.5F);
        sideWest.rotateAngleY = (float) Math.PI * 0.5F;
        sideWest.setTextureOffset(0, 0).addBox(-8.5F, -8.0F, 0.0F, 16, 16, 0);
        crucible.addChild(sideWest);

        sideEast = new ModelRenderer(this);
        sideEast.setRotationPoint(5.0F, -8.0F, -0.5F);
        sideEast.rotateAngleY = (float) Math.PI * 0.5F;
        sideEast.setTextureOffset(0, 0).addBox(-8.5F, -8.0F, 0.0F, 16, 16, 0);
        crucible.addChild(sideEast);

        sauce = new ModelRenderer(this);
        sauce.setRotationPoint(6.0F, -8.0F, -6.0F);
        sauce.setTextureOffset(20, 50).addBox(-13.0F, -6.0F, -1.0F, 14, 0, 14);
        crucible.addChild(sauce);

        beam = new ModelRenderer(this);
        beam.setRotationPoint(0.0F, -14.0F, 0.0F);
        beam.setTextureOffset(44, 22).addBox(5.0F, -18.0F, -5.0F, 0, 18, 10);
        beam.setTextureOffset(44, 32).addBox(-5.0F, -18.0F, 5.0F, 10, 18, 0);
        beam.setTextureOffset(44, 32).addBox(-5.0F, -18.0F, -5.0F, 10, 18, 0);
        beam.setTextureOffset(44, 22).addBox(-5.0F, -18.0F, -5.0F, 0, 18, 10);
        crucible.addChild(beam);
    }

    public void setup(float splashProgress, float conversionProgress, float ageInTicks, int filledLevel) {
        reset();
        float wobble = conversionProgress <= 0.0F ? 0.0F : (float) Math.sin(ageInTicks * 1.5F) * conversionProgress * 0.1F;
        crucible.rotateAngleX = wobble;
        crucible.rotateAngleZ = -wobble;
        sauce.rotationPointY += ConversionCrucibleTileEntity.MAX_FILL_AMOUNT - filledLevel;
        beam.rotationPointY += ConversionCrucibleTileEntity.MAX_FILL_AMOUNT - filledLevel;
        float pulse = (float) Math.sin(splashProgress * Math.PI * 3.0F + ageInTicks * 0.3F) * 0.15F + 0.85F;
        beam.rotationPointY -= 8.0F * conversionProgress;
        beam.rotateAngleY = ageInTicks * 0.05F;
        beam.offsetY = -conversionProgress * 0.5F;
        beam.offsetX = 0.0F;
        beam.offsetZ = 0.0F;
        beam.showModel = true;
        sauce.showModel = true;
        beam.setTextureSize(textureWidth, textureHeight);
        beam.rotationPointX *= pulse;
    }

    public void hideBeam(boolean hide) {
        beam.showModel = !hide;
    }

    public void hideSauce(boolean hide) {
        sauce.showModel = !hide;
    }

    public void render(float scale) {
        crucible.render(scale);
    }

    private void reset() {
        crucible.rotateAngleX = 0.0F;
        crucible.rotateAngleZ = 0.0F;
        sauce.rotationPointY = -8.0F;
        beam.rotationPointY = -14.0F;
        beam.rotateAngleY = 0.0F;
        beam.offsetY = 0.0F;
        beam.showModel = true;
        sauce.showModel = true;
    }
}
