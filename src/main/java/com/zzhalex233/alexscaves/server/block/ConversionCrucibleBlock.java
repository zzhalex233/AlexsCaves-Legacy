package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.block.entity.ConversionCrucibleTileEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.BiomeTreatItem;
import com.zzhalex233.alexscaves.server.item.CaveInfoItem;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ConversionCrucibleBlock extends BlockContainer {
    private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    public static final AxisAlignedBB SUCK_AABB = new AxisAlignedBB(0.1875D, 0.125D, 0.1875D, 0.8125D, 1.25D, 0.8125D);

    public ConversionCrucibleBlock() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(12.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new ConversionCrucibleTileEntity();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDS;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof ConversionCrucibleTileEntity)) {
            return false;
        }
        ConversionCrucibleTileEntity crucible = (ConversionCrucibleTileEntity) tileEntity;
        ItemStack heldStack = player.getHeldItem(hand);
        if (crucible.getConvertingToBiome() == null) {
            String caveBiome = CaveInfoItem.getCaveBiome(heldStack);
            if (heldStack.getItem() == ACItemRegistry.BIOME_TREAT.item() && caveBiome != null && !caveBiome.isEmpty()) {
                if (!world.isRemote) {
                    crucible.setConvertingToBiome(caveBiome);
                    crucible.setFilledLevel(1);
                    crucible.rerollWantedItem();
                    crucible.markUpdated();
                    if (!player.capabilities.isCreativeMode) {
                        heldStack.shrink(1);
                    }
                }
                return true;
            }
            return false;
        }
        if (crucible.getWantItem().isEmpty()) {
            if (!world.isRemote) {
                crucible.rerollWantedItem();
                crucible.markUpdated();
            }
            return true;
        }
        if (!heldStack.isEmpty() && crucible.matchesWantedItem(heldStack)) {
            if (!world.isRemote) {
                ItemStack copy = heldStack.copy();
                copy.setCount(1);
                crucible.consumeItem(copy);
                if (!player.capabilities.isCreativeMode) {
                    heldStack.shrink(1);
                }
                crucible.markUpdated();
            }
            return true;
        }
        return false;
    }
}
