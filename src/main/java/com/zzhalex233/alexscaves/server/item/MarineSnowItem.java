package com.zzhalex233.alexscaves.server.item;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.entity.MusselTileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MarineSnowItem extends Item {
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() == ACBlockRegistry.MUSSEL.block()) {
            MusselTileEntity tile = tile(worldIn, pos);
            if (tile != null && tile.getMussels() < 5) {
                if (!worldIn.isRemote) {
                    tile.setMussels(tile.getMussels() + 1);
                    worldIn.notifyBlockUpdate(pos, state, state, 3);
                    worldIn.playEvent(2005, pos, 0);
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                }
                return EnumActionResult.SUCCESS;
            }
        } else if (state.getBlock() == Blocks.SPONGE) {
            if (!worldIn.isRemote && worldIn.rand.nextBoolean()) {
                BlockPos spawnPos = pos.offset(facing);
                worldIn.spawnEntity(new EntityItem(worldIn, spawnPos.getX() + 0.5D, spawnPos.getY() + 0.5D, spawnPos.getZ() + 0.5D, new ItemStack(Blocks.SPONGE, 1, 1)));
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private MusselTileEntity tile(World world, BlockPos pos) {
        net.minecraft.tileentity.TileEntity tile = world.getTileEntity(pos);
        return tile instanceof MusselTileEntity ? (MusselTileEntity) tile : null;
    }
}
