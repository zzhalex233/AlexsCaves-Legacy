package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.server.block.entity.MagnetTileEntity;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagnetBlock extends BlockContainer {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    private static final AxisAlignedBB[] SHAPE_UP = new AxisAlignedBB[] {
            box(0, 6, 5, 6, 16, 11),
            box(0, 0, 5, 16, 6, 11),
            box(10, 6, 5, 16, 16, 11)
    };
    private static final AxisAlignedBB[] SHAPE_DOWN = new AxisAlignedBB[] {
            box(0, 0, 5, 6, 10, 11),
            box(0, 10, 5, 16, 16, 11),
            box(10, 0, 5, 16, 10, 11)
    };
    private static final AxisAlignedBB[] SHAPE_NORTH = new AxisAlignedBB[] {
            box(0, 5, 0, 6, 11, 10),
            box(0, 5, 10, 16, 11, 16),
            box(10, 5, 0, 16, 11, 10)
    };
    private static final AxisAlignedBB[] SHAPE_SOUTH = new AxisAlignedBB[] {
            box(10, 5, 6, 16, 11, 16),
            box(0, 5, 0, 16, 11, 6),
            box(0, 5, 6, 6, 11, 16)
    };
    private static final AxisAlignedBB[] SHAPE_EAST = new AxisAlignedBB[] {
            box(6, 5, 0, 16, 11, 6),
            box(0, 5, 0, 6, 11, 16),
            box(6, 5, 10, 16, 11, 16)
    };
    private static final AxisAlignedBB[] SHAPE_WEST = new AxisAlignedBB[] {
            box(0, 5, 10, 10, 11, 16),
            box(10, 5, 0, 16, 11, 16),
            box(0, 5, 0, 10, 11, 6)
    };

    private final boolean azure;

    public MagnetBlock(boolean azure) {
        super(Material.IRON);
        this.azure = azure;
        setHardness(4.0F);
        setResistance(12.0F);
        setSoundType(ACSoundTypes.NEODYMIUM);
        setLightLevel(0.1875F);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(POWERED, false));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new MagnetTileEntity();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return state.getValue(POWERED) ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, facing).withProperty(POWERED, world.isBlockPowered(pos));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            updatePowered(state, worldIn, pos);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, java.util.Random rand) {
        if (!worldIn.isRemote) {
            updatePowered(state, worldIn, pos);
        }
    }

    private void updatePowered(IBlockState state, World world, BlockPos pos) {
        boolean powered = world.isBlockPowered(pos);
        if (powered != state.getValue(POWERED)) {
            world.setBlockState(pos, state.withProperty(POWERED, powered), 3);
            world.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), this, false);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof MagnetTileEntity) || playerIn.isSneaking()) {
            return false;
        }
        MagnetTileEntity magnet = (MagnetTileEntity) tile;
        ItemStack held = playerIn.getHeldItem(hand);
        if (magnet.canAddRange() && magnet.isExtenderItem(held)) {
            if (!worldIn.isRemote) {
                magnet.increaseRange(1);
                if (!playerIn.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
            }
            playerIn.swingArm(hand);
            return true;
        }
        if (magnet.canRemoveRange() && magnet.isRetracterItem(held)) {
            if (!worldIn.isRemote) {
                magnet.increaseRange(-1);
                if (!playerIn.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
            }
            playerIn.swingArm(hand);
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof MagnetTileEntity && !worldIn.isRemote) {
            ((MagnetTileEntity) tile).dropIngots(azure);
        }
        super.breakBlock(worldIn, pos, state);
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
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, java.util.List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
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

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7)).withProperty(POWERED, (meta & 8) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() | (state.getValue(POWERED) ? 8 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, POWERED);
    }

    private static AxisAlignedBB[] boxesFor(IBlockState state) {
        switch (state.getValue(FACING)) {
            case DOWN:
                return SHAPE_DOWN;
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            case UP:
            default:
                return SHAPE_UP;
        }
    }

    private static AxisAlignedBB box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AxisAlignedBB(minX / 16.0D, minY / 16.0D, minZ / 16.0D, maxX / 16.0D, maxY / 16.0D, maxZ / 16.0D);
    }
}
