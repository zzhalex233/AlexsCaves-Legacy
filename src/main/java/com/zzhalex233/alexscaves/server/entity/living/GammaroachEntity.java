package com.zzhalex233.alexscaves.server.entity.living;

import java.util.Comparator;
import java.util.List;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GammaroachEntity extends EntityMob {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_SPRAY = 1;
    public static final int ANIMATION_RAM = 2;
    private int animation = ANIMATION_NONE;
    private int animationTick;
    private int sprayCooldown;
    private boolean fed;

    public GammaroachEntity(World world) {
        super(world);
        setSize(1.4F, 0.8F);
        stepHeight = 1.1F;
        experienceValue = 5;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new GammaroachMeleeAI());
        tasks.addTask(2, new EntityAIWander(this, 1.0D, 45));
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 20, true, true, living -> living.isPotionActive(ACEffectRegistry.IRRADIATED) && !(living instanceof GammaroachEntity)));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (sprayCooldown > 0) {
            sprayCooldown--;
        }
        if (animation != ANIMATION_NONE) {
            animationTick++;
            if (animation == ANIMATION_SPRAY) {
                updateSprayAnimation();
                if (animationTick >= 40) {
                    setAnimation(ANIMATION_NONE);
                }
            } else if (animation == ANIMATION_RAM && animationTick >= 25) {
                setAnimation(ANIMATION_NONE);
            }
        }
    }

    private void updateSprayAnimation() {
        if (animationTick == 10 && !world.isRemote) {
            EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(world, posX, posY + 0.2D, posZ);
            cloud.setOwner(this);
            cloud.setParticle(EnumParticleTypes.SPELL_MOB);
            cloud.setColor(0x77D60E);
            cloud.addEffect(new PotionEffect(ACEffectRegistry.IRRADIATED, 2000));
            cloud.setRadius(2.3F);
            cloud.setDuration(200);
            cloud.setWaitTime(10);
            cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
            world.spawnEntity(cloud);
        }
        if (world.isRemote && animationTick >= 10 && animationTick <= 30) {
            for (int i = 0; i < 2; i++) {
                world.spawnParticle(EnumParticleTypes.SPELL_MOB, posX + (rand.nextDouble() - 0.5D) * width, posY + 0.4D + rand.nextDouble() * 0.5D, posZ + (rand.nextDouble() - 0.5D) * width, 0.47D, 0.84D, 0.05D);
            }
        }
    }

    public void triggerSpraying() {
        if (!world.isRemote && sprayCooldown <= 0 && animation == ANIMATION_NONE) {
            playSound(ACSoundRegistry.GAMMAROACH_SPRAY, 1.0F, getSoundPitch());
            setAnimation(ANIMATION_SPRAY);
            sprayCooldown = 10000 + rand.nextInt(24000);
        }
    }

    private void setAnimation(int animation) {
        this.animation = animation;
        this.animationTick = 0;
        world.setEntityState(this, (byte) (animation == ANIMATION_SPRAY ? 61 : animation == ANIMATION_RAM ? 62 : 60));
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 60 || id == 61 || id == 62) {
            animation = id == 61 ? ANIMATION_SPRAY : id == 62 ? ANIMATION_RAM : ANIMATION_NONE;
            animationTick = 0;
        } else if (id == 49) {
            for (int i = 0; i < 8; i++) {
                world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY + 0.5D, posZ, (rand.nextFloat() - 0.5F) * 0.1F, rand.nextFloat() * 0.15F, (rand.nextFloat() - 0.5F) * 0.1F, net.minecraft.item.Item.getIdFromItem(ACItemRegistry.SPELUNKIE.item()));
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean hurt = super.attackEntityFrom(source, amount);
        Entity attacker = source.getTrueSource();
        if (hurt && attacker instanceof EntityLivingBase && !((EntityLivingBase) attacker).isPotionActive(ACEffectRegistry.IRRADIATED)) {
            triggerSpraying();
        }
        return hurt;
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ACItemRegistry.SPELUNKIE.item() && (!fed || getAttackTarget() == player)) {
            if (!world.isRemote) {
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                fed = true;
                setAttackTarget(null);
                setRevengeTarget(null);
                world.setEntityState(this, (byte) 49);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        return cls != GammaroachEntity.class && super.canAttackClass(cls);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (animation == ANIMATION_SPRAY || animation == ANIMATION_RAM && animationTick < 12) {
            super.travel(0.0F, vertical, 0.0F);
            return;
        }
        super.travel(strafe, vertical, forward);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("SprayCooldown", sprayCooldown);
        compound.setBoolean("Fed", fed);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        sprayCooldown = compound.getInteger("SprayCooldown");
        fed = compound.getBoolean("Fed");
    }

    public int getAnimation() {
        return animation;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    public boolean isFed() {
        return fed;
    }

    @Override
    public boolean canBeHitWithPotion() {
        return false;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return effect.getPotion() != ACEffectRegistry.IRRADIATED && super.isPotionApplicable(effect);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    protected boolean canDespawn() {
        return !fed && super.canDespawn();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GAMMAROACH_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GAMMAROACH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GAMMAROACH_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.block.Block block) {
        if (!isChild()) {
            playSound(ACSoundRegistry.GAMMAROACH_STEP, 0.7F, 1.0F);
        }
    }

    private class GammaroachMeleeAI extends EntityAIBase {
        private int attackCooldown;
        private int checkForMobsTime;
        private EntityMob pickupMonster;

        private GammaroachMeleeAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public void resetTask() {
            if (pickupMonster != null) {
                if (pickupMonster.isRidingSameEntity(GammaroachEntity.this)) {
                    pickupMonster.dismountRidingEntity();
                }
                pickupMonster = null;
            }
            checkForMobsTime = 20;
        }

        @Override
        public void updateTask() {
            checkForMobsTime--;
            EntityLivingBase target = getAttackTarget();
            if (target == null) {
                return;
            }
            if (checkForMobsTime < 0) {
                checkForMobsTime = 120 + rand.nextInt(100);
                List<EntityMob> monsters = world.getEntitiesWithinAABB(EntityMob.class, getEntityBoundingBox().grow(30.0D, 12.0D, 30.0D), mob -> mob instanceof IMob && mob != GammaroachEntity.this && mob.getDistanceSq(target) > 25.0D && !mob.isRiding());
                monsters.sort(Comparator.comparingDouble(GammaroachEntity.this::getDistanceSq));
                pickupMonster = monsters.isEmpty() ? null : monsters.get(0);
            }
            getLookHelper().setLookPositionWithEntity(target, 180.0F, 30.0F);
            if ((pickupMonster == null || pickupMonster.isRidingSameEntity(GammaroachEntity.this)) && animation == ANIMATION_NONE) {
                getNavigator().tryMoveToEntityLiving(target, 1.0D);
            } else if (pickupMonster != null && pickupMonster.isEntityAlive() && !pickupMonster.isRiding()) {
                getNavigator().tryMoveToEntityLiving(pickupMonster, 1.0D);
                getLookHelper().setLookPositionWithEntity(pickupMonster, 180.0F, 30.0F);
                if (getDistanceSq(pickupMonster) < (1.5F + pickupMonster.width) * (1.5F + pickupMonster.width)) {
                    pickupMonster.startRiding(GammaroachEntity.this, true);
                    pickupMonster.attackEntityFrom(DamageSource.CACTUS, 1.0F);
                }
            } else {
                pickupMonster = null;
            }
            if (attackCooldown > 0) {
                attackCooldown--;
            }
            double reach = 2.0D + target.width;
            if (getDistanceSq(target) <= reach * reach && animation == ANIMATION_NONE && attackCooldown <= 0) {
                setAnimation(ANIMATION_RAM);
                attackCooldown = 25;
            }
            if (animation == ANIMATION_RAM && animationTick == 10 && getDistanceSq(target) <= reach * reach + 1.0D) {
                playSound(ACSoundRegistry.GAMMAROACH_ATTACK, 1.0F, getSoundPitch());
                attackEntityAsMob(target);
                if (pickupMonster != null) {
                    pickupMonster.dismountRidingEntity();
                    pickupMonster = null;
                }
            }
        }
    }
}
