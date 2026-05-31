package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.config.ACConfig;
import com.zzhalex233.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NucleeperEntity extends EntityMob {
    private static final DataParameter<Boolean> TRIGGERED = EntityDataManager.createKey(NucleeperEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> CLOSE_TIME = EntityDataManager.createKey(NucleeperEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> EXPLODING = EntityDataManager.createKey(NucleeperEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CHARGED = EntityDataManager.createKey(NucleeperEntity.class, DataSerializers.BOOLEAN);
    private float closeProgress;
    private float prevCloseProgress;
    private float explodeProgress;
    private float prevExplodeProgress;
    private float sirenAngle;
    private float prevSirenAngle;
    private int catScareTime;
    private boolean spawnedExplosion;

    public NucleeperEntity(World world) {
        super(world);
        setSize(1.4F, 3.2F);
        stepHeight = 1.1F;
        experienceValue = 12;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(TRIGGERED, false);
        dataManager.register(CLOSE_TIME, 0);
        dataManager.register(EXPLODING, false);
        dataManager.register(CHARGED, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAvoidEntity<>(this, RaycatEntity.class, 10.0F, 1.0D, 1.2D) {
            @Override
            public void updateTask() {
                super.updateTask();
                catScareTime = 20;
            }
        });
        tasks.addTask(2, new NucleeperMeleeAI());
        tasks.addTask(3, new EntityAIWander(this, 1.0D, 45));
        tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(5, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevCloseProgress = closeProgress;
        prevExplodeProgress = explodeProgress;
        prevSirenAngle = sirenAngle;
        int time = getCloseTime();
        if (isExploding() && explodeProgress < 5.0F) {
            explodeProgress++;
        }
        if (!isExploding() && explodeProgress > 0.0F) {
            explodeProgress--;
        }
        if (isTriggered() && !world.isRemote) {
            if (catScareTime > 0 && !isExploding()) {
                if (time > 0) {
                    setCloseTime(time - 1);
                } else {
                    setTriggered(false);
                }
            } else if (time < ACConfig.getNucleeperFuseTime()) {
                setCloseTime(time + 1);
            } else if (isEntityAlive()) {
                setExploding(true);
            }
        }
        if (isTriggered() && isEntityAlive() && ticksExisted % 20 == 0) {
            world.playSound(null, posX, posY, posZ, ACSoundRegistry.NUCLEEPER_CHARGE, SoundCategory.HOSTILE, 1.5F, 0.7F + closeProgress * 1.5F);
        }
        sirenAngle += (10.0F + 30.0F * closeProgress) % 360.0F;
        closeProgress = getCloseTime() / (float) ACConfig.getNucleeperFuseTime();
        if (catScareTime > 0) {
            catScareTime--;
        }
        if (world.isRemote && (isTriggered() || isExploding())) {
            world.spawnParticle(EnumParticleTypes.REDSTONE, posX + (rand.nextDouble() - 0.5D) * width, posY + height * 0.65D, posZ + (rand.nextDouble() - 0.5D) * width, 0.1D, 1.0D, 0.0D);
        }
        if (isExploding() && explodeProgress >= 5.0F) {
            if (!world.isRemote && !spawnedExplosion) {
                explode();
                spawnedExplosion = true;
            }
            setDead();
        }
        if (isCharged() && isEntityAlive() && ticksExisted % 150 == 0) {
            heal(1.0F);
        }
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == Items.FLINT_AND_STEEL || stack.getItem() == Items.FIRE_CHARGE) {
            world.playSound(player, posX, posY, posZ, stack.getItem() == Items.FIRE_CHARGE ? SoundEvents.ITEM_FIRECHARGE_USE : SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.HOSTILE, 1.0F, rand.nextFloat() * 0.4F + 0.8F);
            if (!world.isRemote) {
                setTriggered(true);
                if (!player.capabilities.isCreativeMode) {
                    if (stack.getItem() == Items.FLINT_AND_STEEL) {
                        stack.damageItem(1, player);
                    } else {
                        stack.shrink(1);
                    }
                }
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
        super.onStruckByLightning(lightningBolt);
        setCharged(true);
    }

    private void explode() {
        NuclearExplosionEntity explosion = new NuclearExplosionEntity(world);
        explosion.setPosition(posX, posY, posZ);
        explosion.setNukeSize(isCharged() ? 1.75F : 1.0F);
        if (!world.getGameRules().getBoolean("mobGriefing")) {
            explosion.setNoGriefing(true);
        }
        world.spawnEntity(explosion);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int gunpowder = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        for (int i = 0; i < gunpowder; i++) {
            dropItem(Items.GUNPOWDER, 1);
        }
        if (rand.nextFloat() < 0.35F + lootingModifier * 0.08F) {
            dropItem(ACItemRegistry.FISSILE_CORE.item(), 1);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Charged", isCharged());
        compound.setInteger("CloseTime", getCloseTime());
        compound.setBoolean("Triggered", isTriggered());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setCharged(compound.getBoolean("Charged"));
        setCloseTime(compound.getInteger("CloseTime"));
        setTriggered(compound.getBoolean("Triggered"));
    }

    public int getCloseTime() {
        return dataManager.get(CLOSE_TIME);
    }

    public void setCloseTime(int time) {
        dataManager.set(CLOSE_TIME, time);
    }

    public boolean isTriggered() {
        return dataManager.get(TRIGGERED);
    }

    public void setTriggered(boolean triggered) {
        dataManager.set(TRIGGERED, triggered);
    }

    public boolean isExploding() {
        return dataManager.get(EXPLODING);
    }

    public void setExploding(boolean exploding) {
        dataManager.set(EXPLODING, exploding);
    }

    public boolean isCharged() {
        return dataManager.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        dataManager.set(CHARGED, charged);
    }

    public float getCloseProgress(float partialTicks) {
        return prevCloseProgress + (closeProgress - prevCloseProgress) * partialTicks;
    }

    public float getSirenAngle(float partialTicks) {
        return prevSirenAngle + (sirenAngle - prevSirenAngle) * partialTicks;
    }

    public float getExplodeProgress(float partialTicks) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTicks) * 0.2F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.NUCLEEPER_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.NUCLEEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.NUCLEEPER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.block.Block block) {
        playSound(ACSoundRegistry.NUCLEEPER_STEP, 1.0F, 1.0F);
    }

    private class NucleeperMeleeAI extends EntityAIBase {
        private NucleeperMeleeAI() {
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
            getNavigator().tryMoveToEntityLiving(target, 1.0D);
            getLookHelper().setLookPositionWithEntity(target, 180.0F, 30.0F);
            if (getDistance(target) < 3.5F + target.width) {
                setTriggered(true);
            }
        }
    }
}
