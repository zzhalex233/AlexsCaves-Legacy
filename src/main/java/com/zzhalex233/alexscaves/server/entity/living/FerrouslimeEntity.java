package com.zzhalex233.alexscaves.server.entity.living;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class FerrouslimeEntity extends EntityMob {
    private static final DataParameter<Integer> HEADS = EntityDataManager.createKey(FerrouslimeEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ATTACK_TICK = EntityDataManager.createKey(FerrouslimeEntity.class, DataSerializers.VARINT);
    private final Map<Integer, Vec3d> headOffsets = new HashMap<>();
    public float prevHeadCount = 1.0F;
    private float prevMergeProgress = 5.0F;
    private float mergeProgress = 5.0F;
    private float prevAttackProgress;
    private float attackProgress;
    private int mergeCooldown;
    private int noMoveTime;

    public FerrouslimeEntity(World world) {
        super(world);
        setSize(1.0F, 1.0F);
        setNoGravity(true);
        experienceValue = 2;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(HEADS, 1);
        dataManager.register(ATTACK_TICK, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new MeleeAI());
        tasks.addTask(2, new FormAI());
        tasks.addTask(3, new EntityAIWander(this, 1.0D, 20));
        tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(5, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevMergeProgress = mergeProgress;
        prevAttackProgress = attackProgress;
        setNoGravity(true);
        super.onLivingUpdate();
        updateHeadAttributes();
        int attackTick = dataManager.get(ATTACK_TICK);
        if (attackTick > 0) {
            dataManager.set(ATTACK_TICK, attackTick - 1);
            attackProgress = Math.min(5.0F, attackProgress + 1.0F);
        } else if (attackProgress > 0.0F) {
            EntityLivingBase target = getAttackTarget();
            if (attackProgress >= 3.0F && target != null && getDistance(target) < getSlimeSize(1.0F)) {
                target.attackEntityFrom(DamageSource.causeMobDamage(this), 4.0F + getHeadCount() * 2.0F);
            }
            attackProgress--;
        }
        if (world.isRemote && isEntityAlive()) {
            float slimeSize = getSlimeSize(1.0F);
            for (int i = 0; i < Math.ceil(slimeSize); i++) {
                world.spawnParticle(EnumParticleTypes.SLIME, posX + (rand.nextDouble() - 0.5D) * (slimeSize + 1.5F), posY + (rand.nextDouble() - 0.5D) * (slimeSize + 1.5F), posZ + (rand.nextDouble() - 0.5D) * (slimeSize + 1.5F), 0.0D, 0.0D, 0.0D);
            }
        } else {
            EntityLivingBase target = getAttackTarget();
            if (target != null && target.isEntityAlive()) {
                if (Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) < 0.1D) {
                    noMoveTime++;
                } else {
                    noMoveTime = 0;
                }
                if (noMoveTime > 40 && mergeCooldown <= 0) {
                    split(1200);
                }
            }
        }
        if (mergeCooldown > 0) {
            mergeCooldown--;
        }
    }

    private void updateHeadAttributes() {
        if (prevHeadCount != getHeadCount()) {
            setSize(getSlimeSize(1.0F), getSlimeSize(1.0F));
            if (mergeProgress < 5.0F) {
                mergeProgress++;
            } else {
                getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MathHelper.clamp(getHeadCount() * 10.0D, 10.0D, 100.0D));
                getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(MathHelper.clamp(getHeadCount() * 2.0D, 2.0D, 10.0D));
                if (getHealth() < getMaxHealth()) {
                    heal((float) Math.ceil(getMaxHealth() - getHealth()));
                }
                prevHeadCount = getHeadCount();
            }
        }
    }

    @Override
    public void setDead() {
        if (!world.isRemote && isEntityAlive() && getHealth() <= 0.0F && getHeadCount() >= 2) {
            int ours = getHeadCount() / 2;
            int theirs = getHeadCount() - ours;
            world.spawnEntity(makeSlime(ours, 1200));
            world.spawnEntity(makeSlime(theirs, 1200));
        }
        super.setDead();
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, net.minecraft.block.state.IBlockState state, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, Block block) {
    }

    public int getHeadCount() {
        return dataManager.get(HEADS);
    }

    public void setHeadCount(int headCount) {
        dataManager.set(HEADS, Math.max(1, headCount));
        headOffsets.clear();
        mergeProgress = 0.0F;
        setSize(getSlimeSize(1.0F), getSlimeSize(1.0F));
    }

    public float getMergeProgress(float partialTick) {
        return (prevMergeProgress + (mergeProgress - prevMergeProgress) * partialTick) * 0.2F;
    }

    public float getAttackProgress(float partialTick) {
        return (prevAttackProgress + (attackProgress - prevAttackProgress) * partialTick) * 0.2F;
    }

    public float getSlimeSize(float partialTicks) {
        float smoothHeadCount = getHeadCount() - 1 + getMergeProgress(partialTicks);
        return Math.min((float) (Math.log(Math.max(1.0F, smoothHeadCount)) + 1.0F) * 1.2F, 3.2F);
    }

    public Vec3d getHeadOffsetPos(int i) {
        if (i <= 1) {
            return Vec3d.ZERO;
        }
        Vec3d existing = headOffsets.get(i);
        if (existing != null) {
            return existing;
        }
        Vec3d offset = new Vec3d(rand.nextFloat() - 0.5F, rand.nextFloat() - 0.5F, rand.nextFloat() - 0.5F).scale(getSlimeSize(1.0F) * 0.5F);
        headOffsets.put(i, offset);
        return offset;
    }

    public boolean split(int cooldown) {
        if (getHeadCount() >= 2) {
            int ours = getHeadCount() / 2;
            int theirs = getHeadCount() - ours;
            world.spawnEntity(makeSlime(ours, cooldown));
            world.spawnEntity(makeSlime(theirs, cooldown));
            setDead();
            return true;
        }
        return false;
    }

    private FerrouslimeEntity makeSlime(int heads, int cooldown) {
        FerrouslimeEntity slime = new FerrouslimeEntity(world);
        slime.copyLocationAndAnglesFrom(this);
        slime.setHeadCount(heads);
        if (hasCustomName()) {
            slime.setCustomNameTag(getCustomNameTag());
        }
        slime.setNoAI(isAIDisabled());
        slime.setEntityInvulnerable(isEntityInvulnerable(DamageSource.GENERIC));
        slime.mergeCooldown = cooldown;
        slime.rotationYaw = rotationYaw;
        slime.renderYawOffset = renderYawOffset;
        return slime;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        dataManager.set(ATTACK_TICK, 10);
        return super.attackEntityAsMob(entityIn);
    }

    private boolean canForm() {
        return isEntityAlive() && mergeCooldown <= 0;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (getHeadCount() <= 1 && rand.nextInt(2 + lootingModifier) > 0) {
            entityDropItem(new ItemStack(ACItemRegistry.FERROUSLIME_BALL.item()), 0.0F);
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        setHeadCount(1 + rand.nextInt(2));
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Heads", getHeadCount());
        compound.setInteger("MergeCooldown", mergeCooldown);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setHeadCount(Math.max(1, compound.getInteger("Heads")));
        mergeCooldown = compound.getInteger("MergeCooldown");
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.FERROUSLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.FERROUSLIME_DEATH;
    }

    private class MeleeAI extends EntityAIBase {
        private int cooldown;

        private MeleeAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target == null) {
                return;
            }
            getNavigator().tryMoveToEntityLiving(target, 1.0D);
            getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
            if (getDistance(target) < 1.0F + getSlimeSize(1.0F) && canEntityBeSeen(target) && cooldown <= 0) {
                attackEntityAsMob(target);
                cooldown = 10;
            }
            if (cooldown > 0) {
                cooldown--;
            }
        }

        @Override
        public void resetTask() {
            cooldown = 0;
        }
    }

    private class FormAI extends EntityAIBase {
        private int executionCooldown;
        private FerrouslimeEntity otherSlime;

        private FormAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (!canForm()) {
                return false;
            }
            if (--executionCooldown > 0) {
                return false;
            }
            executionCooldown = getAttackTarget() == null ? 100 : 20;
            List<FerrouslimeEntity> list = world.getEntitiesWithinAABB(FerrouslimeEntity.class, getEntityBoundingBox().grow(30.0D));
            otherSlime = null;
            for (FerrouslimeEntity slime : list) {
                if (slime != FerrouslimeEntity.this && slime.canForm() && (otherSlime == null || slime.getDistance(FerrouslimeEntity.this) < otherSlime.getDistance(FerrouslimeEntity.this))) {
                    otherSlime = slime;
                }
            }
            return otherSlime != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return otherSlime != null && canForm() && otherSlime.canForm() && getDistance(otherSlime) < 32.0F;
        }

        @Override
        public void updateTask() {
            getNavigator().tryMoveToEntityLiving(otherSlime, 1.0D);
            if (getDistance(otherSlime) <= 0.5F + (width + otherSlime.width) * 0.5F && otherSlime.canForm()) {
                setHeadCount(getHeadCount() + otherSlime.getHeadCount());
                otherSlime.setDead();
                playSound(ACSoundRegistry.FERROUSLIME_COMBINE, 1.0F, 1.0F);
                mergeCooldown = 600;
                otherSlime = null;
            }
        }
    }
}
