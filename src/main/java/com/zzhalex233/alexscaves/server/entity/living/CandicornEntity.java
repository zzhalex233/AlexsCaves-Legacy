package com.zzhalex233.alexscaves.server.entity.living;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.entity.ACEntityRegistry;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class CandicornEntity extends EntityTameable {
    private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(CandicornEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RUNNING = EntityDataManager.createKey(CandicornEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CHARGING = EntityDataManager.createKey(CandicornEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(CandicornEntity.class, DataSerializers.VARINT);
    private final EntityAISit aiSit = new EntityAISit(this);
    private int chargeCooldown;
    private int gallopSoundCounter;
    private float prevRunProgress;
    private float runProgress;
    private float prevChargeProgress;
    private float chargeProgress;

    public CandicornEntity(World world) {
        super(world);
        setSize(1.5F, 1.8F);
        stepHeight = 1.1F;
        experienceValue = 6;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SADDLED, false);
        dataManager.register(RUNNING, false);
        dataManager.register(CHARGING, false);
        dataManager.register(VARIANT, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, aiSit);
        tasks.addTask(2, new EntityAIFollowOwner(this, 1.1D, 8.0F, 2.0F));
        tasks.addTask(3, new EntityAIAttackMelee(this, 1.15D, true));
        tasks.addTask(4, new EntityAIMate(this, 1.0D));
        tasks.addTask(5, new EntityAIFollowParent(this, 1.0D));
        tasks.addTask(6, new EntityAITempt(this, 1.1D, ACItemRegistry.CARAMEL_APPLE.item(), false));
        tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevRunProgress = runProgress;
        prevChargeProgress = chargeProgress;
        super.onLivingUpdate();
        if (chargeCooldown > 0) {
            chargeCooldown--;
        }
        setRunning(isBeingRidden() && getControllingPassenger() instanceof EntityLivingBase && ((EntityLivingBase) getControllingPassenger()).moveForward > 0.15F);
        if (isRunning() && runProgress < 5.0F) {
            runProgress++;
        }
        if (!isRunning() && runProgress > 0.0F) {
            runProgress--;
        }
        if (isCharging() && chargeProgress < 5.0F) {
            chargeProgress++;
        }
        if (!isCharging() && chargeProgress > 0.0F) {
            chargeProgress--;
        }
        if (!isBeingRidden() || chargeCooldown <= 35) {
            setCharging(false);
        }
        if (isCharging()) {
            for (EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(1.4D, 0.8D, 1.4D))) {
                if (living != this && !isOwner(living) && !living.isRidingSameEntity(this) && !(living instanceof CandicornEntity)) {
                    if (living.attackEntityFrom(DamageSource.causeMobDamage(this), 7.0F)) {
                        living.addVelocity(-MathHelper.sin(rotationYaw * 0.017453292F) * 1.2D, 0.25D, MathHelper.cos(rotationYaw * 0.017453292F) * 1.2D);
                    }
                }
            }
        }
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        Entity passenger = getControllingPassenger();
        if (isBeingRidden() && canBeSteered() && passenger instanceof EntityLivingBase) {
            EntityLivingBase rider = (EntityLivingBase) passenger;
            rotationYaw = rider.rotationYaw;
            prevRotationYaw = rotationYaw;
            rotationPitch = rider.rotationPitch * 0.5F;
            setRotation(rotationYaw, rotationPitch);
            renderYawOffset = rotationYaw;
            rotationYawHead = rotationYaw;
            strafe = rider.moveStrafing * 0.5F;
            forward = rider.moveForward;
            if (forward <= 0.0F) {
                forward *= 0.25F;
            }
            if (forward > 0.15F && isSaddled() && chargeCooldown <= 0) {
                setCharging(true);
                playSound(ACSoundRegistry.CANDICORN_CHARGE_START, 0.8F, 1.0F);
                chargeCooldown = 60;
            }
            setAIMoveSpeed((float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * (isCharging() ? 1.8F : 1.25F));
            super.travel(strafe, vertical, forward);
            return;
        }
        super.travel(strafe, vertical, forward);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ACItemRegistry.CARAMEL_APPLE.item() && !isTamed()) {
            if (!world.isRemote) {
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                if (rand.nextInt(3) == 0) {
                    setTamedBy(player);
                    aiSit.setSitting(true);
                    setSitting(true);
                    navigator.clearPath();
                    setAttackTarget(null);
                    world.setEntityState(this, (byte) 7);
                } else {
                    world.setEntityState(this, (byte) 6);
                }
            }
            return true;
        }
        if (isTamed() && isOwner(player)) {
            if (!isSaddled() && stack.getItem() == Items.SADDLE && !isChild()) {
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                setSaddled(true);
                playSound(net.minecraft.init.SoundEvents.ENTITY_PIG_SADDLE, 0.5F, 1.0F);
                return true;
            }
            if (player.isSneaking()) {
                if (!world.isRemote) {
                    aiSit.setSitting(!isSitting());
                    setSitting(!isSitting());
                }
                return true;
            }
            if (isSaddled() && !isChild()) {
                player.startRiding(this);
                return true;
            }
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean canBeSteered() {
        Entity passenger = getControllingPassenger();
        return isSaddled() && passenger instanceof EntityLivingBase && isOwner((EntityLivingBase) passenger);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return getPassengers().isEmpty() ? null : getPassengers().get(0);
    }

    @Override
    public double getMountedYOffset() {
        return height * 0.75D;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (isPassenger(passenger)) {
            passenger.setPosition(posX, posY + getMountedYOffset() + passenger.getYOffset(), posZ);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        playSound(ACSoundRegistry.CANDICORN_CHARGE_START, 0.6F, 1.4F);
        return super.attackEntityAsMob(entity);
    }

    @Override
    protected void playStepSound(net.minecraft.util.math.BlockPos pos, net.minecraft.block.Block blockIn) {
        if (isRunning()) {
            if (++gallopSoundCounter % 4 == 0) {
                playSound(ACSoundRegistry.CANDICORN_GALLOP, 0.3F, getSoundPitch());
            }
        } else {
            playSound(ACSoundRegistry.CANDICORN_STEP, 0.2F, getSoundPitch());
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == ACItemRegistry.CARAMEL_APPLE.item();
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return otherAnimal instanceof CandicornEntity && otherAnimal != this && isTamed() && ((CandicornEntity) otherAnimal).isTamed() && isInLove() && otherAnimal.isInLove();
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        CandicornEntity child = new CandicornEntity(world);
        EntityLivingBase owner = getOwner();
        if (owner instanceof EntityPlayer) {
            child.setTamedBy((EntityPlayer) owner);
        }
        child.setVariant(getVariant());
        return child;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        setVariant(rand.nextInt(5));
        return livingdata;
    }

    @Override
    protected boolean canDespawn() {
        return !isTamed() && super.canDespawn();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.CANDICORN_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.CANDICORN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.CANDICORN_DEATH;
    }

    public boolean isSaddled() {
        return dataManager.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        dataManager.set(SADDLED, saddled);
    }

    public boolean isRunning() {
        return dataManager.get(RUNNING);
    }

    public void setRunning(boolean running) {
        dataManager.set(RUNNING, running);
    }

    public boolean isCharging() {
        return dataManager.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        dataManager.set(CHARGING, charging);
    }

    public int getVariant() {
        return dataManager.get(VARIANT);
    }

    public void setVariant(int variant) {
        dataManager.set(VARIANT, MathHelper.clamp(variant, 0, 4));
    }

    public float getRunProgress(float partialTicks) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTicks) * 0.2F;
    }

    public float getChargeProgress(float partialTicks) {
        return (prevChargeProgress + (chargeProgress - prevChargeProgress) * partialTicks) * 0.2F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Saddled", isSaddled());
        compound.setInteger("Variant", getVariant());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setSaddled(compound.getBoolean("Saddled"));
        setVariant(compound.getInteger("Variant"));
    }
}
