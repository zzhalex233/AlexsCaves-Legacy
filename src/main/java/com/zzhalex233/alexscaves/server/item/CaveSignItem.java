package com.zzhalex233.alexscaves.server.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CaveSignItem extends Item {
    private final BlockStandingSign standingSign;
    private final BlockWallSign wallSign;

    public CaveSignItem(BlockStandingSign standingSign, BlockWallSign wallSign) {
        this.standingSign = standingSign;
        this.wallSign = wallSign;
        setMaxStackSize(16);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState clicked = world.getBlockState(pos);
        BlockPos placePos = clicked.getBlock().isReplaceable(world, pos) ? pos : pos.offset(facing);
        Block signBlock = facing == EnumFacing.UP ? standingSign : wallSign;
        if (facing == EnumFacing.DOWN || !player.canPlayerEdit(placePos, facing, stack) || !world.mayPlace(signBlock, placePos, false, facing, player)) {
            return EnumActionResult.FAIL;
        }
        IBlockState state = facing == EnumFacing.UP
            ? standingSign.getDefaultState().withProperty(BlockStandingSign.ROTATION, MathHelper.floor((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15)
            : wallSign.getDefaultState().withProperty(BlockWallSign.FACING, facing);
        if (!world.setBlockState(placePos, state, 11)) {
            return EnumActionResult.FAIL;
        }
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(placePos);
            if (tile instanceof TileEntitySign) {
                ((TileEntitySign) tile).setPlayer(player);
                player.openEditSign((TileEntitySign) tile);
            }
        }
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }
}
