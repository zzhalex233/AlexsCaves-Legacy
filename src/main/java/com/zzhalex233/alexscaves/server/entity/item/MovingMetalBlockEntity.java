package com.zzhalex233.alexscaves.server.entity.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zzhalex233.alexscaves.server.entity.util.MovingBlockData;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class MovingMetalBlockEntity extends Entity implements IEntityAdditionalSpawnData {
    private final List<MovingBlockData> blockData = new ArrayList<>();
    private int placementCooldown = 40;

    public MovingMetalBlockEntity(World world) {
        super(world);
        setSize(1.0F, 1.0F);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        move(MoverType.SELF, motionX, motionY, motionZ);
        setEntityBoundingBox(makeBoundingBox());
        if (!world.isRemote) {
            if (placementCooldown > 0) {
                placementCooldown--;
            } else {
                tryPlace();
            }
        }
        motionX *= 0.98D;
        motionY *= 0.98D;
        motionZ *= 0.98D;
    }

    public void setPlacementCooldown(int cooldown) {
        placementCooldown = cooldown;
    }

    public void setBlockData(List<MovingBlockData> data) {
        blockData.clear();
        blockData.addAll(data);
        setEntityBoundingBox(makeBoundingBox());
    }

    public List<MovingBlockData> getBlockData() {
        return Collections.unmodifiableList(blockData);
    }

    private void tryPlace() {
        if (blockData.isEmpty()) {
            setDead();
            return;
        }
        BlockPos basePos = new BlockPos(this);
        for (MovingBlockData data : blockData) {
            BlockPos setPos = basePos.add(data.offset());
            IBlockState at = world.getBlockState(setPos);
            if (at.getMaterial() == Material.AIR || at.getBlock().isReplaceable(world, setPos)) {
                continue;
            }
            placementCooldown = 5 + rand.nextInt(10);
            return;
        }
        for (MovingBlockData data : blockData) {
            BlockPos setPos = basePos.add(data.offset());
            world.setBlockState(setPos, data.state(), 3);
            NBTTagCompound tileData = data.tileData();
            if (tileData != null) {
                TileEntity tile = world.getTileEntity(setPos);
                if (tile != null) {
                    tileData.setInteger("x", setPos.getX());
                    tileData.setInteger("y", setPos.getY());
                    tileData.setInteger("z", setPos.getZ());
                    tile.readFromNBT(tileData);
                    tile.markDirty();
                }
            }
        }
        setDead();
    }

    public static NBTTagCompound createTagFromData(List<MovingBlockData> blocks) {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (MovingBlockData data : blocks) {
            list.appendTag(data.toTag());
        }
        tag.setTag("BlockData", list);
        return tag;
    }

    public void setAllBlockData(NBTTagCompound tag) {
        blockData.clear();
        if (tag.hasKey("BlockData")) {
            NBTTagList list = tag.getTagList("BlockData", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                blockData.add(new MovingBlockData(world, list.getCompoundTagAt(i)));
            }
        }
        setEntityBoundingBox(makeBoundingBox());
    }

    public NBTTagCompound getAllBlockData() {
        return createTagFromData(blockData);
    }

    private AxisAlignedBB makeBoundingBox() {
        BlockPos basePos = new BlockPos(this);
        AxisAlignedBB box = new AxisAlignedBB(posX - 0.5D, posY - 0.5D, posZ - 0.5D, posX + 0.5D, posY + 0.5D, posZ + 0.5D);
        for (MovingBlockData data : blockData) {
            box = box.union(data.boundingBox(world, basePos));
        }
        return box;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        placementCooldown = compound.getInteger("PlacementCooldown");
        if (compound.hasKey("BlockDataContainer")) {
            setAllBlockData(compound.getCompoundTag("BlockDataContainer"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("PlacementCooldown", placementCooldown);
        compound.setTag("BlockDataContainer", getAllBlockData());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        ByteBufUtils.writeTag(buffer, getAllBlockData());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        setAllBlockData(ByteBufUtils.readTag(additionalData));
    }

    @Override
    public boolean hitByEntity(Entity entityIn) {
        return true;
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        return false;
    }
}
