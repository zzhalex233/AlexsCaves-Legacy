package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.block.MetalScaffoldingBlock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MetalScaffoldingItem extends ItemBlock {
    public MetalScaffoldingItem(Block block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState clicked = worldIn.getBlockState(pos);
        if (clicked.getBlock() instanceof MetalScaffoldingBlock) {
            BlockPos target = pos;
            EnumFacing extend = facing == EnumFacing.UP ? player.getHorizontalFacing() : facing;
            for (int i = 0; i < MetalScaffoldingBlock.STABILITY_MAX_DISTANCE; i++) {
                target = target.offset(extend);
                if (!(worldIn.getBlockState(target).getBlock() instanceof MetalScaffoldingBlock)) {
                    break;
                }
            }
            if (!worldIn.getBlockState(target).getBlock().isReplaceable(worldIn, target)) {
                return EnumActionResult.FAIL;
            }
            return super.onItemUse(player, worldIn, target, hand, facing, hitX, hitY, hitZ);
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
