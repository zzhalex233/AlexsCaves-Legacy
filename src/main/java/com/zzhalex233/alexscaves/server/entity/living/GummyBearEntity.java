package com.zzhalex233.alexscaves.server.entity.living;

import javax.annotation.Nullable;

import com.zzhalex233.alexscaves.server.entity.ACEntityRegistry;
import com.zzhalex233.alexscaves.server.entity.util.GummyColors;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class GummyBearEntity extends EntityAnimal {
    private static final DataParameter<Integer> GUMMY_COLOR = EntityDataManager.createKey(GummyBearEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(GummyBearEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> STANDING = EntityDataManager.createKey(GummyBearEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(GummyBearEntity.class, DataSerializers.BOOLEAN);
    private float prevSitProgress;
    private float sitProgress;
    private float prevStandProgress;
    private float standProgress;
    private float prevSleepProgress;
    private float sleepProgress;
    private int idlePoseTime;

    public GummyBearEntity(World world) {
        super(world);
        setSize(1.4F, 1.65F);
        experienceValue = 6;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(GUMMY_COLOR, GummyColors.RED.ordinal());
        dataManager.register(SITTING, false);
        dataManager.register(STANDING, false);
        dataManager.register(SLEEPING, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new GummyPoseAI());
        tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true));
        tasks.addTask(3, new EntityAIPanic(this, 1.15D));
        tasks.addTask(4, new EntityAIMate(this, 1.0D));
        tasks.addTask(5, new EntityAIFollowParent(this, 1.0D));
        tasks.addTask(6, new EntityAITempt(this, 1.1D, ACItemRegistry.SWEETISH_FISH_RED.item(), false));
        tasks.addTask(7, new EntityAIWanderAvoidWater(this, 0.9D));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, SweetishFishEntity.class, 80, true, false, fish -> fish instanceof SweetishFishEntity && ((SweetishFishEntity) fish).getGummyColor() == getGummyColor()));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(36.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevSitProgress = sitProgress;
        prevStandProgress = standProgress;
        prevSleepProgress = sleepProgress;
        super.onLivingUpdate();
        if (isSitting() && sitProgress < 10.0F) {
            sitProgress++;
        }
        if (!isSitting() && sitProgress > 0.0F) {
            sitProgress--;
        }
        if (isStanding() && standProgress < 10.0F) {
            standProgress++;
        }
        if (!isStanding() && standProgress > 0.0F) {
            standProgress--;
        }
        if (isBearSleeping() && sleepProgress < 10.0F) {
            sleepProgress++;
        }
        if (!isBearSleeping() && sleepProgress > 0.0F) {
            sleepProgress--;
        }
        if (!world.isRemote && getAttackTarget() == null && idlePoseTime <= 0 && onGround && rand.nextInt(250) == 0) {
            idlePoseTime = 80 + rand.nextInt(160);
            int pose = rand.nextInt(3);
            setSitting(pose == 0);
            setStanding(pose == 1);
            setBearSleeping(pose == 2);
        }
        if (idlePoseTime > 0 && --idlePoseTime == 0) {
            setSitting(false);
            setStanding(false);
            setBearSleeping(false);
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (isFood(stack) && getHealth() < getMaxHealth()) {
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            heal(8.0F);
            setSitting(true);
            setStanding(false);
            setBearSleeping(false);
            idlePoseTime = 40;
            playSound(ACSoundRegistry.GUMMY_BEAR_EAT, 1.0F, getSoundPitch());
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        setStanding(true);
        setSitting(false);
        setBearSleeping(false);
        idlePoseTime = 30;
        return super.attackEntityAsMob(entity);
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == ACItemRegistry.byName(SweetishFishEntity.getDropNameForColor(getGummyColor(), false)).item();
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return otherAnimal instanceof GummyBearEntity && otherAnimal != this && ((GummyBearEntity) otherAnimal).getGummyColor() == getGummyColor() && isInLove() && otherAnimal.isInLove();
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        GummyBearEntity child = new GummyBearEntity(world);
        child.setGrowingAge(-24000);
        child.setGummyColor(getGummyColor());
        return child;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        setGummyColor(GummyColors.getRandom(rand, true));
        return livingdata;
    }

    @Override
    protected void playStepSound(net.minecraft.util.math.BlockPos pos, net.minecraft.block.Block blockIn) {
        if (!isChild()) {
            playSound(ACSoundRegistry.GUMMY_BEAR_STEP, 0.25F, getSoundPitch());
        }
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return effect.getPotion() != MobEffects.HUNGER && super.isPotionApplicable(effect);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isBearSleeping() ? ACSoundRegistry.GUMMY_BEAR_SNORE : ACSoundRegistry.GUMMY_BEAR_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.GUMMY_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GUMMY_BEAR_DEATH;
    }

    public GummyColors getGummyColor() {
        return GummyColors.fromOrdinal(dataManager.get(GUMMY_COLOR));
    }

    public void setGummyColor(GummyColors color) {
        dataManager.set(GUMMY_COLOR, color.ordinal());
    }

    public boolean isSitting() {
        return dataManager.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        dataManager.set(SITTING, sitting);
    }

    public boolean isStanding() {
        return dataManager.get(STANDING);
    }

    public void setStanding(boolean standing) {
        dataManager.set(STANDING, standing);
    }

    public boolean isBearSleeping() {
        return dataManager.get(SLEEPING);
    }

    public void setBearSleeping(boolean sleeping) {
        dataManager.set(SLEEPING, sleeping);
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) * 0.1F;
    }

    public float getStandProgress(float partialTicks) {
        return (prevStandProgress + (standProgress - prevStandProgress) * partialTicks) * 0.1F;
    }

    public float getSleepProgress(float partialTicks) {
        return (prevSleepProgress + (sleepProgress - prevSleepProgress) * partialTicks) * 0.1F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("GummyColor", getGummyColor().ordinal());
        compound.setBoolean("Sitting", isSitting());
        compound.setBoolean("Standing", isStanding());
        compound.setBoolean("Sleeping", isBearSleeping());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setGummyColor(GummyColors.fromOrdinal(compound.getInteger("GummyColor")));
        setSitting(compound.getBoolean("Sitting"));
        setStanding(compound.getBoolean("Standing"));
        setBearSleeping(compound.getBoolean("Sleeping"));
    }

    private class GummyPoseAI extends net.minecraft.entity.ai.EntityAIBase {
        private GummyPoseAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            return isSitting() || isBearSleeping();
        }

        @Override
        public void startExecuting() {
            getNavigator().clearPath();
        }

        @Override
        public boolean shouldContinueExecuting() {
            return isSitting() || isBearSleeping();
        }
    }
}
