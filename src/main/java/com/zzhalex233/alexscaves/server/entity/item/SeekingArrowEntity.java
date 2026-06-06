package com.zzhalex233.alexscaves.server.entity.item;

import java.util.List;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SeekingArrowEntity extends EntityArrow {
    private static final DataParameter<Integer> ARC_TOWARDS_ENTITY_ID = EntityDataManager.createKey(SeekingArrowEntity.class, DataSerializers.VARINT);
    private boolean stopSeeking;

    public SeekingArrowEntity(World worldIn) {
        super(worldIn);
    }

    public SeekingArrowEntity(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    public SeekingArrowEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(ARC_TOWARDS_ENTITY_ID, -1);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        int targetId = getArcTowardsId();
        if (!isInGround() && !stopSeeking) {
            if (targetId == -1) {
                if (!world.isRemote) {
                    Entity closest = null;
                    float range = Math.min(10.0F, 3.0F + ticksExisted / 4.0F);
                    List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(range));
                    for (Entity entity : entities) {
                        if (canSeek(entity) && (closest == null || entity.getDistanceSq(this) < closest.getDistanceSq(this))) {
                            closest = entity;
                        }
                    }
                    if (closest != null) {
                        playSound(ACSoundRegistry.SEEKING_ARROW_LOCKON, 5.0F, 1.0F);
                        setArcTowardsId(closest.getEntityId());
                    }
                }
            } else {
                Entity target = world.getEntityByID(targetId);
                if (target != null) {
                    Vec3d targetVec = target.getPositionVector().add(0.0D, target.height * 0.65D, 0.0D).subtract(getPositionVector());
                    if (targetVec.length() > target.width) {
                        Vec3d current = new Vec3d(motionX, motionY, motionZ).scale(0.3D).add(targetVec.normalize().scale(0.7D));
                        motionX = current.x;
                        motionY = current.y;
                        motionZ = current.z;
                        velocityChanged = true;
                    }
                }
            }
        }
        if (world.isRemote && !isInGround()) {
            world.spawnParticle(EnumParticleTypes.REDSTONE, posX + motionX, posY + motionY, posZ + motionZ, 1.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        super.arrowHit(living);
        playSound(ACSoundRegistry.SEEKING_ARROW_HIT, 1.0F, 1.0F);
        stopSeeking = true;
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ACItemRegistry.SEEKING_ARROW.item());
    }

    private boolean canSeek(Entity entity) {
        Entity shooter = shootingEntity;
        if (!(entity instanceof EntityLivingBase) || entity == shooter || isEntityEqual(entity)) {
            return false;
        }
        return shooter == null || !entity.isOnSameTeam(shooter);
    }

    private boolean isInGround() {
        return inGround;
    }

    private int getArcTowardsId() {
        return dataManager.get(ARC_TOWARDS_ENTITY_ID);
    }

    private void setArcTowardsId(int id) {
        dataManager.set(ARC_TOWARDS_ENTITY_ID, id);
    }
}
