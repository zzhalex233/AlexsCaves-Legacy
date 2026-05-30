package com.zzhalex233.alexscaves.server.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class EdibleBlockItem extends ItemBlock {
    private final ACItemRegistry.Food food;

    public EdibleBlockItem(Block block, ACItemRegistry.Food food) {
        super(block);
        this.food = food;
    }

    public ACItemRegistry.Food food() {
        return food;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        return startEating(worldIn, playerIn, handIn, food);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        return finishEating(this, stack, worldIn, entityLiving, food);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return eatingDuration(food);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }

    public static ActionResult<ItemStack> startEating(World world, EntityPlayer player, EnumHand hand, ACItemRegistry.Food food) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.canEat(food.alwaysEdible())) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public static ItemStack finishEating(Item item, ItemStack stack, World worldIn, EntityLivingBase entityLiving, ACItemRegistry.Food food) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            player.getFoodStats().addStats(food.healAmount(), food.saturation());
            worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            if (!worldIn.isRemote && food.effect() != null && worldIn.rand.nextFloat() < food.effectChance()) {
                player.addPotionEffect(new net.minecraft.potion.PotionEffect(food.effect()));
            }
            StatBase stat = StatList.getObjectUseStats(item);
            if (stat != null) {
                player.addStat(stat);
            }
            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
            }
        }
        stack.shrink(1);
        return stack;
    }

    public static int eatingDuration(ACItemRegistry.Food food) {
        return food.fast() ? 16 : 32;
    }
}
