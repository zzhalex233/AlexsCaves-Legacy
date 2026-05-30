package com.zzhalex233.alexscaves.server.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DrinkableBottledItem extends ACFoodItem {
    public DrinkableBottledItem(ACItemRegistry.Food food) {
        super(food);
        setMaxStackSize(16);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        ItemStack result = super.onItemUseFinish(stack, worldIn, entityLiving);
        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
        if (result.isEmpty()) {
            return bottle;
        }
        if (entityLiving instanceof EntityPlayer && !((EntityPlayer) entityLiving).capabilities.isCreativeMode) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            if (!player.inventory.addItemStackToInventory(bottle)) {
                player.dropItem(bottle, false);
            }
        }
        return result;
    }
}
