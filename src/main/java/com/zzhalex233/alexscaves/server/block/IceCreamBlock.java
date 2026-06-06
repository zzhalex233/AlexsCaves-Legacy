package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IceCreamBlock extends BlockFalling {
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 2);
    private static final AxisAlignedBB DRIPPING_AABB = new AxisAlignedBB(0.0D, 0.125D, 0.0D, 1.0D, 1.0D, 1.0D);

    public IceCreamBlock() {
        super(Material.CAKE);
        setHardness(3.0F);
        setResistance(3.0F);
        setSoundType(SoundType.SLIME);
        setDefaultState(blockState.getBaseState().withProperty(TYPE, 0));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            if (BlockFalling.canFallThrough(worldIn.getBlockState(pos.down())) && pos.getY() >= 0) {
                worldIn.setBlockToAir(pos);
                worldIn.spawnEntity(new EntityFallingBlock(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state));
            } else {
                IBlockState typed = state.withProperty(TYPE, getType(worldIn, pos));
                if (typed != state) {
                    worldIn.setBlockState(pos, typed, 3);
                }
            }
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, net.minecraft.entity.EntityLivingBase placer) {
        return getDefaultState().withProperty(TYPE, getType(world, pos));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(TYPE) == 2 ? DRIPPING_AABB : FULL_BLOCK_AABB;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, Math.max(0, Math.min(2, meta)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getValue(TYPE) != 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    private int getType(IBlockAccess world, BlockPos pos) {
        IBlockState below = world.getBlockState(pos.down());
        if (below.getBlock() == this) {
            return 0;
        }
        return below.isSideSolid(world, pos.down(), EnumFacing.UP) ? 1 : 2;
    }
}
