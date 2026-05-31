package com.zzhalex233.alexscaves.server.entity.living;

import java.util.ArrayList;
import java.util.List;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.util.MagnetronJoint;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagnetronEntity extends EntityMob {
    private static final DataParameter<Boolean> FORMED = EntityDataManager.createKey(MagnetronEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ATTACK_POSE = EntityDataManager.createKey(MagnetronEntity.class, DataSerializers.VARINT);
    private static final int BLOCK_COUNT = MagnetronJoint.values().length * 2;
    private static final float FORM_TIME = 40.0F;

    private final int[] partIds = new int[BLOCK_COUNT];
    private float prevWheelRot;
    private float wheelRot;
    private float prevWheelYaw;
    private float wheelYaw;
    private float prevRollLeanProgress;
    private float rollLeanProgress;
    private float prevFormProgress;
    private float formProgress;
    private float prevAttackPoseProgress;
    private float attackPoseProgress;
    private AttackPose prevAttackPose = AttackPose.NONE;
    private boolean hasFormedAttributes;
    private int movingSoundTimer;

    public MagnetronEntity(World world) {
        super(world);
        setSize(1.2F, 1.2F);
        experienceValue = 13;
        formProgress = FORM_TIME;
        prevFormProgress = FORM_TIME;
        for (int i = 0; i < partIds.length; i++) {
            partIds[i] = -1;
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FORMED, false);
        dataManager.register(ATTACK_POSE, AttackPose.NONE.ordinal());
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new MagnetronMeleeAI());
        tasks.addTask(2, new EntityAIWander(this, 1.0D, 45));
        tasks.addTask(3, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(6.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevWheelRot = wheelRot;
        prevWheelYaw = wheelYaw;
        prevRollLeanProgress = rollLeanProgress;
        prevFormProgress = formProgress;
        prevAttackPoseProgress = attackPoseProgress;
        double speed = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        if (!isFormed() && speed > 0.01D) {
            wheelRot += Math.max(speed * 10.0D, 1.0D) * 15.0F;
            rollLeanProgress = Math.min(5.0F, rollLeanProgress + 1.0F);
            if (++movingSoundTimer > 20) {
                playSound(ACSoundRegistry.MAGNETRON_ROLL, getSoundVolume(), getSoundPitch());
                movingSoundTimer = 0;
            }
        } else {
            rollLeanProgress = Math.max(0.0F, rollLeanProgress - 1.0F);
            wheelRot = approachDegrees(wheelRot, 0.0F, 15.0F);
        }
        wheelYaw = approachDegrees(wheelYaw, renderYawOffset, 15.0F);
        if (!world.isRemote && !isFormed()) {
            EntityLivingBase target = getAttackTarget();
            if (target instanceof EntityPlayer && getDistance(target) < 8.0F) {
                startForming();
            }
        }
        if (isFormed() && formProgress < FORM_TIME) {
            if (formProgress == 0.0F) {
                playSound(ACSoundRegistry.MAGNETRON_ASSEMBLE, getSoundVolume(), getSoundPitch());
            }
            formProgress++;
        }
        if (!isFormed() && formProgress > 0.0F) {
            formProgress = 0.0F;
        }
        if (getAttackPose() != prevAttackPose) {
            attackPoseProgress = Math.min(10.0F, attackPoseProgress + 1.0F);
            if (attackPoseProgress >= 10.0F) {
                prevAttackPose = getAttackPose();
            }
        } else {
            attackPoseProgress = 10.0F;
        }
        if (isFormed() && !hasFormedAttributes) {
            hasFormedAttributes = true;
            setSize(1.6F, 3.2F);
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);
            getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
            heal(80.0F);
        }
        ensureParts();
    }

    private void startForming() {
        if (isFormed()) {
            return;
        }
        setFormed(true);
        List<IBlockState> states = findBodyBlocks();
        int index = 0;
        for (MagnetronJoint joint : MagnetronJoint.values()) {
            spawnOrUpdatePart(index++, joint, false, states.get((index - 1) % states.size()));
            spawnOrUpdatePart(index++, joint, true, states.get((index - 1) % states.size()));
        }
    }

    private List<IBlockState> findBodyBlocks() {
        List<IBlockState> states = new ArrayList<>();
        BlockPos origin = getPosition();
        for (BlockPos pos : BlockPos.getAllInBox(origin.add(-6, -3, -6), origin.add(6, 4, 6))) {
            IBlockState state = world.getBlockState(pos);
            if (isMagnetronBodyBlock(state)) {
                states.add(state);
                if (world.getGameRules().getBoolean("mobGriefing")) {
                    world.setBlockToAir(pos);
                }
                if (states.size() >= BLOCK_COUNT) {
                    return states;
                }
            }
        }
        while (states.size() < BLOCK_COUNT) {
            states.add(states.size() < 2 ? Blocks.ANVIL.getDefaultState() : ACBlockRegistry.SCRAP_METAL.block().getDefaultState());
        }
        return states;
    }

    private boolean isMagnetronBodyBlock(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.IRON_BLOCK || block == Blocks.ANVIL || block == Blocks.IRON_BARS || block == ACBlockRegistry.SCRAP_METAL.block() || block == ACBlockRegistry.SCRAP_METAL_PLATE.block() || block == ACBlockRegistry.BLOCK_OF_SCARLET_NEODYMIUM.block() || block == ACBlockRegistry.BLOCK_OF_AZURE_NEODYMIUM.block();
    }

    private void ensureParts() {
        if (!isFormed() || world.isRemote) {
            return;
        }
        int index = 0;
        for (MagnetronJoint joint : MagnetronJoint.values()) {
            spawnOrUpdatePart(index++, joint, false, ACBlockRegistry.SCRAP_METAL.block().getDefaultState());
            spawnOrUpdatePart(index++, joint, true, ACBlockRegistry.SCRAP_METAL_PLATE.block().getDefaultState());
        }
    }

    private void spawnOrUpdatePart(int index, MagnetronJoint joint, boolean left, IBlockState state) {
        Entity entity = partIds[index] == -1 ? null : world.getEntityByID(partIds[index]);
        MagnetronPartEntity part;
        if (entity instanceof MagnetronPartEntity && !entity.isDead) {
            part = (MagnetronPartEntity) entity;
        } else {
            part = new MagnetronPartEntity(world, this, joint, left);
            part.setPosition(posX, posY, posZ);
            world.spawnEntity(part);
            partIds[index] = part.getEntityId();
        }
        part.setParentId(getEntityId());
        part.setJoint(joint);
        part.setLeft(left);
        part.setBlockState(state);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean hit = super.attackEntityFrom(source, amount);
        if (hit && source.getTrueSource() instanceof EntityPlayer && !isFormed() && !isAIDisabled()) {
            startForming();
        }
        return hit;
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote) {
            removeParts();
        }
    }

    @Override
    public void setDead() {
        super.setDead();
        if (!world.isRemote) {
            removeParts();
        }
    }

    private void removeParts() {
        for (int id : partIds) {
            Entity entity = id == -1 ? null : world.getEntityByID(id);
            if (entity instanceof MagnetronPartEntity) {
                entity.setDead();
            }
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    public boolean hasNoGravity() {
        return isFormed() || super.hasNoGravity();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.MAGNETRON_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ACSoundRegistry.MAGNETRON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.MAGNETRON_DEATH;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Formed", isFormed());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setFormed(compound.getBoolean("Formed"));
    }

    public boolean isFormed() {
        return dataManager.get(FORMED);
    }

    public void setFormed(boolean formed) {
        dataManager.set(FORMED, formed);
    }

    public float getFormProgress(float partialTicks) {
        return (prevFormProgress + (formProgress - prevFormProgress) * partialTicks) / FORM_TIME;
    }

    public float getRollPosition(float partialTicks) {
        return prevWheelRot + (wheelRot - prevWheelRot) * partialTicks;
    }

    public float getWheelYaw(float partialTicks) {
        return prevWheelYaw + (wheelYaw - prevWheelYaw) * partialTicks;
    }

    public float getRollLeanProgress(float partialTicks) {
        return (prevRollLeanProgress + (rollLeanProgress - prevRollLeanProgress) * partialTicks) / 5.0F;
    }

    public AttackPose getAttackPose() {
        return AttackPose.get(dataManager.get(ATTACK_POSE));
    }

    public void setAttackPose(AttackPose pose) {
        dataManager.set(ATTACK_POSE, pose.ordinal());
        prevAttackPoseProgress = 0.0F;
        attackPoseProgress = 0.0F;
    }

    public AttackPose getPrevAttackPose() {
        return prevAttackPose;
    }

    public float getAttackPoseProgress(float partialTicks) {
        return (prevAttackPoseProgress + (attackPoseProgress - prevAttackPoseProgress) * partialTicks) / 10.0F;
    }

    public MagnetronPartEntity[] getPartsArray() {
        List<MagnetronPartEntity> parts = new ArrayList<>();
        for (int id : partIds) {
            Entity entity = id == -1 ? null : world.getEntityByID(id);
            if (entity instanceof MagnetronPartEntity) {
                parts.add((MagnetronPartEntity) entity);
            }
        }
        return parts.toArray(new MagnetronPartEntity[0]);
    }

    public enum AttackPose {
        NONE,
        RIGHT_PUNCH,
        LEFT_PUNCH,
        SLAM;

        public static AttackPose get(int index) {
            return values()[MathHelper.clamp(index, 0, values().length - 1)];
        }
    }

    private static float approachDegrees(float current, float target, float step) {
        return current + MathHelper.clamp(MathHelper.wrapDegrees(target - current), -step, step);
    }

    private class MagnetronMeleeAI extends EntityAIAttackMelee {
        private int poseCooldown;

        private MagnetronMeleeAI() {
            super(MagnetronEntity.this, 1.0D, true);
        }

        @Override
        public void updateTask() {
            super.updateTask();
            if (poseCooldown > 0) {
                poseCooldown--;
            }
            EntityLivingBase target = getAttackTarget();
            if (target != null && isFormed() && getDistance(target) < 5.0F && poseCooldown <= 0) {
                AttackPose pose = rand.nextInt(4) == 0 ? AttackPose.SLAM : (rand.nextBoolean() ? AttackPose.LEFT_PUNCH : AttackPose.RIGHT_PUNCH);
                setAttackPose(pose);
                playSound(ACSoundRegistry.MAGNETRON_ATTACK, getSoundVolume(), getSoundPitch());
                dealPoseDamage(pose);
                poseCooldown = pose == AttackPose.SLAM ? 25 : 18;
            } else if (poseCooldown == 0 && getAttackPose() != AttackPose.NONE) {
                setAttackPose(AttackPose.NONE);
            }
        }

        private void dealPoseDamage(AttackPose pose) {
            AxisAlignedBB box = getEntityBoundingBox().grow(pose == AttackPose.SLAM ? 5.0D : 3.0D, 2.0D, pose == AttackPose.SLAM ? 5.0D : 3.0D);
            for (EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
                if (living != MagnetronEntity.this && !isOnSameTeam(living)) {
                    living.attackEntityFrom(DamageSource.causeMobDamage(MagnetronEntity.this), pose == AttackPose.SLAM ? 10.0F : 6.0F);
                    double dx = living.posX - posX;
                    double dz = living.posZ - posZ;
                    double d = Math.max(dx * dx + dz * dz, 0.001D);
                    living.addVelocity(dx / d * 0.7D, pose == AttackPose.SLAM ? 0.45D : 0.25D, dz / d * 0.7D);
                }
            }
        }
    }
}
