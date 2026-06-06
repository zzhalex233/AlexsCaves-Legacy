package com.zzhalex233.alexscaves.server.entity.item;

import java.util.UUID;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpinningPeppermintEntity extends Entity {
    private static final DataParameter<Float> SPIN_RADIUS = EntityDataManager.createKey(SpinningPeppermintEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SPIN_SPEED = EntityDataManager.createKey(SpinningPeppermintEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> START_ANGLE = EntityDataManager.createKey(SpinningPeppermintEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> STRAIGHT = EntityDataManager.createKey(SpinningPeppermintEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> LIFESPAN = EntityDataManager.createKey(SpinningPeppermintEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> SEEKING_ENTITY = EntityDataManager.createKey(SpinningPeppermintEntity.class, DataSerializers.VARINT);
    public final ItemStack peppermintRenderStack = new ItemStack(ACBlockRegistry.SMALL_PEPPERMINT.item());
    private EntityLivingBase owner;
    private UUID ownerUUID;
    private int despawnsIn = -1;
    private int prevDespawnsIn;
    private float spinAngle;

    public SpinningPeppermintEntity(World worldIn) {
        super(worldIn);
        setSize(1.0F, 1.0F);
    }

    @Override
    protected void entityInit() {
        dataManager.register(SPIN_RADIUS, 1.0F);
        dataManager.register(SPIN_SPEED, 1.0F);
        dataManager.register(START_ANGLE, 0.0F);
        dataManager.register(STRAIGHT, false);
        dataManager.register(LIFESPAN, 200);
        dataManager.register(SEEKING_ENTITY, -1);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (despawnsIn == -1) {
            despawnsIn = getLifespan();
        }
        prevDespawnsIn = despawnsIn;
        if (despawnsIn-- <= 0 && !world.isRemote) {
            setDead();
        }
        if (isStraight()) {
            Vec3d push = new Vec3d(0.0D, 0.0D, -0.01D * getSpinSpeed()).rotateYaw((float) -Math.toRadians(rotationYaw));
            motionX += push.x;
            motionY += push.y;
            motionZ += push.z;
            motionX *= 0.9D;
            motionY = motionY * 0.9D - 0.08D;
            motionZ *= 0.9D;
            if (collidedVertically) {
                motionX *= 0.4D;
                motionY = 0.9D;
                motionZ *= 0.4D;
            }
        } else {
            Vec3d center = getSpinCenter();
            float progress = Math.min(1.0F, ticksExisted / 30.0F);
            Vec3d orbit = new Vec3d(0.0D, 0.0D, progress * getSpinRadius()).rotateYaw((float) -Math.toRadians(getStartAngle() + spinAngle));
            Vec3d target = center.add(orbit);
            Vec3d delta = target.subtract(getPositionVector()).scale(0.05D * getSpinSpeed());
            motionX = delta.x;
            motionY = delta.y;
            motionZ = delta.z;
            spinAngle += getSpinSpeed();
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
        hurtEntities();
    }

    private Vec3d getSpinCenter() {
        Entity seeking = getSeekingEntityId() == -1 ? null : world.getEntityByID(getSeekingEntityId());
        if (seeking != null) {
            Vec3d toward = seeking.getPositionEyes(1.0F).subtract(getPositionVector());
            if (Math.sqrt(toward.x * toward.x + toward.y * toward.y + toward.z * toward.z) > 1.0D) {
                toward = toward.normalize();
            }
            setSpinRadius(4.0F - 4.0F * Math.min(1.0F, ticksExisted / 30.0F));
            return getPositionVector().add(toward);
        }
        EntityLivingBase owner = getOwner();
        return owner == null ? getPositionVector() : owner.getPositionVector().add(0.0D, owner.height * 0.45D, 0.0D);
    }

    private void hurtEntities() {
        boolean hit = false;
        EntityLivingBase owner = getOwner();
        AxisAlignedBB box = getEntityBoundingBox().grow(0.1D);
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
            if (owner != null && entity != owner && !entity.isOnSameTeam(owner) && entity.attackEntityFrom(DamageSource.causeMobDamage(owner), 3.0F)) {
                hit = true;
                entity.knockBack(this, 0.3F, posX - entity.posX, posZ - entity.posZ);
            }
        }
        if (hit && getSeekingEntityId() != -1 && !world.isRemote) {
            setDead();
        }
    }

    public void setOwner(EntityLivingBase owner) {
        this.owner = owner;
        this.ownerUUID = owner == null ? null : owner.getUniqueID();
    }

    public EntityLivingBase getOwner() {
        if (owner == null && ownerUUID != null && !world.isRemote) {
            EntityPlayer player = world.getPlayerEntityByUUID(ownerUUID);
            if (player != null) {
                owner = player;
            }
        }
        return owner;
    }

    public float getDespawnTime(float partialTicks) {
        return prevDespawnsIn + (despawnsIn - prevDespawnsIn) * partialTicks;
    }

    public float getSpinSpeed() {
        return dataManager.get(SPIN_SPEED);
    }

    public void setSpinSpeed(float spinSpeed) {
        dataManager.set(SPIN_SPEED, spinSpeed);
    }

    public float getSpinRadius() {
        return dataManager.get(SPIN_RADIUS);
    }

    public void setSpinRadius(float spinRadius) {
        dataManager.set(SPIN_RADIUS, spinRadius);
    }

    public boolean isStraight() {
        return dataManager.get(STRAIGHT);
    }

    public void setStraight(boolean straight) {
        dataManager.set(STRAIGHT, straight);
    }

    public float getStartAngle() {
        return dataManager.get(START_ANGLE);
    }

    public void setStartAngle(float startAngle) {
        dataManager.set(START_ANGLE, startAngle);
    }

    public int getLifespan() {
        return dataManager.get(LIFESPAN);
    }

    public void setLifespan(int lifespan) {
        dataManager.set(LIFESPAN, lifespan);
    }

    public int getSeekingEntityId() {
        return dataManager.get(SEEKING_ENTITY);
    }

    public void setSeekingEntityId(int seekingEntityId) {
        dataManager.set(SEEKING_ENTITY, seekingEntityId);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        despawnsIn = compound.hasKey("DespawnsIn") ? compound.getInteger("DespawnsIn") : -1;
        setStraight(compound.getBoolean("Straight"));
        setLifespan(compound.getInteger("Lifespan"));
        setSpinSpeed(compound.getFloat("SpinSpeed"));
        setSpinRadius(compound.getFloat("SpinRadius"));
        setStartAngle(compound.getFloat("StartAngle"));
        setSeekingEntityId(compound.getInteger("SeekingEntity"));
        spinAngle = compound.getFloat("SpinAngle");
        if (compound.hasUniqueId("Owner")) {
            ownerUUID = compound.getUniqueId("Owner");
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("DespawnsIn", despawnsIn);
        compound.setBoolean("Straight", isStraight());
        compound.setInteger("Lifespan", getLifespan());
        compound.setFloat("SpinSpeed", getSpinSpeed());
        compound.setFloat("SpinRadius", getSpinRadius());
        compound.setFloat("StartAngle", getStartAngle());
        compound.setInteger("SeekingEntity", getSeekingEntityId());
        compound.setFloat("SpinAngle", spinAngle);
        if (ownerUUID != null) {
            compound.setUniqueId("Owner", ownerUUID);
        }
    }
}
