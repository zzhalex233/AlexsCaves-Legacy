package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.block.entity.TeslaBulbTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TeslaBulbBlock extends BlockContainer {
    public static final PropertyBool DOWN = PropertyBool.create("down");
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.1875D, 0.0625D, 0.1875D, 0.6875D, 0.9375D, 0.6875D);

    public TeslaBulbBlock() {
        super(Material.GLASS);
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.GLASS);
        setLightLevel(1.0F);
        setDefaultState(blockState.getBaseState().withProperty(DOWN, false));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TeslaBulbTileEntity();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(DOWN, facing == EnumFacing.DOWN);
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return canAttachTo(worldIn, pos, side == EnumFacing.DOWN);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return canAttachTo(worldIn, pos, false) || canAttachTo(worldIn, pos, true);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canAttachTo(worldIn, pos, state.getValue(DOWN))) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        explode(worldIn, pos);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        explode(world, pos);
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, net.minecraft.entity.Entity entityIn) {
        if (entityIn instanceof EntityArrow || entityIn instanceof EntityThrowable) {
            explode(worldIn, pos);
        }
    }

    private void explode(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TeslaBulbTileEntity) {
            ((TeslaBulbTileEntity) tile).explode();
        }
    }

    private boolean canAttachTo(World world, BlockPos pos, boolean down) {
        EnumFacing face = down ? EnumFacing.UP : EnumFacing.DOWN;
        BlockPos support = pos.offset(down ? EnumFacing.UP : EnumFacing.DOWN);
        IBlockState state = world.getBlockState(support);
        Block block = state.getBlock();
        return state.isSideSolid(world, support, face)
                || block == ACBlockRegistry.GALENA_PILLAR.block()
                || block == ACBlockRegistry.ENERGIZED_GALENA_NEUTRAL.block()
                || block == ACBlockRegistry.ENERGIZED_GALENA_SCARLET.block()
                || block == ACBlockRegistry.ENERGIZED_GALENA_AZURE.block()
                || GalenaSpireBlock.isGalenaSpireConnectable(state, down);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
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
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DOWN) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(DOWN, meta == 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DOWN);
    }
}
