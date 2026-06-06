package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.entity.util.KeybindUsingMount;
import com.zzhalex233.alexscaves.server.item.CandyCaneHookItem;
import com.zzhalex233.alexscaves.server.message.MountedEntityKeyMessage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GumWormSegmentEntity extends Entity implements KeybindUsingMount {
    private static final DataParameter<Integer> HEAD_ID = EntityDataManager.createKey(GumWormSegmentEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FRONT_ID = EntityDataManager.createKey(GumWormSegmentEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> BACK_ID = EntityDataManager.createKey(GumWormSegmentEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> INDEX = EntityDataManager.createKey(GumWormSegmentEntity.class, DataSerializers.VARINT);
    private float prevZRot;
    private float zRot;
    private int zRotTickOffset;
    private int jumpKeyCooldown;
    private boolean wasJumpKeyDown;

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
        GumWormSegmentEntity ridingSegment = null;
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
            if (i == 3) {
                ridingSegment = current;
            }
        }
        if (ridingSegment == null) {
            ridingSegment = prev;
        }
        if (ridingSegment != null) {
            gumWorm.setRidingSegmentId(ridingSegment.getEntityId());
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
            if (isBeingRidden() && head instanceof GumWormEntity && !((GumWormEntity) head).hasARidingHook() && !getPassengers().isEmpty()) {
                getPassengers().get(0).dismountRidingEntity();
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
        if (world.isRemote) {
            EntityPlayer player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.getRidingEntity() == this) {
                if (AlexsCaves.PROXY.isKeyDown(1)) {
                    AlexsCaves.NETWORK_WRAPPER.sendToServer(new MountedEntityKeyMessage(getEntityId(), player.getEntityId(), 0));
                    postDismount(player);
                }
                boolean jumpKeyDown = AlexsCaves.PROXY.isKeyDown(0);
                if (jumpKeyDown && !wasJumpKeyDown && jumpKeyCooldown <= 0) {
                    AlexsCaves.NETWORK_WRAPPER.sendToServer(new MountedEntityKeyMessage(getEntityId(), player.getEntityId(), 1));
                    jumpKeyCooldown = 5;
                }
                wasJumpKeyDown = jumpKeyDown;
            }
        }
        if (jumpKeyCooldown > 0) {
            jumpKeyCooldown--;
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
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (isPassenger(passenger)) {
            Entity head = getHeadEntity();
            if (head instanceof GumWormEntity && passenger instanceof EntityPlayer) {
                ((GumWormEntity) head).tickController((EntityPlayer) passenger);
            }
            Vec3d riderPosition = getRiderPosition(passenger);
            passenger.setPosition(riderPosition.x, riderPosition.y, riderPosition.z);
            passenger.fallDistance = 0.0F;
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (type == 0) {
            keyPresser.dismountRidingEntity();
            postDismount(keyPresser);
        } else if (type == 1 && getHeadEntity() instanceof GumWormEntity) {
            ((GumWormEntity) getHeadEntity()).onPlayerJump(20);
        }
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

    public Vec3d getRiderPosition(Entity rider) {
        Vec3d offset = new Vec3d(0.0D, height + 0.25D + rider.getYOffset(), 0.15D).rotatePitch(-rotationPitch * 0.017453292F).rotateYaw(-rotationYaw * 0.017453292F);
        return getPositionVector().add(offset);
    }

    private void postDismount(Entity rider) {
        if (rider instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) rider;
            ItemStack main = player.getHeldItemMainhand();
            ItemStack off = player.getHeldItemOffhand();
            if (main.getItem() instanceof CandyCaneHookItem) {
                CandyCaneHookItem.setReelingIn(main, true);
            }
            if (off.getItem() instanceof CandyCaneHookItem) {
                CandyCaneHookItem.setReelingIn(off, true);
            }
        }
    }

    private float approachAngle(float value, float target, float step) {
        return value + MathHelper.clamp(MathHelper.wrapDegrees(target - value), -step, step);
    }
}
