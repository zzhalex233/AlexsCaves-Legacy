package com.zzhalex233.alexscaves.server.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

public class SackOfSatingItem extends Item {
    public SackOfSatingItem() {
        setMaxStackSize(1);
        addPropertyOverride(new net.minecraft.util.ResourceLocation("open"), (stack, world, entity) -> isChewing(stack, world == null ? 0 : world.getTotalWorldTime()) ? 1.0F : getHunger(stack) > 0 ? 0.5F : 0.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack sack = playerIn.getHeldItem(handIn);
        ItemStack food = findFood(playerIn);
        if (food.isEmpty()) {
            return new ActionResult<>(EnumActionResult.PASS, sack);
        }
        if (!worldIn.isRemote) {
            setHunger(sack, getHunger(sack) + calculateWholeStackHungerValue(food));
            returnContainerItems(playerIn, food);
            food.setCount(0);
            setChewTimestamp(sack, worldIn.getTotalWorldTime());
        }
        playerIn.swingArm(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, sack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        int hunger = getHunger(stack);
        if (!worldIn.isRemote && hunger > 0 && entityIn instanceof EntityPlayer && entityIn.ticksExisted % 100 == 0) {
            EntityPlayer player = (EntityPlayer) entityIn;
            long timestamp = getFeedTimestamp(stack);
            if (!player.capabilities.disableDamage && player.canEat(false) && (timestamp == -1 || worldIn.getTotalWorldTime() - timestamp > 40)) {
                player.getFoodStats().addStats(1, 0.05F);
                setHunger(stack, hunger - 1);
                setFeedTimestamp(stack, worldIn.getTotalWorldTime());
                worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
        if (isChewing(stack, worldIn.getTotalWorldTime()) && entityIn.ticksExisted % 6 == 0) {
            worldIn.playSound(null, entityIn.posX, entityIn.posY, entityIn.posZ, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.3F + 1.3F);
        }
    }

    @Override
    public IRarity getForgeRarity(ItemStack stack) {
        return ACItemRegistry.RARITY_SWEET;
    }

    public static int getHunger(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger("HungerValue");
    }

    public static boolean isChewing(ItemStack stack, long gameTime) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("ChewTimestamp") && gameTime - tag.getLong("ChewTimestamp") < 30;
    }

    private static long getFeedTimestamp(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("FeedTimestamp") ? tag.getLong("FeedTimestamp") : -1;
    }

    private static void setHunger(ItemStack stack, int hunger) {
        getOrCreateTag(stack).setInteger("HungerValue", hunger);
    }

    private static void setChewTimestamp(ItemStack stack, long timestamp) {
        getOrCreateTag(stack).setLong("ChewTimestamp", timestamp);
    }

    private static void setFeedTimestamp(ItemStack stack, long timestamp) {
        getOrCreateTag(stack).setLong("FeedTimestamp", timestamp);
    }

    private static int calculateWholeStackHungerValue(ItemStack foodStack) {
        return foodStack.getItem() instanceof ItemFood ? ((ItemFood) foodStack.getItem()).getHealAmount(foodStack) * foodStack.getCount() : 0;
    }

    private ItemStack findFood(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemFood && stack.getItem() != this) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static void returnContainerItems(EntityPlayer player, ItemStack food) {
        ItemStack remainder = food.getItem().getContainerItem(food);
        if (remainder.isEmpty() && food.getItem() == Items.MUSHROOM_STEW) {
            remainder = new ItemStack(Items.BOWL);
        }
        if (remainder.isEmpty()) {
            return;
        }
        remainder.setCount(food.getCount());
        if (!player.inventory.addItemStackToInventory(remainder)) {
            player.dropItem(remainder, false);
        }
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
