package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.block.entity.ACSignTileEntity;

import net.minecraft.block.BlockWallSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CaveWallSignBlock extends BlockWallSign {
    private final String woodName;

    public CaveWallSignBlock(String woodName) {
        this.woodName = woodName;
        setHardness(1.0F);
        setSoundType(SoundType.WOOD);
    }

    public String getWoodName() {
        return woodName;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new ACSignTileEntity();
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune) {
        return ACBlockRegistry.signItemFor(woodName);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(ACBlockRegistry.signItemFor(woodName));
    }
}
