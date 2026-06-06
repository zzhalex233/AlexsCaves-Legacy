package com.zzhalex233.alexscaves.server.item;

import java.util.UUID;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.zzhalex233.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class CandyCaneHookItem extends Item {
    public CandyCaneHookItem() {
        setMaxStackSize(1);
        setMaxDamage(200);
        addPropertyOverride(new ResourceLocation("cast"), (stack, world, entity) -> isActive(stack) ? 1.0F : 0.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemStack opposite = player.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (!world.isRemote) {
            if (canLaunchHook(player, stack, world, true, hand) && (hand == EnumHand.MAIN_HAND || opposite.getItem() != this || isHookLaunchedInWorld(world, opposite))) {
                CandyCaneHookEntity hook = new CandyCaneHookEntity(world, player, stack, hand == EnumHand.OFF_HAND);
                hook.setReeling(false);
                world.spawnEntity(hook);
                setLastLaunchedHookUUID(stack, hook.getUniqueID());
                setReelingIn(stack, false);
                world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.CANDY_CANE_HOOK_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
                player.addStat(StatList.getObjectUseStats(this));
                player.swingArm(hand);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
            if (!(player.getRidingEntity() instanceof GumWormSegmentEntity) && !(opposite.getItem() == this && !isActive(opposite)) && isActive(stack)) {
                if (opposite.getItem() == this && isActive(opposite) && !isReelingIn(opposite)) {
                    setReelingIn(opposite, true);
                    opposite.damageItem(1, player);
                }
                if (!isReelingIn(stack)) {
                    setReelingIn(stack, true);
                    stack.damageItem(1, player);
                    world.playSound(null, player.posX, player.posY, player.posZ, ACSoundRegistry.CANDY_CANE_HOOK_REEL, SoundCategory.NEUTRAL, 1.0F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
                    player.swingArm(hand);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote && entity instanceof EntityPlayer && isActive(stack)) {
            EntityPlayer player = (EntityPlayer) entity;
            if (stack != player.getHeldItemMainhand() && stack != player.getHeldItemOffhand()) {
                if (!isReelingIn(stack)) {
                    setReelingIn(stack, true);
                }
                if (canLaunchHook(player, stack, world, false, EnumHand.MAIN_HAND)) {
                    setLastLaunchedHookUUID(stack, null);
                }
            }
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

    public static boolean isActive(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("LastLaunchedHookUUIDMost") && tag.hasKey("LastLaunchedHookUUIDLeast");
    }

    @Nullable
    public static UUID getLaunchedHookUUID(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("LastLaunchedHookUUIDMost") && tag.hasKey("LastLaunchedHookUUIDLeast") ? new UUID(tag.getLong("LastLaunchedHookUUIDMost"), tag.getLong("LastLaunchedHookUUIDLeast")) : null;
    }

    public static void setLastLaunchedHookUUID(ItemStack stack, @Nullable UUID uuid) {
        NBTTagCompound tag = getOrCreateTag(stack);
        if (uuid == null) {
            tag.removeTag("LastLaunchedHookUUIDMost");
            tag.removeTag("LastLaunchedHookUUIDLeast");
            tag.removeTag("Reeling");
        } else {
            tag.setLong("LastLaunchedHookUUIDMost", uuid.getMostSignificantBits());
            tag.setLong("LastLaunchedHookUUIDLeast", uuid.getLeastSignificantBits());
        }
    }

    public static boolean isReelingIn(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return isActive(stack) && tag != null && tag.getBoolean("Reeling");
    }

    public static void setReelingIn(ItemStack stack, boolean reeling) {
        getOrCreateTag(stack).setBoolean("Reeling", reeling);
    }

    private boolean isHookLaunchedInWorld(World world, ItemStack stack) {
        UUID hookUUID = getLaunchedHookUUID(stack);
        if (hookUUID != null) {
            for (Entity entity : world.loadedEntityList) {
                if (hookUUID.equals(entity.getUniqueID()) && entity instanceof CandyCaneHookEntity) {
                    CandyCaneHookEntity hook = (CandyCaneHookEntity) entity;
                    return hook.isEntityAlive() && hook.ticksExisted > 0;
                }
            }
        }
        return false;
    }

    public static boolean canLaunchHook(EntityPlayer player, ItemStack stack, World world, boolean checkHands, EnumHand hand) {
        UUID hookUUID = getLaunchedHookUUID(stack);
        if (hookUUID == null) {
            return true;
        }
        for (Entity entity : world.loadedEntityList) {
            if (hookUUID.equals(entity.getUniqueID()) && entity instanceof CandyCaneHookEntity) {
                CandyCaneHookEntity hook = (CandyCaneHookEntity) entity;
                return !(hook.isEntityAlive() && hook.getOwner() == player && (!checkHands || hook.getHandLaunchedFrom() == hand));
            }
        }
        return true;
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
