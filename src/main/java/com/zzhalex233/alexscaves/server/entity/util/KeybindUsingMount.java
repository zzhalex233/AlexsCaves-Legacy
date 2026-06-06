package com.zzhalex233.alexscaves.server.entity.util;

import net.minecraft.entity.Entity;

public interface KeybindUsingMount {
    void onKeyPacket(Entity keyPresser, int type);
}
