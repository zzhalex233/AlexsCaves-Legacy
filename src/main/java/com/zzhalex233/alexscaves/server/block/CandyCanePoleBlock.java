package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CandyCanePoleBlock extends Block {
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    private static final AxisAlignedBB POLE = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
    private final Block strippedBlock;

    public CandyCanePoleBlock(Block strippedBlock) {
        super(Material.CAKE);
        this.strippedBlock = strippedBlock;
        setHardness(0.0F);
        setResistance(0.0F);
        setSoundType(ACSoundTypes.HARD_CANDY);
        setDefaultState(blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(NORTH, connectsTo(worldIn, pos, EnumFacing.NORTH))
                .withProperty(EAST, connectsTo(worldIn, pos, EnumFacing.EAST))
                .withProperty(SOUTH, connectsTo(worldIn, pos, EnumFacing.SOUTH))
                .withProperty(WEST, connectsTo(worldIn, pos, EnumFacing.WEST));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (strippedBlock == null || stack.getItem() != Items.IRON_AXE && stack.getItem() != Items.WOODEN_AXE && stack.getItem() != Items.STONE_AXE && stack.getItem() != Items.DIAMOND_AXE && stack.getItem() != Items.GOLDEN_AXE) {
            return false;
        }
        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, strippedBlock.getDefaultState(), 11);
            stack.damageItem(1, playerIn);
        }
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return POLE;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST);
    }

    private boolean connectsTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return world.getBlockState(pos.offset(facing)).getBlock() instanceof CandyCanePoleBlock
                && !(world.getBlockState(pos.up()).getBlock() instanceof CandyCanePoleBlock)
                && !(world.getBlockState(pos.offset(facing).up()).getBlock() instanceof CandyCanePoleBlock);
    }

    public static CandyCanePoleBlock stripped() {
        return new CandyCanePoleBlock(null);
    }
}
