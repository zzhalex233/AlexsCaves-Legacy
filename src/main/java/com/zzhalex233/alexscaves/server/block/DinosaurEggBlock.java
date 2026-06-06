package com.zzhalex233.alexscaves.server.block;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;

import com.zzhalex233.alexscaves.server.entity.living.DinosaurEntity;
import com.zzhalex233.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.zzhalex233.alexscaves.server.entity.living.RelicheirusEntity;
import com.zzhalex233.alexscaves.server.entity.living.SubterranodonEntity;
import com.zzhalex233.alexscaves.server.entity.living.VallumraptorEntity;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DinosaurEggBlock extends Block {
    public static final PropertyInteger HATCH = PropertyInteger.create("hatch", 0, 2);

    private final Class<? extends EntityAgeable> births;
    private final AxisAlignedBB shape;

    public DinosaurEggBlock(Class<? extends EntityAgeable> births, int widthPx, int heightPx) {
        super(Material.DRAGON_EGG);
        this.births = births;
        double inset = (16.0D - widthPx) / 32.0D;
        this.shape = new AxisAlignedBB(inset, 0.0D, inset, 1.0D - inset, heightPx / 16.0D, 1.0D - inset);
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(HATCH, 0));
    }

    public boolean canHatchAt(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos.down());
        return state.getBlock() != Blocks.BEDROCK && state.isSideSolid(world, pos.down(), EnumFacing.UP);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (canGrow(world, world.getBlockState(pos.down())) && canHatchAt(world, pos) && world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 15.0D, false) != null) {
            int hatch = state.getValue(HATCH);
            if (hatch < 2) {
                world.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_EGG, net.minecraft.util.SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                world.setBlockState(pos, state.withProperty(HATCH, hatch + 1), 2);
            } else {
                spawnDinosaurs(world, pos, state);
            }
        }
    }

    public void spawnDinosaurs(World world, BlockPos pos, IBlockState state) {
        world.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_EGG, net.minecraft.util.SoundCategory.BLOCKS, 0.7F, 0.9F + world.rand.nextFloat() * 0.2F);
        world.setBlockToAir(pos);
        for (int i = 0; i < getDinosaursBornFrom(state); i++) {
            world.playEvent(2001, pos, Block.getStateId(state));
            if (!world.isRemote) {
                EntityAgeable dinosaur = createDinosaur(world);
                if (dinosaur != null) {
                    dinosaur.setGrowingAge(-24000);
                    dinosaur.setLocationAndAngles(pos.getX() + 0.3D + i * 0.2D, pos.getY(), pos.getZ() + 0.3D, 0.0F, 0.0F);
                    tameFromHatching(world, pos, dinosaur);
                    world.spawnEntity(dinosaur);
                }
            }
        }
    }

    protected EntityAgeable createDinosaur(World world) {
        try {
            Constructor<? extends EntityAgeable> constructor = births.getConstructor(World.class);
            return constructor.newInstance(world);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    protected boolean canGrow(World world, IBlockState stateBelow) {
        return world.rand.nextInt(stateBelow.getBlock() == ACBlockRegistry.FERN_THATCH.block() ? 10 : 20) == 0;
    }

    protected int getDinosaursBornFrom(IBlockState state) {
        return 1;
    }

    protected void removeOneEgg(World world, BlockPos pos, IBlockState state) {
        world.playSound(null, pos, SoundEvents.BLOCK_STONE_BREAK, net.minecraft.util.SoundCategory.BLOCKS, 0.7F, 0.9F + world.rand.nextFloat() * 0.2F);
        world.destroyBlock(pos, false);
    }

    private void tameFromHatching(World world, BlockPos pos, EntityAgeable dinosaur) {
        if (!(dinosaur instanceof EntityTameable) || !(dinosaur instanceof SubterranodonEntity || dinosaur instanceof VallumraptorEntity)) {
            return;
        }
        EntityPlayer player = world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 10.0D, false);
        if (player != null) {
            EntityTameable tameable = (EntityTameable) dinosaur;
            tameable.setTamedBy(player);
            tameable.setSitting(true);
        }
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        tryTrample(world, pos, entity, 100);
        super.onEntityWalk(world, pos, entity);
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        if (!(entity instanceof EntityZombie)) {
            tryTrample(world, pos, entity, 3);
        }
        super.onFallenUpon(world, pos, entity, fallDistance);
    }

    private void tryTrample(World world, BlockPos pos, Entity trampler, int chances) {
        if (canTrample(world, trampler) && !world.isRemote && world.rand.nextInt(chances) == 0) {
            if (trampler instanceof EntityLivingBase && !(trampler instanceof EntityPlayer && ((EntityPlayer) trampler).isCreative())) {
                AxisAlignedBB bb = new AxisAlignedBB(pos).grow(25.0D);
                List<EntityLiving> parents = world.getEntitiesWithinAABB(EntityLiving.class, bb, entity -> entity.isEntityAlive() && births.isInstance(entity));
                for (EntityLiving parent : parents) {
                    parent.setAttackTarget((EntityLivingBase) trampler);
                }
            }
            removeOneEgg(world, pos, world.getBlockState(pos));
        }
    }

    private boolean canTrample(World world, Entity trampler) {
        if (isDinosaur(trampler) || births.isInstance(trampler) || !(trampler instanceof EntityLivingBase)) {
            return false;
        }
        return trampler instanceof EntityPlayer || ForgeEventFactory.getMobGriefingEvent(world, trampler);
    }

    private boolean isDinosaur(Entity entity) {
        return entity instanceof DinosaurEntity || entity instanceof GrottoceratopsEntity || entity instanceof RelicheirusEntity || entity instanceof SubterranodonEntity;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (canHatchAt(world, pos) && !world.isRemote) {
            world.playEvent(2005, pos, 0);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return shape;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        return shape;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        removeOneEgg(world, pos, world.getBlockState(pos));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HATCH);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(HATCH, Math.min(2, meta & 3));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HATCH);
    }
}
