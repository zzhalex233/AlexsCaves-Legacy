package com.zzhalex233.alexscaves.server.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ACFoodItem extends ItemFood {
    private final ACItemRegistry.Food food;

    public ACFoodItem(ACItemRegistry.Food food) {
        super(food.healAmount(), food.saturation(), food.meat());
        this.food = food;
        if (food.effect() != null) {
            setPotionEffect(food.effect(), food.effectChance());
        }
        if (food.alwaysEdible()) {
            setAlwaysEdible();
        }
        if (food.bowlRemainder()) {
            setMaxStackSize(1);
        }
    }

    public ACItemRegistry.Food food() {
        return food;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return food.fast() ? 16 : super.getMaxItemUseDuration(stack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return food.drink() ? EnumAction.DRINK : super.getItemUseAction(stack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        ItemStack result = super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!food.bowlRemainder()) {
            return result;
        }
        ItemStack bowl = new ItemStack(Items.BOWL);
        if (result.isEmpty()) {
            return bowl;
        }
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            if (!player.inventory.addItemStackToInventory(bowl)) {
                player.dropItem(bowl, false);
            }
        }
        return result;
    }
}
