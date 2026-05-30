package com.zzhalex233.alexscaves.server.potion;

public class TickingPotion extends ACPotion {
    protected TickingPotion(String name, boolean badEffect, int color) {
        super(name, badEffect, color);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }
}
