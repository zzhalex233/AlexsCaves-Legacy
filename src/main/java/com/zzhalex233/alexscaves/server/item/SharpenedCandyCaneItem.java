package com.zzhalex233.alexscaves.server.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class SharpenedCandyCaneItem extends ItemFood {
    public SharpenedCandyCaneItem() {
        super(2, 0.1F, false);
        setPotionEffect(new PotionEffect(ACEffectRegistry.SUGAR_RUSH, 200), 0.01F);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
                player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.8F, 0.8F + itemRand.nextFloat() * 0.4F);
            }
        }
        target.knockBack(attacker, 0.15F, attacker.posX - target.posX, attacker.posZ - target.posZ);
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getItemAttributeModifiers(slot));
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 3.0D, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 4.0D, 0));
        }
        return modifiers;
    }
}
