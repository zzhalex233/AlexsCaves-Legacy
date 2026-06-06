package com.zzhalex233.alexscaves.server.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HeartOfIronBlock extends BlockRotatedPillar {
    private static final AxisAlignedBB[] SHAPE_X = new AxisAlignedBB[] {
            box(5, 0, 0, 11, 4, 16),
            box(5, 12, 0, 11, 16, 16),
            box(5, 4, 0, 11, 12, 4),
            box(5, 4, 12, 11, 12, 16)
    };
    private static final AxisAlignedBB[] SHAPE_Y = new AxisAlignedBB[] {
            box(0, 5, 0, 16, 11, 4),
            box(0, 5, 12, 16, 11, 16),
            box(0, 5, 4, 4, 11, 12),
            box(12, 5, 4, 16, 11, 12)
    };
    private static final AxisAlignedBB[] SHAPE_Z = new AxisAlignedBB[] {
            box(0, 0, 5, 16, 4, 11),
            box(0, 12, 5, 16, 16, 11),
            box(0, 4, 5, 4, 12, 11),
            box(12, 4, 5, 16, 12, 11)
    };

    public HeartOfIronBlock() {
        super(Material.IRON);
        setHardness(4.0F);
        setResistance(4.0F);
        setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(AXIS, placer == null ? facing.getAxis() : nearestLookingAxis(placer));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB[] boxes = boxesFor(state);
        AxisAlignedBB result = boxes[0];
        for (int i = 1; i < boxes.length; i++) {
            result = result.union(boxes[i]);
        }
        return result;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        for (AxisAlignedBB box : boxesFor(state)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        }
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

    private static AxisAlignedBB[] boxesFor(IBlockState state) {
        switch (state.getValue(AXIS)) {
            case X:
                return SHAPE_X;
            case Z:
                return SHAPE_Z;
            case Y:
            default:
                return SHAPE_Y;
        }
    }

    private static EnumFacing.Axis nearestLookingAxis(EntityLivingBase placer) {
        Vec3d look = placer.getLookVec();
        double x = Math.abs(look.x);
        double y = Math.abs(look.y);
        double z = Math.abs(look.z);
        return y >= x && y >= z ? EnumFacing.Axis.Y : x >= z ? EnumFacing.Axis.X : EnumFacing.Axis.Z;
    }

    private static AxisAlignedBB box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AxisAlignedBB(minX / 16.0D, minY / 16.0D, minZ / 16.0D, maxX / 16.0D, maxY / 16.0D, maxZ / 16.0D);
    }
}
