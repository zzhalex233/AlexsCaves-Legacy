package com.zzhalex233.alexscaves.server.potion;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public class DarknessIncarnateEffect extends TrackedDurationEffect {
    protected DarknessIncarnateEffect() {
        super("darkness_incarnate", false, 0X510E0E);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        toggleFlight(entity, true);
        if (entity.onGround) {
            entity.motionY += 0.1D;
            entity.velocityChanged = true;
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributeMap, int amplifier) {
        super.removeAttributesModifiersFromEntity(entity, attributeMap, amplifier);
        toggleFlight(entity, false);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    private static void toggleFlight(EntityLivingBase entity, boolean flight) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            boolean creative = player.capabilities.isCreativeMode || player.capabilities.disableDamage;
            player.capabilities.allowFlying = creative || flight;
            player.capabilities.isFlying = creative || flight;
            if (!flight && !creative) {
                player.capabilities.isFlying = false;
                player.capabilities.allowFlying = false;
            }
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).sendPlayerAbilities();
            }
        }
        entity.fallDistance = 0.0F;
    }

    public static boolean isInLight(EntityLivingBase entity, int threshold) {
        BlockPos pos = entity.getPosition();
        int light = entity.world.getLightFor(EnumSkyBlock.BLOCK, pos);
        if (entity.world.canSeeSky(pos) && entity.world.isDaytime()) {
            light = 15;
        }
        return light >= threshold;
    }
}
