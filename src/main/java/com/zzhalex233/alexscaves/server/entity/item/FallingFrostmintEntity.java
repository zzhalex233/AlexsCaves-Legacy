package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class FallingFrostmintEntity extends EntityFallingBlock implements IEntityAdditionalSpawnData {
    private IBlockState frostmintState = ACBlockRegistry.FROSTMINT.block().getDefaultState();

    public FallingFrostmintEntity(World worldIn) {
        super(worldIn);
    }

    public FallingFrostmintEntity(World worldIn, double x, double y, double z) {
        this(worldIn, x, y, z, ACBlockRegistry.FROSTMINT.block().getDefaultState());
    }

    private FallingFrostmintEntity(World world, double x, double y, double z, IBlockState state) {
        super(world, x, y, z, state);
        frostmintState = state;
    }

    public static FallingFrostmintEntity fall(World world, BlockPos pos, IBlockState state) {
        FallingFrostmintEntity entity = new FallingFrostmintEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);
        world.setBlockToAir(pos);
        world.spawnEntity(entity);
        return entity;
    }

    @Override
    public void onUpdate() {
        if (frostmintState.getMaterial() == Material.AIR) {
            setDead();
            return;
        }
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (!hasNoGravity()) {
            motionY -= 0.04D;
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
        if (!world.isRemote) {
            BlockPos pos = new BlockPos(this);
            if (isInPurpleSoda(pos)) {
                explodeFrostmint();
                setDead();
                return;
            }
            if (!onGround) {
                if (fallTime++ > 600 || pos.getY() <= 0) {
                    dropFrostmint();
                    setDead();
                }
            } else {
                motionX *= 0.7D;
                motionY *= -0.5D;
                motionZ *= 0.7D;
                land(pos);
            }
        }
        motionX *= 0.98D;
        motionY *= 0.98D;
        motionZ *= 0.98D;
    }

    private void land(BlockPos pos) {
        IBlockState current = world.getBlockState(pos);
        if (!isFrostmint(current) && !current.getBlock().isReplaceable(world, pos) && !current.getBlock().isAir(current, world, pos)) {
            pos = pos.up();
            current = world.getBlockState(pos);
        }
        if (isInPurpleSoda(pos)) {
            explodeFrostmint();
        } else if (isSingleFrostmint(current)) {
            world.setBlockState(pos, ACBlockRegistry.FROSTMINT_DOUBLE.getDefaultState(), 3);
        } else if (isDoubleFrostmint(current)) {
            placeAboveOrDrop(pos);
        } else if (current.getBlock().isReplaceable(world, pos) && isSupported(pos)) {
            world.setBlockState(pos, landingState(), 3);
        } else {
            dropFrostmint();
        }
        setDead();
    }

    private void placeAboveOrDrop(BlockPos pos) {
        BlockPos above = pos.up();
        IBlockState aboveState = world.getBlockState(above);
        if (aboveState.getBlock().isReplaceable(world, above)) {
            world.setBlockState(above, landingState(), 3);
        } else {
            dropFrostmint();
        }
    }

    private boolean isSupported(BlockPos pos) {
        IBlockState below = world.getBlockState(pos.down());
        return !BlockFalling.canFallThrough(below) || isFrostmint(below);
    }

    private IBlockState landingState() {
        if (isDoubleFrostmint(frostmintState)) {
            return ACBlockRegistry.FROSTMINT_DOUBLE.getDefaultState();
        }
        return ACBlockRegistry.FROSTMINT.block().getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
    }

    private boolean isInPurpleSoda(BlockPos pos) {
        return world.getBlockState(pos).getBlock() == ACBlockRegistry.PURPLE_SODA.block();
    }

    private boolean isFrostmint(IBlockState state) {
        return isSingleFrostmint(state) || isDoubleFrostmint(state);
    }

    private boolean isSingleFrostmint(IBlockState state) {
        return state.getBlock() == ACBlockRegistry.FROSTMINT.block();
    }

    private boolean isDoubleFrostmint(IBlockState state) {
        return state.getBlock() == ACBlockRegistry.FROSTMINT_DOUBLE;
    }

    private void explodeFrostmint() {
        world.createExplosion(this, posX, posY + 0.5D, posZ, 4.0F, true);
    }

    private void dropFrostmint() {
        if (shouldDropItem && world.getGameRules().getBoolean("doEntityDrops")) {
            int count = isDoubleFrostmint(frostmintState) ? 2 : 1;
            Block.spawnAsEntity(world, new BlockPos(this), new ItemStack(ACBlockRegistry.FROSTMINT.item(), count));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("FrostmintState", Block.getStateId(frostmintState));
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        frostmintState = sanitizeState(Block.getStateById(compound.getInteger("FrostmintState")));
    }

    @Override
    public IBlockState getBlock() {
        return frostmintState;
    }

    @Override
    public void writeSpawnData(ByteBuf byteBuf) {
        byteBuf.writeInt(Block.getStateId(frostmintState));
    }

    @Override
    public void readSpawnData(ByteBuf byteBuf) {
        frostmintState = sanitizeState(Block.getStateById(byteBuf.readInt()));
    }

    private IBlockState sanitizeState(IBlockState state) {
        return state == null || state.getMaterial() == Material.AIR ? ACBlockRegistry.FROSTMINT.block().getDefaultState() : state;
    }
}
