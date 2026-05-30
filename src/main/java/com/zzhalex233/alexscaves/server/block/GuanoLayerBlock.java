package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.server.entity.item.FallingGuanoEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuanoLayerBlock extends BlockSnow {
    public static final PropertyInteger LAYERS = BlockSnow.LAYERS;

    public GuanoLayerBlock() {
        super();
        setHardness(0.3F);
        setResistance(0.3F);
        setSoundType(SoundType.SNOW);
        setDefaultState(blockState.getBaseState().withProperty(LAYERS, 1));
        setTickRandomly(true);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return true;
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
        if (!worldIn.isRemote && isFree(worldIn.getBlockState(pos.down()))) {
            FallingGuanoEntity.fall(worldIn, pos, state);
        }
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getValue(LAYERS) == 1;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (GuanoBlock.isForlornEntity(entityIn)) {
            entityIn.motionX *= 0.9D;
            entityIn.motionZ *= 0.9D;
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ACItemRegistry.GUANO.item();
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return state.getValue(LAYERS);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(40) == 0) {
            com.zzhalex233.alexscaves.client.particle.FlyParticle.spawn(worldIn, pos, rand);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LAYERS);
    }

    public static boolean isFree(IBlockState state) {
        return state.getBlock() == ACBlockRegistry.GUANO_LAYER.block() && state.getValue(LAYERS) < 8 || BlockFalling.canFallThrough(state);
    }
}
