package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.entity.item.MeltedCaramelEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class CaramelCubeEntity extends EntityMob {
    private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(CaramelCubeEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> WANTS_TO_JUMP = EntityDataManager.createKey(CaramelCubeEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_JUMPED = EntityDataManager.createKey(CaramelCubeEntity.class, DataSerializers.BOOLEAN);
    private float prevSquishProgress;
    private float squishProgress;
    private float prevJumpProgress;
    private float jumpProgress;
    private float prevJiggleTime;
    private float jiggleTime;
    private int jumpDelay;
    private float chosenDegrees;
    private int nextRandomizeTime;
    private int attackCooldown;

    public CaramelCubeEntity(World world) {
        super(world);
        setSize(0.8F, 0.8F);
        experienceValue = 2;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SIZE, 0);
        dataManager.register(WANTS_TO_JUMP, false);
        dataManager.register(HAS_JUMPED, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new JumpMoveAI());
        targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false, entity -> Math.abs(entity.posY - posY) <= 4.0D));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevJumpProgress = jumpProgress;
        prevSquishProgress = squishProgress;
        prevJiggleTime = jiggleTime;
        boolean wasGrounded = onGround;
        super.onLivingUpdate();
        boolean jumping = !onGround && ticksExisted > 4;
        boolean squish = !jumping && (wantsToJump() || hasJumped() && onGround);
        jumpProgress = approach(jumpProgress, jumping ? 3.0F : 0.0F, 1.0F);
        squishProgress = approach(squishProgress, squish ? 5.0F : 0.0F, 1.0F);
        if (squishProgress >= 5.0F) {
            setHasJumped(false);
        }
        if (hasJumped() && onGround && !wasGrounded) {
            jiggleTime = 5.0F;
            playSound(getSquishSound(), getSoundVolume(), getSoundPitch());
            spawnLandParticles();
        } else if (jiggleTime > 0.0F) {
            jiggleTime--;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        }
    }

    private float approach(float value, float target, float step) {
        if (value < target) {
            return Math.min(target, value + step);
        }
        return value > target ? Math.max(target, value - step) : value;
    }

    @Override
    protected void jump() {
        motionY = 0.42D + (getSlimeSize() == 2 ? 0.3D : getSlimeSize() == 1 ? 0.1D : 0.0D);
        isAirBorne = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
    }

    private void spawnLandParticles() {
        int size = 1 + getSlimeSize();
        for (int i = 0; i < size * 6; i++) {
            float angle = rand.nextFloat() * ((float) Math.PI * 2F);
            float spread = rand.nextFloat() * 0.5F + 0.65F;
            float x = MathHelper.sin(angle) * size * 0.5F * spread;
            float z = MathHelper.cos(angle) * size * 0.5F * spread;
            world.spawnParticle(EnumParticleTypes.SLIME, posX + x, posY + 0.15D, posZ + z, 0.0D, 0.0D, 0.0D);
        }
    }

    private void spawnMeltedCaramel() {
        int size = 1 + getSlimeSize();
        for (int i = 0; i < size; i++) {
            float angle = rand.nextFloat() * ((float) Math.PI * 2F);
            float spread = rand.nextFloat() * 0.5F + 0.65F;
            float x = MathHelper.sin(angle) * size * 0.5F * spread;
            float z = MathHelper.cos(angle) * size * 0.5F * spread;
            MeltedCaramelEntity caramel = new MeltedCaramelEntity(world);
            Vec3d ground = MeltedCaramelEntity.getGroundBelowPosition(world, new Vec3d(posX + x, posY + 0.02D, posZ + z));
            caramel.setDespawnsIn(40 + (size - 1) * 40);
            caramel.setPosition(ground.x, ground.y, ground.z);
            caramel.motionX = -motionX;
            caramel.motionZ = -motionZ;
            world.spawnEntity(caramel);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean hurt = super.attackEntityFrom(source, amount);
        if (hurt && !world.isRemote) {
            spawnMeltedCaramel();
        }
        return hurt;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        playSound(ACSoundRegistry.CARAMEL_CUBE_ATTACK, getSoundVolume(), getSoundPitch());
        return super.attackEntityAsMob(entityIn);
    }

    public void setSlimeSize(int size, boolean heal) {
        int clamped = MathHelper.clamp(size, 0, 2);
        if (dataManager != null) {
            dataManager.set(SIZE, clamped);
        }
        float dimension = clamped == 2 ? 3.5F : clamped == 1 ? 1.5F : 0.8F;
        setSize(dimension, dimension);
        if (getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null) {
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D + 6.0D * clamped);
            getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D + 0.1D * clamped);
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D + 2.0D * clamped);
        }
        if (heal) {
            setHealth(getMaxHealth());
        }
    }

    public int getSlimeSize() {
        return Math.min(dataManager.get(SIZE), 2);
    }

    public void setWantsToJump(boolean wantsToJump) {
        dataManager.set(WANTS_TO_JUMP, wantsToJump);
    }

    public boolean wantsToJump() {
        return dataManager.get(WANTS_TO_JUMP);
    }

    public void setHasJumped(boolean hasJumped) {
        dataManager.set(HAS_JUMPED, hasJumped);
    }

    public boolean hasJumped() {
        return dataManager.get(HAS_JUMPED);
    }

    public float getJumpProgress(float partialTick) {
        return (prevJumpProgress + (jumpProgress - prevJumpProgress) * partialTick) * 0.33F;
    }

    public float getSquishProgress(float partialTick) {
        return (prevSquishProgress + (squishProgress - prevSquishProgress) * partialTick) * 0.2F;
    }

    public float getJiggleTime(float partialTick) {
        return (prevJiggleTime + (jiggleTime - prevJiggleTime) * partialTick) * 0.2F;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SIZE.equals(key)) {
            setSlimeSize(getSlimeSize(), false);
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    public void setDead() {
        int size = getSlimeSize();
        if (!world.isRemote && size > 0 && getHealth() <= 0.0F) {
            int childSize = size - 1;
            int count = size >= 2 ? 2 : 2 + rand.nextInt(2);
            for (int i = 0; i < count; i++) {
                float dx = ((float) (i % 2) - 0.5F) * size * 0.25F;
                float dz = ((float) (i / 2) - 0.5F) * size * 0.25F;
                CaramelCubeEntity child = new CaramelCubeEntity(world);
                child.setSlimeSize(childSize, true);
                child.setLocationAndAngles(posX + dx, posY + 0.5D, posZ + dz, rand.nextFloat() * 360.0F, 0.0F);
                if (hasCustomName()) {
                    child.setCustomNameTag(getCustomNameTag());
                }
                child.setNoAI(isAIDisabled());
                world.spawnEntity(child);
            }
        }
        super.setDead();
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (getSlimeSize() == 0) {
            int count = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
            if (count > 0) {
                entityDropItem(new ItemStack(ACItemRegistry.CARAMEL.item(), count), 0.0F);
            }
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        setSlimeSize(rand.nextInt(3), true);
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("SlimeSize", getSlimeSize());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setSlimeSize(compound.getInteger("SlimeSize"), false);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(Math.max(0.0F, distance - 5.0F), damageMultiplier);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return super.isPotionApplicable(effect) && effect.getPotion() != MobEffects.HUNGER;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return getSquishSound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return getSquishSound();
    }

    protected SoundEvent getJumpSound() {
        return getSquishSound();
    }

    protected SoundEvent getSquishSound() {
        return getSlimeSize() == 0 ? ACSoundRegistry.CARAMEL_CUBE_SMALL : ACSoundRegistry.CARAMEL_CUBE_BIG;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block block) {
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        super.travel(strafe, vertical, forward);
    }

    private class JumpMoveAI extends EntityAIBase {
        private JumpMoveAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            return !isRiding();
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !isRiding();
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            boolean aggressive = target != null && target.isEntityAlive();
            if (aggressive) {
                getLookHelper().setLookPositionWithEntity(target, 10.0F, 10.0F);
                chosenDegrees = rotationYaw;
                double dist = getDistance(target);
                if (dist < width + 0.25D + target.width && hasJumped() && onGround && attackCooldown <= 0) {
                    attackCooldown = 5;
                    attackEntityAsMob(target);
                }
            } else if (--nextRandomizeTime <= 0) {
                nextRandomizeTime = 40 + rand.nextInt(60);
                chosenDegrees = rand.nextInt(360);
            }
            rotationYaw = updateRotation(rotationYaw, chosenDegrees, 90.0F);
            renderYawOffset = rotationYaw;
            rotationYawHead = rotationYaw;
            if (onGround) {
                float speed = (float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * (aggressive ? 1.5F : 1.0F);
                if (--jumpDelay <= 0) {
                    setWantsToJump(false);
                    jumpDelay = aggressive ? 6 : 20;
                    jump();
                    setHasJumped(true);
                    playSound(getJumpSound(), getSoundVolume(), getSoundPitch());
                    moveForward = speed;
                } else {
                    setWantsToJump(true);
                    moveForward = 0.0F;
                }
            } else {
                moveForward = (float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
                move(MoverType.SELF, motionX, motionY, motionZ);
            }
        }

        private float updateRotation(float current, float target, float max) {
            float delta = MathHelper.wrapDegrees(target - current);
            if (delta > max) {
                delta = max;
            }
            if (delta < -max) {
                delta = -max;
            }
            return current + delta;
        }
    }
}
