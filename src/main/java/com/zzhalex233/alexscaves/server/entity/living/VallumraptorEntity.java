package com.zzhalex233.alexscaves.server.entity.living;

import java.util.List;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class VallumraptorEntity extends DinosaurEntity {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_CALL_1 = 1;
    public static final int ANIMATION_CALL_2 = 2;
    public static final int ANIMATION_SCRATCH_1 = 3;
    public static final int ANIMATION_SCRATCH_2 = 4;
    public static final int ANIMATION_SHAKE = 5;
    public static final int ANIMATION_STARTLEAP = 6;
    public static final int ANIMATION_MELEE_BITE = 7;
    public static final int ANIMATION_MELEE_SLASH_1 = 8;
    public static final int ANIMATION_MELEE_SLASH_2 = 9;
    public static final int ANIMATION_GRAB = 10;
    private static final DataParameter<Boolean> RUNNING = EntityDataManager.createKey(VallumraptorEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LEAPING = EntityDataManager.createKey(VallumraptorEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ELDER = EntityDataManager.createKey(VallumraptorEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> PUZZLED_HEAD_ROT = EntityDataManager.createKey(VallumraptorEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> RELAXED_FOR = EntityDataManager.createKey(VallumraptorEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HIDING_FOR = EntityDataManager.createKey(VallumraptorEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ANIMATION = EntityDataManager.createKey(VallumraptorEntity.class, DataSerializers.VARINT);

    public float prevRunProgress;
    public float runProgress;
    public float prevLeapProgress;
    public float leapProgress;
    public float prevRelaxedProgress;
    public float relaxedProgress;
    public float prevHideProgress;
    public float hideProgress;
    private float prevPuzzleHeadRot;
    private float targetPuzzleRot;
    private float prevTailYaw;
    private float tailYaw;
    private int animationTick;
    private int eatHeldItemIn;

    public VallumraptorEntity(World world) {
        super(world);
        setSize(0.95F, 1.65F);
        tailYaw = rotationYaw;
        prevTailYaw = rotationYaw;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(RUNNING, false);
        dataManager.register(LEAPING, false);
        dataManager.register(ELDER, false);
        dataManager.register(PUZZLED_HEAD_ROT, 0.0F);
        dataManager.register(RELAXED_FOR, 0);
        dataManager.register(HIDING_FOR, 0);
        dataManager.register(ANIMATION, ANIMATION_NONE);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAISit(this));
        tasks.addTask(2, new EntityAIFollowOwner(this, 1.0D, 5.0F, 2.0F));
        tasks.addTask(3, new EntityAITempt(this, 1.1D, ACItemRegistry.DINOSAUR_NUGGET.item(), false));
        tasks.addTask(4, new LeapMeleeAI());
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        tasks.addTask(6, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, VallumraptorEntity.class));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityMob>(this, EntityMob.class, 40, true, false, null));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(28.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevRunProgress = runProgress;
        prevLeapProgress = leapProgress;
        prevRelaxedProgress = relaxedProgress;
        prevHideProgress = hideProgress;
        prevTailYaw = tailYaw;
        prevPuzzleHeadRot = getPuzzledHeadRot();
        runProgress = approach(runProgress, isRunning() ? 5.0F : 0.0F, 1.0F);
        leapProgress = approach(leapProgress, isLeaping() ? 5.0F : 0.0F, 1.0F);
        relaxedProgress = approach(relaxedProgress, getRelaxedFor() > 0 ? 20.0F : 0.0F, 1.0F);
        hideProgress = approach(hideProgress, getHideFor() > 0 ? 20.0F : 0.0F, 1.0F);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(isRunning() ? 0.35D : 0.23D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(isElder() ? 32.0D : 28.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(isElder() ? 5.0D : 0.0D);
        if (getAnimation() != ANIMATION_NONE && ++animationTick > getAnimationLength(getAnimation())) {
            setAnimation(ANIMATION_NONE);
        }
        if (!world.isRemote) {
            tickServerBehavior();
        }
        if (isLeaping()) {
            motionX *= 1.04D;
            motionZ *= 1.04D;
            if (onGround && animationTick > 8) {
                setLeaping(false);
            }
        }
        tailYaw = approachDegrees(tailYaw, renderYawOffset, 8.0F);
    }

    private void tickServerBehavior() {
        if (getRelaxedFor() > 0) {
            setRelaxedForTime(getRelaxedFor() - 1);
        }
        if (getHideFor() > 0) {
            setHideFor(getHideFor() - 1);
            if (ticksExisted % 40 == 0 && getHealth() < getMaxHealth()) {
                heal(2.0F);
            }
        }
        if (getAttackTarget() == null && getAnimation() == ANIMATION_NONE && rand.nextInt(120) == 0) {
            setAnimation(rand.nextBoolean() ? ANIMATION_SCRATCH_1 : ANIMATION_SCRATCH_2);
        }
        if (eatHeldItemIn > 0) {
            eatHeldItemIn--;
        } else if (canTargetItem(getHeldItemMainhand())) {
            world.setEntityState(this, (byte) 45);
            heal(5.0F);
            getHeldItemMainhand().shrink(1);
            setRelaxedForTime(120 + rand.nextInt(120));
        } else {
            pickUpWantedItem();
        }
        EntityLivingBase target = getAttackTarget();
        if (target != null && getHealth() < getMaxHealth() * 0.45F && isTamed() && getHideFor() <= 0) {
            setHideFor(100 + rand.nextInt(60));
            setAttackTarget(null);
        }
        puzzledTick();
    }

    private void pickUpWantedItem() {
        if (!getHeldItemMainhand().isEmpty()) {
            return;
        }
        AxisAlignedBB box = getEntityBoundingBox().grow(2.0D);
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box, item -> item != null && !item.isDead && canTargetItem(item.getItem()));
        if (!items.isEmpty()) {
            EntityItem item = items.get(0);
            setAnimation(ANIMATION_GRAB);
            ItemStack copy = item.getItem().copy();
            copy.setCount(1);
            setItemStackToSlot(EntityEquipmentSlot.MAINHAND, copy);
            item.getItem().shrink(1);
            eatHeldItemIn = isTamed() ? 50 : 180;
        }
    }

    private void puzzledTick() {
        float current = getPuzzledHeadRot();
        float dist = Math.abs(targetPuzzleRot - current);
        if (getAttackTarget() != null || getAnimation() != ANIMATION_NONE || getRelaxedFor() > 0) {
            targetPuzzleRot = 0.0F;
        } else if (rand.nextInt(10) == 0 && dist <= 0.1F) {
            targetPuzzleRot = rand.nextFloat() < 0.25F ? 0.0F : (rand.nextFloat() * 50.0F * (rand.nextBoolean() ? 1.0F : -1.0F));
        }
        if (dist > 0.1F) {
            setPuzzledHeadRot(current + Math.copySign(Math.min(dist, 6.0F), targetPuzzleRot - current));
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 45) {
            for (int i = 0; i < 8; i++) {
                world.spawnParticle(net.minecraft.util.EnumParticleTypes.ITEM_CRACK, posX, posY + height * 0.65D, posZ, (rand.nextDouble() - 0.5D) * 0.1D, rand.nextDouble() * 0.15D, (rand.nextDouble() - 0.5D) * 0.1D, net.minecraft.item.Item.getIdFromItem(getHeldItemMainhand().getItem()));
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!isTamed() && stack.getItem() == ACItemRegistry.DINOSAUR_NUGGET.item()) {
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            if (!world.isRemote) {
                if (rand.nextInt(3) == 0) {
                    setTamedBy(player);
                    setAttackTarget(null);
                    navigator.clearPath();
                    setAnimation(ANIMATION_CALL_1);
                    world.setEntityState(this, (byte) 7);
                } else {
                    world.setEntityState(this, (byte) 6);
                }
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean hit = super.attackEntityAsMob(entity);
        if (hit) {
            playSound(ACSoundRegistry.VALLUMRAPTOR_ATTACK, getSoundVolume(), getSoundPitch());
        }
        return hit;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Elder", isElder());
        compound.setInteger("RelaxedTime", getRelaxedFor());
        compound.setInteger("HideTime", getHideFor());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setElder(compound.getBoolean("Elder"));
        setRelaxedForTime(compound.getInteger("RelaxedTime"));
        setHideFor(compound.getInteger("HideTime"));
    }

    @Override
    public net.minecraft.entity.IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, net.minecraft.entity.IEntityLivingData data) {
        if (!world.isRemote && rand.nextInt(2) == 0) {
            setElder(true);
        }
        return super.onInitialSpawn(difficulty, data);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        VallumraptorEntity child = new VallumraptorEntity(world);
        EntityLivingBase owner = getOwner();
        if (owner instanceof EntityPlayer) {
            child.setTamedBy((EntityPlayer) owner);
        }
        return child;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return isTamed() && stack.getItem() == ACItemRegistry.DINOSAUR_NUGGET.item();
    }

    public boolean canTargetItem(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() == ACItemRegistry.DINOSAUR_NUGGET.item() || stack.getItem() == Items.BEEF || stack.getItem() == Items.CHICKEN || stack.getItem() == Items.PORKCHOP || stack.getItem() == Items.MUTTON || stack.getItem() == Items.RABBIT);
    }

    public boolean isRunning() {
        return dataManager.get(RUNNING);
    }

    public void setRunning(boolean running) {
        dataManager.set(RUNNING, running);
    }

    public boolean isLeaping() {
        return dataManager.get(LEAPING);
    }

    public void setLeaping(boolean leaping) {
        dataManager.set(LEAPING, leaping);
    }

    public boolean isElder() {
        return dataManager.get(ELDER);
    }

    public void setElder(boolean elder) {
        dataManager.set(ELDER, elder);
        setSize(elder ? 1.05F : 0.95F, elder ? 1.82F : 1.65F);
    }

    public int getRelaxedFor() {
        return dataManager.get(RELAXED_FOR);
    }

    public void setRelaxedForTime(int ticks) {
        dataManager.set(RELAXED_FOR, Math.max(0, ticks));
    }

    public int getHideFor() {
        return dataManager.get(HIDING_FOR);
    }

    public void setHideFor(int ticks) {
        dataManager.set(HIDING_FOR, Math.max(0, ticks));
    }

    public int getAnimation() {
        return dataManager.get(ANIMATION);
    }

    public void setAnimation(int animation) {
        dataManager.set(ANIMATION, animation);
        animationTick = 0;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    private int getAnimationLength(int animation) {
        switch (animation) {
            case ANIMATION_CALL_2:
                return 25;
            case ANIMATION_SHAKE:
            case ANIMATION_GRAB:
                return 40;
            case ANIMATION_STARTLEAP:
                return 20;
            case ANIMATION_CALL_1:
            case ANIMATION_MELEE_BITE:
            case ANIMATION_MELEE_SLASH_1:
            case ANIMATION_MELEE_SLASH_2:
                return 15;
            case ANIMATION_SCRATCH_1:
            case ANIMATION_SCRATCH_2:
                return 20;
            default:
                return 0;
        }
    }

    public float getRunProgress(float partialTicks) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTicks) * 0.2F;
    }

    public float getLeapProgress(float partialTicks) {
        return (prevLeapProgress + (leapProgress - prevLeapProgress) * partialTicks) * 0.2F;
    }

    public float getRelaxedProgress(float partialTicks) {
        return (prevRelaxedProgress + (relaxedProgress - prevRelaxedProgress) * partialTicks) * 0.05F;
    }

    public float getHideProgress(float partialTicks) {
        return (prevHideProgress + (hideProgress - prevHideProgress) * partialTicks) * 0.05F;
    }

    public float getPuzzledHeadRot(float partialTicks) {
        return prevPuzzleHeadRot + (getPuzzledHeadRot() - prevPuzzleHeadRot) * partialTicks;
    }

    private float getPuzzledHeadRot() {
        return dataManager.get(PUZZLED_HEAD_ROT);
    }

    private void setPuzzledHeadRot(float rot) {
        dataManager.set(PUZZLED_HEAD_ROT, rot);
    }

    public float getTailYaw(float partialTicks) {
        return prevTailYaw + (tailYaw - prevTailYaw) * partialTicks;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return getRelaxedFor() > 0 ? ACSoundRegistry.VALLUMRAPTOR_SLEEP : ACSoundRegistry.VALLUMRAPTOR_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ACSoundRegistry.VALLUMRAPTOR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.VALLUMRAPTOR_DEATH;
    }

    private static float approachDegrees(float current, float target, float step) {
        float delta = MathHelper.wrapDegrees(target - current);
        if (delta > step) {
            delta = step;
        }
        if (delta < -step) {
            delta = -step;
        }
        return current + delta;
    }

    private class LeapMeleeAI extends EntityAIAttackMelee {
        private LeapMeleeAI() {
            super(VallumraptorEntity.this, 1.15D, true);
        }

        @Override
        public boolean shouldExecute() {
            return getRelaxedFor() <= 0 && getHideFor() <= 0 && super.shouldExecute();
        }

        @Override
        public void updateTask() {
            super.updateTask();
            EntityLivingBase target = getAttackTarget();
            setRunning(target != null && getDistanceSq(target) > 9.0D);
            if (target != null && !isLeaping() && onGround && getDistanceSq(target) > 9.0D && getDistanceSq(target) < 64.0D && rand.nextInt(20) == 0) {
                Vec3d leap = new Vec3d(target.posX - posX, 0.0D, target.posZ - posZ).normalize().scale(0.65D);
                motionX += leap.x;
                motionY = 0.45D;
                motionZ += leap.z;
                setLeaping(true);
                setAnimation(ANIMATION_STARTLEAP);
            }
        }

        @Override
        protected void checkAndPerformAttack(EntityLivingBase enemy, double distance) {
            if (distance <= getAttackReachSqr(enemy)) {
                attackTick = 20;
                swingArm(EnumHand.MAIN_HAND);
                setAnimation(rand.nextBoolean() ? ANIMATION_MELEE_BITE : (rand.nextBoolean() ? ANIMATION_MELEE_SLASH_1 : ANIMATION_MELEE_SLASH_2));
                attackEntityAsMob(enemy);
            }
        }
    }
}
