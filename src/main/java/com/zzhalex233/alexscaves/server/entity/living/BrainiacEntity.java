package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.item.ThrownWasteDrumEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
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
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BrainiacEntity extends EntityMob {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_THROW_BARREL = 1;
    public static final int ANIMATION_DRINK_BARREL = 2;
    public static final int ANIMATION_BITE = 3;
    public static final int ANIMATION_SMASH = 4;
    private static final DataParameter<Boolean> HAS_BARREL = EntityDataManager.createKey(BrainiacEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TONGUE_TARGET_ID = EntityDataManager.createKey(BrainiacEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LICK_TICKS = EntityDataManager.createKey(BrainiacEntity.class, DataSerializers.VARINT);
    private int animation = ANIMATION_NONE;
    private int animationTick;
    private float prevRaiseArmsAmount;
    private float raiseArmsAmount;
    private float prevLeftArmAmount;
    private float raiseLeftArmAmount;
    private float prevShootTongueAmount;
    private float shootTongueAmount;
    private float prevLastTongueDistance;
    private float lastTongueDistance;
    private boolean drankBarrel;
    private boolean threwBarrel;

    public BrainiacEntity(World world) {
        super(world);
        setSize(1.3F, 2.5F);
        stepHeight = 1.1F;
        experienceValue = 12;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(HAS_BARREL, true);
        dataManager.register(TONGUE_TARGET_ID, -1);
        dataManager.register(LICK_TICKS, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new BrainiacMeleeAI());
        tasks.addTask(2, new PickupBarrelAI());
        tasks.addTask(3, new EntityAIWander(this, 1.0D, 45));
        tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(5, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevRaiseArmsAmount = raiseArmsAmount;
        prevLeftArmAmount = raiseLeftArmAmount;
        prevShootTongueAmount = shootTongueAmount;
        prevLastTongueDistance = lastTongueDistance;
        raiseArmsAmount = approach(raiseArmsAmount, animation == ANIMATION_SMASH ? 5.0F : 0.0F);
        raiseLeftArmAmount = approach(raiseLeftArmAmount, raisingLeftArm() ? 5.0F : 0.0F);
        shootTongueAmount = approach(shootTongueAmount, getLickTicks() > 0 ? 10.0F : 0.0F);
        if (animation != ANIMATION_NONE) {
            animationTick++;
            tickAnimation();
        }
        updateTongue();
    }

    private float approach(float value, float target) {
        if (value < target) {
            return value + 1.0F;
        }
        return value > target ? value - 1.0F : value;
    }

    private void tickAnimation() {
        if (!world.isRemote && hasBarrel()) {
            if (animation == ANIMATION_DRINK_BARREL && animationTick >= 60 && !drankBarrel) {
                drankBarrel = true;
                setHasBarrel(false);
                heal(10.0F);
                addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 400));
                addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 400));
                addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 4));
            }
            if (animation == ANIMATION_THROW_BARREL && animationTick >= 15 && !threwBarrel) {
                threwBarrel = true;
                throwWasteDrum();
            }
        }
        if (animationTick >= animationLength(animation)) {
            setAnimation(ANIMATION_NONE);
        }
    }

    private void throwWasteDrum() {
        EntityLivingBase target = getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return;
        }
        setHasBarrel(false);
        Vec3d look = getLookVec();
        Vec3d hand = getPositionEyes(1.0F).add(look.x * 0.65D, -0.3D, look.z * 0.65D);
        ThrownWasteDrumEntity drum = new ThrownWasteDrumEntity(world, hand.x, hand.y, hand.z);
        Vec3d toss = target.getPositionEyes(1.0F).subtract(hand);
        drum.rotationYaw = (float) (-Math.atan2(toss.x, toss.z) * 180.0D / Math.PI);
        double distance = Math.max(1.0D, getDistance(target));
        Vec3d motion = new Vec3d(toss.x * 0.35D, 0.4D, toss.z * 0.35D).normalize().scale(distance * 0.2D);
        drum.motionX = motion.x;
        drum.motionY = motion.y;
        drum.motionZ = motion.z;
        world.spawnEntity(drum);
    }

    private void updateTongue() {
        Entity tongueTarget = getTongueTarget();
        if (world.isRemote) {
            if (tongueTarget != null && tongueTarget.isEntityAlive()) {
                lastTongueDistance = getDistance(tongueTarget) - 0.5F;
            }
            return;
        }
        EntityLivingBase target = getAttackTarget();
        if (getLickTicks() > 0) {
            setLickTicks(getLickTicks() - 1);
            if (target != null && target.isEntityAlive() && canEntityBeSeen(target) && getDistance(target) < 20.0F) {
                setTongueTargetId(target.getEntityId());
                getLookHelper().setLookPositionWithEntity(target, 180.0F, 30.0F);
            } else {
                setTongueTargetId(-1);
            }
        } else {
            setTongueTargetId(-1);
        }
        if (tongueTarget instanceof EntityLivingBase && shootTongueAmount >= 5.0F) {
            EntityLivingBase living = (EntityLivingBase) tongueTarget;
            postAttackEffect(living);
            living.attackEntityFrom(DamageSource.causeMobDamage(this), 4.0F);
            living.knockBack(this, 0.3F, posX - living.posX, posZ - living.posZ);
            setLickTicks(0);
        }
    }

    private int animationLength(int anim) {
        switch (anim) {
            case ANIMATION_THROW_BARREL:
                return 30;
            case ANIMATION_DRINK_BARREL:
                return 75;
            case ANIMATION_BITE:
                return 25;
            case ANIMATION_SMASH:
                return 20;
            default:
                return 0;
        }
    }

    public void setAnimation(int animation) {
        this.animation = animation;
        animationTick = 0;
        drankBarrel = false;
        threwBarrel = false;
        world.setEntityState(this, (byte) (70 + animation));
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id >= 70 && id <= 74) {
            animation = id - 70;
            animationTick = 0;
            drankBarrel = false;
            threwBarrel = false;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (animation == ANIMATION_DRINK_BARREL || animation == ANIMATION_THROW_BARREL || animation == ANIMATION_SMASH && animationTick < 15 || animation == ANIMATION_BITE && animationTick < 16) {
            super.travel(0.0F, vertical, 0.0F);
            return;
        }
        super.travel(strafe, vertical, forward);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int flesh = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < flesh; i++) {
            dropItem(Items.ROTTEN_FLESH, 1);
        }
        if (rand.nextFloat() < 0.4F + lootingModifier * 0.08F) {
            dropItem(ACItemRegistry.GREEN_SOYLENT.item(), 1);
        }
        if (rand.nextFloat() < 0.35F + lootingModifier * 0.08F) {
            dropItem(ACItemRegistry.CHARRED_REMNANT.item(), 1);
        }
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        super.dropEquipment(wasRecentlyHit, lootingModifier);
        DamageSource source = getLastDamageSource();
        if (hasBarrel() && source != null && (source.getTrueSource() != null || source.getImmediateSource() != null)) {
            entityDropItem(new ItemStack(ACBlockRegistry.WASTE_DRUM.item()), 0.0F);
        }
    }

    public void postAttackEffect(EntityLivingBase entity) {
        if (entity != null && entity.isEntityAlive()) {
            entity.addPotionEffect(new PotionEffect(ACEffectRegistry.IRRADIATED, 400));
        }
    }

    public Entity getTongueTarget() {
        int id = dataManager.get(TONGUE_TARGET_ID);
        return id == -1 ? null : world.getEntityByID(id);
    }

    public int getAnimation() {
        return animation;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public boolean hasBarrel() {
        return dataManager.get(HAS_BARREL);
    }

    public void setHasBarrel(boolean barrel) {
        dataManager.set(HAS_BARREL, barrel);
    }

    public int getLickTicks() {
        return dataManager.get(LICK_TICKS);
    }

    public void setLickTicks(int ticks) {
        dataManager.set(LICK_TICKS, ticks);
    }

    public void setTongueTargetId(int id) {
        dataManager.set(TONGUE_TARGET_ID, id);
    }

    public boolean raisingLeftArm() {
        return animation == ANIMATION_DRINK_BARREL || animation == ANIMATION_BITE || animation == ANIMATION_THROW_BARREL;
    }

    public float getRaiseArmsAmount(float partialTick) {
        return (prevRaiseArmsAmount + (raiseArmsAmount - prevRaiseArmsAmount) * partialTick) * 0.2F;
    }

    public float getRaiseLeftArmAmount(float partialTick) {
        return (prevLeftArmAmount + (raiseLeftArmAmount - prevLeftArmAmount) * partialTick) * 0.2F;
    }

    public float getShootTongueAmount(float partialTick) {
        return (prevShootTongueAmount + (shootTongueAmount - prevShootTongueAmount) * partialTick) * 0.1F;
    }

    public float getLastTongueDistance(float partialTick) {
        return prevLastTongueDistance + (lastTongueDistance - prevLastTongueDistance) * partialTick;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("HasBarrel", hasBarrel());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setHasBarrel(compound.getBoolean("HasBarrel"));
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.BRAINIAC_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.BRAINIAC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.BRAINIAC_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.block.Block block) {
        playSound(ACSoundRegistry.BRAINIAC_STEP, 1.0F, 1.0F);
    }

    private class BrainiacMeleeAI extends EntityAIBase {
        private int tongueCooldown;

        private BrainiacMeleeAI() {
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
            if (target == null || !target.isEntityAlive()) {
                return;
            }
            if (tongueCooldown > 0) {
                tongueCooldown--;
            }
            double dist = getDistance(target);
            getLookHelper().setLookPositionWithEntity(target, 180.0F, 30.0F);
            if (animation == ANIMATION_NONE) {
                if (getHealth() < getMaxHealth() * 0.5F && hasBarrel() && rand.nextInt(20) == 0) {
                    setAnimation(ANIMATION_DRINK_BARREL);
                    return;
                }
                getNavigator().tryMoveToEntityLiving(target, 1.2D);
                if (canEntityBeSeen(target)) {
                    if (getHealth() < getMaxHealth() * 0.75F && dist < 20.0D && hasBarrel() && rand.nextInt(30) == 0) {
                        setAnimation(ANIMATION_THROW_BARREL);
                        playSound(ACSoundRegistry.BRAINIAC_THROW, 1.0F, getSoundPitch());
                        return;
                    }
                    if (dist < width + target.width + 3.5D) {
                        setAnimation(rand.nextBoolean() ? ANIMATION_SMASH : ANIMATION_BITE);
                        playSound(ACSoundRegistry.BRAINIAC_ATTACK, 1.0F, getSoundPitch());
                        return;
                    }
                    if (tongueCooldown == 0 && rand.nextInt(16) == 0 && dist < 25.0D) {
                        playSound(ACSoundRegistry.BRAINIAC_LICK, 1.0F, getSoundPitch());
                        setLickTicks(20);
                        tongueCooldown = 15 + rand.nextInt(15);
                    }
                } else {
                    setLickTicks(0);
                }
            }
            if (animation == ANIMATION_SMASH && animationTick >= 10 && animationTick <= 15) {
                checkAndDealDamage(target, 2.0F);
            }
            if (animation == ANIMATION_BITE && animationTick >= 10 && animationTick <= 15) {
                checkAndDealDamage(target, 1.0F);
            }
        }

        private void checkAndDealDamage(EntityLivingBase target, float multiplier) {
            if (canEntityBeSeen(target) && getDistance(target) < width + target.width + 2.0D && target.attackEntityFrom(DamageSource.causeMobDamage(BrainiacEntity.this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * multiplier)) {
                postAttackEffect(target);
                target.knockBack(BrainiacEntity.this, 0.3F, posX - target.posX, posZ - target.posZ);
            }
        }
    }

    private class PickupBarrelAI extends EntityAIBase {
        private BlockPos targetPos;
        private int cooldown;

        private PickupBarrelAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (hasBarrel()) {
                return false;
            }
            if (cooldown-- > 0) {
                return false;
            }
            cooldown = 40 + rand.nextInt(40);
            targetPos = findNearbyDrum();
            return targetPos != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return targetPos != null && !hasBarrel() && world.getBlockState(targetPos).getBlock() == ACBlockRegistry.WASTE_DRUM.block();
        }

        @Override
        public void resetTask() {
            targetPos = null;
        }

        @Override
        public void updateTask() {
            if (targetPos == null) {
                return;
            }
            getNavigator().tryMoveToXYZ(targetPos.getX() + 0.5D, targetPos.getY() + 1.0D, targetPos.getZ() + 0.5D, 1.0D);
            getLookHelper().setLookPosition(targetPos.getX() + 0.5D, targetPos.getY() + 0.5D, targetPos.getZ() + 0.5D, 180.0F, 30.0F);
            if (getDistanceSqToCenter(targetPos) < (width + 1.0F) * (width + 1.0F)) {
                getNavigator().clearPath();
                if (animation == ANIMATION_NONE) {
                    setAnimation(ANIMATION_BITE);
                }
                if (animation == ANIMATION_BITE && animationTick >= 10 && animationTick <= 15 && world.getBlockState(targetPos).getBlock() == ACBlockRegistry.WASTE_DRUM.block()) {
                    world.destroyBlock(targetPos, false);
                    setHasBarrel(true);
                }
            }
        }

        private BlockPos findNearbyDrum() {
            BlockPos origin = new BlockPos(BrainiacEntity.this);
            int radius = 6;
            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;
            for (BlockPos pos : BlockPos.getAllInBox(origin.add(-radius, -2, -radius), origin.add(radius, 2, radius))) {
                if (world.getBlockState(pos).getBlock() == ACBlockRegistry.WASTE_DRUM.block()) {
                    double dist = getDistanceSqToCenter(pos);
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = pos.toImmutable();
                    }
                }
            }
            return best;
        }
    }
}
