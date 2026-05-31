package com.zzhalex233.alexscaves.server.entity.living;

import java.util.UUID;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.item.MineGuardianAnchorEntity;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class MineGuardianEntity extends EntityMob {
    private static final DataParameter<Integer> ANCHOR_ID = EntityDataManager.createKey(MineGuardianEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MAX_CHAIN_LENGTH = EntityDataManager.createKey(MineGuardianEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> EXPLODING = EntityDataManager.createKey(MineGuardianEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> EYE_CLOSED = EntityDataManager.createKey(MineGuardianEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SCANNING = EntityDataManager.createKey(MineGuardianEntity.class, DataSerializers.BOOLEAN);
    private UUID anchorUUID;
    private float prevExplodeProgress;
    private float explodeProgress;
    private float prevScanProgress;
    private float scanProgress;
    private int scanTime;
    private int maxScanTime;
    private int maxSleepTime = 200 + rand.nextInt(100);
    private int lastScanTime;
    private int timeSinceHadTarget;
    private boolean hasExploded;

    public MineGuardianEntity(World world) {
        super(world);
        setSize(1.5F, 1.5F);
        experienceValue = 6;
        isImmuneToFire = false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(ANCHOR_ID, -1);
        dataManager.register(MAX_CHAIN_LENGTH, 8);
        dataManager.register(EXPLODING, false);
        dataManager.register(EYE_CLOSED, false);
        dataManager.register(SCANNING, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new MineGuardianAttackAI());
        tasks.addTask(2, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevExplodeProgress = explodeProgress;
        prevScanProgress = scanProgress;
        if (isScanning() && scanProgress < 5.0F) {
            scanProgress++;
        } else if (!isScanning() && scanProgress > 0.0F) {
            scanProgress--;
        }
        if (isExploding() && explodeProgress < 10.0F) {
            explodeProgress += 0.5F;
        } else if (!isExploding() && explodeProgress > 0.0F) {
            explodeProgress -= 0.5F;
        }
        if (isExploding()) {
            tickExplosion();
        }
        if (!world.isRemote) {
            tickAnchor();
            tickAirAndFlop();
            tickScanning();
        }
    }

    private void tickAnchor() {
        Entity anchor = getAnchor();
        if (anchor == null) {
            setMaxChainLength(7 + rand.nextInt(6));
            MineGuardianAnchorEntity created = new MineGuardianAnchorEntity(this);
            world.spawnEntity(created);
            setAnchor(created);
        } else if (anchor instanceof MineGuardianAnchorEntity) {
            ((MineGuardianAnchorEntity) anchor).linkWithGuardian(this);
        }
    }

    private void tickAirAndFlop() {
        if (isInWater()) {
            setAir(300);
        } else if (onGround) {
            motionX += (rand.nextFloat() * 2.0F - 1.0F) * 0.6F;
            motionY += 0.6D;
            motionZ += (rand.nextFloat() * 2.0F - 1.0F) * 0.6F;
            rotationYaw = rand.nextFloat() * 360.0F;
            onGround = false;
            playSound(ACSoundRegistry.MINE_GUARDIAN_FLOP, getSoundVolume(), getSoundPitch());
            velocityChanged = true;
        }
    }

    private void tickScanning() {
        EntityLivingBase target = getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            timeSinceHadTarget++;
        } else {
            timeSinceHadTarget = 0;
        }
        if (isScanning()) {
            setEyeClosed(false);
            if (scanTime < maxScanTime) {
                if (scanTime % 20 == 0) {
                    playSound(ACSoundRegistry.MINE_GUARDIAN_SCAN, 1.0F, 1.0F);
                }
                if (scanTime % 5 == 0 && scanProgress >= 5.0F) {
                    EntityLivingBase found = findScanTarget();
                    if (found != null) {
                        setAttackTarget(found);
                        setScanning(false);
                    }
                }
                scanTime++;
            } else {
                scanTime = 0;
                lastScanTime = ticksExisted;
                setScanning(false);
            }
        } else if (isEyeClosed()) {
            int sinceScan = ticksExisted - lastScanTime;
            if (timeSinceHadTarget == 0 || !isInWater()) {
                setEyeClosed(false);
            } else if (isInWater() && (timeSinceHadTarget > maxSleepTime && sinceScan > 200 || hurtTime > 0)) {
                maxSleepTime = 200 + rand.nextInt(100);
                setScanning(true);
                scanTime = 0;
                maxScanTime = 100 + rand.nextInt(100);
            }
        } else if (isInWater() && timeSinceHadTarget > 100) {
            setEyeClosed(true);
        }
    }

    private EntityLivingBase findScanTarget() {
        Vec3d start = getPositionEyes(1.0F);
        Vec3d look = getLookVec();
        Vec3d end = start.add(look.scale(8.0D));
        RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);
        Vec3d center = hit == null ? end : hit.hitVec;
        AxisAlignedBB box = new AxisAlignedBB(center.x - 3.0D, center.y - 3.0D, center.z - 3.0D, center.x + 3.0D, center.y + 3.0D, center.z + 3.0D);
        EntityLivingBase nearest = null;
        for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, box, this::canAttackPlayer)) {
            if (canEntityBeSeen(player) && (nearest == null || getDistance(player) < getDistance(nearest))) {
                nearest = player;
            }
        }
        return nearest;
    }

    private boolean canAttackPlayer(EntityPlayer player) {
        return player != null && player.isEntityAlive() && !player.capabilities.disableDamage && world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    private void tickExplosion() {
        if (explodeProgress >= 10.0F && !hasExploded) {
            hasExploded = true;
            if (!world.isRemote) {
                playSound(isInWater() ? ACSoundRegistry.MINE_GUARDIAN_EXPLODE : ACSoundRegistry.MINE_GUARDIAN_LAND_EXPLODE, 1.3F, 1.0F);
                world.createExplosion(this, posX, posY + height * 0.5D, posZ, 5.0F, world.getGameRules().getBoolean("mobGriefing"));
                setDead();
            }
        } else if (world.isRemote && explodeProgress >= 8.0F) {
            world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, 0.0D, 0.0D);
        }
        motionX *= 0.3D;
        motionZ *= 0.3D;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (isInWater()) {
            moveRelative(strafe, vertical, forward, 0.04F);
            move(MoverType.SELF, motionX, motionY, motionZ);
            motionX *= 0.9D;
            motionY *= 0.9D;
            motionZ *= 0.9D;
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    public boolean getCanSpawnHere() {
        BlockPos pos = getPosition();
        IBlockState below = world.getBlockState(pos.down());
        return isInWater() && world.getDifficulty() != EnumDifficulty.PEACEFUL && (below.getBlock() == ACBlockRegistry.MUCK.block() || below.getBlock() == ACBlockRegistry.ABYSSMARINE.block() || below.getBlock() == Blocks.STONE) && super.getCanSpawnHere();
    }

    public Entity getAnchor() {
        if (world.isRemote) {
            int id = dataManager.get(ANCHOR_ID);
            return id == -1 ? null : world.getEntityByID(id);
        }
        if (anchorUUID == null) {
            return null;
        }
        for (Entity entity : world.loadedEntityList) {
            if (anchorUUID.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
    }

    public void setAnchor(Entity entity) {
        anchorUUID = entity == null ? null : entity.getUniqueID();
        dataManager.set(ANCHOR_ID, entity == null ? -1 : entity.getEntityId());
    }

    public boolean isExploding() {
        return dataManager.get(EXPLODING);
    }

    public void setExploding(boolean exploding) {
        dataManager.set(EXPLODING, exploding);
    }

    public boolean isEyeClosed() {
        return dataManager.get(EYE_CLOSED);
    }

    public void setEyeClosed(boolean closed) {
        dataManager.set(EYE_CLOSED, closed);
    }

    public boolean isScanning() {
        return dataManager.get(SCANNING);
    }

    public void setScanning(boolean scanning) {
        dataManager.set(SCANNING, scanning);
    }

    public int getMaxChainLength() {
        return dataManager.get(MAX_CHAIN_LENGTH);
    }

    public void setMaxChainLength(int length) {
        dataManager.set(MAX_CHAIN_LENGTH, length);
    }

    public float getScanProgress(float partialTick) {
        return (prevScanProgress + (scanProgress - prevScanProgress) * partialTick) * 0.2F;
    }

    public float getExplodeProgress(float partialTick) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTick) * 0.1F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (anchorUUID != null) {
            compound.setUniqueId("AnchorUUID", anchorUUID);
        }
        compound.setInteger("MaxChainLength", getMaxChainLength());
        compound.setBoolean("EyeClosed", isEyeClosed());
        compound.setInteger("ScanTime", scanTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("AnchorUUID")) {
            anchorUUID = compound.getUniqueId("AnchorUUID");
        }
        setMaxChainLength(compound.getInteger("MaxChainLength"));
        setEyeClosed(compound.getBoolean("EyeClosed"));
        scanTime = compound.getInteger("ScanTime");
    }

    @Override
    public net.minecraft.entity.IEntityLivingData onInitialSpawn(net.minecraft.world.DifficultyInstance difficulty, net.minecraft.entity.IEntityLivingData livingdata) {
        net.minecraft.entity.IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        setEyeClosed(true);
        timeSinceHadTarget = 10;
        return data;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 2;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isInWater() ? ACSoundRegistry.MINE_GUARDIAN_IDLE : ACSoundRegistry.MINE_GUARDIAN_LAND_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return isInWater() ? ACSoundRegistry.MINE_GUARDIAN_HURT : ACSoundRegistry.MINE_GUARDIAN_LAND_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isInWater() ? ACSoundRegistry.MINE_GUARDIAN_DEATH : ACSoundRegistry.MINE_GUARDIAN_LAND_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_GUARDIAN_FLOP;
    }

    private class MineGuardianAttackAI extends EntityAIBase {
        private int timer;

        private MineGuardianAttackAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive();
        }

        @Override
        public void startExecuting() {
            timer = 0;
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target == null) {
                return;
            }
            timer++;
            faceEntity(target, 30.0F, 30.0F);
            getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
            double dist = getDistance(target);
            if (dist > 2.0F) {
                if (isInWater()) {
                    getNavigator().tryMoveToEntityLiving(target, 1.6D);
                    Vec3d pull = target.getPositionVector().add(new Vec3d(0.0D, target.height * 0.5D, 0.0D)).subtract(getPositionVector());
                    if (pull.length() > 1.0D) {
                        pull = pull.normalize();
                    }
                    motionX += pull.x * 0.05D;
                    motionY += pull.y * 0.04D;
                    motionZ += pull.z * 0.05D;
                }
            } else {
                setExploding(true);
            }
            if (timer > 300) {
                lastScanTime = ticksExisted;
                timeSinceHadTarget = 5;
                setEyeClosed(true);
                setAttackTarget(null);
            }
        }
    }
}
