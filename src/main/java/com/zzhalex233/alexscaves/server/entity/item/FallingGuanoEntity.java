package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.GuanoLayerBlock;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class FallingGuanoEntity extends EntityFallingBlock implements IEntityAdditionalSpawnData {
    private IBlockState guanoState = ACBlockRegistry.GUANO_LAYER.block().getDefaultState();

    public FallingGuanoEntity(World worldIn) {
        super(worldIn);
    }

    public FallingGuanoEntity(World worldIn, double x, double y, double z) {
        this(worldIn, x, y, z, ACBlockRegistry.GUANO_LAYER.block().getDefaultState());
    }

    private FallingGuanoEntity(World world, double x, double y, double z, IBlockState state) {
        super(world, x, y, z, state);
        guanoState = state;
    }

    public static FallingGuanoEntity fall(World world, BlockPos pos, IBlockState state) {
        FallingGuanoEntity entity = new FallingGuanoEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);
        world.setBlockToAir(pos);
        world.spawnEntity(entity);
        return entity;
    }

    @Override
    public void onUpdate() {
        if (guanoState.getMaterial() == Material.AIR) {
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
            if (!onGround) {
                if (fallTime++ > 600 || pos.getY() <= 0) {
                    dropGuano();
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
        if (!current.getBlock().isReplaceable(world, pos) && !current.getBlock().isAir(current, world, pos)) {
            pos = pos.up();
            current = world.getBlockState(pos);
        }
        int fallingLayers = getLayerCount(guanoState);
        if (current.getBlock() == ACBlockRegistry.GUANO_LAYER.block()) {
            placeLayers(pos, current.getValue(GuanoLayerBlock.LAYERS) + fallingLayers);
        } else if (current.getBlock().isReplaceable(world, pos) && !BlockFalling.canFallThrough(world.getBlockState(pos.down()))) {
            placeLayers(pos, fallingLayers);
        } else {
            dropGuano();
        }
        setDead();
    }

    private void placeLayers(BlockPos pos, int layers) {
        world.setBlockState(pos, layerState(Math.min(layers, 8)), 3);
        if (layers > 8) {
            BlockPos above = pos.up();
            IBlockState aboveState = world.getBlockState(above);
            int existing = aboveState.getBlock() == ACBlockRegistry.GUANO_LAYER.block() ? aboveState.getValue(GuanoLayerBlock.LAYERS) : 0;
            int remaining = layers - 8;
            if (existing > 0 && existing < 8 || existing == 0 && aboveState.getBlock().isReplaceable(world, above)) {
                int together = remaining + existing;
                world.setBlockState(above, layerState(Math.min(together, 8)), 3);
                if (together > 8) {
                    Block.spawnAsEntity(world, pos, new ItemStack(ACBlockRegistry.GUANO_LAYER.item(), together - 8));
                }
            } else {
                Block.spawnAsEntity(world, pos, new ItemStack(ACBlockRegistry.GUANO_LAYER.item(), remaining));
            }
        }
    }

    private IBlockState layerState(int layers) {
        return ACBlockRegistry.GUANO_LAYER.block().getDefaultState().withProperty(GuanoLayerBlock.LAYERS, Math.max(1, Math.min(layers, 8)));
    }

    private int getLayerCount(IBlockState state) {
        return state.getBlock() == ACBlockRegistry.GUANO_LAYER.block() ? state.getValue(GuanoLayerBlock.LAYERS) : 8;
    }

    private void dropGuano() {
        if (shouldDropItem && world.getGameRules().getBoolean("doEntityDrops")) {
            Block.spawnAsEntity(world, new BlockPos(this), new ItemStack(ACBlockRegistry.GUANO_LAYER.item(), getLayerCount(guanoState)));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("GuanoState", Block.getStateId(guanoState));
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        guanoState = Block.getStateById(compound.getInteger("GuanoState"));
        if (guanoState == null || guanoState.getMaterial() == Material.AIR) {
            guanoState = ACBlockRegistry.GUANO_LAYER.block().getDefaultState();
        }
    }

    @Override
    public IBlockState getBlock() {
        return guanoState;
    }

    @Override
    public void writeSpawnData(ByteBuf byteBuf) {
        byteBuf.writeInt(Block.getStateId(guanoState));
    }

    @Override
    public void readSpawnData(ByteBuf byteBuf) {
        guanoState = Block.getStateById(byteBuf.readInt());
    }
}
