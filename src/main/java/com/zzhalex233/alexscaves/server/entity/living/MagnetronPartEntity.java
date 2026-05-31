package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.entity.util.MagnetronJoint;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;

public class MagnetronPartEntity extends Entity {
    private static final DataParameter<Integer> PARENT_ID = EntityDataManager.createKey(MagnetronPartEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> JOINT = EntityDataManager.createKey(MagnetronPartEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> LEFT = EntityDataManager.createKey(MagnetronPartEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BLOCK_STATE = EntityDataManager.createKey(MagnetronPartEntity.class, DataSerializers.VARINT);

    public MagnetronPartEntity(World world) {
        super(world);
        setSize(0.9F, 0.9F);
        noClip = true;
    }

    public MagnetronPartEntity(World world, MagnetronEntity parent, MagnetronJoint joint, boolean left) {
        this(world);
        setParentId(parent.getEntityId());
        setJoint(joint);
        setLeft(left);
    }

    @Override
    protected void entityInit() {
        dataManager.register(PARENT_ID, -1);
        dataManager.register(JOINT, 0);
        dataManager.register(LEFT, false);
        dataManager.register(BLOCK_STATE, Block.getStateId(Blocks.STONE.getDefaultState()));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        noClip = true;
        MagnetronEntity parent = getParent();
        if (!world.isRemote) {
            if (ticksExisted > 5 && (parent == null || parent.isDead || !parent.isFormed())) {
                setDead();
                return;
            }
            if (parent != null) {
                Vec3d target = parent.getPositionVector().add(getJoint().getTargetPosition(parent, isLeft()));
                Vec3d delta = target.subtract(posX, posY, posZ);
                Vec3d move = delta.length() > 1.0D ? delta.normalize().scale(0.65D) : delta.scale(0.45D);
                setPosition(posX + move.x, posY + move.y, posZ + move.z);
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        MagnetronEntity parent = getParent();
        return parent != null && !parent.isDead && parent.attackEntityFrom(source, amount);
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
        setParentId(compound.getInteger("ParentId"));
        setJoint(MagnetronJoint.values()[Math.max(0, Math.min(MagnetronJoint.values().length - 1, compound.getInteger("Joint")))]);
        setLeft(compound.getBoolean("Left"));
        dataManager.set(BLOCK_STATE, compound.getInteger("BlockState"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("ParentId", getParentId());
        compound.setInteger("Joint", getJoint().ordinal());
        compound.setBoolean("Left", isLeft());
        compound.setInteger("BlockState", dataManager.get(BLOCK_STATE));
    }

    public MagnetronEntity getParent() {
        Entity entity = getParentId() == -1 ? null : world.getEntityByID(getParentId());
        return entity instanceof MagnetronEntity ? (MagnetronEntity) entity : null;
    }

    public int getParentId() {
        return dataManager.get(PARENT_ID);
    }

    public void setParentId(int id) {
        dataManager.set(PARENT_ID, id);
    }

    public MagnetronJoint getJoint() {
        return MagnetronJoint.values()[dataManager.get(JOINT)];
    }

    public void setJoint(MagnetronJoint joint) {
        dataManager.set(JOINT, joint.ordinal());
    }

    public boolean isLeft() {
        return dataManager.get(LEFT);
    }

    public void setLeft(boolean left) {
        dataManager.set(LEFT, left);
    }

    public IBlockState getBlockState() {
        return Block.getStateById(dataManager.get(BLOCK_STATE));
    }

    public void setBlockState(IBlockState state) {
        dataManager.set(BLOCK_STATE, Block.getStateId(state == null ? Blocks.STONE.getDefaultState() : state));
    }
}
