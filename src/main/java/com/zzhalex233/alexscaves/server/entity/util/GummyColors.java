package com.zzhalex233.alexscaves.server.entity.util;

import java.util.Random;

import net.minecraft.util.math.MathHelper;

public enum GummyColors {
    RED,
    GREEN,
    YELLOW,
    BLUE,
    PINK;

    public static GummyColors fromOrdinal(int gummyColor) {
        return values()[MathHelper.clamp(gummyColor, 0, values().length - 1)];
    }

    public static GummyColors getRandom(Random random, boolean init) {
        return fromOrdinal(random.nextInt(values().length - (init ? 0 : 1)));
    }
}
