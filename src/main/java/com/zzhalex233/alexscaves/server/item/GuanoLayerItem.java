package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.block.GuanoLayerBlock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuanoLayerItem extends ItemBlock {
    public GuanoLayerItem(Block block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() == block) {
            int layers = state.getValue(GuanoLayerBlock.LAYERS);
            if (layers < 8) {
                IBlockState placed = state.withProperty(GuanoLayerBlock.LAYERS, layers + 1);
                AxisAlignedBB collision = placed.getCollisionBoundingBox(worldIn, pos);
                if ((collision == null || worldIn.checkNoEntityCollision(collision.offset(pos))) && worldIn.setBlockState(pos, placed, 11)) {
                    playPlaceSound(worldIn, pos, placed);
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    private void playPlaceSound(World world, BlockPos pos, IBlockState state) {
        net.minecraft.block.SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
        world.playSound(null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
    }
}
