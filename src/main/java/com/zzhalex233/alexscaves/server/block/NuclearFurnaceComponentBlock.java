package com.zzhalex233.alexscaves.server.block;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.entity.NuclearFurnaceTileEntity;
import com.zzhalex233.alexscaves.server.inventory.ACGuiHandler;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class NuclearFurnaceComponentBlock extends Block {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public NuclearFurnaceComponentBlock() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(1001.0F);
        setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(ACTIVE) && entity instanceof EntityLivingBase) {
            BlockPos corner = getCornerForFurnace(world, pos, true);
            if (corner != null && corner.getY() == pos.getY() - 1 && world.getTileEntity(corner) instanceof NuclearFurnaceTileEntity && ((NuclearFurnaceTileEntity) world.getTileEntity(corner)).isUndergoingFission()) {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(com.zzhalex233.alexscaves.server.potion.ACEffectRegistry.IRRADIATED, 2000, 3));
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, net.minecraft.item.ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        BlockPos corner = getCornerForFurnace(world, pos, false);
        if (!world.isRemote && corner != null && isCornerForFurnace(world, corner, true, false)) {
            EnumFacing facing = placer == null ? EnumFacing.NORTH : EnumFacing.getDirectionFromEntityLiving(pos, placer).getOpposite();
            world.setBlockState(corner, ACBlockRegistry.NUCLEAR_FURNACE.block().getDefaultState().withProperty(NuclearFurnaceBlock.FACING, facing), 3);
            activateNeighbors(world, corner, true);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!state.getValue(ACTIVE) || player.isSneaking()) {
            return false;
        }
        BlockPos corner = getCornerForFurnace(world, pos, true);
        if (corner == null || !(world.getTileEntity(corner) instanceof NuclearFurnaceTileEntity)) {
            return false;
        }
        if (!world.isRemote) {
            ((NuclearFurnaceTileEntity) world.getTileEntity(corner)).onPlayerUse(player);
            player.openGui(AlexsCaves.INSTANCE, ACGuiHandler.NUCLEAR_FURNACE, world, corner.getX(), corner.getY(), corner.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (state.getValue(ACTIVE)) {
            BlockPos corner = getCornerForFurnace(world, pos, true);
            if (corner != null && world.getTileEntity(corner) instanceof NuclearFurnaceTileEntity && ((NuclearFurnaceTileEntity) world.getTileEntity(corner)).getCriticality() >= 2) {
                ((NuclearFurnaceTileEntity) world.getTileEntity(corner)).destroyWhileCritical(false);
            }
        }
        super.breakBlock(world, pos, state);
    }

    public static BlockPos getCornerForFurnace(IBlockAccess world, BlockPos pos, boolean postConstruction) {
        if (postConstruction) {
            for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) {
                BlockPos check = pos.add(x, y, z);
                if (world.getBlockState(check).getBlock() == ACBlockRegistry.NUCLEAR_FURNACE.block()) {
                    return check;
                }
            }
            return null;
        }
        BlockPos corner = pos;
        while (canBecomeAComponent(world, corner.west(), false)) corner = corner.west();
        while (canBecomeAComponent(world, corner.down(), false)) corner = corner.down();
        while (canBecomeAComponent(world, corner.north(), false)) corner = corner.north();
        return canBecomeAComponent(world, corner, false) ? corner : null;
    }

    public static boolean isCornerForFurnace(IBlockAccess world, BlockPos corner, boolean checkMiddle, boolean active) {
        for (int x = 0; x <= 1; x++) for (int y = 0; y <= 1; y++) for (int z = 0; z <= 1; z++) {
            if (!checkMiddle && x == 0 && y == 0 && z == 0) {
                continue;
            }
            IBlockState state = world.getBlockState(corner.add(x, y, z));
            if (state.getBlock() != ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.block() || state.getValue(ACTIVE) != active) {
                return false;
            }
        }
        return true;
    }

    public static void activateNeighbors(World world, BlockPos corner, boolean active) {
        for (int x = 0; x <= 1; x++) for (int y = 0; y <= 1; y++) for (int z = 0; z <= 1; z++) {
            BlockPos pos = corner.add(x, y, z);
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.block()) {
                world.setBlockState(pos, state.withProperty(ACTIVE, active), 3);
            }
        }
    }

    public static boolean canBecomeAComponent(IBlockAccess world, BlockPos pos, boolean postConstruction) {
        IBlockState state = world.getBlockState(pos);
        return postConstruction ? state.getBlock() == ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.block() || state.getBlock() == ACBlockRegistry.NUCLEAR_FURNACE.block() : state.getBlock() == ACBlockRegistry.NUCLEAR_FURNACE_COMPONENT.block() && !state.getValue(ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }
}
