package com.zzhalex233.alexscaves.server.entity.item;

import java.util.UUID;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.entity.living.DeepOneBaseEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WaveEntity extends Entity {
    private static final DataParameter<Boolean> SLAMMING = EntityDataManager.createKey(WaveEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> LIFESPAN = EntityDataManager.createKey(WaveEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WAITING_TICKS = EntityDataManager.createKey(WaveEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> WAVE_YAW = EntityDataManager.createKey(WaveEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WAVE_SCALE = EntityDataManager.createKey(WaveEntity.class, DataSerializers.FLOAT);
    @Nullable
    private EntityLivingBase owner;
    @Nullable
    private UUID ownerUUID;
    private float slamProgress;
    private float prevSlamProgress;
    public int activeWaveTicks;

    public WaveEntity(World world) {
        super(world);
        setSize(0.9F, 0.9F);
    }

    public WaveEntity(World world, EntityLivingBase owner) {
        this(world);
        setOwner(owner);
    }

    @Override
    protected void entityInit() {
        dataManager.register(SLAMMING, false);
        dataManager.register(LIFESPAN, 10);
        dataManager.register(WAITING_TICKS, 0);
        dataManager.register(WAVE_YAW, 0.0F);
        dataManager.register(WAVE_SCALE, 1.0F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevSlamProgress = slamProgress;
        if (getWaitingTicks() > 0) {
            if (!world.isRemote) {
                setWaitingTicks(getWaitingTicks() - 1);
            }
            setInvisible(true);
            return;
        } else if (isInvisible()) {
            setInvisible(false);
        }
        if (isSlamming() && slamProgress < 10.0F) {
            slamProgress += 1.0F;
        }
        if (isSlamming() && slamProgress >= 10.0F) {
            setDead();
            return;
        }
        if (!hasNoGravity() && !isInWater()) {
            motionY -= 0.04D;
        }
        float active = Math.min(activeWaveTicks / 10.0F, 1.0F);
        Vec3d direction = new Vec3d(0.0D, 0.0D, active * active * 0.2F).rotateYaw((float) Math.toRadians(-getWaveYaw()));
        if (world.isRemote) {
            spawnClientParticles();
        } else {
            attackEntities(getSlamAmount(1.0F) * 2.0F + 1.0F + getWaveScale());
            rotationYaw = getWaveYaw();
        }
        motionX = motionX * 0.9D + direction.x;
        motionY *= 0.9D;
        motionZ = motionZ * 0.9D + direction.z;
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.99D;
        motionY *= 0.98D;
        motionZ *= 0.99D;
        if (activeWaveTicks > getLifespan() || activeWaveTicks > 10 && MathHelper.sqrt(motionX * motionX + motionZ * motionZ) < 0.04F) {
            setSlamming(true);
        }
        activeWaveTicks++;
    }

    public float getSlamAmount(float partialTicks) {
        return (prevSlamProgress + (slamProgress - prevSlamProgress) * partialTicks) * 0.1F;
    }

    public void setOwner(@Nullable EntityLivingBase owner) {
        this.owner = owner;
        this.ownerUUID = owner == null ? null : owner.getUniqueID();
    }

    @Nullable
    public EntityLivingBase getOwner() {
        if (owner == null && ownerUUID != null && !world.isRemote) {
            Entity entity = ((net.minecraft.world.WorldServer) world).getEntityFromUuid(ownerUUID);
            if (entity instanceof EntityLivingBase) {
                owner = (EntityLivingBase) entity;
            }
        }
        return owner;
    }

    public boolean isSlamming() {
        return dataManager.get(SLAMMING);
    }

    public void setSlamming(boolean slamming) {
        dataManager.set(SLAMMING, slamming);
    }

    public int getLifespan() {
        return dataManager.get(LIFESPAN);
    }

    public void setLifespan(int lifespan) {
        dataManager.set(LIFESPAN, lifespan);
    }

    public int getWaitingTicks() {
        return dataManager.get(WAITING_TICKS);
    }

    public void setWaitingTicks(int waitingTicks) {
        dataManager.set(WAITING_TICKS, waitingTicks);
    }

    public float getWaveYaw() {
        return dataManager.get(WAVE_YAW);
    }

    public void setWaveYaw(float yaw) {
        dataManager.set(WAVE_YAW, yaw);
        rotationYaw = yaw;
    }

    public float getWaveScale() {
        return dataManager.get(WAVE_SCALE);
    }

    public void setWaveScale(float waveScale) {
        dataManager.set(WAVE_SCALE, waveScale);
        setSize(0.9F * waveScale, 0.9F * waveScale);
    }

    private void attackEntities(float scale) {
        EntityLivingBase owner = getOwner();
        AxisAlignedBB bashBox = getEntityBoundingBox().grow(0.5D);
        DamageSource source = owner == null ? DamageSource.MAGIC : DamageSource.causeIndirectMagicDamage(this, owner);
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, bashBox)) {
            if (canDamage(entity, owner)) {
                entity.attackEntityFrom(source, scale + 1.0F);
                setSlamming(true);
                double x = MathHelper.sin(getWaveYaw() * ((float) Math.PI / 180.0F));
                double z = -MathHelper.cos(getWaveYaw() * ((float) Math.PI / 180.0F));
                entity.addVelocity(x * (0.1D + 0.5D * scale), 0.1D, z * (0.1D + 0.5D * scale));
                entity.velocityChanged = true;
            }
        }
    }

    private boolean canDamage(EntityLivingBase entity, @Nullable EntityLivingBase owner) {
        return !(entity instanceof DeepOneBaseEntity) && (owner == null || entity != owner && !entity.isOnSameTeam(owner));
    }

    private void spawnClientParticles() {
        for (int particleCount = 0; particleCount < Math.max(1, getWaveScale()); particleCount++) {
            for (int i = 0; i <= 4; i++) {
                float xOffset = i / 4.0F - 0.5F + (rand.nextFloat() - 0.5F) * 0.2F;
                spawnParticleAt((0.2F + rand.nextFloat() * 0.2F) * getWaveScale(), 1.2F, xOffset * 1.2F * getWaveScale(), EnumParticleTypes.WATER_SPLASH);
                spawnParticleAt((0.2F + rand.nextFloat() * 0.2F) * getWaveScale(), -0.2F, xOffset * 1.4F * getWaveScale(), EnumParticleTypes.WATER_BUBBLE);
            }
        }
    }

    private void spawnParticleAt(float yOffset, float zOffset, float xOffset, EnumParticleTypes particleType) {
        Vec3d offset = new Vec3d(xOffset, yOffset, zOffset).rotateYaw((float) Math.toRadians(-getWaveYaw()));
        world.spawnParticle(particleType, posX + offset.x, posY + offset.y, posZ + offset.z, motionX, 0.1D, motionZ);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasUniqueId("Owner")) {
            ownerUUID = compound.getUniqueId("Owner");
        }
        setLifespan(compound.getInteger("Lifespan"));
        setWaitingTicks(compound.getInteger("WaitingTicks"));
        setWaveYaw(compound.getFloat("WaveYaw"));
        setWaveScale(compound.hasKey("WaveScale") ? compound.getFloat("WaveScale") : 1.0F);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (ownerUUID != null) {
            compound.setUniqueId("Owner", ownerUUID);
        }
        compound.setInteger("Lifespan", getLifespan());
        compound.setInteger("WaitingTicks", getWaitingTicks());
        compound.setFloat("WaveYaw", getWaveYaw());
        compound.setFloat("WaveScale", getWaveScale());
    }
}
