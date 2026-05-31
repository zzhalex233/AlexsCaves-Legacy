package com.zzhalex233.alexscaves.server.entity.living;

import java.util.UUID;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoundroidWinchEntity extends EntityMob {
    private static final DataParameter<Integer> HEAD_ID = EntityDataManager.createKey(BoundroidWinchEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> LATCHED = EntityDataManager.createKey(BoundroidWinchEntity.class, DataSerializers.BOOLEAN);
    private static final float MAX_DIST_TO_CEILING = 2.9F;
    private UUID headUUID;
    private float prevLatchProgress;
    private float latchProgress;
    private float distanceToCeiling;
    private boolean goingUp;
    private int noLatchCooldown;
    private int changeLatchStateTime;

    public BoundroidWinchEntity(World world) {
        super(world);
        setSize(0.9F, 0.9F);
        experienceValue = 0;
    }

    public BoundroidWinchEntity(BoundroidEntity parent) {
        this(parent.world);
        setHead(parent);
        setPosition(parent.posX, parent.posY + 0.5D, parent.posZ);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(HEAD_ID, -1);
        dataManager.register(LATCHED, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new MeleeAI());
        tasks.addTask(2, new EntityAIWander(this, 1.0D, 45));
        tasks.addTask(3, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return true;
    }

    @Override
    public boolean canDespawn() {
        return getHead() == null;
    }

    @Override
    public void onLivingUpdate() {
        prevLatchProgress = latchProgress;
        super.onLivingUpdate();
        if (isLatched() && latchProgress < 5.0F) {
            latchProgress++;
        } else if (!isLatched() && latchProgress > 0.0F) {
            latchProgress--;
        }
        if (noLatchCooldown > 0) {
            noLatchCooldown--;
        }
        Entity head = getHead();
        if (head instanceof BoundroidEntity) {
            BoundroidEntity boundroid = (BoundroidEntity) head;
            if (!world.isRemote) {
                linkWithHead(boundroid);
                tickLinkedHead(boundroid);
            }
            hurtTime = boundroid.hurtTime;
            deathTime = boundroid.deathTime;
        } else if (!world.isRemote) {
            setDead();
        }
    }

    private void tickLinkedHead(BoundroidEntity boundroid) {
        double distance = getDistance(boundroid);
        double distanceGoal = isLatched() ? 1.25D + Math.sin(ticksExisted * 0.1D) * 0.25D : 3.5D;
        if (distance > distanceGoal && !boundroid.stopPullingUp()) {
            Vec3d moveTo = getChainFrom(1.0F).subtract(boundroid.getPositionVector());
            if (moveTo.length() > 1.0D) {
                moveTo = moveTo.normalize();
            }
            double speed = boundroid.getAttackTarget() != null ? 0.3D : 0.1D;
            boundroid.draggedClimable = true;
            boundroid.motionX = boundroid.motionX * 0.95D + moveTo.x * speed;
            boundroid.motionY = boundroid.motionY * 0.7D + moveTo.y * speed;
            boundroid.motionZ = boundroid.motionZ * 0.95D + moveTo.z * speed;
        } else {
            boundroid.draggedClimable = false;
        }
        distanceToCeiling = calculateDistanceToCeiling();
        if (isLatched()) {
            setNoGravity(true);
            boundroid.stopGravity = true;
            motionY = Math.min(0.4D, motionY + 0.14D);
            motionX *= 0.85D;
            motionZ *= 0.85D;
            if (distanceToCeiling > MAX_DIST_TO_CEILING || !isEntityAlive() || noLatchCooldown > 0) {
                changeLatchStateTime++;
            } else {
                changeLatchStateTime = 0;
            }
            if (changeLatchStateTime > 5) {
                setLatched(false);
                if (noLatchCooldown > 0) {
                    playSound(ACSoundRegistry.BOUNDROID_DAZED, 2.0F, 1.0F);
                }
                changeLatchStateTime = 0;
            }
        } else {
            setNoGravity(false);
            boundroid.stopGravity = false;
            if ((distanceToCeiling < MAX_DIST_TO_CEILING || collidedVertically && !onGround) && noLatchCooldown <= 0) {
                changeLatchStateTime++;
            } else {
                changeLatchStateTime = 0;
            }
            if (changeLatchStateTime > 5) {
                setLatched(true);
                changeLatchStateTime = 0;
            }
            if (goingUp && !hasBlockAbove()) {
                goingUp = false;
            }
            if (goingUp) {
                motionY = 1.5D;
            } else if (onGround && noLatchCooldown == 0 && rand.nextInt(30) == 0 && distanceToCeiling > MAX_DIST_TO_CEILING && hasBlockAbove()) {
                goingUp = true;
            }
        }
    }

    private boolean hasBlockAbove() {
        BlockPos pos = new BlockPos(posX, posY + height, posZ);
        while (pos.getY() < world.getHeight()) {
            if (world.getBlockState(pos).isFullBlock()) {
                return true;
            }
            pos = pos.up();
        }
        return false;
    }

    private float calculateDistanceToCeiling() {
        BlockPos ceiling = getCeilingOf(getPosition());
        return (float) (ceiling.getY() - getEntityBoundingBox().maxY);
    }

    public BlockPos getCeilingOf(BlockPos pos) {
        while (!world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.DOWN) && pos.getY() < world.getHeight()) {
            pos = pos.up();
        }
        return pos;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity head = getHead();
        if (isEntityInvulnerable(source)) {
            return false;
        }
        if (head instanceof BoundroidEntity && !head.isEntityInvulnerable(source)) {
            boolean hurt = head.attackEntityFrom(source, amount);
            if (hurt) {
                noLatchCooldown = 60 + rand.nextInt(60);
            }
            return hurt;
        }
        return super.attackEntityFrom(source, amount);
    }

    public void linkWithHead(Entity head) {
        setHead(head);
    }

    public void setHead(Entity entity) {
        headUUID = entity == null ? null : entity.getUniqueID();
        dataManager.set(HEAD_ID, entity == null ? -1 : entity.getEntityId());
        if (entity instanceof BoundroidEntity) {
            ((BoundroidEntity) entity).setWinch(this);
        }
    }

    public Entity getHead() {
        if (world.isRemote) {
            int id = dataManager.get(HEAD_ID);
            return id == -1 ? null : world.getEntityByID(id);
        }
        if (headUUID == null) {
            return null;
        }
        for (Entity entity : world.loadedEntityList) {
            if (headUUID.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
    }

    public boolean isLatched() {
        return dataManager.get(LATCHED);
    }

    public void setLatched(boolean latched) {
        dataManager.set(LATCHED, latched);
    }

    public float getLatchProgress(float partialTicks) {
        return (prevLatchProgress + (latchProgress - prevLatchProgress) * partialTicks) * 0.2F;
    }

    public Vec3d getChainFrom(float partialTicks) {
        return new Vec3d(prevPosX + (posX - prevPosX) * partialTicks, prevPosY + (posY - prevPosY) * partialTicks + 0.2D, prevPosZ + (posZ - prevPosZ) * partialTicks);
    }

    public Vec3d getChainTo(float partialTicks) {
        Entity head = getHead();
        if (head instanceof BoundroidEntity) {
            return new Vec3d(head.prevPosX + (head.posX - head.prevPosX) * partialTicks, head.prevPosY + (head.posY - head.prevPosY) * partialTicks + head.height, head.prevPosZ + (head.posZ - head.prevPosZ) * partialTicks);
        }
        return getChainFrom(partialTicks).add(0.0D, 0.3D, 0.0D);
    }

    public float getChainLength(float partialTicks) {
        return (float) getChainTo(partialTicks).subtract(getChainFrom(partialTicks)).length();
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return super.isPotionApplicable(effect) && effect.getPotion() != ACEffectRegistry.MAGNETIZING;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (headUUID != null) {
            compound.setUniqueId("HeadUUID", headUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("HeadUUID")) {
            headUUID = compound.getUniqueId("HeadUUID");
        }
    }

    private class MeleeAI extends EntityAIBase {
        private MeleeAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return getHead() instanceof BoundroidEntity && ((BoundroidEntity) getHead()).getAttackTarget() != null;
        }

        @Override
        public void resetTask() {
            Entity head = getHead();
            if (head instanceof BoundroidEntity) {
                ((BoundroidEntity) head).setScared(false);
            }
        }

        @Override
        public void updateTask() {
            Entity head = getHead();
            if (!(head instanceof BoundroidEntity)) {
                return;
            }
            BoundroidEntity boundroid = (BoundroidEntity) head;
            EntityLivingBase target = boundroid.getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                return;
            }
            if (isLatched()) {
                boundroid.setScared(false);
                BlockPos ceiling = getCeilingOf(target.getPosition());
                getNavigator().tryMoveToXYZ(ceiling.getX() + 0.5D, ceiling.getY() - 1.0D, ceiling.getZ() + 0.5D, 1.0D);
            } else {
                boundroid.setScared(true);
                if (getNavigator().noPath()) {
                    Vec3d away = getPositionVector().subtract(target.getPositionVector()).normalize();
                    getNavigator().tryMoveToXYZ(posX + away.x * 8.0D, posY, posZ + away.z * 8.0D, 1.3D);
                }
            }
            faceEntity(target, 90.0F, 90.0F);
        }
    }
}
