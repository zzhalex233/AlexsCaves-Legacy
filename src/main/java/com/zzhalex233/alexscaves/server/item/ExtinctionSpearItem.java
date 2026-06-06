package com.zzhalex233.alexscaves.server.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.zzhalex233.alexscaves.server.entity.item.ExtinctionSpearEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

public class ExtinctionSpearItem extends Item {
    public ExtinctionSpearItem() {
        setMaxStackSize(1);
        setMaxDamage(1300);
        addPropertyOverride(new ResourceLocation("throwing"), (stack, world, entity) -> entity != null && entity.getActiveItemStack() == stack ? 1.0F : 0.0F);
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
        if (power > 0.1F) {
            if (!worldIn.isRemote) {
                ItemStack spearStack = stack.copy();
                if (!player.capabilities.isCreativeMode) {
                    spearStack.damageItem(1, player);
                }
                ExtinctionSpearEntity spear = new ExtinctionSpearEntity(worldIn, player, spearStack);
                spear.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, power * 3.5F, 1.0F);
                spear.pickupStatus = player.capabilities.isCreativeMode ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.ALLOWED;
                worldIn.spawnEntity(spear);
                worldIn.playSound(null, spear.posX, spear.posY, spear.posZ, ACSoundRegistry.EXTINCTION_SPEAR_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            player.addStat(StatList.getObjectUseStats(this));
        }
        killGrottoGhostsFor(player, false);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (count == getMaxItemUseDuration(stack) && !player.world.isRemote) {
            player.world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.EXTINCTION_SPEAR_SUMMON, SoundCategory.PLAYERS, 1.0F, 1.0F);
            int heads = 3 + EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.HERD_PHALANX, stack);
            float rotateBy = 360.0F / heads;
            for (int i = 0; i < heads; i++) {
                DinosaurSpiritEntity spirit = new DinosaurSpiritEntity(player.world);
                spirit.setPosition(player.posX, player.posY, player.posZ);
                spirit.setDinosaurType(DinosaurSpiritEntity.DinosaurType.GROTTOCERATOPS);
                if (player instanceof EntityPlayer) {
                    spirit.setPlayer((EntityPlayer) player);
                } else {
                    spirit.setPlayerUUID(player.getUniqueID());
                }
                spirit.setRotateOffset(i * rotateBy);
                player.world.spawnEntity(spirit);
            }
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.setFire(5);
        if (!attacker.world.isRemote) {
            DinosaurSpiritEntity spirit = new DinosaurSpiritEntity(attacker.world);
            Vec3d between = attacker.getPositionVector().add(target.getPositionVector()).scale(0.5D);
            spirit.setPosition(between.x, attacker.posY + 1.0D, between.z);
            spirit.setDinosaurType(DinosaurSpiritEntity.DinosaurType.TREMORSAURUS);
            if (attacker instanceof EntityPlayer) {
                spirit.setPlayer((EntityPlayer) attacker);
            } else {
                spirit.setPlayerUUID(attacker.getUniqueID());
            }
            spirit.setEnchantmentLevel(EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.CHOMPING_SPIRIT, stack));
            spirit.setAttackingEntityId(target.getEntityId());
            spirit.faceTarget(target);
            spirit.setDelaySpawn(5);
            attacker.world.spawnEntity(spirit);
        }
        stack.damageItem(1, attacker);
        return true;
    }

    public static boolean killGrottoGhostsFor(EntityPlayer player, boolean justTheClosest) {
        DinosaurSpiritEntity closest = null;
        AxisAlignedBB area = player.getEntityBoundingBox().grow(30.0D);
        for (DinosaurSpiritEntity spirit : player.world.getEntitiesWithinAABB(DinosaurSpiritEntity.class, area)) {
            if (player.getUniqueID().equals(spirit.getPlayerUUID()) && spirit.getDinosaurType() == DinosaurSpiritEntity.DinosaurType.GROTTOCERATOPS && !spirit.isFading()) {
                if (!justTheClosest) {
                    spirit.setFading(true);
                } else if (closest == null || player.getDistanceSq(spirit) < player.getDistanceSq(closest)) {
                    closest = spirit;
                }
            }
        }
        if (justTheClosest && closest != null) {
            closest.setFading(true);
            return true;
        }
        return !justTheClosest;
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
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ACItemRegistry.TECTONIC_SHARD.item() || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return ACItemRegistry.RARITY_DEMONIC;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getItemAttributeModifiers(slot));
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 8.0D, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.9D, 0));
        }
        return modifiers;
    }

    private float getPowerForTime(int useTicks) {
        float power = (float) useTicks / 20.0F;
        power = (power * power + power * 2.0F) / 3.0F;
        return Math.min(power, 1.0F);
    }
}
