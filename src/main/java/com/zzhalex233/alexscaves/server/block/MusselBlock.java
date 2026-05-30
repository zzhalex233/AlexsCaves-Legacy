package com.zzhalex233.alexscaves.server.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zzhalex233.alexscaves.server.block.entity.MusselTileEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MusselBlock extends BlockDirectional {
    public static final PropertyInteger MUSSELS = PropertyInteger.create("mussels", 1, 5);
    private static final AxisAlignedBB SHAPE_UP = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.25D, 0.9375D);
    private static final AxisAlignedBB SHAPE_DOWN = new AxisAlignedBB(0.0625D, 0.75D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
    private static final AxisAlignedBB SHAPE_WEST = new AxisAlignedBB(0.75D, 0.0625D, 0.0625D, 1.0D, 0.9375D, 0.9375D);
    private static final AxisAlignedBB SHAPE_EAST = new AxisAlignedBB(0.0D, 0.0625D, 0.0625D, 0.25D, 0.9375D, 0.9375D);
    private static final AxisAlignedBB SHAPE_NORTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.75D, 0.9375D, 0.9375D, 1.0D);
    private static final AxisAlignedBB SHAPE_SOUTH = new AxisAlignedBB(0.0625D, 0.0625D, 0.0D, 0.9375D, 0.9375D, 0.25D);

    public MusselBlock() {
        super(Material.ROCK);
        setHardness(1.0F);
        setResistance(1.0F);
        setSoundType(SoundType.STONE);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(MUSSELS, 1));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new MusselTileEntity();
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return canAttachTo(worldIn, pos.offset(side.getOpposite()), side);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (canPlaceBlockOnSide(worldIn, pos, facing)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, canPlaceBlockOnSide(world, pos, facing) ? facing : EnumFacing.UP);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canAttachTo(worldIn, pos.offset(state.getValue(FACING).getOpposite()), state.getValue(FACING))) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote || rand.nextInt(20) != 0 || !canGrowOn(worldIn.getBlockState(pos.offset(state.getValue(FACING).getOpposite())))) {
            return;
        }
        MusselTileEntity tile = tile(worldIn, pos);
        if (tile == null) {
            return;
        }
        if (tile.getMussels() < 5) {
            tile.setMussels(tile.getMussels() + 1);
            sync(worldIn, pos, state);
            return;
        }
        trySpread(worldIn, pos, rand);
    }

    private void trySpread(World world, BlockPos pos, Random rand) {
        BlockPos target = pos.add(rand.nextInt(7) - 3, rand.nextInt(7) - 3, rand.nextInt(7) - 3);
        if (world.getBlockState(target).getBlock() != Blocks.WATER && !world.isAirBlock(target)) {
            return;
        }
        List<EnumFacing> facings = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.values()) {
            if (canAttachTo(world, target.offset(facing.getOpposite()), facing)) {
                facings.add(facing);
            }
        }
        if (!facings.isEmpty()) {
            world.setBlockState(target, getDefaultState().withProperty(FACING, facings.get(rand.nextInt(facings.size()))), 3);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = playerIn.getHeldItem(hand);
        if (held.getItem() != Item.getItemFromBlock(this)) {
            return false;
        }
        MusselTileEntity tile = tile(worldIn, pos);
        if (tile == null || tile.getMussels() >= 5) {
            return false;
        }
        if (!worldIn.isRemote) {
            tile.setMussels(tile.getMussels() + 1);
            if (!playerIn.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            sync(worldIn, pos, state);
        }
        return true;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        MusselTileEntity tile = tile(world, pos);
        if (!player.capabilities.isCreativeMode && tile != null && tile.getMussels() > 1) {
            if (!world.isRemote) {
                dropOneMussel(world, pos, world.rand, 0);
                tile.setMussels(tile.getMussels() - 1);
                sync(world, pos, state);
                world.playEvent(2001, pos, Block.getStateId(state));
            }
            return false;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Random random = world instanceof World ? ((World) world).rand : RANDOM;
        drops.add(random.nextFloat() < pearlChance(fortune) ? new ItemStack(ACItemRegistry.PEARL.item()) : new ItemStack(Item.getItemFromBlock(this)));
    }

    private void dropOneMussel(World world, BlockPos pos, Random random, int fortune) {
        Block.spawnAsEntity(world, pos, random.nextFloat() < pearlChance(fortune) ? new ItemStack(ACItemRegistry.PEARL.item()) : new ItemStack(Item.getItemFromBlock(this)));
    }

    private float pearlChance(int fortune) {
        return fortune <= 0 ? 0.1F : fortune == 1 ? 0.15F : fortune == 2 ? 0.2F : 0.3F;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        MusselTileEntity tile = tile(worldIn, pos);
        return state.withProperty(MUSSELS, tile == null ? 1 : tile.getMussels());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case DOWN:
                return SHAPE_DOWN;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case UP:
            default:
                return SHAPE_UP;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
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
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
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
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta)).withProperty(MUSSELS, 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, MUSSELS);
    }

    private boolean canAttachTo(World world, BlockPos pos, EnumFacing facing) {
        return world.getBlockState(pos).isSideSolid(world, pos, facing);
    }

    private boolean canGrowOn(IBlockState state) {
        Block block = state.getBlock();
        return state.getMaterial() == Material.WOOD || block == Blocks.PLANKS || block == Blocks.LOG || block == Blocks.LOG2 || block == ACBlockRegistry.GUANOSTONE.block() || block == ACBlockRegistry.ABYSSMARINE.block();
    }

    private MusselTileEntity tile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof MusselTileEntity ? (MusselTileEntity) tile : null;
    }

    private void sync(World world, BlockPos pos, IBlockState state) {
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }
}
