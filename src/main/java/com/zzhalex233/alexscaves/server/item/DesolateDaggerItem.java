package com.zzhalex233.alexscaves.server.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.zzhalex233.alexscaves.server.entity.item.DesolateDaggerEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

public class DesolateDaggerItem extends Item {
    public DesolateDaggerItem() {
        setMaxStackSize(1);
        setMaxDamage(360);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        if (!attacker.world.isRemote) {
            DesolateDaggerEntity dagger = new DesolateDaggerEntity(attacker.world);
            dagger.copyLocationAndAnglesFrom(attacker);
            dagger.setTargetId(target.getEntityId());
            dagger.setOwnerId(attacker.getEntityId());
            dagger.setItemStack(stack);
            attacker.world.spawnEntity(dagger);
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
        return repair.getItem() == ACItemRegistry.PURE_DARKNESS.item() || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return ACItemRegistry.RARITY_DEMONIC;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getItemAttributeModifiers(slot));
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 3.0D, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 2.0D, 0));
        }
        return modifiers;
    }
}
