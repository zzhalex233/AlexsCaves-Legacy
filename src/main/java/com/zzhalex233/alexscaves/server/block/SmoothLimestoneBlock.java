package com.zzhalex233.alexscaves.server.block;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmoothLimestoneBlock extends BasicCaveBlock {
    private static final List<java.util.function.Supplier<ACBlockRegistry.BlockEntry>> RANDOM_CAVE_PAINTINGS = Arrays.asList(
            () -> ACBlockRegistry.CAVE_PAINTING_AMBERSOL,
            () -> ACBlockRegistry.CAVE_PAINTING_DARK,
            () -> ACBlockRegistry.CAVE_PAINTING_FOOTPRINT,
            () -> ACBlockRegistry.CAVE_PAINTING_FOOTPRINTS,
            () -> ACBlockRegistry.CAVE_PAINTING_TREE_STARS,
            () -> ACBlockRegistry.CAVE_PAINTING_PEWEN,
            () -> ACBlockRegistry.CAVE_PAINTING_TRILOCARIS,
            () -> ACBlockRegistry.CAVE_PAINTING_GROTTOCERATOPS,
            () -> ACBlockRegistry.CAVE_PAINTING_GROTTOCERATOPS_FRIEND,
            () -> ACBlockRegistry.CAVE_PAINTING_DINO_NUGGETS,
            () -> ACBlockRegistry.CAVE_PAINTING_VALLUMRAPTOR_CHEST,
            () -> ACBlockRegistry.CAVE_PAINTING_VALLUMRAPTOR_FRIEND,
            () -> ACBlockRegistry.CAVE_PAINTING_RELICHEIRUS,
            () -> ACBlockRegistry.CAVE_PAINTING_RELICHEIRUS_SLASH,
            () -> ACBlockRegistry.CAVE_PAINTING_ENDERMAN,
            () -> ACBlockRegistry.CAVE_PAINTING_PORTAL,
            () -> ACBlockRegistry.CAVE_PAINTING_SUBTERRANODON,
            () -> ACBlockRegistry.CAVE_PAINTING_SUBTERRANODON_RIDE,
            () -> ACBlockRegistry.CAVE_PAINTING_TREMORSAURUS,
            () -> ACBlockRegistry.CAVE_PAINTING_TREMORSAURUS_FRIEND
    );

    public SmoothLimestoneBlock() {
        super(Material.ROCK, 1.2F, 4.5F, SoundType.STONE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.getItem() != Items.COAL || stack.getMetadata() != 1) {
            return false;
        }
        if (!playerIn.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        if (!worldIn.isRemote) {
            if (worldIn.rand.nextFloat() < 0.3F && attemptPlaceMysteryCavePainting(worldIn, pos, facing, true)) {
                attemptPlaceMysteryCavePainting(worldIn, pos, facing, false);
            } else {
                ACBlockRegistry.BlockEntry entry = RANDOM_CAVE_PAINTINGS.get(worldIn.rand.nextInt(RANDOM_CAVE_PAINTINGS.size())).get();
                worldIn.setBlockState(pos, entry.block().getDefaultState().withProperty(CavePaintingBlock.FACING, facing), 3);
            }
        }
        return true;
    }

    private boolean attemptPlaceMysteryCavePainting(World world, BlockPos pos, EnumFacing facing, boolean checkOnly) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos paintingPos = paintingPos(pos, facing, i, j);
                if (world.getBlockState(paintingPos).getBlock() != this) {
                    return false;
                }
                if (!checkOnly) {
                    world.setBlockState(paintingPos, getMysteryCavePainting(i, j).block().getDefaultState().withProperty(CavePaintingBlock.FACING, facing), 3);
                }
            }
        }
        return true;
    }

    private BlockPos paintingPos(BlockPos pos, EnumFacing facing, int i, int j) {
        if (facing == EnumFacing.DOWN) {
            return pos.offset(EnumFacing.SOUTH, i).offset(EnumFacing.WEST, j);
        }
        if (facing == EnumFacing.UP) {
            return pos.offset(EnumFacing.NORTH, i).offset(EnumFacing.WEST, j);
        }
        return pos.up(i).offset(facing.rotateY(), j);
    }

    private ACBlockRegistry.BlockEntry getMysteryCavePainting(int i, int j) {
        if (i == -1 && j == -1) return ACBlockRegistry.CAVE_PAINTING_MYSTERY_9;
        if (i == -1 && j == 0) return ACBlockRegistry.CAVE_PAINTING_MYSTERY_8;
        if (i == -1 && j == 1) return ACBlockRegistry.CAVE_PAINTING_MYSTERY_7;
        if (i == 0 && j == -1) return ACBlockRegistry.CAVE_PAINTING_MYSTERY_6;
        if (i == 0 && j == 0) return ACBlockRegistry.CAVE_PAINTING_MYSTERY_5;
        if (i == 0 && j == 1) return ACBlockRegistry.CAVE_PAINTING_MYSTERY_4;
        if (i == 1 && j == -1) return ACBlockRegistry.CAVE_PAINTING_MYSTERY_3;
        if (i == 1 && j == 0) return ACBlockRegistry.CAVE_PAINTING_MYSTERY_2;
        if (i == 1 && j == 1) return ACBlockRegistry.CAVE_PAINTING_MYSTERY_1;
        return ACBlockRegistry.CAVE_PAINTING_DARK;
    }
}
