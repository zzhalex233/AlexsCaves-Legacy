package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;

public class ThornwoodBoatModel extends ACBoatModel {
    private final ModelRenderer bottom;
    private final ModelRenderer left;
    private final ModelRenderer right;
    private final ModelRenderer paddleRight;
    private final ModelRenderer paddleLeft;
    private final ModelRenderer front;
    private final ModelRenderer back;
    private final ModelRenderer cull;

    public ThornwoodBoatModel() {
        textureWidth = 128;
        textureHeight = 128;
        bottom = part(0, 34, 0.0F, 22.5F, 0.0F, -8.0F, -2.5F, -14.0F, 16, 4, 28, false);
        left = part(60, 0, 0.0F, 24.0F, 0.0F, 8.0F, -9.0F, -2.0F, 2, 6, 17, true);
        addBox(left, 38, 66, 8.0F, -12.0F, -15.0F, 2, 9, 13, false);
        right = part(60, 0, 0.0F, 24.0F, 0.0F, -10.0F, -9.0F, -2.0F, 2, 6, 17, false);
        addBox(right, 38, 66, -10.0F, -12.0F, -15.0F, 2, 9, 13, true);
        paddleRight = part(81, 34, -8.75F, 14.0F, -0.505F, -18.25F, -8.0F, -0.505F, 15, 10, 0, true);
        setRotateAngle(paddleRight, 0.0F, 0.0F, -0.5236F);
        addBox(paddleRight, 60, 23, -12.25F, -1.0F, -0.495F, 18, 2, 2, true);
        paddleLeft = part(81, 34, 8.75F, 14.0F, -0.505F, 3.25F, -8.0F, -0.505F, 15, 10, 0, false);
        setRotateAngle(paddleLeft, 0.0F, 0.0F, 0.5236F);
        addBox(paddleLeft, 60, 23, -5.75F, -1.0F, -0.495F, 18, 2, 2, false);
        front = part(0, 66, 0.0F, 2.8F, -15.8F, -8.0F, 5.2F, -1.2F, 16, 13, 3, false);
        addBox(front, 14, 34, -2.0F, 0.2F, -1.2F, 4, 5, 3, false);
        addBox(front, 0, 0, -2.0F, -5.8F, -4.2F, 4, 6, 6, false);
        addBox(front, 18, 12, -2.0F, -11.8F, -4.2F, 4, 6, 0, false);
        addBox(front, 20, 0, -2.0F, -5.8F, 1.8F, 4, 6, 6, false);
        back = part(81, 0, 0.0F, 12.6667F, 15.8333F, -8.0F, 2.3333F, -1.8333F, 16, 6, 3, false);
        addBox(back, 0, 34, -2.0F, -2.6667F, -1.8333F, 4, 5, 3, false);
        addBox(back, 0, 12, -2.0F, -7.6667F, -1.8333F, 4, 5, 5, false);
        cull = part(0, 94, 0.0F, 24.0F, 0.0F, -8.0F, -9.0F, -14.0F, 16, 6, 28, false);
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
    }

    @Override
    public ModelRenderer getWaterMask() {
        return cull;
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
