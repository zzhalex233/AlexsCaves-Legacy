package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AcidicRadrockBlock extends BasicCaveBlock {
    public AcidicRadrockBlock() {
        super(Material.ROCK, 2.5F, 7.0F, SoundType.STONE);
        setLightLevel(0.25F);
    }

    @Override
    public void harvestBlock(World worldIn, net.minecraft.entity.player.EntityPlayer player, BlockPos pos, IBlockState state, net.minecraft.tileentity.TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (!worldIn.isRemote && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0 && worldIn.rand.nextInt(3) == 0) {
            worldIn.setBlockState(pos, ACBlockRegistry.ACID.block().getDefaultState(), 3);
        }
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        EnumFacing facing = EnumFacing.values()[rand.nextInt(EnumFacing.values().length)];
        if (facing == EnumFacing.UP) {
            return;
        }
        BlockPos offset = pos.offset(facing);
        IBlockState neighbor = worldIn.getBlockState(offset);
        if (!neighbor.isSideSolid(worldIn, offset, facing.getOpposite())) {
            double x = pos.getX() + (facing.getXOffset() == 0 ? rand.nextDouble() : 0.5D + facing.getXOffset() * 0.6D);
            double y = pos.getY() + (facing.getYOffset() == 0 ? rand.nextDouble() : 0.5D + facing.getYOffset() * 0.6D);
            double z = pos.getZ() + (facing.getZOffset() == 0 ? rand.nextDouble() : 0.5D + facing.getZOffset() * 0.6D);
            worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 0.45D, 0.9D, 0.05D);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
