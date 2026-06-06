package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ShotGumModel extends ModelBase {
    private final ModelRenderer shotGum;
    private final ModelRenderer handle;
    private final ModelRenderer gumLayer;
    private final ModelRenderer gumLayer2;
    private final ModelRenderer gumLayer3;
    private final ModelRenderer gumLayer4;
    private final ModelRenderer stock;
    private final ModelRenderer cubeR1;
    private final ScalableModelRenderer barrels;
    private final ModelRenderer crank;

    public ShotGumModel() {
        textureWidth = 128;
        textureHeight = 128;

        shotGum = new ModelRenderer(this);
        shotGum.setRotationPoint(0.0F, 21.0F, 6.0F);
        shotGum.setTextureOffset(32, 23).addBox(-4.0F, -9.0F, -5.0F, 8, 8, 8, 0.0F);
        shotGum.setTextureOffset(0, 30).addBox(-4.0F, -9.0F, -5.0F, 8, 8, 8, 0.25F);

        handle = new ModelRenderer(this);
        handle.setRotationPoint(0.0F, 0.5F, -1.0F);
        handle.setTextureOffset(7, 40).addBox(0.0F, -1.5F, -3.0F, 0, 3, 6, 0.0F);
        shotGum.addChild(handle);

        ModelRenderer gumLayers = new ModelRenderer(this);
        gumLayers.setRotationPoint(0.0F, -1.0F, -2.0F);
        shotGum.addChild(gumLayers);

        gumLayer = gumLayer(-3.8F, 7);
        gumLayer2 = gumLayer(-1.9F, 9);
        gumLayer3 = gumLayer(0.0F, 11);
        gumLayer4 = gumLayer(1.9F, 13);
        gumLayers.addChild(gumLayer);
        gumLayers.addChild(gumLayer2);
        gumLayers.addChild(gumLayer3);
        gumLayers.addChild(gumLayer4);

        stock = new ModelRenderer(this);
        stock.setRotationPoint(0.0F, -2.75F, 3.0F);
        shotGum.addChild(stock);

        cubeR1 = new ModelRenderer(this);
        cubeR1.setRotationPoint(0.0F, 3.0F, 0.0F);
        cubeR1.rotateAngleX = -0.5236F;
        cubeR1.setTextureOffset(0, 49).addBox(-2.0F, -7.0F, 1.0F, 4, 3, 7, 0.0F);
        cubeR1.setTextureOffset(41, 44).addBox(-2.0F, -4.0F, -3.0F, 4, 4, 11, 0.0F);
        cubeR1.setTextureOffset(0, 59).addBox(-2.0F, -7.0F, 1.0F, 4, 7, 7, 0.25F);
        stock.addChild(cubeR1);

        barrels = new ScalableModelRenderer(this);
        barrels.setRotationPoint(0.0F, -3.8333F, -5.0F);
        barrels.setTextureOffset(0, 0).addBox(-3.5F, -0.6667F, -12.0F, 7, 3, 12, 0.25F);
        barrels.setTextureOffset(0, 0).addBox(-0.5F, -2.6667F, -12.0F, 1, 2, 0, 0.0F);
        barrels.setTextureOffset(0, 15).addBox(-3.5F, -0.6667F, -12.0F, 7, 3, 12, 0.0F);
        shotGum.addChild(barrels);

        crank = new ModelRenderer(this);
        crank.setRotationPoint(4.0F, -5.0F, -1.0F);
        crank.setTextureOffset(8, 0).addBox(0.0F, -2.0F, 0.0F, 2, 4, 0, 0.0F);
        shotGum.addChild(crank);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        shotGum.render(scale);
    }

    public void setup(float shootProgress, float gumballsLeft, float crankAngle) {
        resetPose();
        gumLayer4.showModel = gumballsLeft > 0;
        gumLayer3.showModel = gumballsLeft > 1;
        gumLayer2.showModel = gumballsLeft > 2;
        gumLayer.showModel = gumballsLeft > 3;
        crank.rotateAngleX += crankAngle * ((float) Math.PI / 180.0F);
        shotGum.rotateAngleX -= shootProgress * 20.0F * ((float) Math.PI / 180.0F);
        barrels.setScale(1.0F + shootProgress * 0.3F, 1.0F + shootProgress * 0.3F, 1.0F - shootProgress * 0.5F);
        barrels.rotationPointY -= shootProgress;
        barrels.rotationPointZ += shootProgress;
    }

    private void resetPose() {
        shotGum.setRotationPoint(0.0F, 21.0F, 6.0F);
        shotGum.rotateAngleX = shotGum.rotateAngleY = shotGum.rotateAngleZ = 0.0F;
        barrels.setRotationPoint(0.0F, -3.8333F, -5.0F);
        barrels.rotateAngleX = barrels.rotateAngleY = barrels.rotateAngleZ = 0.0F;
        barrels.setScale(1.0F, 1.0F, 1.0F);
        crank.rotateAngleX = crank.rotateAngleY = crank.rotateAngleZ = 0.0F;
    }

    private ModelRenderer gumLayer(float y, int textureY) {
        ModelRenderer layer = new ModelRenderer(this);
        layer.setRotationPoint(0.0F, -4.0F, 1.0F);
        layer.setTextureOffset(31, textureY).addBox(-4.0F, y, -4.0F, 8, 2, 8, -0.05F);
        return layer;
    }

    private static class ScalableModelRenderer extends ModelRenderer {
        private float scaleX = 1.0F;
        private float scaleY = 1.0F;
        private float scaleZ = 1.0F;

        private ScalableModelRenderer(ModelBase model) {
            super(model);
        }

        private void setScale(float scaleX, float scaleY, float scaleZ) {
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            this.scaleZ = scaleZ;
        }

        @Override
        public void render(float scale) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scaleX, scaleY, scaleZ);
            super.render(scale);
            GlStateManager.popMatrix();
        }
    }
}
