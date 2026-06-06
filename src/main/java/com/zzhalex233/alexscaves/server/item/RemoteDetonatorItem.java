package com.zzhalex233.alexscaves.server.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockTNT;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class RemoteDetonatorItem extends Item {
    public RemoteDetonatorItem() {
        setMaxStackSize(1);
        addPropertyOverride(new net.minecraft.util.ResourceLocation("active"), (stack, world, entity) -> isActive(stack) ? 1.0F : 0.0F);
    }

    public static boolean isActive(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("BombX") && stack.getTagCompound().hasKey("BombY") && stack.getTagCompound().hasKey("BombZ");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.getBlockState(pos).getBlock() != Blocks.TNT) {
            return EnumActionResult.PASS;
        }
        world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 1.0F, 1.2F);
        if (!world.isRemote) {
            ItemStack target = player.capabilities.isCreativeMode ? stack.copy() : stack;
            bind(target, world, pos);
            if (player.capabilities.isCreativeMode) {
                target.setCount(1);
                if (!player.inventory.addItemStackToInventory(target)) {
                    player.dropItem(target, false);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!isActive(stack)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!world.isRemote) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.getInteger("BombDimension") == world.provider.getDimension()) {
                BlockPos pos = new BlockPos(tag.getInteger("BombX"), tag.getInteger("BombY"), tag.getInteger("BombZ"));
                if (world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() == Blocks.TNT) {
                    ((BlockTNT) Blocks.TNT).explode(world, pos, world.getBlockState(pos).withProperty(BlockTNT.EXPLODE, true), player);
                    world.setBlockToAir(pos);
                }
            }
            tag.removeTag("BombX");
            tag.removeTag("BombY");
            tag.removeTag("BombZ");
            tag.removeTag("BombDimension");
            stack.setTagCompound(tag);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private static void bind(ItemStack stack, World world, BlockPos pos) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        tag.setInteger("BombX", pos.getX());
        tag.setInteger("BombY", pos.getY());
        tag.setInteger("BombZ", pos.getZ());
        tag.setInteger("BombDimension", world.provider.getDimension());
        stack.setTagCompound(tag);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (isActive(stack)) {
            NBTTagCompound tag = stack.getTagCompound();
            tooltip.add(TextFormatting.GRAY + I18n.format("item.alexscaves.remote_detonator.desc", tag.getInteger("BombX"), tag.getInteger("BombY"), tag.getInteger("BombZ")));
        }
    }
}
