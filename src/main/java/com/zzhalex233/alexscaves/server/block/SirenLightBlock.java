package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.block.entity.SirenLightTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class SirenLightBlock extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    private static final AxisAlignedBB SHAPE_UP = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.625D, 0.8125D);
    private static final AxisAlignedBB SHAPE_DOWN = new AxisAlignedBB(0.1875D, 0.375D, 0.1875D, 0.8125D, 1.0D, 0.8125D);
    private static final AxisAlignedBB SHAPE_NORTH = new AxisAlignedBB(0.1875D, 0.1875D, 0.375D, 0.8125D, 0.8125D, 1.0D);
    private static final AxisAlignedBB SHAPE_SOUTH = new AxisAlignedBB(0.1875D, 0.1875D, 0.0D, 0.8125D, 0.8125D, 0.625D);
    private static final AxisAlignedBB SHAPE_EAST = new AxisAlignedBB(0.0D, 0.1875D, 0.1875D, 0.625D, 0.8125D, 0.8125D);
    private static final AxisAlignedBB SHAPE_WEST = new AxisAlignedBB(0.375D, 0.1875D, 0.1875D, 1.0D, 0.8125D, 0.8125D);

    public SirenLightBlock() {
        super(Material.GLASS);
        setHardness(1.0F);
        setResistance(12.0F);
        setSoundType(SoundType.GLASS);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(POWERED, false));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new SirenLightTileEntity();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, facing).withProperty(POWERED, world.isBlockPowered(pos));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        updatePower(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        updatePower(worldIn, pos, state);
    }

    private void updatePower(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            boolean powered = world.isBlockPowered(pos);
            if (powered != state.getValue(POWERED)) {
                world.setBlockState(pos, state.withProperty(POWERED, powered), 3);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = playerIn.getHeldItem(hand);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof SirenLightTileEntity) || playerIn.isSneaking() || !(held.getItem() instanceof ItemDye)) {
            return false;
        }
        int color = EnumDyeColor.byDyeDamage(held.getMetadata()).getColorValue();
        if (!worldIn.isRemote && ((SirenLightTileEntity) tile).setColor(color)) {
            worldIn.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, net.minecraft.util.SoundCategory.BLOCKS, 0.8F, 1.2F);
            if (!playerIn.capabilities.isCreativeMode) {
                held.shrink(1);
            }
        }
        return true;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(POWERED) ? 10 : 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case DOWN: return SHAPE_DOWN;
            case NORTH: return SHAPE_NORTH;
            case SOUTH: return SHAPE_SOUTH;
            case WEST: return SHAPE_WEST;
            case EAST: return SHAPE_EAST;
            case UP:
            default: return SHAPE_UP;
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7)).withProperty(POWERED, (meta & 8) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() | (state.getValue(POWERED) ? 8 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, POWERED);
    }
}
