package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.entity.ACEntityRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GumWormSegmentEntity extends Entity {
    private static final DataParameter<Integer> HEAD_ID = EntityDataManager.createKey(GumWormSegmentEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FRONT_ID = EntityDataManager.createKey(GumWormSegmentEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> BACK_ID = EntityDataManager.createKey(GumWormSegmentEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> INDEX = EntityDataManager.createKey(GumWormSegmentEntity.class, DataSerializers.VARINT);
    private float prevZRot;
    private float zRot;
    private int zRotTickOffset;

    public GumWormSegmentEntity(World world) {
        super(world);
        setSize(2.0F, 2.0F);
        noClip = true;
        zRotTickOffset = rand.nextInt(10);
    }

    @Override
    protected void entityInit() {
        dataManager.register(HEAD_ID, -1);
        dataManager.register(FRONT_ID, -1);
        dataManager.register(BACK_ID, -1);
        dataManager.register(INDEX, 0);
    }

    public static void createWormSegmentsFor(GumWormEntity gumWorm, int count) {
        GumWormSegmentEntity prev = null;
        for (int i = 0; i < count; i++) {
            GumWormSegmentEntity current = new GumWormSegmentEntity(gumWorm.world);
            current.setHeadId(gumWorm.getEntityId());
            current.setFrontId(prev == null ? gumWorm.getEntityId() : prev.getEntityId());
            current.setIndex(i);
            current.setPosition(gumWorm.posX, gumWorm.posY, gumWorm.posZ);
            gumWorm.world.spawnEntity(current);
            if (prev != null) {
                prev.setBackId(current.getEntityId());
            }
            prev = current;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevZRot = zRot;
        noClip = true;
        Entity head = getHeadEntity();
        Entity front = getFrontEntity();
        Entity back = getBackEntity();
        if (!world.isRemote) {
            if (ticksExisted > 5 && (head == null || head.isDead || front == null || front.isDead)) {
                setDead();
                return;
            }
            if (front != null) {
                Vec3d ideal = getIdealPosition(front, head);
                Vec3d delta = ideal.subtract(posX, posY, posZ);
                double extra = Math.max(delta.length() - 1.5D, 0.0D);
                Vec3d move = delta.length() > 1.0D ? delta.normalize().scale(0.7D + extra) : delta.scale(0.35D);
                setPosition(posX + move.x, posY + move.y, posZ + move.z);
                faceFront(front);
            }
        }
        if (zRotTickOffset-- < 0) {
            float target = 0.0F;
            if (front instanceof GumWormEntity) {
                target = ((GumWormEntity) front).getBodyZRot(1.0F);
            } else if (front instanceof GumWormSegmentEntity) {
                target = ((GumWormSegmentEntity) front).getBodyZRot(1.0F);
            }
            zRot = approachAngle(zRot, target + (getIndex() * 5 + 15), 8.0F);
        }
        if (back != null && back.isDead) {
            setBackId(-1);
        }
    }

    private Vec3d getIdealPosition(Entity front, Entity head) {
        float stretch = head instanceof GumWormEntity && ((GumWormEntity) head).isLeaping() ? -3.2F : -2.5F;
        float swing = head == null ? 0.0F : (0.5F + getIndex() * 0.05F) * MathHelper.sin(head.ticksExisted * 0.2F - getIndex());
        float yaw = front.rotationYaw * 0.017453292F;
        return front.getPositionVector().add(-MathHelper.sin(yaw) * stretch + MathHelper.cos(yaw) * swing, 0.0D, MathHelper.cos(yaw) * stretch + MathHelper.sin(yaw) * swing);
    }

    private void faceFront(Entity front) {
        double dx = front.posX - posX;
        double dy = front.posY - posY;
        double dz = front.posZ - posZ;
        rotationYaw = (float) (-Math.atan2(dx, dz) * 180.0D / Math.PI);
        rotationPitch = (float) (-(Math.atan2(dy, MathHelper.sqrt(dx * dx + dz * dz)) * 180.0D / Math.PI));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity head = getHeadEntity();
        return head instanceof GumWormEntity && !head.isDead && head.attackEntityFrom(source, amount);
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
        setHeadId(compound.getInteger("HeadId"));
        setFrontId(compound.getInteger("FrontId"));
        setBackId(compound.getInteger("BackId"));
        setIndex(compound.getInteger("Index"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("HeadId", getHeadId());
        compound.setInteger("FrontId", getFrontId());
        compound.setInteger("BackId", getBackId());
        compound.setInteger("Index", getIndex());
    }

    public Entity getHeadEntity() {
        return getHeadId() == -1 ? null : world.getEntityByID(getHeadId());
    }

    public Entity getFrontEntity() {
        return getFrontId() == -1 ? null : world.getEntityByID(getFrontId());
    }

    public Entity getBackEntity() {
        return getBackId() == -1 ? null : world.getEntityByID(getBackId());
    }

    public int getHeadId() {
        return dataManager.get(HEAD_ID);
    }

    public void setHeadId(int id) {
        dataManager.set(HEAD_ID, id);
    }

    public int getFrontId() {
        return dataManager.get(FRONT_ID);
    }

    public void setFrontId(int id) {
        dataManager.set(FRONT_ID, id);
    }

    public int getBackId() {
        return dataManager.get(BACK_ID);
    }

    public void setBackId(int id) {
        dataManager.set(BACK_ID, id);
    }

    public int getIndex() {
        return dataManager.get(INDEX);
    }

    public void setIndex(int index) {
        dataManager.set(INDEX, index);
    }

    public float getBodyZRot(float partialTicks) {
        return prevZRot + (zRot - prevZRot) * partialTicks;
    }

    private float approachAngle(float value, float target, float step) {
        return value + MathHelper.clamp(MathHelper.wrapDegrees(target - value), -step, step);
    }
}
