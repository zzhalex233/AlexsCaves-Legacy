package com.zzhalex233.alexscaves.server.entity.item;

import java.util.ArrayList;
import java.util.List;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GumballEntity extends EntityThrowable {
    private static final DataParameter<Integer> MAXIMUM_BOUNCES = EntityDataManager.createKey(GumballEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> BOUNCES = EntityDataManager.createKey(GumballEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(GumballEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(GumballEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> EXPLOSIVE = EntityDataManager.createKey(GumballEntity.class, DataSerializers.BOOLEAN);
    private final List<Integer> hitEntityIds = new ArrayList<>();
    private float prevExplodeProgress;
    private float explodeProgress;
    private int bounceSoundCooldown;

    public GumballEntity(World world) {
        super(world);
        setSize(0.35F, 0.35F);
    }

    public GumballEntity(World world, EntityLivingBase thrower) {
        super(world, thrower);
        setColor(world.rand.nextInt(11));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(MAXIMUM_BOUNCES, 5);
        dataManager.register(BOUNCES, 0);
        dataManager.register(DAMAGE, 2.0F);
        dataManager.register(COLOR, rand.nextInt(11));
        dataManager.register(EXPLOSIVE, false);
    }

    @Override
    public void onUpdate() {
        prevExplodeProgress = explodeProgress;
        super.onUpdate();
        if (isExplosive() && getBounces() >= getMaximumBounces()) {
            motionX = motionY = motionZ = 0.0D;
            if (explodeProgress++ > 20.0F && !world.isRemote) {
                world.createExplosion(getThrower(), posX, posY + 0.5D, posZ, 2.0F, false);
                setDead();
            }
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY + 0.4D, posZ, 0.0D, 0.1D, 0.0D);
        }
        if (bounceSoundCooldown > 0) {
            bounceSoundCooldown--;
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            bounceFromDirection(result.sideHit == null ? EnumFacing.UP : result.sideHit);
        } else if (result.entityHit != null && result.entityHit != getThrower() && !(result.entityHit instanceof GumballEntity)) {
            if (!hitEntityIds.contains(result.entityHit.getEntityId())) {
                hitEntityIds.add(result.entityHit.getEntityId());
                hitEntity(result.entityHit);
            }
            Vec3d vec = result.entityHit.getPositionEyes(1.0F).subtract(getPositionEyes(1.0F));
            float yaw = -((float) Math.atan2(vec.x, vec.z)) * 57.295776F;
            bounceFromDirection(EnumFacing.fromAngle(yaw));
        }
    }

    private void hitEntity(Entity entity) {
        Entity thrower = getThrower();
        if (thrower == null || entity != thrower && !entity.isOnSameTeam(thrower)) {
            playSound(ACSoundRegistry.GUMBALL_HIT, 1.0F, 1.0F);
            entity.attackEntityFrom(thrower instanceof EntityLivingBase ? DamageSource.causeMobDamage((EntityLivingBase) thrower) : DamageSource.GENERIC, getDamage());
        }
    }

    private void bounceFromDirection(EnumFacing direction) {
        if (getBounces() > getMaximumBounces() - 1 && isExplosive()) {
            motionX = motionY = motionZ = 0.0D;
            setBounces(getMaximumBounces());
            return;
        }
        if (bounceSoundCooldown == 0) {
            bounceSoundCooldown = 5;
            playSound(ACSoundRegistry.GUMBALL_BOUNCE, 1.0F, 1.0F);
        }
        if (direction.getAxis() == EnumFacing.Axis.X) {
            motionX = -motionX * 0.8D;
        } else if (direction.getAxis() == EnumFacing.Axis.Y) {
            motionY = -motionY * 0.5D;
        } else {
            motionZ = -motionZ * 0.8D;
        }
        if (!world.isRemote) {
            setBounces(getBounces() + 1);
            if (getBounces() > getMaximumBounces()) {
                setDead();
            }
        }
        velocityChanged = true;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.08F;
    }

    public int getMaximumBounces() {
        return dataManager.get(MAXIMUM_BOUNCES);
    }

    public void setMaximumBounces(int bounces) {
        dataManager.set(MAXIMUM_BOUNCES, bounces);
    }

    public int getBounces() {
        return dataManager.get(BOUNCES);
    }

    public void setBounces(int bounces) {
        dataManager.set(BOUNCES, bounces);
    }

    public float getDamage() {
        return dataManager.get(DAMAGE);
    }

    public void setDamage(float damage) {
        dataManager.set(DAMAGE, damage);
    }

    public int getColor() {
        return dataManager.get(COLOR);
    }

    public void setColor(int color) {
        dataManager.set(COLOR, color);
    }

    public boolean isExplosive() {
        return dataManager.get(EXPLOSIVE);
    }

    public void setExplosive(boolean explosive) {
        dataManager.set(EXPLOSIVE, explosive);
    }

    public float getExplodeProgress(float partialTicks) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTicks) / 20.0F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Color", getColor());
        compound.setInteger("Bounces", getBounces());
        compound.setInteger("MaximumBounces", getMaximumBounces());
        compound.setFloat("Damage", getDamage());
        compound.setBoolean("Explosive", isExplosive());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setColor(compound.getInteger("Color"));
        setBounces(compound.getInteger("Bounces"));
        setMaximumBounces(compound.getInteger("MaximumBounces"));
        setDamage(compound.getFloat("Damage"));
        setExplosive(compound.getBoolean("Explosive"));
    }
}
