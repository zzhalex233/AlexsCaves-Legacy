package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.block.SprinklesBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SprinklesItem extends EdibleBlockItem {
    public SprinklesItem(Block block, ACItemRegistry.Food food) {
        super(block, food);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() == block) {
            int amount = state.getValue(SprinklesBlock.AMOUNT);
            if (amount < 4) {
                IBlockState placed = state.withProperty(SprinklesBlock.AMOUNT, amount + 1);
                if (worldIn.setBlockState(pos, placed, 11)) {
                    SoundType sound = placed.getBlock().getSoundType(placed, worldIn, pos, player);
                    worldIn.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
