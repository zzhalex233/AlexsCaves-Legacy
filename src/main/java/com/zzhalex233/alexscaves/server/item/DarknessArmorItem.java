package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.config.ACConfig;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;
import com.zzhalex233.alexscaves.server.potion.DarknessIncarnateEffect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class DarknessArmorItem extends ACArmorItem {
    private static final String CLOAK_CHARGE = "CloakCharge";

    public DarknessArmorItem(ItemArmor.ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, slot, "darkness_armor", ACItemRegistry.RARITY_DEMONIC);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "alexscaves:textures/armor/darkness_armor.png";
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (!world.isRemote && stack.getItem() == ACItemRegistry.CLOAK_OF_DARKNESS.item()) {
            tickCloak(world, player, stack);
        }
    }

    private static void tickCloak(World world, EntityPlayer player, ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        int charge = tag.getInteger(CLOAK_CHARGE);
        if (canCharge(player)) {
            charge = Math.min(ACConfig.getDarknessCloakChargeTime(), charge + 1);
            if (charge >= ACConfig.getDarknessCloakChargeTime()) {
                tag.setInteger(CLOAK_CHARGE, 0);
                player.addPotionEffect(new PotionEffect(ACEffectRegistry.DARKNESS_INCARNATE, ACConfig.getDarknessCloakFlightTime(), 0, false, false));
                return;
            }
        } else if (charge > 0) {
            charge--;
        }
        tag.setInteger(CLOAK_CHARGE, charge);
    }

    private static boolean canCharge(EntityPlayer player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ACItemRegistry.HOOD_OF_DARKNESS.item()
                && !player.isPotionActive(ACEffectRegistry.DARKNESS_INCARNATE)
                && (!DarknessIncarnateEffect.isInLight(player, 11) || player.capabilities.isCreativeMode);
    }

    public static float getMeterProgress(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0.0F : tag.getInteger(CLOAK_CHARGE) / (float) ACConfig.getDarknessCloakChargeTime();
    }
}
