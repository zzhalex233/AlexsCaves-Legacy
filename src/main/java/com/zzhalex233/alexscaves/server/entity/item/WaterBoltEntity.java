package com.zzhalex233.alexscaves.server.entity.item;

import java.util.List;

import com.zzhalex233.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WaterBoltEntity extends EntityThrowable {
    private static final DataParameter<Integer> ARC_TOWARDS_ENTITY_ID = EntityDataManager.createKey(WaterBoltEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> BUBBLING = EntityDataManager.createKey(WaterBoltEntity.class, DataSerializers.BOOLEAN);
    private final Vec3d[] trailPositions = new Vec3d[64];
    private int trailPointer = -1;
    private int wooshSoundTime;
    private int dieIn = -1;
    private boolean playedSplashSound;
    public boolean ricochet;
    public float seekAmount = 0.3F;

    public WaterBoltEntity(World world) {
        super(world);
        setSize(0.6F, 0.6F);
    }

    public WaterBoltEntity(World world, EntityLivingBase thrower) {
        super(world, thrower);
        setSize(0.6F, 0.6F);
        float offset = thrower instanceof net.minecraft.entity.player.EntityPlayer ? 0.3F : 0.1F;
        setPosition(thrower.posX, thrower.posY + thrower.getEyeHeight() - offset, thrower.posZ);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(ARC_TOWARDS_ENTITY_ID, -1);
        dataManager.register(BUBBLING, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            Entity target = getArcingTowards();
            if (target != null && (ticksExisted > 3 || seekAmount > 0.3F) && dieIn == -1 && getDistance(target) > 1.5F && (ticksExisted < 20 || seekAmount > 0.3F && ticksExisted < 40)) {
                Vec3d arcVec = target.getPositionVector().add(0.0D, 0.85D * target.height, 0.0D).subtract(getPositionVector()).normalize();
                double scale = 1.0D - (seekAmount - 0.3F) * 0.3D;
                motionX = motionX * scale + arcVec.x * seekAmount;
                motionY = motionY * scale + arcVec.y * seekAmount;
                motionZ = motionZ * scale + arcVec.z * seekAmount;
                velocityChanged = true;
            }
        } else {
            for (int i = 0; i < 3 + rand.nextInt(2); i++) {
                world.spawnParticle(isInWater() || isBubbling() ? EnumParticleTypes.WATER_BUBBLE : EnumParticleTypes.WATER_SPLASH, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, -0.1D, 0.0D);
            }
        }
        if (wooshSoundTime-- <= 0) {
            wooshSoundTime = 30 + rand.nextInt(30);
            playSound(ACSoundRegistry.SEA_STAFF_WOOSH, 0.7F, 1.0F);
        }
        updateTrail();
        if (dieIn > 0 && --dieIn == 0) {
            setDead();
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!playedSplashSound) {
            playedSplashSound = true;
            playSound(ACSoundRegistry.SEA_STAFF_HIT, 1.0F, 1.0F);
        }
        if (!world.isRemote) {
            damageMobs();
            if (dieIn == -1) {
                dieIn = 5;
            }
        }
    }

    @Override
    public void setDead() {
        if (!world.isRemote) {
            world.setEntityState(this, (byte) 3);
        }
        super.setDead();
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int i = 0; i < 16; i++) {
                world.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX, posY + height * 0.5D, posZ, (rand.nextDouble() - 0.5D) * 0.5D, rand.nextDouble() * 0.5D, (rand.nextDouble() - 0.5D) * 0.5D);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.07F;
    }

    public boolean isBubbling() {
        return dataManager.get(BUBBLING);
    }

    public void setBubbling(boolean bubbling) {
        dataManager.set(BUBBLING, bubbling);
    }

    public void setArcingTowards(Entity entity) {
        dataManager.set(ARC_TOWARDS_ENTITY_ID, entity == null ? -1 : entity.getEntityId());
    }

    public Entity getArcingTowards() {
        int id = dataManager.get(ARC_TOWARDS_ENTITY_ID);
        return id == -1 ? null : world.getEntityByID(id);
    }

    public boolean hasTrail() {
        return trailPointer != -1;
    }

    public Vec3d getTrailPosition(int pointer, float partialTicks) {
        int i = trailPointer - pointer & 63;
        int j = trailPointer - pointer - 1 & 63;
        Vec3d from = trailPositions[j];
        Vec3d delta = trailPositions[i].subtract(from);
        return from.add(delta.scale(partialTicks));
    }

    private void updateTrail() {
        Vec3d trailAt = getPositionVector().add(0.0D, height * 0.5D, 0.0D);
        if (trailPointer == -1) {
            for (int i = 0; i < trailPositions.length; i++) {
                trailPositions[i] = trailAt;
            }
        }
        if (++trailPointer == trailPositions.length) {
            trailPointer = 0;
        }
        trailPositions[trailPointer] = trailAt;
    }

    private void damageMobs() {
        Entity thrower = getThrower();
        AxisAlignedBB bashBox = getEntityBoundingBox().grow(2.0D);
        Entity lastHitMob = null;
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bashBox);
        for (EntityLivingBase entity : entities) {
            if (canDamage(entity, thrower)) {
                lastHitMob = entity;
                DamageSource source = thrower instanceof EntityLivingBase ? DamageSource.causeIndirectMagicDamage(this, thrower) : DamageSource.MAGIC;
                if (entity.attackEntityFrom(source, 3.0F) && isBubbling()) {
                    entity.addPotionEffect(new PotionEffect(ACEffectRegistry.BUBBLED, 200));
                    playSound(ACSoundRegistry.SEA_STAFF_BUBBLE, 1.0F, 1.0F);
                }
            }
        }
        if (ricochet && lastHitMob != null) {
            ricochet = false;
            ricochetFrom(lastHitMob);
        }
    }

    private boolean canDamage(EntityLivingBase entity, Entity thrower) {
        if (entity instanceof DeepOneBaseEntity) {
            return false;
        }
        return thrower == null || entity != thrower && !entity.isOnSameTeam(thrower);
    }

    private void ricochetFrom(Entity hitBy) {
        Entity thrower = getThrower();
        if (!(thrower instanceof EntityLivingBase)) {
            return;
        }
        Entity target = null;
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, hitBy.getEntityBoundingBox().grow(32.0D))) {
            if (canDamage(entity, thrower) && entity != hitBy && entity.getDistance(hitBy) > 3.0D && (target == null || entity.getDistanceSq(hitBy) < target.getDistanceSq(hitBy))) {
                target = entity;
            }
        }
        if (target != null) {
            WaterBoltEntity bolt = new WaterBoltEntity(world, (EntityLivingBase) thrower);
            bolt.copyLocationAndAnglesFrom(this);
            bolt.setArcingTowards(target);
            Vec3d arcVec = target.getPositionVector().add(0.0D, target.height, 0.0D).subtract(getPositionVector()).normalize();
            bolt.motionX = arcVec.x;
            bolt.motionY = arcVec.y;
            bolt.motionZ = arcVec.z;
            bolt.setBubbling(isBubbling());
            world.spawnEntity(bolt);
        }
    }

    @Override
    public boolean isInWater() {
        return super.isInWater() || world.handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Bubbling", isBubbling());
        compound.setBoolean("Ricochet", ricochet);
        compound.setFloat("SeekAmount", seekAmount);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setBubbling(compound.getBoolean("Bubbling"));
        ricochet = compound.getBoolean("Ricochet");
        if (compound.hasKey("SeekAmount")) {
            seekAmount = compound.getFloat("SeekAmount");
        }
    }
}
