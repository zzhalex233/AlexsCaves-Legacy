package com.zzhalex233.alexscaves.server.entity.util;

public interface UnderzealotSacrifice {
    void triggerSacrificeIn(int time);

    boolean isValidSacrifice(int distanceFromGround);
}
