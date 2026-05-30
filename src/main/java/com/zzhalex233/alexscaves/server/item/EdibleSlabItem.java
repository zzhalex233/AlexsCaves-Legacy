package com.zzhalex233.alexscaves.server.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EdibleSlabItem extends ItemSlab {
    private final ACItemRegistry.Food food;

    public EdibleSlabItem(Block block, BlockSlab singleSlab, BlockSlab doubleSlab, ACItemRegistry.Food food) {
        super(block, singleSlab, doubleSlab);
        this.food = food;
    }

    public ACItemRegistry.Food food() {
        return food;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        return EdibleBlockItem.startEating(worldIn, playerIn, handIn, food);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        return EdibleBlockItem.finishEating(this, stack, worldIn, entityLiving, food);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return EdibleBlockItem.eatingDuration(food);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }
}
