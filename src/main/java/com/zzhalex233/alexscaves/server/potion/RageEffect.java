package com.zzhalex233.alexscaves.server.potion;

import java.util.UUID;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.AxisAlignedBB;

public class RageEffect extends TickingPotion {
    private static final UUID RAGE_ATTACK_DAMAGE_UUID = UUID.fromString("1eaf83ff-7207-4596-b37a-d7a07b3ec4ff");

    protected RageEffect() {
        super("rage", false, 0XBA2E2E);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        if (attribute != null) {
            float levelScale = (1 + amplifier) * 2.5F;
            float damage = (1.0F - entity.getHealth() / entity.getMaxHealth()) * levelScale;
            removeRageModifier(entity);
            attribute.applyModifier(new AttributeModifier(RAGE_ATTACK_DAMAGE_UUID, "Rage attack boost", damage, 0));
        }
        if (!entity.world.isRemote && entity instanceof EntityLiving && entity.ticksExisted % 10 == 0 && entity.getRNG().nextInt(2) == 0) {
            pickNearestTarget((EntityLiving) entity);
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributeMap, int amplifier) {
        super.removeAttributesModifiersFromEntity(entity, attributeMap, amplifier);
        removeRageModifier(entity);
    }

    private static void removeRageModifier(EntityLivingBase entity) {
        IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        if (attribute != null && attribute.getModifier(RAGE_ATTACK_DAMAGE_UUID) != null) {
            attribute.removeModifier(RAGE_ATTACK_DAMAGE_UUID);
        }
    }

    private static void pickNearestTarget(EntityLiving living) {
        if (living.getAttackTarget() != null) {
            return;
        }
        AxisAlignedBB area = living.getEntityBoundingBox().grow(80.0D);
        EntityLivingBase nearest = null;
        for (EntityLivingBase target : living.world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
            if (target != living && target.isEntityAlive() && living.canAttackClass(target.getClass()) && (nearest == null || living.getDistanceSq(target) < living.getDistanceSq(nearest))) {
                nearest = target;
            }
        }
        if (nearest != null) {
            living.setAttackTarget(nearest);
        }
    }
}
