package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.entity.ACEntityRegistry;
import com.zzhalex233.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class GalenaGauntletItem extends Item {
    public GalenaGauntletItem() {
        setMaxStackSize(1);
        setMaxDamage(400);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemStack other = player.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (!isMagneticItem(other, EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.CRYSTALLIZATION, stack) > 0)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        if (!player.capabilities.isCreativeMode && stack.isItemStackDamageable()) {
            stack.damageItem(1, player);
        }
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase living, int timeLeft) {
        if (living instanceof EntityPlayer) {
            ((EntityPlayer) living).getCooldownTracker().setCooldown(this, 5);
        }
        living.playSound(ACSoundRegistry.GALENA_GAUNTLET_STOP, 1.0F, 1.0F);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase living, int count) {
        World world = living.world;
        ItemStack otherStack = getOtherHandStack(living, stack);
        boolean crystallization = EnchantmentHelper.getEnchantmentLevel(ACEnchantmentRegistry.CRYSTALLIZATION, stack) > 0;
        if (!isMagneticItem(otherStack, crystallization)) {
            living.stopActiveHand();
            return;
        }
        updateUseTime(stack, true);
        if (world.isRemote) {
            AlexsCaves.PROXY.playGalenaGauntletSound(living);
        }
        if (!world.isRemote && !hasControlledWeapon(world, living)) {
            ItemStack copy = otherStack.splitStack(1);
            MagneticWeaponEntity weapon = new MagneticWeaponEntity(world);
            weapon.setItemStack(copy);
            Vec3d spawnAt = living.getPositionEyes(1.0F).add(living.getLookVec().scale(0.75D));
            weapon.setPosition(spawnAt.x, spawnAt.y, spawnAt.z);
            weapon.setController(living);
            world.spawnEntity(weapon);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isRemote) {
            boolean using = entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getActiveItemStack() == stack;
            updateUseTime(stack, using);
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
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
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == Item.getItemFromBlock(com.zzhalex233.alexscaves.server.block.ACBlockRegistry.PACKED_GALENA.block()) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    private static ItemStack getOtherHandStack(EntityLivingBase living, ItemStack stack) {
        ItemStack main = living.getHeldItemMainhand();
        return main == stack ? living.getHeldItemOffhand() : main;
    }

    private static boolean hasControlledWeapon(World world, EntityLivingBase living) {
        for (MagneticWeaponEntity weapon : world.getEntitiesWithinAABB(MagneticWeaponEntity.class, living.getEntityBoundingBox().grow(64.0D))) {
            Entity controller = weapon.getController();
            if (controller != null && controller.getUniqueID().equals(living.getUniqueID())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMagneticItem(ItemStack stack, boolean crystallization) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        if (isFerromagneticItem(stack)) {
            return true;
        }
        if (crystallization) {
            return item == Items.DIAMOND || item == Items.QUARTZ || item == Items.DIAMOND_SWORD || item == Items.DIAMOND_PICKAXE || item == Items.DIAMOND_AXE || item == Items.DIAMOND_SHOVEL || item == Items.DIAMOND_HOE || item == ACItemRegistry.DESOLATE_DAGGER.item();
        }
        return false;
    }

    private static boolean isFerromagneticItem(ItemStack stack) {
        Item item = stack.getItem();
        if (item == ACItemRegistry.HEAVYWEIGHT.item() || item == Items.IRON_SWORD || item == Items.IRON_PICKAXE || item == Items.IRON_AXE || item == Items.IRON_SHOVEL || item == Items.IRON_HOE) {
            return true;
        }
        if (item == Items.IRON_HELMET || item == Items.IRON_CHESTPLATE || item == Items.IRON_LEGGINGS || item == Items.IRON_BOOTS || item == Items.CHAINMAIL_HELMET || item == Items.CHAINMAIL_CHESTPLATE || item == Items.CHAINMAIL_LEGGINGS || item == Items.CHAINMAIL_BOOTS) {
            return true;
        }
        if (item == Items.SHEARS || item == Items.FLINT_AND_STEEL || item == Items.BUCKET || item == Items.COMPASS || item == Items.SHIELD || item == Items.CAULDRON) {
            return true;
        }
        if (item == Items.MINECART || item == Items.CHEST_MINECART || item == Items.COMMAND_BLOCK_MINECART || item == Items.FURNACE_MINECART || item == Items.HOPPER_MINECART || item == Items.TNT_MINECART) {
            return true;
        }
        Block block = Block.getBlockFromItem(item);
        if (block == Blocks.IRON_BLOCK || block == Blocks.IRON_ORE || block == Blocks.IRON_BARS || block == Blocks.IRON_DOOR || block == Blocks.IRON_TRAPDOOR || block == Blocks.HOPPER || block == Blocks.ANVIL || block == Blocks.RAIL || block == Blocks.DETECTOR_RAIL || block == Blocks.ACTIVATOR_RAIL || block == Blocks.GOLDEN_RAIL || block == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            return true;
        }
        for (int id : OreDictionary.getOreIDs(stack)) {
            String name = OreDictionary.getOreName(id);
            if ("ingotIron".equals(name) || "nuggetIron".equals(name) || "oreIron".equals(name) || "blockIron".equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static void updateUseTime(ItemStack stack, boolean using) {
        int useTime = getUseTime(stack);
        if (using && useTime < 5) {
            setUseTime(stack, useTime + 1);
        } else if (!using && useTime > 0) {
            setUseTime(stack, useTime - 1);
        }
    }

    public static void setUseTime(ItemStack stack, int useTime) {
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setInteger("PrevUseTime", getUseTime(stack));
        tag.setInteger("UseTime", useTime);
    }

    public static int getUseTime(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null ? tag.getInteger("UseTime") : 0;
    }

    public static float getLerpedUseTime(ItemStack stack, float partialTick) {
        NBTTagCompound tag = stack.getTagCompound();
        float prev = tag != null ? tag.getInteger("PrevUseTime") : 0.0F;
        float current = tag != null ? tag.getInteger("UseTime") : 0.0F;
        return prev + partialTick * (current - prev);
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
