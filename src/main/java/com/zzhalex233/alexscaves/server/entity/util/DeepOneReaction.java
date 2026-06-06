package com.zzhalex233.alexscaves.server.entity.util;

public enum DeepOneReaction {
    STALKING,
    AGGRESSIVE,
    NEUTRAL,
    HELPFUL;

    public static DeepOneReaction fromReputation(int reputation) {
        if (reputation <= -10) {
            return AGGRESSIVE;
        }
        if (reputation <= 10) {
            return STALKING;
        }
        if (reputation <= 30) {
            return NEUTRAL;
        }
        return HELPFUL;
    }
}
