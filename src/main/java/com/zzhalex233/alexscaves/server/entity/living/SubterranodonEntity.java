package com.zzhalex233.alexscaves.server.entity.living;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
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
import net.minecraft.item.ItemFishFood;
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

public class SubterranodonEntity extends EntityTameable {
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(SubterranodonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HOVERING = EntityDataManager.createKey(SubterranodonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ALT_SKIN = EntityDataManager.createKey(SubterranodonEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ATTACK_TICKS = EntityDataManager.createKey(SubterranodonEntity.class, DataSerializers.VARINT);
    private final EntityAISit aiSit = new EntityAISit(this);
    private float prevFlyProgress;
    private float flyProgress;
    private float prevHoverProgress;
    private float hoverProgress;
    private float prevFlightPitch;
    private float flightPitch;
    private float prevFlightRoll;
    private float flightRoll;
    private int flapSoundTime;
    private int timeFlying;

    public SubterranodonEntity(World world) {
        super(world);
        setSize(1.8F, 1.35F);
        stepHeight = 1.0F;
        experienceValue = 6;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FLYING, false);
        dataManager.register(HOVERING, false);
        dataManager.register(ALT_SKIN, 0);
        dataManager.register(ATTACK_TICKS, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, aiSit);
        tasks.addTask(2, new EntityAIAttackMelee(this, 1.4D, false));
        tasks.addTask(3, new EntityAIFollowOwner(this, 1.2D, 8.0F, 2.0F));
        tasks.addTask(4, new EntityAIMate(this, 1.0D));
        tasks.addTask(5, new EntityAIFollowParent(this, 1.0D));
        tasks.addTask(6, new EntityAITempt(this, 1.1D, Items.FISH, false));
        tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAIOwnerHurtByTarget(this));
        targetTasks.addTask(3, new EntityAIOwnerHurtTarget(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevFlyProgress = flyProgress;
        prevHoverProgress = hoverProgress;
        prevFlightPitch = flightPitch;
        prevFlightRoll = flightRoll;
        super.onLivingUpdate();
        flyProgress = approach(flyProgress, isFlying() ? 5.0F : 0.0F);
        hoverProgress = approach(hoverProgress, isHovering() ? 5.0F : 0.0F);
        if (getAttackTicks() > 0) {
            setAttackTicks(getAttackTicks() - 1);
        }
        if (!world.isRemote && (isBeingRidden() || getAttackTarget() != null && getAttackTarget().isEntityAlive())) {
            setFlying(true);
            setSitting(false);
            aiSit.setSitting(false);
        }
        if (isFlying()) {
            setNoGravity(true);
            timeFlying++;
            setHovering(isBeingRidden() && Math.abs(motionY) < 0.08D || !isBeingRidden() && getAttackTarget() == null);
            if (!world.isRemote && ++flapSoundTime >= 10) {
                flapSoundTime = 0;
                playSound(ACSoundRegistry.SUBTERRANODON_FLAP, 0.7F, getSoundPitch());
            }
            if (!world.isRemote && onGround && timeFlying > 35 && !isBeingRidden() && getAttackTarget() == null) {
                setFlying(false);
                setHovering(false);
            }
        } else {
            setNoGravity(false);
            setHovering(false);
            flapSoundTime = 0;
            timeFlying = 0;
        }
        tickRotation((float) (motionY * -114.59156D));
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        Entity passenger = getControllingPassenger();
        if (isBeingRidden() && canBeSteered() && passenger instanceof EntityLivingBase) {
            EntityLivingBase rider = (EntityLivingBase) passenger;
            rotationYaw = rider.rotationYaw;
            prevRotationYaw = rotationYaw;
            rotationPitch = rider.rotationPitch * 0.35F;
            setRotation(rotationYaw, rotationPitch);
            renderYawOffset = rotationYaw;
            rotationYawHead = rotationYaw;
            strafe = rider.moveStrafing * 0.35F;
            forward = rider.moveForward * (rider.moveForward <= 0.0F ? 0.25F : 0.9F);
            vertical = forward > 0.0F ? -MathHelper.sin(rider.rotationPitch * 0.017453292F) * 0.75F : 0.0F;
            setFlying(true);
            setAIMoveSpeed((float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 1.35F);
            moveRelative(strafe, vertical, forward, 0.06F);
            move(MoverType.SELF, motionX, motionY, motionZ);
            motionX *= 0.91D;
            motionY *= 0.91D;
            motionZ *= 0.91D;
            return;
        }
        if (isFlying()) {
            if (getAttackTarget() != null && getAttackTarget().isEntityAlive()) {
                flyToward(getAttackTarget().posX, getAttackTarget().posY + getAttackTarget().height * 0.5D, getAttackTarget().posZ, 1.2D);
            } else {
                motionY += MathHelper.sin(ticksExisted * 0.15F) * 0.006D;
            }
            moveRelative(strafe, vertical, forward, 0.02F);
            move(MoverType.SELF, motionX, motionY, motionZ);
            motionX *= 0.91D;
            motionY *= 0.91D;
            motionZ *= 0.91D;
            return;
        }
        super.travel(strafe, vertical, forward);
    }

    private void flyToward(double x, double y, double z, double speed) {
        double dx = x - posX;
        double dy = y - posY;
        double dz = z - posZ;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length > 0.001D) {
            motionX = (motionX + dx / length * speed * 0.05D) * 0.95D;
            motionY = (motionY + dy / length * speed * 0.05D) * 0.95D - 0.01D;
            motionZ = (motionZ + dz / length * speed * 0.05D) * 0.95D;
            rotationYaw = approachAngle(rotationYaw, (float) (-Math.atan2(dx, dz) * 180.0D / Math.PI), 8.0F);
            renderYawOffset = rotationYaw;
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        setAttackTicks(15);
        playSound(ACSoundRegistry.SUBTERRANODON_ATTACK, 0.8F, getSoundPitch());
        return super.attackEntityAsMob(entity);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!isTamed() && isTamingItem(stack)) {
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
            if (player.isSneaking()) {
                if (!world.isRemote) {
                    aiSit.setSitting(!isSitting());
                    setSitting(!isSitting());
                }
                return true;
            }
            if (!isChild()) {
                player.startRiding(this);
                return true;
            }
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.FISH && stack.getMetadata() == ItemFishFood.FishType.COD.getMetadata();
    }

    private boolean isTamingItem(ItemStack stack) {
        return stack.getItem() == ACItemRegistry.TRILOCARIS_TAIL.item() || stack.getItem() == ACItemRegistry.COOKED_TRILOCARIS_TAIL.item();
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return otherAnimal instanceof SubterranodonEntity && otherAnimal != this && isTamed() && ((SubterranodonEntity) otherAnimal).isTamed() && isInLove() && otherAnimal.isInLove();
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        SubterranodonEntity child = new SubterranodonEntity(world);
        EntityLivingBase owner = getOwner();
        if (owner instanceof EntityPlayer) {
            child.setTamedBy((EntityPlayer) owner);
        }
        child.setGrowingAge(-24000);
        child.setAltSkin(rand.nextInt(3));
        return child;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        setAltSkin(rand.nextInt(3));
        return livingdata;
    }

    @Override
    protected boolean canDespawn() {
        return !isTamed() && super.canDespawn();
    }

    @Override
    public boolean canBeSteered() {
        Entity passenger = getControllingPassenger();
        return passenger instanceof EntityLivingBase && isOwner((EntityLivingBase) passenger);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return getPassengers().isEmpty() ? null : getPassengers().get(0);
    }

    @Override
    public double getMountedYOffset() {
        return isFlying() ? height * 0.35D : height * 0.55D;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (isPassenger(passenger)) {
            passenger.setPosition(posX, posY + getMountedYOffset() + passenger.getYOffset(), posZ - MathHelper.cos(rotationYaw * 0.017453292F) * (isFlying() ? 0.6D : 0.1D));
            passenger.fallDistance = 0.0F;
        }
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, net.minecraft.block.state.IBlockState state, net.minecraft.util.math.BlockPos pos) {
        if (!isFlying()) {
            super.updateFallState(y, onGroundIn, state, pos);
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        if (!isFlying()) {
            super.fall(distance, damageMultiplier);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.SUBTERRANODON_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.SUBTERRANODON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.SUBTERRANODON_DEATH;
    }

    public boolean isFlying() {
        return dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        dataManager.set(FLYING, flying);
    }

    public boolean isHovering() {
        return dataManager.get(HOVERING);
    }

    public void setHovering(boolean hovering) {
        dataManager.set(HOVERING, hovering);
    }

    public int getAltSkin() {
        return dataManager.get(ALT_SKIN);
    }

    public void setAltSkin(int skin) {
        dataManager.set(ALT_SKIN, MathHelper.clamp(skin, 0, 2));
    }

    public int getAttackTicks() {
        return dataManager.get(ATTACK_TICKS);
    }

    public void setAttackTicks(int ticks) {
        dataManager.set(ATTACK_TICKS, MathHelper.clamp(ticks, 0, 20));
    }

    public float getFlyProgress(float partialTicks) {
        return (prevFlyProgress + (flyProgress - prevFlyProgress) * partialTicks) * 0.2F;
    }

    public float getHoverProgress(float partialTicks) {
        return (prevHoverProgress + (hoverProgress - prevHoverProgress) * partialTicks) * 0.2F;
    }

    public float getFlightPitch(float partialTicks) {
        return prevFlightPitch + (flightPitch - prevFlightPitch) * partialTicks;
    }

    public float getFlightRoll(float partialTicks) {
        return prevFlightRoll + (flightRoll - prevFlightRoll) * partialTicks;
    }

    private float approach(float value, float target) {
        return value < target ? Math.min(target, value + 1.0F) : value > target ? Math.max(target, value - 1.0F) : value;
    }

    private float approachAngle(float value, float target, float step) {
        return value + MathHelper.clamp(MathHelper.wrapDegrees(target - value), -step, step);
    }

    private void tickRotation(float yMotion) {
        flightPitch = yMotion;
        float yawChange = prevRotationYaw - rotationYaw;
        if (isFlying() && yawChange > 1.0F) {
            flightRoll += 10.0F;
        } else if (isFlying() && yawChange < -1.0F) {
            flightRoll -= 10.0F;
        } else if (flightRoll > 0.0F) {
            flightRoll = Math.max(flightRoll - 5.0F, 0.0F);
        } else if (flightRoll < 0.0F) {
            flightRoll = Math.min(flightRoll + 5.0F, 0.0F);
        }
        flightRoll = MathHelper.clamp(flightRoll, -60.0F, 60.0F);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Flying", isFlying());
        compound.setInteger("TimeFlying", timeFlying);
        compound.setInteger("AltSkin", getAltSkin());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setFlying(compound.getBoolean("Flying"));
        timeFlying = compound.getInteger("TimeFlying");
        setAltSkin(compound.getInteger("AltSkin"));
    }
}
