package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.block.entity.AbyssalAltarTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AbyssalAltarBlock extends Block {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public AbyssalAltarBlock() {
        super(Material.ROCK);
        setHardness(2.5F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new AbyssalAltarTileEntity();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
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
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(ACTIVE) ? 5 : 0;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof AbyssalAltarTileEntity)) {
            return false;
        }
        ItemStack held = player.getHeldItem(hand);
        AbyssalAltarTileEntity altar = (AbyssalAltarTileEntity) tile;
        if (world.isRemote) {
            return true;
        }
        if (altar.getStackInSlot(0).isEmpty()) {
            if (held.isEmpty()) {
                return true;
            }
            ItemStack copy = held.copy();
            copy.setCount(1);
            altar.setInventorySlotContents(0, copy);
            altar.onEntityInteract(player, false);
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
        } else if (altar.queueItemDrop(altar.getStackInSlot(0).copy())) {
            altar.onEntityInteract(player, true);
            altar.setInventorySlotContents(0, ItemStack.EMPTY);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IInventory) {
            InventoryHelper.dropInventoryItems(world, pos, (IInventory) tile);
            world.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof IInventory ? net.minecraft.inventory.Container.calcRedstoneFromInventory((IInventory) tile) : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE, (meta & 1) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }
}
