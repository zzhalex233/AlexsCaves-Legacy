package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SulfurBudBlock extends BlockDirectional {
    private final AxisAlignedBB shapeUp;
    private final AxisAlignedBB shapeDown;
    private final AxisAlignedBB shapeWest;
    private final AxisAlignedBB shapeEast;
    private final AxisAlignedBB shapeNorth;
    private final AxisAlignedBB shapeSouth;
    private final int minDust;
    private final int maxDust;

    public SulfurBudBlock(int pixWidth, int pixHeight, int minDust, int maxDust) {
        super(Material.ROCK);
        setHardness(1.0F);
        setResistance(2.0F);
        setSoundType(SoundType.STONE);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        this.minDust = minDust;
        this.maxDust = maxDust;
        double half = pixWidth / 32.0D;
        double height = pixHeight / 16.0D;
        this.shapeUp = new AxisAlignedBB(0.5D - half, 0.0D, 0.5D - half, 0.5D + half, height, 0.5D + half);
        this.shapeDown = new AxisAlignedBB(0.5D - half, 1.0D - height, 0.5D - half, 0.5D + half, 1.0D, 0.5D + half);
        this.shapeNorth = new AxisAlignedBB(0.5D - half, 0.5D - half, 0.0D, 0.5D + half, 0.5D + half, height);
        this.shapeSouth = new AxisAlignedBB(0.5D - half, 0.5D - half, 1.0D - height, 0.5D + half, 0.5D + half, 1.0D);
        this.shapeEast = new AxisAlignedBB(0.0D, 0.5D - half, 0.5D - half, height, 0.5D + half, 0.5D + half);
        this.shapeWest = new AxisAlignedBB(1.0D - height, 0.5D - half, 0.5D - half, 1.0D, 0.5D + half, 0.5D + half);
    }

    public SulfurBudBlock sound(SoundType soundType) {
        setSoundType(soundType);
        return this;
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, net.minecraft.entity.EntityLivingBase placer, net.minecraft.util.EnumHand hand) {
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
        if (worldIn.isRemote || rand.nextInt(3) != 0 || !isDrippingAcidAbove(worldIn, pos)) {
            return;
        }
        Block next = nextStage();
        if (next != null) {
            worldIn.setBlockState(pos, next.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
        }
    }

    private Block nextStage() {
        if (this == ACBlockRegistry.SULFUR_BUD_SMALL.block()) {
            return ACBlockRegistry.SULFUR_BUD_MEDIUM.block();
        }
        if (this == ACBlockRegistry.SULFUR_BUD_MEDIUM.block()) {
            return ACBlockRegistry.SULFUR_BUD_LARGE.block();
        }
        return this == ACBlockRegistry.SULFUR_BUD_LARGE.block() ? ACBlockRegistry.SULFUR_CLUSTER.block() : null;
    }

    private boolean isDrippingAcidAbove(World world, BlockPos pos) {
        pos = pos.up();
        while (world.isAirBlock(pos) && pos.getY() < world.getHeight()) {
            pos = pos.up();
        }
        Block block = world.getBlockState(pos).getBlock();
        return block == ACBlockRegistry.ACID.block() || block == ACBlockRegistry.ACIDIC_RADROCK.block();
    }

    private boolean canAttachTo(World world, BlockPos pos, EnumFacing side) {
        return world.getBlockState(pos).isSideSolid(world, pos, side);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int count = minDust + RANDOM.nextInt(maxDust - minDust + 1);
        if (fortune > 0) {
            count += RANDOM.nextInt(fortune + 1);
        }
        drops.add(new ItemStack(ACItemRegistry.SULFUR_DUST.item(), Math.min(5, count)));
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, net.minecraft.entity.player.EntityPlayer player) {
        return true;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ACItemRegistry.SULFUR_DUST.item();
    }

    @Override
    public int quantityDropped(Random random) {
        return minDust + random.nextInt(maxDust - minDust + 1);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return Math.min(5, quantityDropped(random) + (fortune > 0 ? random.nextInt(fortune + 1) : 0));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case DOWN:
                return shapeDown;
            case EAST:
                return shapeEast;
            case WEST:
                return shapeWest;
            case NORTH:
                return shapeNorth;
            case SOUTH:
                return shapeSouth;
            case UP:
            default:
                return shapeUp;
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
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
