package com.zzhalex233.alexscaves.server.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PrimitiveClubItem extends Item {
    public PrimitiveClubItem() {
        setMaxStackSize(1);
        setMaxDamage(120);
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        if (!target.world.isRemote) {
            boolean stunned = target.getRNG().nextFloat() < 0.8F;
            if (stunned) {
                target.addPotionEffect(new PotionEffect(ACEffectRegistry.STUNNED, 150 + target.getRNG().nextInt(150)));
            }
            target.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ, stunned ? ACSoundRegistry.PRIMITIVE_CLUB_HIT : ACSoundRegistry.PRIMITIVE_CLUB_MISS, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if ((double) state.getBlockHardness(worldIn, pos) != 0.0D) {
            stack.damageItem(2, entityLiving);
        }
        return true;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ACItemRegistry.HEAVY_BONE.item() || repair.getItem() == Items.BONE || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getItemAttributeModifiers(slot));
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 8.0D, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.75D, 0));
        }
        return modifiers;
    }
}
