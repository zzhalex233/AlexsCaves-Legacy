package com.zzhalex233.alexscaves.server.potion;

import net.minecraft.entity.SharedMonsterAttributes;

public class SugarRushEffect extends TickingPotion {
    protected SugarRushEffect() {
        super("sugar_rush", false, 0XFFA4EB);
        registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68020638", 0.7D, 2);
    }
}
