package com.zzhalex233.alexscaves.server.entity.living;

import java.util.List;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
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
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class ForsakenEntity extends EntityMob {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_SUMMON = 1;
    public static final int ANIMATION_PREPARE_JUMP = 2;
    public static final int ANIMATION_BITE = 3;
    public static final int ANIMATION_LEFT_SLASH = 4;
    public static final int ANIMATION_RIGHT_SLASH = 5;
    public static final int ANIMATION_GROUND_SMASH = 6;
    public static final int ANIMATION_SONIC_ATTACK = 7;
    public static final int ANIMATION_SONIC_BLAST = 8;
    public static final int ANIMATION_LEFT_PICKUP = 9;
    public static final int ANIMATION_RIGHT_PICKUP = 10;
    private static final DataParameter<Boolean> RUNNING = EntityDataManager.createKey(ForsakenEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LEAPING = EntityDataManager.createKey(ForsakenEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> HELD_ID = EntityDataManager.createKey(ForsakenEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DARKNESS_TIME = EntityDataManager.createKey(ForsakenEntity.class, DataSerializers.VARINT);
    private int animation = ANIMATION_NONE;
    private int animationTick;
    private float prevRunProgress;
    private float runProgress;
    private float prevLeapProgress;
    private float leapProgress;
    private float prevDarknessProgress;
    private float darknessProgress;
    private int sonicCooldown;
    private int jumpCooldown;

    public ForsakenEntity(World world) {
        super(world);
        setSize(2.4F, 3.9F);
        stepHeight = 1.5F;
        experienceValue = 40;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(RUNNING, false);
        dataManager.register(LEAPING, false);
        dataManager.register(HELD_ID, -1);
        dataManager.register(DARKNESS_TIME, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new AttackAI());
        tasks.addTask(2, new EntityAIWander(this, 1.0D, 30));
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 12.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, ForsakenEntity.class));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityMob.class, 60, false, true, entity -> !(entity instanceof ForsakenEntity) && !(entity instanceof UnderzealotEntity)));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.6D);
    }

    @Override
    public void onLivingUpdate() {
        prevRunProgress = runProgress;
        prevLeapProgress = leapProgress;
        prevDarknessProgress = darknessProgress;
        super.onLivingUpdate();
        runProgress = approach(runProgress, isRunning() ? 5.0F : 0.0F, 1.0F);
        leapProgress = approach(leapProgress, isLeaping() ? 5.0F : 0.0F, 1.0F);
        darknessProgress = approach(darknessProgress, getDarknessTime() > 0 ? 5.0F : 0.0F, 1.0F);
        if (animation != ANIMATION_NONE && ++animationTick >= getAnimationDuration(animation)) {
            setAnimation(ANIMATION_NONE);
        }
        if (animation == ANIMATION_PREPARE_JUMP && animationTick == 9 && onGround) {
            setLeaping(true);
            playSound(ACSoundRegistry.FORSAKEN_LEAP, 1.0F, 1.0F);
        }
        if (isLeaping() && onGround && leapProgress >= 5.0F && ticksExisted % 5 == 0) {
            setLeaping(false);
        }
        if (!world.isRemote) {
            if (sonicCooldown > 0) {
                sonicCooldown--;
            }
            if (jumpCooldown > 0) {
                jumpCooldown--;
            }
            updateDarknessHealing();
        } else {
            if (darknessProgress > 0.0F && rand.nextInt(4) == 0) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, 0.02D, 0.0D);
            }
            if ((animation == ANIMATION_SONIC_ATTACK || animation == ANIMATION_SONIC_BLAST) && animationTick > 10 && animationTick < 30 && ticksExisted % 3 == 0) {
                world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, posX, posY + height * 0.55D, posZ, (rand.nextDouble() - 0.5D) * 0.4D, 0.0D, (rand.nextDouble() - 0.5D) * 0.4D);
            }
        }
    }

    private void updateDarknessHealing() {
        if (getHealth() < getMaxHealth() * 0.5F) {
            int light = Math.max(world.getLightFor(EnumSkyBlock.BLOCK, getPosition().up()), world.getLight(getPosition().up()));
            if (light <= 4) {
                setDarknessTime(30);
                if (ticksExisted % 30 == 0) {
                    heal(1.0F);
                }
            } else if (getDarknessTime() > 0) {
                setDarknessTime(getDarknessTime() - 1);
            }
        } else {
            setDarknessTime(0);
        }
    }

    private float approach(float value, float target, float step) {
        if (value < target) {
            return Math.min(target, value + step);
        }
        return value > target ? Math.max(target, value - step) : value;
    }

    private int getAnimationDuration(int anim) {
        switch (anim) {
            case ANIMATION_SUMMON:
                return 50;
            case ANIMATION_LEFT_SLASH:
            case ANIMATION_RIGHT_SLASH:
                return 33;
            case ANIMATION_GROUND_SMASH:
                return 30;
            case ANIMATION_SONIC_ATTACK:
                return 35;
            case ANIMATION_SONIC_BLAST:
                return 45;
            case ANIMATION_LEFT_PICKUP:
            case ANIMATION_RIGHT_PICKUP:
                return 48;
            default:
                return 15;
        }
    }

    public int getAnimation() {
        return animation;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
        animationTick = 0;
        world.setEntityState(this, (byte) (120 + animation));
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id >= 120 && id <= 130) {
            animation = id - 120;
            animationTick = 0;
        } else if (id == 77) {
            for (int i = 0; i < 30; i++) {
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + (rand.nextDouble() - 0.5D) * 4.0D, posY + 0.2D, posZ + (rand.nextDouble() - 0.5D) * 4.0D, rand.nextDouble() - 0.5D, rand.nextDouble() * 0.6D, rand.nextDouble() - 0.5D, net.minecraft.block.Block.getStateId(world.getBlockState(new BlockPos(this).down())));
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public boolean isRunning() {
        return dataManager.get(RUNNING);
    }

    public void setRunning(boolean running) {
        dataManager.set(RUNNING, running);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(running ? 0.45D : 0.25D);
    }

    public boolean isLeaping() {
        return dataManager.get(LEAPING);
    }

    public void setLeaping(boolean leaping) {
        dataManager.set(LEAPING, leaping);
    }

    public int getHeldMobId() {
        return dataManager.get(HELD_ID);
    }

    public void setHeldMobId(int id) {
        dataManager.set(HELD_ID, id);
    }

    public int getDarknessTime() {
        return dataManager.get(DARKNESS_TIME);
    }

    public void setDarknessTime(int time) {
        dataManager.set(DARKNESS_TIME, time);
    }

    public float getRunProgress(float partialTick) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTick) * 0.2F;
    }

    public float getLeapProgress(float partialTick) {
        return (prevLeapProgress + (leapProgress - prevLeapProgress) * partialTick) * 0.2F;
    }

    public float getDarknessProgress(float partialTick) {
        return (prevDarknessProgress + (darknessProgress - prevDarknessProgress) * partialTick) * 0.2F;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int darkness = 7 + rand.nextInt(6) + rand.nextInt(1 + lootingModifier);
        entityDropItem(new ItemStack(ACItemRegistry.PURE_DARKNESS.item(), darkness), 0.0F);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Running", isRunning());
        compound.setBoolean("Leaping", isLeaping());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setRunning(compound.getBoolean("Running"));
        setLeaping(compound.getBoolean("Leaping"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.FORSAKEN_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.FORSAKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.FORSAKEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.block.Block blockIn) {
        playSound(ACSoundRegistry.FORSAKEN_STEP, 0.5F, 1.0F);
    }

    private class AttackAI extends EntityAIBase {
        private AttackAI() {
            setMutexBits(3);
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
            double dist = getDistance(target);
            getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
            boolean frozen = animation == ANIMATION_LEFT_PICKUP || animation == ANIMATION_RIGHT_PICKUP || animation == ANIMATION_SONIC_ATTACK || animation == ANIMATION_SONIC_BLAST;
            if (!frozen) {
                getNavigator().tryMoveToEntityLiving(target, 1.0D);
            } else {
                getNavigator().clearPath();
            }
            if (animation == ANIMATION_NONE && dist < width + target.width + 1.2D) {
                float roll = rand.nextFloat();
                if (roll < 0.2F && target.width < 2.0F) {
                    setAnimation(rand.nextBoolean() ? ANIMATION_LEFT_PICKUP : ANIMATION_RIGHT_PICKUP);
                    playSound(ACSoundRegistry.FORSAKEN_GRAB, 1.0F, 1.0F);
                    setHeldMobId(target.getEntityId());
                } else if (roll < 0.45F) {
                    setAnimation(rand.nextBoolean() ? ANIMATION_LEFT_SLASH : ANIMATION_RIGHT_SLASH);
                } else if (roll < 0.7F) {
                    setAnimation(ANIMATION_GROUND_SMASH);
                } else {
                    setAnimation(ANIMATION_BITE);
                    playSound(ACSoundRegistry.FORSAKEN_BITE, 1.0F, 1.0F);
                }
            }
            if (animation == ANIMATION_NONE && dist > 10.0D && sonicCooldown <= 0 && canEntityBeSeen(target)) {
                setAnimation(dist > 16.0D ? ANIMATION_SONIC_ATTACK : ANIMATION_SONIC_BLAST);
                playSound(dist > 16.0D ? ACSoundRegistry.FORSAKEN_SCREECH : ACSoundRegistry.FORSAKEN_AOE, 1.4F, 1.0F);
                sonicCooldown = 160 + rand.nextInt(120);
            }
            if (animation == ANIMATION_NONE && dist > 25.0D && jumpCooldown <= 0 && onGround) {
                setAnimation(ANIMATION_PREPARE_JUMP);
                jumpCooldown = 80 + rand.nextInt(80);
            }
            handleAnimationDamage(target);
            setRunning(dist < 64.0D && dist > width + target.width + 1.2D && !frozen);
        }

        private void handleAnimationDamage(EntityLivingBase target) {
            if (animation == ANIMATION_PREPARE_JUMP && isLeaping()) {
                Vec3d to = target.getPositionVector().subtract(getPositionVector());
                if (to.length() > 0.01D) {
                    Vec3d add = to.normalize().scale(0.22D);
                    motionX += add.x;
                    motionY = 0.35D;
                    motionZ += add.z;
                }
            }
            if (animation == ANIMATION_BITE && animationTick == 7) {
                damage(target, 1.0F, 0.4F);
            }
            if ((animation == ANIMATION_LEFT_SLASH || animation == ANIMATION_RIGHT_SLASH) && animationTick == 16) {
                damage(target, 0.8F, 2.0F);
            }
            if ((animation == ANIMATION_LEFT_PICKUP || animation == ANIMATION_RIGHT_PICKUP) && animationTick >= 12 && animationTick <= 38 && getHeldMobId() == target.getEntityId()) {
                pullTarget(target);
                if (animationTick == 32) {
                    damage(target, 1.2F, 1.0F);
                    setHeldMobId(-1);
                }
            }
            if (animation == ANIMATION_GROUND_SMASH && animationTick == 12) {
                world.setEntityState(ForsakenEntity.this, (byte) 77);
                List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D));
                for (EntityLivingBase living : list) {
                    if (living != ForsakenEntity.this && !(living instanceof UnderzealotEntity)) {
                        damage(living, 0.8F, 3.0F);
                        living.motionY += 0.5D;
                    }
                }
            }
            if (animation == ANIMATION_SONIC_ATTACK && animationTick == 22 && canEntityBeSeen(target)) {
                target.attackEntityFrom(DamageSource.causeMobDamage(ForsakenEntity.this).setMagicDamage(), 8.0F);
                target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 1));
                target.knockBack(ForsakenEntity.this, 1.0F, posX - target.posX, posZ - target.posZ);
            }
            if (animation == ANIMATION_SONIC_BLAST && animationTick % 5 == 0 && animationTick >= 10 && animationTick <= 30) {
                List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(14.0D, 8.0D, 14.0D));
                for (EntityLivingBase living : list) {
                    if (living != ForsakenEntity.this && !(living instanceof UnderzealotEntity) && living.getDistance(ForsakenEntity.this) <= 14.0F) {
                        living.attackEntityFrom(DamageSource.causeMobDamage(ForsakenEntity.this).setMagicDamage(), 6.0F);
                    }
                }
            }
        }

        private boolean damage(EntityLivingBase target, float multiplier, float extraRange) {
            if (getDistance(target) <= width + target.width + extraRange && canEntityBeSeen(target)) {
                boolean hurt = target.attackEntityFrom(DamageSource.causeMobDamage(ForsakenEntity.this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * multiplier);
                if (hurt) {
                    target.knockBack(ForsakenEntity.this, 0.5F, posX - target.posX, posZ - target.posZ);
                }
                return hurt;
            }
            return false;
        }

        private void pullTarget(EntityLivingBase target) {
            Vec3d pos = getPositionVector();
            Vec3d hand = new Vec3d(pos.x + (animation == ANIMATION_LEFT_PICKUP ? 1.4D : -1.4D) * MathHelper.cos(rotationYaw * 0.017453292F), pos.y + 2.7D, pos.z + (animation == ANIMATION_LEFT_PICKUP ? 1.4D : -1.4D) * MathHelper.sin(rotationYaw * 0.017453292F));
            Vec3d delta = hand.subtract(target.getPositionVector()).scale(0.25D);
            target.motionX += delta.x;
            target.motionY += delta.y;
            target.motionZ += delta.z;
            target.fallDistance = 0.0F;
            target.velocityChanged = true;
        }
    }
}
