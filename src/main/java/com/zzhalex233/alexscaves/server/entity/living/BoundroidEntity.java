package com.zzhalex233.alexscaves.server.entity.living;

import java.util.UUID;

import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoundroidEntity extends EntityMob {
    private static final DataParameter<Integer> WINCH_ID = EntityDataManager.createKey(BoundroidEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SLAMMING = EntityDataManager.createKey(BoundroidEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SCARED = EntityDataManager.createKey(BoundroidEntity.class, DataSerializers.BOOLEAN);
    private UUID winchUUID;
    private float prevGroundProgress;
    private float groundProgress;
    public boolean draggedClimable;
    public boolean stopGravity;
    public int stopSlammingFor;
    private int stayOnGroundFor;

    public BoundroidEntity(World world) {
        super(world);
        setSize(1.4F, 1.1F);
        experienceValue = 5;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(WINCH_ID, -1);
        dataManager.register(SLAMMING, false);
        dataManager.register(SCARED, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(1, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevGroundProgress = groundProgress;
        super.onLivingUpdate();
        renderYawOffset = rotationYaw;
        if (onGround && groundProgress < 5.0F) {
            groundProgress++;
        } else if (!onGround && groundProgress > 0.0F) {
            groundProgress--;
        }
        if (!world.isRemote) {
            Entity winch = getWinch();
            if (winch == null && isEntityAlive()) {
                BoundroidWinchEntity created = new BoundroidWinchEntity(this);
                world.spawnEntity(created);
                setWinch(created);
            } else if (winch instanceof BoundroidWinchEntity) {
                ((BoundroidWinchEntity) winch).linkWithHead(this);
            }
            EntityLivingBase target = getAttackTarget();
            if (target != null && target.isEntityAlive() && getDistance(target) < 1.8D && stopSlammingFor <= 0) {
                setSlamming(true);
            }
            if (isSlamming()) {
                motionY = -1.0D;
                if (onGround) {
                    slamGround();
                }
            }
        }
        if (stopGravity) {
            motionY *= 0.2D;
            fallDistance = 0.0F;
        }
        if (stopSlammingFor > 0) {
            stopSlammingFor--;
        }
        if (stayOnGroundFor > 0) {
            stayOnGroundFor--;
        }
    }

    private void slamGround() {
        setSlamming(false);
        playSound(ACSoundRegistry.BOUNDROID_SLAM, 1.0F, 1.0F);
        stayOnGroundFor = 10;
        stopSlammingFor = 30 + rand.nextInt(20);
        world.setEntityState(this, (byte) 45);
        AxisAlignedBB bashBox = getEntityBoundingBox().grow(1.5D, 0.5D, 1.5D);
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, bashBox)) {
            if (entity != this && !(entity instanceof BoundroidEntity) && !(entity instanceof BoundroidWinchEntity) && !isOnSameTeam(entity)) {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), 5.0F);
            }
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 45) {
            spawnGroundEffects();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void spawnGroundEffects() {
        for (int i = 0; i < 40; i++) {
            double angle = rand.nextDouble() * Math.PI * 2.0D;
            double dist = 0.3D + rand.nextDouble() * 1.4D;
            double x = posX + Math.sin(angle) * dist;
            double z = posZ + Math.cos(angle) * dist;
            BlockPos ground = new BlockPos(x, posY - 0.2D, z);
            IBlockState state = world.getBlockState(ground);
            if (!state.getBlock().isAir(state, world, ground)) {
                world.spawnParticle(net.minecraft.util.EnumParticleTypes.BLOCK_CRACK, x, ground.getY() + 1.0D, z, rand.nextGaussian() * 0.05D, rand.nextDouble() * 0.12D, rand.nextGaussian() * 0.05D, Block.getStateId(state));
            }
        }
    }

    public void setWinch(Entity entity) {
        winchUUID = entity == null ? null : entity.getUniqueID();
        dataManager.set(WINCH_ID, entity == null ? -1 : entity.getEntityId());
    }

    public Entity getWinch() {
        if (world.isRemote) {
            int id = dataManager.get(WINCH_ID);
            return id == -1 ? null : world.getEntityByID(id);
        }
        if (winchUUID == null) {
            return null;
        }
        for (Entity entity : world.loadedEntityList) {
            if (winchUUID.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
    }

    public float getGroundProgress(float partialTicks) {
        return (prevGroundProgress + (groundProgress - prevGroundProgress) * partialTicks) * 0.2F;
    }

    public boolean isScared() {
        return dataManager.get(SCARED);
    }

    public void setScared(boolean scared) {
        dataManager.set(SCARED, scared);
    }

    public boolean isSlamming() {
        return dataManager.get(SLAMMING);
    }

    public void setSlamming(boolean slamming) {
        dataManager.set(SLAMMING, slamming);
    }

    public boolean stopPullingUp() {
        return isSlamming() || stayOnGroundFor > 0;
    }

    @Override
    public boolean isOnLadder() {
        return super.isOnLadder() || collidedHorizontally && draggedClimable;
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return source == DamageSource.IN_WALL || super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return super.isPotionApplicable(effect) && effect.getPotion() != ACEffectRegistry.MAGNETIZING;
    }

    @Override
    public void setDead() {
        Entity winch = getWinch();
        if (!world.isRemote && winch instanceof BoundroidWinchEntity && getHealth() <= 0.0F) {
            winch.setDead();
        }
        super.setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (winchUUID != null) {
            compound.setUniqueId("WinchUUID", winchUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("WinchUUID")) {
            winchUUID = compound.getUniqueId("WinchUUID");
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.BOUNDROID_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ACSoundRegistry.BOUNDROID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.BOUNDROID_DEATH;
    }

    public static class TargetAI extends EntityAIBase {
        private final BoundroidEntity boundroid;

        public TargetAI(BoundroidEntity boundroid) {
            this.boundroid = boundroid;
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return boundroid.getAttackTarget() != null;
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = boundroid.getAttackTarget();
            if (target != null) {
                float yaw = (float) (-MathHelper.atan2(target.posX - boundroid.posX, target.posZ - boundroid.posZ) * 57.2957763671875D);
                boundroid.rotationYaw = yaw;
                boundroid.renderYawOffset = yaw;
            }
        }
    }
}
