package com.zzhalex233.alexscaves.server.misc;

public class ACMath {
    public static float smin(float a, float b, float k) {
        float h = Math.max(k - Math.abs(a - b), 0.0F) / k;
        return Math.min(a, b) - h * h * k * 0.25F;
    }

    public static float approach(float current, float target, float step) {
        return current < target ? Math.min(current + step, target) : Math.max(current - step, target);
    }

    public static float approachRotation(float current, float target, float step) {
        float delta = net.minecraft.util.math.MathHelper.wrapDegrees(target - current);
        return approach(current, current + delta, step);
    }
}
