package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.AlexsCaves;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class CaveBookItem extends Item {
    public CaveBookItem() {
        setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        playerIn.swingArm(handIn);
        if (worldIn.isRemote) {
            AlexsCaves.PROXY.openBookGUI(stack);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("item.alexscaves.cave_book.desc"));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (ACTabbedItem.isAlexsCavesTab(tab)) {
            items.add(new ItemStack(this));
        }
    }
}
