package com.zzhalex233.alexscaves.server.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;

public class TrackedDurationEffect extends TickingPotion {
    private int lastDuration = -1;
    private int firstDuration = -1;

    protected TrackedDurationEffect(String name, boolean badEffect, int color) {
        super(name, badEffect, color);
    }

    public int getActiveTime() {
        return firstDuration - lastDuration;
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        lastDuration = duration;
        if (duration <= 0) {
            resetDuration();
        } else if (firstDuration == -1) {
            firstDuration = duration;
        }
        return duration > 0;
    }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap attributeMap, int amplifier) {
        resetDuration();
        super.applyAttributesModifiersToEntity(entity, attributeMap, amplifier);
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributeMap, int amplifier) {
        resetDuration();
        super.removeAttributesModifiersFromEntity(entity, attributeMap, amplifier);
    }

    private void resetDuration() {
        lastDuration = -1;
        firstDuration = -1;
    }
}
