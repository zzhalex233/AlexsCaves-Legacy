package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.server.misc.ACMath;
import com.zzhalex233.alexscaves.server.misc.VoronoiGenerator;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FrostedChocolateBlock extends BasicCaveBlock {
    private static final VoronoiGenerator VORONOI_GENERATOR = new VoronoiGenerator(42L);
    private static final float COLORIZER_SAMPLE_SCALE = 0.0175F;
    private static final double COLORIZER_BLUR_RADIUS = 0.45F;
    private static final int COLORIZER_EDGE_SIZE_BLOCKS = 8;
    private static final int COLORIZER_R_DIFFERENCE = 5;
    private static final int COLORIZER_G_DIFFERENCE = 20;
    private static final int COLORIZER_B_DIFFERENCE = 50;
    private static final int MIN_SPIRAL_BY = 2;
    private static final int DOUBLE_SPIRAL_BY = 3;

    static {
        VORONOI_GENERATOR.setOffsetAmount(0.35F);
    }

    public FrostedChocolateBlock(float hardness, float resistance, SoundType sound) {
        super(Material.CAKE, hardness, resistance, sound);
        setTickRandomly(true);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, net.minecraft.block.Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        updateFrosting(worldIn, pos);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        updateFrosting(worldIn, pos);
    }

    private void updateFrosting(World world, BlockPos pos) {
        IBlockState above = world.getBlockState(pos.up());
        if (!world.isRemote && above.getBlock() != ACBlockRegistry.BLOCK_OF_FROSTING.block() && above.isFullCube()) {
            world.setBlockState(pos, ACBlockRegistry.BLOCK_OF_CHOCOLATE.block().getDefaultState(), 3);
        }
    }

    public static int calculateFrostingColor(BlockPos pos) {
        if (pos == null) {
            return 0xFFFFFF;
        }
        VoronoiGenerator.VoronoiInfo info = VORONOI_GENERATOR.get2(pos.getX() * COLORIZER_SAMPLE_SCALE, pos.getZ() * COLORIZER_SAMPLE_SCALE);
        if (info.distance >= 0.5F) {
            return 0xFFFFFF;
        }
        double closestDist = Math.min(info.distance, info.distance1);
        double rotateDir = info.hash < 0 ? -1.0D : 1.0D;
        double spiralCount = MIN_SPIRAL_BY + DOUBLE_SPIRAL_BY * (1.0D + info.hash);
        double angle = rotateDir * info.distance * 360.0D * spiralCount;
        double targetSpiralX = Math.sin(0.017453292519943295D * angle);
        double targetSpiralZ = Math.cos(0.017453292519943295D * angle);
        double d0 = targetSpiralX - info.localPos.x;
        double d1 = targetSpiralZ - info.localPos.z;
        double distToTarget = Math.pow(d0 * d0 + d1 * d1, 2.0D);
        double distToCenter = closestDist * 2.0F > COLORIZER_BLUR_RADIUS ? 1.0F - ACMath.smin((float) ((closestDist * 2.0F - COLORIZER_BLUR_RADIUS) / COLORIZER_BLUR_RADIUS), 1.0F, 0.2F) : 1.0F;
        double edgeDistScaled = COLORIZER_SAMPLE_SCALE * COLORIZER_EDGE_SIZE_BLOCKS;
        if (info.distance1 < info.distance + edgeDistScaled) {
            double lessBy = (info.distance + edgeDistScaled - info.distance1) / edgeDistScaled;
            distToCenter *= 1.0F - lessBy;
        }
        int rDec = (int) ACMath.smin((float) (COLORIZER_R_DIFFERENCE * distToTarget * distToCenter), COLORIZER_R_DIFFERENCE, 0.1F);
        int gDec = (int) ACMath.smin((float) (COLORIZER_G_DIFFERENCE * distToTarget * distToCenter), COLORIZER_G_DIFFERENCE, 0.1F);
        int bDec = (int) ACMath.smin((float) (COLORIZER_B_DIFFERENCE * distToTarget * distToCenter), COLORIZER_B_DIFFERENCE, 0.1F);
        return 255 - rDec << 16 | 255 - gDec << 8 | 255 - bDec;
    }
}
