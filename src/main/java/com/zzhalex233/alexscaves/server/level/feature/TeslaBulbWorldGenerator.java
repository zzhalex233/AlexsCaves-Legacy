package com.zzhalex233.alexscaves.server.level.feature;

import java.util.Random;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.GalenaSpireBlock;
import com.zzhalex233.alexscaves.server.block.TeslaBulbBlock;
import com.zzhalex233.alexscaves.server.config.ACConfig;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TeslaBulbWorldGenerator implements IWorldGenerator {
    private static final int MAX_Y = 60;

    public static void register() {
        GameRegistry.registerWorldGenerator(new TeslaBulbWorldGenerator(), 80);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!ACConfig.isCaveGenerationEnabled() || world.isRemote || world.provider.getDimension() != 0) {
            return;
        }
        int count = 1 + random.nextInt(12);
        for (int i = 0; i < count; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int y = random.nextInt(MAX_Y + 1);
            int z = chunkZ * 16 + random.nextInt(16);
            BlockPos origin = new BlockPos(x, y, z);
            if (y < world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY()) {
                place(world, random, origin);
            }
        }
    }

    private static boolean place(World world, Random random, BlockPos origin) {
        boolean ceiling = random.nextBoolean();
        BlockPos generateAt = origin;
        IBlockState originState = world.getBlockState(generateAt);
        if (!isWater(originState) && !world.isAirBlock(generateAt)) {
            return false;
        }
        if (ceiling) {
            while ((isWater(world.getBlockState(generateAt)) || !world.getBlockState(generateAt).isSideSolid(world, generateAt, EnumFacing.DOWN)) && generateAt.getY() < world.getHeight() - 1) {
                generateAt = generateAt.up();
            }
        } else {
            while ((isWater(world.getBlockState(generateAt)) || !world.getBlockState(generateAt).isSideSolid(world, generateAt, EnumFacing.UP)) && generateAt.getY() > 1) {
                generateAt = generateAt.down();
            }
        }
        if (!isTeslaBulbBase(world.getBlockState(generateAt))) {
            return false;
        }
        int centerHeight = 3 + random.nextInt(3);
        generatePillar(world, generateAt, random, centerHeight, ceiling, random.nextFloat() < 0.25F);
        for (int i = 0; i < 4 + random.nextInt(4); i++) {
            BlockPos offset = generateAt.add(random.nextInt(8) - 4, ceiling ? -3 : 3, random.nextInt(8) - 4);
            if (ceiling) {
                while (!world.getBlockState(offset).isSideSolid(world, offset, EnumFacing.DOWN) && offset.getY() < world.getHeight() - 1) {
                    offset = offset.up();
                }
            } else {
                while (!world.getBlockState(offset).isSideSolid(world, offset, EnumFacing.UP) && offset.getY() > 1) {
                    offset = offset.down();
                }
            }
            if (!isTeslaBulbBase(world.getBlockState(offset.offset(ceiling ? EnumFacing.UP : EnumFacing.DOWN))) || offset.getX() == generateAt.getX() && offset.getZ() == generateAt.getZ()) {
                continue;
            }
            int dist = (int) Math.ceil(manhattan(offset, generateAt) * 0.2F);
            generatePillar(world, offset, random, Math.min(centerHeight - dist, 1) + random.nextInt(2), ceiling, false);
        }
        return true;
    }

    private static void generatePillar(World world, BlockPos pos, Random random, int height, boolean ceiling, boolean tesla) {
        BlockPos begin = pos.offset(ceiling ? EnumFacing.UP : EnumFacing.DOWN, 3);
        IBlockState spireState = ACBlockRegistry.GALENA_SPIRE.block().getDefaultState().withProperty(GalenaSpireBlock.DOWN, ceiling);
        int spireCount = 0;
        int checked = 0;
        while (spireCount <= height && checked < 25) {
            checked++;
            int shape = 0;
            if (spireCount > height - 1) {
                shape = 3;
            } else if (spireCount > height - 2) {
                shape = 2;
            } else if (spireCount >= 1) {
                shape = 1;
            }
            begin = ceiling ? begin.down() : begin.up();
            IBlockState prevState = world.getBlockState(begin);
            if (prevState.getBlock() == ACBlockRegistry.GALENA_SPIRE.block() || prevState.getMaterial().isLiquid() && !isWater(prevState)) {
                break;
            }
            if (canReplace(prevState, world, begin)) {
                if (shape == 3 && tesla) {
                    world.setBlockState(begin, ACBlockRegistry.TESLA_BULB.block().getDefaultState().withProperty(TeslaBulbBlock.DOWN, ceiling), 3);
                    break;
                }
                world.setBlockState(begin, spireState.withProperty(GalenaSpireBlock.SHAPE, shape), 3);
                spireCount++;
            }
        }
    }

    private static boolean canReplace(IBlockState state, World world, BlockPos pos) {
        return world.isAirBlock(pos) || state.getMaterial().isReplaceable() || isWater(state);
    }

    private static boolean isWater(IBlockState state) {
        return state.getMaterial() == Material.WATER;
    }

    private static boolean isTeslaBulbBase(IBlockState state) {
        Block block = state.getBlock();
        return block == ACBlockRegistry.GALENA.block()
                || block == ACBlockRegistry.ENERGIZED_GALENA_NEUTRAL.block()
                || block == ACBlockRegistry.ENERGIZED_GALENA_SCARLET.block()
                || block == ACBlockRegistry.ENERGIZED_GALENA_AZURE.block();
    }

    private static int manhattan(BlockPos first, BlockPos second) {
        return Math.abs(first.getX() - second.getX()) + Math.abs(first.getY() - second.getY()) + Math.abs(first.getZ() - second.getZ());
    }
}
