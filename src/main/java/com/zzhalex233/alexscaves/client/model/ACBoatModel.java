package com.zzhalex233.alexscaves.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.MathHelper;

public abstract class ACBoatModel extends ModelBase {
    public abstract ModelRenderer getWaterMask();

    protected void setupPaddleAnims(EntityBoat boat, ModelRenderer leftPaddle, ModelRenderer rightPaddle, float partialTicks) {
        animatePaddle(boat, 0, leftPaddle, partialTicks);
        animatePaddle(boat, 1, rightPaddle, partialTicks);
    }

    private static void animatePaddle(EntityBoat boat, int paddleIndex, ModelRenderer paddle, float partialTicks) {
        float rowingTime = boat.getRowingTime(paddleIndex, partialTicks);
        float side = paddleIndex == 1 ? -1.0F : 1.0F;
        paddle.rotateAngleZ = -side * interpolate(-(float) Math.PI / 3.0F, -0.2617994F, (MathHelper.sin(-rowingTime) + 1.0F) / 2.0F);
        paddle.rotateAngleY = side * interpolate(-(float) Math.PI / 4.0F, (float) Math.PI / 4.0F, (MathHelper.sin(-rowingTime + 1.0F) + 1.0F) / 2.0F);
    }

    private static float interpolate(float min, float max, float delta) {
        return min + (max - min) * MathHelper.clamp(delta, 0.0F, 1.0F);
    }

    protected static void setRotateAngle(ModelRenderer renderer, float x, float y, float z) {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }
}
