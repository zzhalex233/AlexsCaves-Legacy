package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;

public class PewenBoatModel extends ACBoatModel {
    private final ModelRenderer bottom;
    private final ModelRenderer right;
    private final ModelRenderer left;
    private final ModelRenderer back;
    private final ModelRenderer front;
    private final ModelRenderer paddleLeft;
    private final ModelRenderer paddleRight;
    private final ModelRenderer waterMask;
    private final ModelRenderer main;

    public PewenBoatModel() {
        textureWidth = 128;
        textureHeight = 128;
        bottom = part(0, 0, 0.0F, 22.5F, 2.0F, -10.0F, -1.5F, -17.0F, 20, 3, 32, false);
        addBox(bottom, 97, 65, 6.0F, 5.5F, 2.0F, 2, 2, 2, true);
        addBox(bottom, 80, 49, 1.0F, 1.5F, 2.0F, 7, 4, 8, true);
        addBox(bottom, 80, 49, -8.0F, 1.5F, 2.0F, 7, 4, 8, false);
        addBox(bottom, 97, 65, -8.0F, 5.5F, 2.0F, 2, 2, 2, false);
        right = part(68, 72, 0.0F, 24.0F, 0.0F, -10.0F, -16.0F, 3.0F, 2, 13, 12, false);
        addBox(right, 61, 45, -10.0F, -11.0F, -13.0F, 2, 8, 16, true);
        left = part(0, 61, 0.0F, 24.0F, 0.0F, 8.0F, -11.0F, -13.0F, 2, 8, 16, false);
        addBox(left, 0, 0, 8.0F, -16.0F, 3.0F, 2, 13, 12, false);
        back = part(36, 69, 0.0F, 14.5F, 16.0F, -10.0F, -6.5F, -1.0F, 20, 13, 2, false);
        front = part(72, 0, 0.0F, 24.0F, 0.0F, -10.0F, -11.0F, -15.0F, 20, 8, 2, false);
        paddleLeft = part(46, 100, 10.5F, 12.25F, 1.0F, -6.0F, -1.25F, -1.0F, 18, 2, 2, false);
        setRotateAngle(paddleLeft, 0.0F, 0.0F, 0.5236F);
        addBox(paddleLeft, 72, 25, 8.0F, -4.25F, -0.9F, 7, 6, 1, true);
        paddleRight = part(46, 100, -10.5F, 12.25F, 1.0F, -12.0F, -1.25F, -1.0F, 18, 2, 2, true);
        setRotateAngle(paddleRight, 0.0F, 0.0F, -0.5236F);
        addBox(paddleRight, 72, 25, -15.0F, -4.25F, -0.9F, 7, 6, 1, false);
        waterMask = part(0, 0, 0.0F, 25.0F, 0.0F, -8.0F, -13.0F, -13.0F, 16, 10, 28, false);
        main = part(0, 35, 0.0F, 24.0F, 0.0F, -10.0F, -19.0F, -15.0F, 20, 8, 18, false);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        setupPaddleAnims((EntityBoat) entity, paddleLeft, paddleRight, limbSwing);
        bottom.render(scale);
        right.render(scale);
        left.render(scale);
        back.render(scale);
        front.render(scale);
        paddleLeft.render(scale);
        paddleRight.render(scale);
        main.render(scale);
    }

    @Override
    public ModelRenderer getWaterMask() {
        return waterMask;
    }

    private ModelRenderer part(int u, int v, float px, float py, float pz, float x, float y, float z, int dx, int dy, int dz, boolean mirror) {
        ModelRenderer renderer = new ModelRenderer(this, u, v);
        renderer.mirror = mirror;
        renderer.setRotationPoint(px, py, pz);
        renderer.addBox(x, y, z, dx, dy, dz);
        return renderer;
    }

    private void addBox(ModelRenderer renderer, int u, int v, float x, float y, float z, int dx, int dy, int dz, boolean mirror) {
        renderer.setTextureOffset(u, v);
        renderer.mirror = mirror;
        renderer.addBox(x, y, z, dx, dy, dz);
    }
}
