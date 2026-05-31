package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
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
import net.minecraft.world.World;

public class GrottoceratopsEntity extends EntityAnimal {
    private static final DataParameter<Integer> ALT_SKIN = EntityDataManager.createKey(GrottoceratopsEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CHEWING = EntityDataManager.createKey(GrottoceratopsEntity.class, DataSerializers.BOOLEAN);
    private float prevTailSwingRot;
    private float tailSwingRot;
    private float prevChewProgress;
    private float chewProgress;
    private int attackCooldown;
    private int chewingTime;

    public GrottoceratopsEntity(World world) {
        super(world);
        setSize(2.4F, 2.6F);
        experienceValue = 8;
        stepHeight = 1.1F;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(ALT_SKIN, 0);
        dataManager.register(CHEWING, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAIPanic(this, 1.2D));
        tasks.addTask(1, new MeleeAI());
        tasks.addTask(2, new EntityAIMate(this, 1.0D));
        tasks.addTask(3, new EntityAITempt(this, 1.1D, ACBlockRegistry.TREE_STAR.item(), false));
        tasks.addTask(4, new EntityAIFollowParent(this, 1.0D));
        tasks.addTask(5, new EntityAIWander(this, 1.0D, 45));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, GrottoceratopsEntity.class));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.9D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevTailSwingRot = tailSwingRot;
        prevChewProgress = chewProgress;
        super.onLivingUpdate();
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (isChewing()) {
            chewProgress = Math.min(1.0F, chewProgress + 0.1F);
            if (--chewingTime <= 0) {
                setChewing(false);
                if (!world.isRemote) {
                    heal(5.0F);
                }
            } else if (chewingTime % 10 == 0) {
                playSound(ACSoundRegistry.GROTTOCERATOPS_GRAZE, 0.8F, getSoundPitch());
            }
        } else {
            chewProgress = Math.max(0.0F, chewProgress - 0.05F);
        }
        if (Math.abs(tailSwingRot) > 0.0F) {
            tailSwingRot = approachDegrees(tailSwingRot, 0.0F, 20.0F);
        }
        if (ticksExisted % 100 == 0 && getHealth() < getMaxHealth()) {
            heal(2.0F);
        }
        if (!world.isRemote && getRevengeTarget() != null && !getRevengeTarget().isEntityAlive()) {
            setRevengeTarget(null);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean tail = rand.nextBoolean();
        tailSwingRot = tail ? -180.0F : 180.0F;
        playSound(ACSoundRegistry.GROTTOCERATOPS_ATTACK, 1.0F, getSoundPitch());
        boolean attacked = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
        if (attacked && entity instanceof EntityLivingBase) {
            float yaw = rotationYaw * 0.017453292F;
            double strength = tail ? 1.2D : 0.8D;
            entity.addVelocity(-MathHelper.sin(yaw) * strength, 0.25D, MathHelper.cos(yaw) * strength);
        }
        return attacked;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (isBreedingItem(stack) && getHealth() < getMaxHealth()) {
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            setChewing(true);
            chewingTime = 40;
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        GrottoceratopsEntity child = new GrottoceratopsEntity(world);
        child.setGrowingAge(-24000);
        child.setAltSkin(rand.nextInt(3));
        return child;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == ACBlockRegistry.TREE_STAR.item();
    }

    @Override
    public IEntityLivingData onInitialSpawn(net.minecraft.world.DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        setAltSkin(rand.nextInt(3));
        return livingdata;
    }

    @Override
    protected void playStepSound(net.minecraft.util.math.BlockPos pos, Block blockIn) {
        if (!isChild()) {
            playSound(ACSoundRegistry.GROTTOCERATOPS_STEP, 0.7F, 0.85F);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return rand.nextInt(4) == 0 ? ACSoundRegistry.GROTTOCERATOPS_CALL : ACSoundRegistry.GROTTOCERATOPS_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.GROTTOCERATOPS_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GROTTOCERATOPS_DEATH;
    }

    public int getAltSkin() {
        return dataManager.get(ALT_SKIN);
    }

    public void setAltSkin(int altSkin) {
        dataManager.set(ALT_SKIN, MathHelper.clamp(altSkin, 0, 2));
    }

    public boolean isChewing() {
        return dataManager.get(CHEWING);
    }

    public void setChewing(boolean chewing) {
        dataManager.set(CHEWING, chewing);
    }

    public float getTailSwingRot(float partialTicks) {
        return prevTailSwingRot + (tailSwingRot - prevTailSwingRot) * partialTicks;
    }

    public float getChewProgress(float partialTicks) {
        return prevChewProgress + (chewProgress - prevChewProgress) * partialTicks;
    }

    private float approachDegrees(float current, float target, float step) {
        float diff = MathHelper.wrapDegrees(target - current);
        if (diff > step) {
            diff = step;
        } else if (diff < -step) {
            diff = -step;
        }
        return current + diff;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("AltSkin", getAltSkin());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setAltSkin(compound.getInteger("AltSkin"));
    }

    private class MeleeAI extends EntityAIBase {
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
            faceEntity(target, 30.0F, 30.0F);
            double distance = getDistance(target);
            if (distance < 3.4D && attackCooldown <= 0) {
                attackEntityAsMob(target);
                attackCooldown = 28;
            } else {
                getNavigator().tryMoveToEntityLiving(target, 1.05D);
            }
        }
    }
}
