package com.zzhalex233.alexscaves.server.block;

import java.util.Random;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NeodymiumPillarBlock extends Block {
    public static final PropertyBool TOP = PropertyBool.create("top");
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    private final boolean azure;

    public NeodymiumPillarBlock(boolean azure) {
        super(Material.ROCK);
        this.azure = azure;
        setHardness(2.0F);
        setResistance(6.0F);
        setSoundType(SoundType.METAL);
        setLightLevel(2.0F / 15.0F);
        setDefaultState(blockState.getBaseState().withProperty(TOP, true).withProperty(FACING, EnumFacing.UP));
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return canAttachTo(world, pos.offset(side.getOpposite()), side);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (canPlaceBlockOnSide(world, pos, facing)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState above = world.getBlockState(pos.offset(facing));
        return getDefaultState().withProperty(FACING, facing).withProperty(TOP, above.getBlock() != this);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canBlockStay(world, pos, state)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            return;
        }
        boolean top = world.getBlockState(pos.offset(state.getValue(FACING))).getBlock() != this;
        if (top != state.getValue(TOP)) {
            world.setBlockState(pos, state.withProperty(TOP, top), 2);
        }
    }

    private boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        return canAttachTo(world, pos.offset(facing.getOpposite()), facing);
    }

    private boolean canAttachTo(World world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == this || state.getBlock().isSideSolid(state, world, pos, facing);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (!world.isRemote) {
            if (world.rand.nextFloat() < Math.min(1.0F, 0.1F + fortune * 0.15F)) {
                spawnAsEntity(world, pos, new ItemStack(azure ? ACItemRegistry.RAW_AZURE_NEODYMIUM.item() : ACItemRegistry.RAW_SCARLET_NEODYMIUM.item(), 1 + world.rand.nextInt(2)));
            } else {
                spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(this)));
            }
        }
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, net.minecraft.entity.player.EntityPlayer player) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(5) == 0) {
            AlexsCaves.PROXY.spawnMagneticOrbit(world, new Vec3d(pos).add(0.5D, 0.5D, 0.5D), azure);
        }
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
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7)).withProperty(TOP, (meta & 8) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() | (state.getValue(TOP) ? 8 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, TOP);
    }
}
