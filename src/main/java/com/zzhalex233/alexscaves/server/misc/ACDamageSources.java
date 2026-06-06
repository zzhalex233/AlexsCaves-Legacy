package com.zzhalex233.alexscaves.server.misc;

import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.entity.EntityLivingBase;

public class ACDamageSources {
    public static final DamageSource RADIATION = new DamageSource("alexscaves.radiation").setDamageBypassesArmor();

    private ACDamageSources() {
    }

    public static DamageSource raygun(EntityLivingBase source, boolean gamma) {
        return new EntityDamageSource(gamma ? "raygun_1" : "raygun_0", source);
    }
}
