package com.zzhalex233.alexscaves.server.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.zzhalex233.alexscaves.server.entity.item.FrostmintSpearEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class FrostmintSpearItem extends Item {
    public FrostmintSpearItem() {
        setMaxStackSize(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (!(entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entityLiving;
        float power = getPowerForTime(getMaxItemUseDuration(stack) - timeLeft);
        if (power <= 0.1F) {
            return;
        }
        if (!worldIn.isRemote) {
            FrostmintSpearEntity spear = new FrostmintSpearEntity(worldIn, player);
            spear.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, power * 2.5F, 1.0F);
            spear.pickupStatus = player.capabilities.isCreativeMode ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.ALLOWED;
            worldIn.spawnEntity(spear);
            worldIn.playSound(null, spear.posX, spear.posY, spear.posZ, ACSoundRegistry.FROSTMINT_SPEAR_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        player.addStat(StatList.getObjectUseStats(this));
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        applyFrostmintFreeze(target, 80);
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getItemAttributeModifiers(slot));
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 5.0D, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.0D, 0));
        }
        return modifiers;
    }

    public static void applyFrostmintFreeze(EntityLivingBase entity, int duration) {
        entity.addPotionEffect(new PotionEffect(ACEffectRegistry.STUNNED, duration, 0));
        entity.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.SLOWNESS, duration + 120, 1));
    }

    private float getPowerForTime(int useTicks) {
        float power = (float) useTicks / 20.0F;
        power = (power * power + power * 2.0F) / 3.0F;
        return Math.min(power, 1.0F);
    }
}
