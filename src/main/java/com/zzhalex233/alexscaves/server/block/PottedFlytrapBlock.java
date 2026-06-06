package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.client.particle.FlyParticle;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PottedFlytrapBlock extends PottedCavePlantBlock {
    public static final PropertyBool OPEN = PropertyBool.create("open");

    public PottedFlytrapBlock(Block plantBlock) {
        super(plantBlock);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(OPEN, true));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        if (state.getValue(OPEN)) {
            worldIn.setBlockState(pos, state.withProperty(OPEN, false), 2);
            worldIn.scheduleUpdate(pos, this, 100 + rand.nextInt(100));
        } else {
            worldIn.setBlockState(pos, state.withProperty(OPEN, true), 2);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(OPEN) && rand.nextInt(3) == 0) {
            FlyParticle.spawn(worldIn, pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(OPEN) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(OPEN, (meta & 1) == 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, OPEN);
    }
}
