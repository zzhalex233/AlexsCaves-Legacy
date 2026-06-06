package com.zzhalex233.alexscaves.server.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GingerbreadDoorBlock extends BlockHorizontal {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool OPEN = BlockDoor.OPEN;
    public static final PropertyEnum<BlockDoor.EnumHingePosition> HINGE = BlockDoor.HINGE;

    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

    public GingerbreadDoorBlock() {
        super(Material.CAKE);
        setHardness(1.0F);
        setResistance(1.5F);
        setSoundType(ACSoundTypes.DENSE_CANDY);
        setDefaultState(blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(OPEN, Boolean.FALSE)
                .withProperty(HINGE, BlockDoor.EnumHingePosition.LEFT));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing horizontal = placer == null ? EnumFacing.NORTH : placer.getHorizontalFacing();
        return getDefaultState().withProperty(FACING, horizontal).withProperty(HINGE, hingeFromHit(horizontal, hitX, hitZ));
    }

    private BlockDoor.EnumHingePosition hingeFromHit(EnumFacing facing, float hitX, float hitZ) {
        boolean right = facing.getAxis() == EnumFacing.Axis.Z ? hitX < 0.5F : hitZ > 0.5F;
        return right ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        boolean closed = !state.getValue(OPEN);
        boolean right = state.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT;
        switch (facing) {
            case EAST:
                return closed ? EAST_AABB : (right ? NORTH_AABB : SOUTH_AABB);
            case SOUTH:
                return closed ? SOUTH_AABB : (right ? EAST_AABB : WEST_AABB);
            case WEST:
                return closed ? WEST_AABB : (right ? SOUTH_AABB : NORTH_AABB);
            case NORTH:
            default:
                return closed ? NORTH_AABB : (right ? WEST_AABB : EAST_AABB);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        state = state.cycleProperty(OPEN);
        world.setBlockState(pos, state, 10);
        world.playSound(player, pos, state.getValue(OPEN) ? SoundEvents.BLOCK_WOODEN_DOOR_OPEN : SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
        return true;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getValue(OPEN);
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
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.withRotation(mirror.toRotation(state.getValue(FACING))).cycleProperty(HINGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
                .withProperty(OPEN, (meta & 4) != 0)
                .withProperty(HINGE, (meta & 8) != 0 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getHorizontalIndex();
        if (state.getValue(OPEN)) {
            meta |= 4;
        }
        if (state.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT) {
            meta |= 8;
        }
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, OPEN, HINGE);
    }
}
