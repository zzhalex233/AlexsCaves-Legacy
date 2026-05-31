package com.zzhalex233.alexscaves.server.entity.living;

import java.util.List;

import com.zzhalex233.alexscaves.server.entity.util.UnderzealotSacrifice;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class UnderzealotEntity extends EntityMob {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_ATTACK_0 = 1;
    public static final int ANIMATION_ATTACK_1 = 2;
    public static final int ANIMATION_BREAKTORCH = 3;
    private static final DataParameter<Boolean> BURIED = EntityDataManager.createKey(UnderzealotEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CARRYING = EntityDataManager.createKey(UnderzealotEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> PRAYING = EntityDataManager.createKey(UnderzealotEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> WORSHIP_TIME = EntityDataManager.createKey(UnderzealotEntity.class, DataSerializers.VARINT);
    private int animation = ANIMATION_NONE;
    private int animationTick;
    private float prevBuriedProgress = 20.0F;
    private float buriedProgress = 20.0F;
    private float prevCarryingProgress;
    private float carryingProgress;
    private float prevPrayingProgress;
    private float prayingProgress;
    private int idleBuryIn = 400;
    private int reemergeTime;
    private BlockPos reemergePos;
    public int sacrificeCooldown;

    public UnderzealotEntity(World world) {
        super(world);
        setSize(0.9F, 1.45F);
        stepHeight = 1.0F;
        experienceValue = 8;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(BURIED, false);
        dataManager.register(CARRYING, false);
        dataManager.register(PRAYING, false);
        dataManager.register(WORSHIP_TIME, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new SacrificeAI());
        tasks.addTask(2, new MeleeAI());
        tasks.addTask(3, new CaptureSacrificeAI());
        tasks.addTask(4, new BreakLightAI());
        tasks.addTask(6, new EntityAIWander(this, 1.0D, 100));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, UnderzealotEntity.class));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false, entity -> !isTargetingBlocked()));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevBuriedProgress = buriedProgress;
        prevCarryingProgress = carryingProgress;
        prevPrayingProgress = prayingProgress;
        super.onLivingUpdate();
        buriedProgress = approach(buriedProgress, isBuried() ? 20.0F : 0.0F, 1.5F);
        carryingProgress = approach(carryingProgress, isCarrying() ? 5.0F : 0.0F, 1.0F);
        prayingProgress = approach(prayingProgress, isPraying() ? 5.0F : 0.0F, 1.0F);
        if (animation != ANIMATION_NONE && ++animationTick >= 15) {
            setAnimation(ANIMATION_NONE);
        }
        if (!world.isRemote) {
            setCarrying(!getPassengers().isEmpty());
            if (isBuried()) {
                motionX *= 0.0D;
                motionZ *= 0.0D;
                if (buriedProgress >= 20.0F && --reemergeTime <= 0) {
                    BlockPos pos = reemergePos == null ? findReemergePos(getPosition(), 10) : reemergePos;
                    setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    setBuried(false);
                    idleBuryIn = 400 + rand.nextInt(800);
                }
            } else if (digsIdle() && --idleBuryIn <= 0 && onGround) {
                setBuried(true);
                reemergeAt(findReemergePos(getPosition(), 10), 40 + rand.nextInt(60));
            }
            if (sacrificeCooldown > 0) {
                sacrificeCooldown--;
            }
        } else if (isDiggingInProgress()) {
            IBlockState state = world.getBlockState(new BlockPos(this).down());
            for (int i = 0; i < 3; i++) {
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + (rand.nextDouble() - 0.5D) * width, posY, posZ + (rand.nextDouble() - 0.5D) * width, rand.nextDouble() - 0.5D, rand.nextDouble() * 0.5D, rand.nextDouble() - 0.5D, net.minecraft.block.Block.getStateId(state));
            }
        }
    }

    private float approach(float value, float target, float step) {
        if (value < target) {
            return Math.min(target, value + step);
        }
        return value > target ? Math.max(target, value - step) : value;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (isPassenger(passenger)) {
            float yaw = renderYawOffset * 0.017453292F;
            passenger.setPosition(posX - MathHelper.sin(yaw) * 0.25F, posY + height + 0.05F, posZ + MathHelper.cos(yaw) * 0.25F);
            passenger.rotationYaw = renderYawOffset;
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean hurt = super.attackEntityFrom(source, amount);
        if (hurt && isBeingRidden() && rand.nextFloat() < 0.65F) {
            removePassengers();
        }
        return hurt;
    }

    @Override
    public boolean canBePushed() {
        return !isBuried() && !isDiggingInProgress();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isBuried();
    }

    public boolean isBuried() {
        return dataManager.get(BURIED);
    }

    public void setBuried(boolean buried) {
        if (buried != isBuried()) {
            playSound(ACSoundRegistry.UNDERZEALOT_DIG, 1.0F, 1.0F);
        }
        dataManager.set(BURIED, buried);
    }

    public boolean isCarrying() {
        return dataManager.get(CARRYING);
    }

    public void setCarrying(boolean carrying) {
        dataManager.set(CARRYING, carrying);
    }

    public boolean isPraying() {
        return dataManager.get(PRAYING);
    }

    public void setPraying(boolean praying) {
        dataManager.set(PRAYING, praying);
    }

    public int getWorshipTime() {
        return dataManager.get(WORSHIP_TIME);
    }

    public void setWorshipTime(int worshipTime) {
        dataManager.set(WORSHIP_TIME, worshipTime);
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
        world.setEntityState(this, (byte) (110 + animation));
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id >= 110 && id <= 113) {
            animation = id - 110;
            animationTick = 0;
        } else if (id == 77) {
            for (int i = 0; i < 10; i++) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + (rand.nextDouble() - 0.5D) * 2.0D, posY + rand.nextDouble() * 2.5D, posZ + (rand.nextDouble() - 0.5D) * 2.0D, 0.0D, 0.04D, 0.0D);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public boolean isTargetingBlocked() {
        return isCarrying() || isPraying() || isBuried() || isDiggingInProgress();
    }

    public boolean digsIdle() {
        EntityLivingBase target = getAttackTarget();
        return !isCarrying() && !isPraying() && animation == ANIMATION_NONE && (target == null || !target.isEntityAlive());
    }

    public void triggerIdleDigging() {
        idleBuryIn = 0;
    }

    public void postSacrifice(UnderzealotSacrifice sacrifice) {
        playSound(ACSoundRegistry.UNDERZEALOT_TRANSFORMATION, 8.0F, 1.0F);
        sacrificeCooldown = 6000 + rand.nextInt(6000);
    }

    public boolean isDiggingInProgress() {
        return buriedProgress > 0.0F && buriedProgress < 20.0F;
    }

    public void reemergeAt(BlockPos pos, int time) {
        reemergePos = pos;
        reemergeTime = time;
    }

    public BlockPos findReemergePos(BlockPos origin, int range) {
        for (int i = 0; i < 15; i++) {
            BlockPos pos = origin.add(rand.nextInt(range) - range / 2, rand.nextInt(range) / 2, rand.nextInt(range) - range / 2);
            while (!world.isAirBlock(pos) && pos.getY() < 250) {
                pos = pos.up();
            }
            while (world.isAirBlock(pos.down()) && pos.getY() > 1) {
                pos = pos.down();
            }
            if (!world.isAirBlock(pos.down()) && pos.distanceSq(origin) < range * range + 10) {
                return pos;
            }
        }
        return origin;
    }

    public float getBuriedProgress(float partialTick) {
        return (prevBuriedProgress + (buriedProgress - prevBuriedProgress) * partialTick) * 0.05F;
    }

    public float getCarryingProgress(float partialTick) {
        return (prevCarryingProgress + (carryingProgress - prevCarryingProgress) * partialTick) * 0.2F;
    }

    public float getPrayingProgress(float partialTick) {
        return (prevPrayingProgress + (prayingProgress - prevPrayingProgress) * partialTick) * 0.2F;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int tatters = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        if (tatters > 0) {
            entityDropItem(new ItemStack(ACItemRegistry.DARK_TATTERS.item(), tatters), 0.0F);
        }
        if (rand.nextFloat() < 0.025F + lootingModifier * 0.01F) {
            entityDropItem(new ItemStack(ACItemRegistry.DESOLATE_DAGGER.item()), 0.0F);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Buried", isBuried());
        compound.setInteger("SacrificeCooldown", sacrificeCooldown);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setBuried(compound.getBoolean("Buried"));
        sacrificeCooldown = compound.getInteger("SacrificeCooldown");
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isBuried() || isCarrying() || isPraying() ? null : ACSoundRegistry.UNDERZEALOT_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.UNDERZEALOT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.UNDERZEALOT_DEATH;
    }

    private class MeleeAI extends EntityAIBase {
        private boolean shouldBurrow;

        private MeleeAI() {
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
            if (shouldBurrow && onGround) {
                shouldBurrow = false;
                setBuried(true);
                reemergeAt(findReemergePos(target.getPosition(), 15), 20 + rand.nextInt(60));
                return;
            }
            if (isBuried() || isDiggingInProgress()) {
                getNavigator().clearPath();
                return;
            }
            getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
            getNavigator().tryMoveToEntityLiving(target, 1.3D);
            double range = width + target.width + 1.0D;
            if (getDistance(target) < range && animation == ANIMATION_NONE) {
                setAnimation(rand.nextBoolean() ? ANIMATION_ATTACK_0 : ANIMATION_ATTACK_1);
                playSound(ACSoundRegistry.UNDERZEALOT_ATTACK, 1.0F, 1.0F);
            }
            if ((animation == ANIMATION_ATTACK_0 || animation == ANIMATION_ATTACK_1) && animationTick == 8 && getDistance(target) < range && canEntityBeSeen(target)) {
                target.attackEntityFrom(DamageSource.causeMobDamage(UnderzealotEntity.this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
                target.knockBack(UnderzealotEntity.this, 0.25F, posX - target.posX, posZ - target.posZ);
                shouldBurrow = rand.nextBoolean();
            }
        }

        @Override
        public void resetTask() {
            shouldBurrow = false;
        }
    }

    private class CaptureSacrificeAI extends EntityAIBase {
        private EntityLivingBase sacrifice;

        private CaptureSacrificeAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (isCarrying() || isBuried() || sacrificeCooldown > 0 || rand.nextInt(20) != 0) {
                return false;
            }
            List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(20.0D), this::isValidSacrifice);
            double best = Double.MAX_VALUE;
            sacrifice = null;
            for (EntityLivingBase entity : list) {
                double dist = getDistanceSq(entity);
                if (dist < best && canEntityBeSeen(entity)) {
                    best = dist;
                    sacrifice = entity;
                }
            }
            return sacrifice != null;
        }

        private boolean isValidSacrifice(EntityLivingBase entity) {
            return entity instanceof UnderzealotSacrifice && !entity.isRiding() && ((UnderzealotSacrifice) entity).isValidSacrifice(getDistanceToGround(entity));
        }

        private int getDistanceToGround(EntityLivingBase entity) {
            int down = 0;
            BlockPos pos = entity.getPosition();
            while (world.isAirBlock(pos) && pos.getY() > 1) {
                pos = pos.down();
                down++;
            }
            return down;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return sacrifice != null && sacrifice.isEntityAlive() && !sacrifice.isRiding() && getDistance(sacrifice) < 32.0F && sacrificeCooldown <= 0;
        }

        @Override
        public void updateTask() {
            getNavigator().tryMoveToEntityLiving(sacrifice, 1.0D);
            getLookHelper().setLookPositionWithEntity(sacrifice, 30.0F, 30.0F);
            if (getDistance(sacrifice) < 1.4F) {
                sacrifice.startRiding(UnderzealotEntity.this, true);
            }
            if (!isBuried() && sacrifice.posY - posY > 0.5D && getDistance(sacrifice) < 2.0F && onGround) {
                jump();
            }
        }
    }

    private class SacrificeAI extends EntityAIBase {
        private SacrificeAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            return isCarrying() && !isBuried() && sacrificeCooldown <= 0;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return isCarrying() && sacrificeCooldown <= 0;
        }

        @Override
        public void updateTask() {
            getNavigator().clearPath();
            setPraying(true);
            setWorshipTime(getWorshipTime() + 1);
            if (getWorshipTime() % 30 == 0) {
                playSound(ACSoundRegistry.UNDERZEALOT_CHANT, 2.0F, 1.0F);
                world.setEntityState(UnderzealotEntity.this, (byte) 77);
            }
            Entity passenger = getPassengers().isEmpty() ? null : getPassengers().get(0);
            if (passenger instanceof UnderzealotSacrifice && getWorshipTime() == 120) {
                ((UnderzealotSacrifice) passenger).triggerSacrificeIn(80);
            }
            if (getWorshipTime() >= 220) {
                playSound(ACSoundRegistry.UNDERZEALOT_TRANSFORMATION, 8.0F, 1.0F);
                sacrificeCooldown = 6000 + rand.nextInt(6000);
                removePassengers();
                setWorshipTime(0);
                setPraying(false);
            }
        }

        @Override
        public void resetTask() {
            setPraying(false);
            setWorshipTime(0);
        }
    }

    private class BreakLightAI extends EntityAIBase {
        private BlockPos lightPos;

        private BreakLightAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (isCarrying() || isBuried() || rand.nextInt(500) != 0) {
                return false;
            }
            lightPos = findLight();
            return lightPos != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return lightPos != null && !isCarrying() && !isBuried() && getDistanceSqToCenter(lightPos) > 3.0D;
        }

        @Override
        public void updateTask() {
            getNavigator().tryMoveToXYZ(lightPos.getX() + 0.5D, lightPos.getY(), lightPos.getZ() + 0.5D, 1.0D);
            getLookHelper().setLookPosition(lightPos.getX() + 0.5D, lightPos.getY() + 0.5D, lightPos.getZ() + 0.5D, 30.0F, 30.0F);
            if (getDistanceSqToCenter(lightPos) < 4.0D) {
                if (animation == ANIMATION_NONE) {
                    setAnimation(ANIMATION_BREAKTORCH);
                } else if (animation == ANIMATION_BREAKTORCH && animationTick == 10 && isBreakableLight(lightPos)) {
                    world.destroyBlock(lightPos, true);
                }
            }
        }

        private BlockPos findLight() {
            BlockPos origin = getPosition();
            for (int i = 0; i < 48; i++) {
                BlockPos pos = origin.add(rand.nextInt(33) - 16, rand.nextInt(9) - 4, rand.nextInt(33) - 16);
                if (isBreakableLight(pos) && canSee(pos)) {
                    return pos;
                }
            }
            return null;
        }

        private boolean canSee(BlockPos pos) {
            return world.rayTraceBlocks(getPositionEyes(1.0F), new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), false, true, false) == null;
        }

        private boolean isBreakableLight(BlockPos pos) {
            IBlockState state = world.getBlockState(pos);
            return state.getBlock() != Blocks.AIR && state.getLightValue(world, pos) > 0 && state.getBlockHardness(world, pos) >= 0.0F;
        }
    }
}
