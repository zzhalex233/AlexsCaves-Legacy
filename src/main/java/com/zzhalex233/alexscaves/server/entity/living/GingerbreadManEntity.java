package com.zzhalex233.alexscaves.server.entity.living;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class GingerbreadManEntity extends EntityMob {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_IDLE_WAVE_LEFT = 1;
    public static final int ANIMATION_IDLE_WAVE_RIGHT = 2;
    public static final int ANIMATION_IDLE_FALL_OVER = 3;
    public static final int ANIMATION_IDLE_JUMP = 4;
    public static final int ANIMATION_SWING_RIGHT = 5;
    public static final int ANIMATION_SWING_LEFT = 6;
    public static final int MAX_VARIANTS = 8;
    private static final DataParameter<Boolean> DANCING = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CARRYING_ITEM = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> OVEN_SPAWNED = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LOST_LEFT_ARM = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LOST_RIGHT_ARM = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LOST_LEFT_LEG = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LOST_RIGHT_LEG = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TEAM_COLOR = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> POSSESSOR_LICOWITCH_ID = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ANIMATION = EntityDataManager.createKey(GingerbreadManEntity.class, DataSerializers.VARINT);
    private float prevSitProgress;
    private float sitProgress;
    private float prevDanceProgress;
    private float danceProgress;
    private float prevCarryItemProgress;
    private float carryItemProgress;
    private int animationTick;
    private int sitFor = -100;
    private int despawnFromOvenCooldown = 2000;

    public GingerbreadManEntity(World world) {
        super(world);
        setSize(0.7F, 1.0F);
        experienceValue = 3;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(DANCING, false);
        dataManager.register(SITTING, false);
        dataManager.register(CARRYING_ITEM, false);
        dataManager.register(OVEN_SPAWNED, false);
        dataManager.register(LOST_LEFT_ARM, false);
        dataManager.register(LOST_RIGHT_ARM, false);
        dataManager.register(LOST_LEFT_LEG, false);
        dataManager.register(LOST_RIGHT_LEG, false);
        dataManager.register(VARIANT, 0);
        dataManager.register(TEAM_COLOR, -1);
        dataManager.register(POSSESSOR_LICOWITCH_ID, -1);
        dataManager.register(ANIMATION, ANIMATION_NONE);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIOpenDoor(this, true));
        tasks.addTask(2, new SitAI());
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.25D, false));
        tasks.addTask(4, new StealItemAI());
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D, 30));
        tasks.addTask(6, new EntityAITempt(this, 1.1D, Items.POTIONITEM, false));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityHusk.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.45D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevDanceProgress = danceProgress;
        prevSitProgress = sitProgress;
        prevCarryItemProgress = carryItemProgress;
        super.onLivingUpdate();
        danceProgress = approach(danceProgress, isDancing() ? 5.0F : 0.0F);
        sitProgress = approach(sitProgress, isSitting() ? 10.0F : 0.0F);
        carryItemProgress = approach(carryItemProgress, isCarryingItem() ? 10.0F : 0.0F);
        if (getAnimation() != ANIMATION_NONE && ++animationTick >= animationLength(getAnimation())) {
            setAnimation(ANIMATION_NONE);
        }
        if (!world.isRemote && isStillEnough() && !isCarryingItem() && rand.nextInt(150) == 0 && getAnimation() == ANIMATION_NONE && !isDancing()) {
            if (rand.nextInt(3) == 0 && sitFor == 0) {
                sitFor = 100 + rand.nextInt(80);
            } else {
                int idle = rand.nextInt(4);
                setAnimation(idle == 0 ? ANIMATION_IDLE_JUMP : idle == 1 ? ANIMATION_IDLE_FALL_OVER : idle == 2 ? ANIMATION_IDLE_WAVE_LEFT : ANIMATION_IDLE_WAVE_RIGHT);
            }
        }
        if (getAnimation() == ANIMATION_IDLE_JUMP && onGround && animationTick == 5) {
            jump();
        }
        if (sitFor > 0) {
            setSitting(true);
            sitFor--;
        } else {
            setSitting(false);
            if (sitFor < 0) {
                sitFor++;
            }
        }
        if (!world.isRemote && isCarryingItem() && getHeldItemOffhand().isEmpty()) {
            setCarryingItem(false);
        }
        if (isOvenSpawned() && !world.isRemote && despawnFromOvenCooldown-- < 0) {
            dropHeldItems();
            setDead();
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        setAnimation(getAnimationForHand(false));
        playSound(ACSoundRegistry.GINGERBREAD_MAN_ATTACK, 0.8F, getSoundPitch());
        return super.attackEntityAsMob(entity);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean hurt = super.attackEntityFrom(source, amount);
        if (hurt && amount >= 1.0F && !world.isRemote) {
            if ((hasBothLegs() || getHealth() <= 0.0F) && rand.nextBoolean()) {
                setLostLimb(rand.nextBoolean(), false, true);
            } else if (rand.nextInt(2) == 0) {
                setLostLimb(rand.nextBoolean(), true, true);
            }
        }
        return hurt;
    }

    private void dropHeldItems() {
        ItemStack main = getHeldItemMainhand();
        ItemStack off = getHeldItemOffhand();
        if (!main.isEmpty()) {
            entityDropItem(main.copy(), 0.0F);
            setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        if (!off.isEmpty()) {
            entityDropItem(off.copy(), 0.0F);
            setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
        }
    }

    private void onLoseLimb(boolean left, boolean arm) {
        for (int i = 0; i < 5; i++) {
            world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX + (rand.nextDouble() - 0.5D) * width, posY + height * (arm ? 0.8D : 0.35D), posZ + (rand.nextDouble() - 0.5D) * width, (rand.nextDouble() - 0.5D) * 0.2D, (rand.nextDouble() - 0.5D) * 0.2D, (rand.nextDouble() - 0.5D) * 0.2D, Item.getIdFromItem(ACItemRegistry.GINGERBREAD_CRUMBS.item()));
        }
        if (!world.isRemote) {
            if (!isOvenSpawned() && isEntityAlive() && rand.nextInt(2) == 0) {
                entityDropItem(new ItemStack(ACItemRegistry.GINGERBREAD_CRUMBS.item()), 0.0F);
            }
            if (arm) {
                boolean lostMain = isLeftHanded() == left;
                ItemStack stack = lostMain ? getHeldItemMainhand() : getHeldItemOffhand();
                if (!stack.isEmpty()) {
                    entityDropItem(stack.copy(), 0.0F);
                    setItemStackToSlot(lostMain ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (LOST_LEFT_ARM.equals(key)) {
            onLoseLimb(true, true);
        } else if (LOST_RIGHT_ARM.equals(key)) {
            onLoseLimb(false, true);
        } else if (LOST_LEFT_LEG.equals(key)) {
            onLoseLimb(true, false);
        } else if (LOST_RIGHT_LEG.equals(key)) {
            onLoseLimb(false, false);
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        setVariant(rand.nextInt(MAX_VARIANTS + 1));
        return livingdata;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GINGERBREAD_MAN_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.GINGERBREAD_MAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GINGERBREAD_MAN_DEATH;
    }

    public boolean isMovementBlocked() {
        return isSitting() || isDancing() || getAnimation() == ANIMATION_IDLE_FALL_OVER || getAnimation() == ANIMATION_IDLE_JUMP;
    }

    public boolean isDancing() {
        return dataManager.get(DANCING);
    }

    public void setDancing(boolean dancing) {
        dataManager.set(DANCING, dancing);
    }

    public boolean isSitting() {
        return dataManager.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        dataManager.set(SITTING, sitting);
    }

    public boolean isCarryingItem() {
        return dataManager.get(CARRYING_ITEM);
    }

    public void setCarryingItem(boolean carryingItem) {
        dataManager.set(CARRYING_ITEM, carryingItem);
    }

    public boolean isOvenSpawned() {
        return dataManager.get(OVEN_SPAWNED);
    }

    public void setOvenSpawned(boolean ovenSpawned) {
        dataManager.set(OVEN_SPAWNED, ovenSpawned);
    }

    public void setDespawnFromOvenCooldown(int cooldown) {
        despawnFromOvenCooldown = cooldown;
    }

    public boolean hasLostLimb(boolean left, boolean arm) {
        return dataManager.get(arm ? left ? LOST_LEFT_ARM : LOST_RIGHT_ARM : left ? LOST_LEFT_LEG : LOST_RIGHT_LEG);
    }

    public void setLostLimb(boolean left, boolean arm, boolean lost) {
        dataManager.set(arm ? left ? LOST_LEFT_ARM : LOST_RIGHT_ARM : left ? LOST_LEFT_LEG : LOST_RIGHT_LEG, lost);
    }

    public boolean hasBothLegs() {
        return !hasLostLimb(true, false) && !hasLostLimb(false, false);
    }

    public int getVariant() {
        return dataManager.get(VARIANT);
    }

    public void setVariant(int variant) {
        dataManager.set(VARIANT, MathHelper.clamp(variant, 0, MAX_VARIANTS));
    }

    public int getGingerbreadTeamColor() {
        return dataManager.get(TEAM_COLOR);
    }

    public void setGingerbreadTeamColor(int color) {
        dataManager.set(TEAM_COLOR, color);
    }

    public int getPossessedByLicowitchId() {
        return dataManager.get(POSSESSOR_LICOWITCH_ID);
    }

    public void setPossessedByLicowitchId(int entityId) {
        dataManager.set(POSSESSOR_LICOWITCH_ID, entityId);
    }

    public int getAnimation() {
        return dataManager.get(ANIMATION);
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public void setAnimation(int animation) {
        dataManager.set(ANIMATION, animation);
        animationTick = 0;
    }

    public int getAnimationForHand(boolean reverse) {
        return isLeftHanded() ? reverse ? ANIMATION_SWING_RIGHT : ANIMATION_SWING_LEFT : reverse ? ANIMATION_SWING_LEFT : ANIMATION_SWING_RIGHT;
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) * 0.1F;
    }

    public float getDanceProgress(float partialTicks) {
        return (prevDanceProgress + (danceProgress - prevDanceProgress) * partialTicks) * 0.2F;
    }

    public float getCarryItemProgress(float partialTicks) {
        return (prevCarryItemProgress + (carryItemProgress - prevCarryItemProgress) * partialTicks) * 0.1F;
    }

    private boolean isStillEnough() {
        return motionX * motionX + motionZ * motionZ < 0.0025D;
    }

    private float approach(float value, float target) {
        return value < target ? Math.min(target, value + 1.0F) : value > target ? Math.max(target, value - 1.0F) : value;
    }

    private int animationLength(int animation) {
        return animation == ANIMATION_IDLE_FALL_OVER ? 50 : animation == ANIMATION_IDLE_JUMP ? 20 : animation == ANIMATION_SWING_LEFT || animation == ANIMATION_SWING_RIGHT ? 15 : 35;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Variant", getVariant());
        compound.setBoolean("CarryingItem", isCarryingItem());
        compound.setBoolean("OvenSpawned", isOvenSpawned());
        compound.setInteger("GingerbreadTeamColor", getGingerbreadTeamColor());
        compound.setInteger("PossessorLicowitchId", getPossessedByLicowitchId());
        compound.setInteger("OvenDespawnCooldown", despawnFromOvenCooldown);
        compound.setBoolean("LostLeftLeg", hasLostLimb(true, false));
        compound.setBoolean("LostRightLeg", hasLostLimb(false, false));
        compound.setBoolean("LostLeftArm", hasLostLimb(true, true));
        compound.setBoolean("LostRightArm", hasLostLimb(false, true));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setVariant(compound.getInteger("Variant"));
        setCarryingItem(compound.getBoolean("CarryingItem"));
        setOvenSpawned(compound.getBoolean("OvenSpawned"));
        setGingerbreadTeamColor(compound.getInteger("GingerbreadTeamColor"));
        setPossessedByLicowitchId(compound.getInteger("PossessorLicowitchId"));
        despawnFromOvenCooldown = compound.getInteger("OvenDespawnCooldown");
        setLostLimb(true, false, compound.getBoolean("LostLeftLeg"));
        setLostLimb(false, false, compound.getBoolean("LostRightLeg"));
        setLostLimb(true, true, compound.getBoolean("LostLeftArm"));
        setLostLimb(false, true, compound.getBoolean("LostRightArm"));
    }

    private class SitAI extends EntityAIBase {
        private SitAI() {
            setMutexBits(5);
        }

        @Override
        public boolean shouldExecute() {
            return !isInWater() && isMovementBlocked();
        }

        @Override
        public boolean shouldContinueExecuting() {
            return isMovementBlocked();
        }

        @Override
        public void startExecuting() {
            getNavigator().clearPath();
        }
    }

    private class StealItemAI extends EntityAIBase {
        private EntityItem targetItem;

        private StealItemAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (isCarryingItem() || hasLostLimb(isLeftHanded(), true) || rand.nextInt(isOvenSpawned() ? 10 : 50) != 0) {
                return false;
            }
            for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().grow(10.0D, 4.0D, 10.0D))) {
                if (!item.isDead && !item.getItem().isEmpty()) {
                    targetItem = item;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return targetItem != null && !targetItem.isDead && !targetItem.getItem().isEmpty() && !isCarryingItem();
        }

        @Override
        public void updateTask() {
            getNavigator().tryMoveToEntityLiving(targetItem, 1.25D);
            if (getDistance(targetItem) < 1.3D) {
                ItemStack duplicate = targetItem.getItem().copy();
                duplicate.setCount(1);
                setItemStackToSlot(EntityEquipmentSlot.OFFHAND, duplicate);
                targetItem.getItem().shrink(1);
                if (targetItem.getItem().isEmpty()) {
                    targetItem.setDead();
                }
                setCarryingItem(true);
                setAnimation(getAnimationForHand(false));
            }
        }

        @Override
        public void resetTask() {
            targetItem = null;
        }
    }
}
