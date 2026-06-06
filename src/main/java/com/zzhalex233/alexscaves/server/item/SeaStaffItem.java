package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.server.entity.item.WaterBoltEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

public class SeaStaffItem extends Item {
    public SeaStaffItem() {
        setMaxStackSize(1);
        setMaxDamage(850);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, ACSoundRegistry.SEA_STAFF_CAST, SoundCategory.PLAYERS, 0.5F, itemRand.nextFloat() * 0.45F + 0.75F);
        playerIn.swingArm(handIn);
        if (!worldIn.isRemote) {
            Entity target = getClosestLookingAtEntityFor(worldIn, playerIn, 128.0D);
            int bolts = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.TRIPLE_SPLASH, stack) > 0 ? 3 : 1;
            for (int i = 0; i < bolts; i++) {
                float yawOffset = i == 0 ? 0.0F : i == 1 ? -50.0F : 50.0F;
                WaterBoltEntity bolt = new WaterBoltEntity(worldIn, playerIn);
                float sideRot = playerIn.rotationYawHead + (handIn == EnumHand.MAIN_HAND ? 45.0F : -45.0F);
                bolt.setPosition(playerIn.posX - playerIn.width * 1.1F * Math.sin(sideRot * Math.PI / 180.0D), playerIn.posY + playerIn.getEyeHeight() - 0.4D, playerIn.posZ + playerIn.width * 1.1F * Math.cos(sideRot * Math.PI / 180.0D));
                bolt.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw + yawOffset, -20.0F, i > 0 ? 1.0F : 2.0F, 12.0F);
                bolt.setBubbling(EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.ENVELOPING_BUBBLE, stack) > 0 && itemRand.nextBoolean());
                bolt.ricochet = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.BOUNCING_BOLT, stack) > 0;
                bolt.seekAmount = 0.3F + EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SOAK_SEEKING, stack) * 0.2F;
                if (target != null) {
                    bolt.setArcingTowards(target);
                }
                worldIn.spawnEntity(bolt);
            }
        }
        playerIn.addStat(StatList.getObjectUseStats(this));
        if (!playerIn.capabilities.isCreativeMode) {
            stack.damageItem(1, playerIn);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote && stack.isItemDamaged() && EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.SEAPAIRING, stack) > 0 && entityIn.isInWater() && worldIn.rand.nextFloat() < 0.02F) {
            stack.setItemDamage(Math.max(0, stack.getItemDamage() - 1));
        }
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
    public IRarity getForgeRarity(ItemStack stack) {
        return net.minecraft.item.EnumRarity.UNCOMMON;
    }

    public static Entity getClosestLookingAtEntityFor(World world, EntityPlayer player, double dist) {
        Entity closest = null;
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d lookEnd = eyes.add(player.getLookVec().scale(dist));
        RayTraceResult blockHit = world.rayTraceBlocks(eyes, lookEnd, false, true, false);
        Vec3d at = blockHit == null ? lookEnd : blockHit.hitVec;
        AxisAlignedBB search = new AxisAlignedBB(at.x - 0.5D, at.y - 0.5D, at.z - 0.5D, at.x + 0.5D, at.y + 0.5D, at.z + 0.5D).grow(15.0D);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, search);
        for (EntityLivingBase entity : entities) {
            if (entity != player && entity instanceof EntityLiving && !entity.isOnSameTeam(player) && player.canEntityBeSeen(entity) && (closest == null || entity.getDistanceSq(at.x, at.y, at.z) < closest.getDistanceSq(at.x, at.y, at.z))) {
                closest = entity;
            }
        }
        return closest;
    }
}
