package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;

public class RainbounceBootsItem extends ACArmorItem {
    public RainbounceBootsItem(ItemArmor.ArmorMaterial material) {
        super(material, EntityEquipmentSlot.FEET, "rainbounce_boots");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "alexscaves:textures/armor/rainbounce_boots.png";
    }

    public static void bounce(EntityLivingBase entity, float fallDistance) {
        if (entity.world.isRemote || entity.isSneaking() || fallDistance <= 2.0F) {
            return;
        }
        double bounce = Math.min(1.6D, 0.18D + fallDistance * 0.08D);
        double inertia = entity.isPotionActive(ACEffectRegistry.SUGAR_RUSH) ? 1.2D : 1.6D;
        entity.motionX *= inertia;
        entity.motionY = bounce;
        entity.motionZ *= inertia;
        entity.fallDistance = 0.0F;
        entity.velocityChanged = true;
        entity.playSound(ACSoundRegistry.RAINBOUNCE_BOOTS_BOUNCE, 1.0F, 1.0F);
        for (int i = 0; i < 5; i++) {
            entity.world.spawnParticle(EnumParticleTypes.CLOUD, entity.posX + (entity.getRNG().nextDouble() - 0.5D) * entity.width, entity.posY + 0.2D, entity.posZ + (entity.getRNG().nextDouble() - 0.5D) * entity.width, 0.0D, 0.0D, 0.0D);
        }
    }
}
