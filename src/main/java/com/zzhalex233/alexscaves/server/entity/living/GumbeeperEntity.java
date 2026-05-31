package com.zzhalex233.alexscaves.server.entity.living;

import java.util.EnumSet;

import com.zzhalex233.alexscaves.server.entity.item.GumballEntity;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class GumbeeperEntity extends EntityMob {
    private static final int DEFAULT_GUMBALLS = 6;
    private static final float MAX_DIAL_ROT = 450.0F;
    private static final DataParameter<Boolean> EXPLODING = EntityDataManager.createKey(GumbeeperEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHOOTING = EntityDataManager.createKey(GumbeeperEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> ATTACK_CHARGE = EntityDataManager.createKey(GumbeeperEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> GUMBALLS_LEFT = EntityDataManager.createKey(GumbeeperEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CHARGED = EntityDataManager.createKey(GumbeeperEntity.class, DataSerializers.BOOLEAN);
    private float prevExplodeProgress;
    private float explodeProgress;
    private float prevDialRot;
    private float dialRot;
    private float prevShootProgress;
    private float shootProgress;
    private int postShootTime;
    private boolean hasExploded;

    public GumbeeperEntity(World world) {
        super(world);
        setSize(1.0F, 1.5F);
        experienceValue = 5;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(EXPLODING, false);
        dataManager.register(SHOOTING, false);
        dataManager.register(ATTACK_CHARGE, 0.0F);
        dataManager.register(GUMBALLS_LEFT, DEFAULT_GUMBALLS);
        dataManager.register(CHARGED, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new AttackAI());
        tasks.addTask(2, new EntityAIWander(this, 1.0D, 45));
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 15.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevExplodeProgress = explodeProgress;
        prevDialRot = dialRot;
        prevShootProgress = shootProgress;
        if (isExploding() && explodeProgress < 20.0F) {
            explodeProgress++;
        } else if (!isExploding() && explodeProgress > 0.0F) {
            explodeProgress--;
        }
        if (isShooting() && shootProgress < 5.0F) {
            shootProgress = Math.min(5.0F, shootProgress + 2.5F);
        } else if (!isShooting() && shootProgress > 0.0F) {
            shootProgress = Math.max(0.0F, shootProgress - 1.0F);
        }
        if (getAttackCharge() == 0.0F) {
            dialRot = approach(dialRot, 0.0F, 30.0F);
        } else {
            dialRot = approach(dialRot, MAX_DIAL_ROT * getAttackCharge(), 10.0F);
        }
        if (postShootTime > 0) {
            postShootTime--;
        } else {
            setShooting(false);
        }
        if (isExploding()) {
            tickExplosion();
        }
        if (isCharged() && isEntityAlive() && ticksExisted % 150 == 0) {
            heal(1.0F);
        }
    }

    private float approach(float value, float target, float step) {
        if (value < target) {
            return Math.min(target, value + step);
        }
        return Math.max(target, value - step);
    }

    private void tickExplosion() {
        if (world.isRemote && explodeProgress >= 18.0F) {
            for (int i = 0; i < 3 + rand.nextInt(2); i++) {
                world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX + (rand.nextDouble() - 0.5D) * 0.6D, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * 0.6D, 0.0D, 0.0D, 0.0D);
            }
        }
        if (explodeProgress >= 20.0F) {
            if (!world.isRemote && !hasExploded) {
                int gumballs = isCharged() ? 30 : 15;
                for (int i = 0; i < gumballs + rand.nextInt(5); i++) {
                    GumballEntity gumball = new GumballEntity(world, this);
                    gumball.setPosition(posX + (rand.nextDouble() - 0.5D) * 0.6D, posY + 0.7D + rand.nextFloat() * 0.5D, posZ + (rand.nextDouble() - 0.5D) * 0.6D);
                    Vec3d delta = new Vec3d(rand.nextFloat() - 0.5F, rand.nextFloat() - 0.25F, rand.nextFloat() - 0.5F).normalize().scale(rand.nextFloat() * 0.25F + 0.75F);
                    gumball.motionX = delta.x;
                    gumball.motionY = delta.y;
                    gumball.motionZ = delta.z;
                    gumball.setMaximumBounces(isCharged() ? 10 : 5);
                    gumball.setDamage((float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() + (isCharged() ? 2.0F : 0.0F));
                    world.spawnEntity(gumball);
                }
                hasExploded = true;
                setDead();
            }
            playSound(ACSoundRegistry.GUMBEEPER_EXPLODE, 1.0F, 1.0F);
        }
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == Items.FLINT_AND_STEEL || stack.getItem() == Items.FIRE_CHARGE) {
            playSound(stack.getItem() == Items.FIRE_CHARGE ? SoundEvents.ITEM_FIRECHARGE_USE : SoundEvents.ITEM_FLINTANDSTEEL_USE, 1.0F, rand.nextFloat() * 0.4F + 0.8F);
            if (!world.isRemote) {
                setExploding(true);
                if (!player.capabilities.isCreativeMode) {
                    stack.damageItem(1, player);
                }
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void onStruckByLightning(net.minecraft.entity.effect.EntityLightningBolt lightningBolt) {
        super.onStruckByLightning(lightningBolt);
        setCharged(true);
    }

    public boolean isExploding() {
        return dataManager.get(EXPLODING);
    }

    public void setExploding(boolean explode) {
        dataManager.set(EXPLODING, explode);
    }

    public boolean isShooting() {
        return dataManager.get(SHOOTING);
    }

    public void setShooting(boolean shooting) {
        dataManager.set(SHOOTING, shooting);
    }

    public float getAttackCharge() {
        return dataManager.get(ATTACK_CHARGE);
    }

    public void setAttackCharge(float charge) {
        dataManager.set(ATTACK_CHARGE, charge);
    }

    public int getGumballsLeft() {
        return dataManager.get(GUMBALLS_LEFT);
    }

    public void setGumballsLeft(int count) {
        dataManager.set(GUMBALLS_LEFT, count);
    }

    public boolean isCharged() {
        return dataManager.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        dataManager.set(CHARGED, charged);
    }

    public float getExplodeProgress(float partialTick) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTick) * 0.05F;
    }

    public float getShootProgress(float partialTick) {
        return (prevShootProgress + (shootProgress - prevShootProgress) * partialTick) * 0.2F;
    }

    public double getDialRot(float partialTick) {
        return prevDialRot + (dialRot - prevDialRot) * partialTick;
    }

    public boolean canShootGumball() {
        return getGumballsLeft() > 0 && dialRot >= MAX_DIAL_ROT && getAttackCharge() >= 1.0F;
    }

    public void shootGumball(EntityLivingBase target) {
        Vec3d spawn = new Vec3d(0.0D, 0.3D, 0.4D).rotateYaw(-renderYawOffset * 0.017453292F).add(getPositionVector());
        int shotCount = isCharged() ? 3 : 1;
        playSound(ACSoundRegistry.GUMBALL_LAUNCH, 1.0F, 1.0F);
        for (int i = 0; i < shotCount; i++) {
            GumballEntity gumball = new GumballEntity(world, this);
            gumball.setPosition(spawn.x, spawn.y, spawn.z);
            Vec3d targetVec = target.getPositionEyes(1.0F);
            if (isCharged() && i != shotCount / 2) {
                targetVec = targetVec.add(new Vec3d(i < shotCount / 2 ? 3.0D : -3.0D, 0.0D, 0.0D).rotateYaw(-renderYawOffset * 0.017453292F));
            }
            double dx = targetVec.x - spawn.x;
            double dy = targetVec.y - spawn.y;
            double dz = targetVec.z - spawn.z;
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            gumball.shoot(dx, dy + horizontal * 0.2D, dz, 1.2F, 14 - world.getDifficulty().ordinal() * 4);
            gumball.setMaximumBounces(isCharged() ? 10 : 5);
            gumball.setDamage((float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() + (isCharged() ? 2.0F : 0.0F));
            world.spawnEntity(gumball);
        }
        playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, rand.nextFloat() * 0.4F + 0.8F);
        if (!isCharged() || rand.nextFloat() < 0.33F) {
            setGumballsLeft(getGumballsLeft() - 1);
        }
        setAttackCharge(0.0F);
        setShooting(true);
        postShootTime = 5;
    }

    public boolean hasLineOfSightToGumballHole(Entity entity) {
        Vec3d start = getPositionVector().add(0.0D, 0.3D, 0.0D);
        Vec3d end = entity.getPositionEyes(1.0F);
        RayTraceResult result = world.rayTraceBlocks(start, end, false, true, false);
        return result == null;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Charged", isCharged());
        compound.setInteger("Gumballs", getGumballsLeft());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setCharged(compound.getBoolean("Charged"));
        setGumballsLeft(compound.hasKey("Gumballs") ? compound.getInteger("Gumballs") : DEFAULT_GUMBALLS);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (wasRecentlyHit && getAttackingEntity() instanceof CaniacEntity) {
            dropItem(ACItemRegistry.DISC_FRAGMENT_TASTY.item(), 1);
        }
        int count = Math.max(0, getGumballsLeft() / 2 + rand.nextInt(2 + lootingModifier));
        if (count > 0) {
            entityDropItem(new ItemStack(ACItemRegistry.GUMBALL_PILE.item(), count), 0.0F);
        }
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return super.isPotionApplicable(effect) && effect.getPotion() != Potion.getPotionFromResourceLocation("hunger");
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ACSoundRegistry.GUMBEEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GUMBEEPER_DEATH;
    }

    private class AttackAI extends EntityAIBase {
        private int strafingTime = -1;
        private boolean strafingClockwise;
        private boolean strafingBackwards;

        private AttackAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public void resetTask() {
            strafingTime = -1;
            setAttackCharge(0.0F);
            setExploding(false);
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target == null) {
                return;
            }
            boolean canRange = getGumballsLeft() > 0;
            double dist = getDistance(target);
            if (!canRange) {
                if (dist < target.width + 1.5F) {
                    setExploding(true);
                } else {
                    getNavigator().tryMoveToEntityLiving(target, 1.5D);
                }
            } else if (dist < 16.0D && hasLineOfSightToGumballHole(target)) {
                getNavigator().clearPath();
                strafingTime++;
            } else {
                getNavigator().tryMoveToEntityLiving(target, 1.0D);
                strafingTime = -1;
            }
            if (strafingTime >= 20) {
                if (rand.nextFloat() < 0.3F) {
                    strafingClockwise = !strafingClockwise;
                }
                if (rand.nextFloat() < 0.3F) {
                    strafingBackwards = !strafingBackwards;
                }
                strafingTime = 0;
            }
            if (strafingTime > -1) {
                if (dist > 12.0D) {
                    strafingBackwards = false;
                } else if (dist < 5.0D) {
                    strafingBackwards = true;
                }
                float yaw = rotationYaw * 0.017453292F;
                double forward = strafingBackwards ? -0.08D : 0.08D;
                double side = strafingClockwise ? 0.04D : -0.04D;
                motionX += -MathHelper.sin(yaw) * forward + MathHelper.cos(yaw) * side;
                motionZ += MathHelper.cos(yaw) * forward + MathHelper.sin(yaw) * side;
                faceEntity(target, 30.0F, 30.0F);
            }
            if (canRange && hasLineOfSightToGumballHole(target)) {
                setAttackCharge(Math.min(1.0F, getAttackCharge() + (isCharged() ? 0.3F : 0.1F)));
                if (canShootGumball()) {
                    shootGumball(target);
                }
            }
        }
    }
}
