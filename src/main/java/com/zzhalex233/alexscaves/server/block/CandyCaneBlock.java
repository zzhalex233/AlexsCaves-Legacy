package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CandyCaneBlock extends BlockRotatedPillar {
    private final Block strippedBlock;

    public CandyCaneBlock(Block strippedBlock) {
        super(Material.CAKE);
        this.strippedBlock = strippedBlock;
        setHardness(2.0F);
        setResistance(3.0F);
        setSoundType(ACSoundTypes.HARD_CANDY);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (strippedBlock == null || stack.getItem() != Items.IRON_AXE && stack.getItem() != Items.WOODEN_AXE && stack.getItem() != Items.STONE_AXE && stack.getItem() != Items.DIAMOND_AXE && stack.getItem() != Items.GOLDEN_AXE) {
            return false;
        }
        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, strippedBlock.getDefaultState().withProperty(AXIS, state.getValue(AXIS)), 11);
            stack.damageItem(1, playerIn);
        }
        return true;
    }

    public static CandyCaneBlock stripped() {
        return new CandyCaneBlock(null);
    }
}
