package com.zzhalex233.alexscaves.server.entity.living;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class NotorEntity extends EntityLiving {
    private static final DataParameter<Boolean> SHOWING_HOLOGRAM = EntityDataManager.createKey(NotorEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> SCANNING_ID = EntityDataManager.createKey(NotorEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HOLOGRAM_ENTITY_ID = EntityDataManager.createKey(NotorEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HOLOGRAM_X = EntityDataManager.createKey(NotorEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HOLOGRAM_Y = EntityDataManager.createKey(NotorEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HOLOGRAM_Z = EntityDataManager.createKey(NotorEntity.class, DataSerializers.VARINT);
    private float prevGroundProgress;
    private float groundProgress;
    private float prevBeamProgress;
    private float beamProgress;
    private float prevHologramProgress;
    private float hologramProgress;
    private float prevPropellerRot;
    private float propellerRot;
    private UUID hologramUUID;
    public int stopScanningFor = 80;
    private int flyingSoundTimer;

    public NotorEntity(World world) {
        super(world);
        moveHelper = new FlightMoveHelper(this);
        setSize(0.8F, 0.8F);
        experienceValue = 4;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SHOWING_HOLOGRAM, false);
        dataManager.register(SCANNING_ID, -1);
        dataManager.register(HOLOGRAM_ENTITY_ID, -1);
        dataManager.register(HOLOGRAM_X, 0);
        dataManager.register(HOLOGRAM_Y, -1);
        dataManager.register(HOLOGRAM_Z, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new HologramAI());
        tasks.addTask(1, new ScanAI());
        tasks.addTask(2, new FlightAI());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevGroundProgress = groundProgress;
        prevBeamProgress = beamProgress;
        prevHologramProgress = hologramProgress;
        prevPropellerRot = propellerRot;
        super.onLivingUpdate();
        Entity hologram = getHologramEntity();
        if (onGround && groundProgress < 5.0F) {
            groundProgress++;
        } else if (!onGround && groundProgress > 0.0F) {
            groundProgress--;
        }
        Entity scanning = getScanningMob();
        boolean hasHologram = hologram != null && showingHologram();
        boolean hasBeam = scanning != null || hasHologram;
        beamProgress = approach(beamProgress, hasBeam ? 5.0F : 0.0F, 1.0F);
        hologramProgress = approach(hologramProgress, hasHologram && beamProgress >= 5.0F ? 5.0F : 0.0F, 1.0F);
        if (!onGround) {
            motionX *= 0.9D;
            motionY *= 0.9D;
            motionZ *= 0.9D;
            propellerRot += Math.max(Math.sqrt(motionX * motionX + motionZ * motionZ) * 10.0D, 3.0D) * 20.0F;
            if (!world.isRemote && flyingSoundTimer++ >= 10) {
                playSound(ACSoundRegistry.NOTOR_FLYING, 0.4F, 1.0F);
                flyingSoundTimer = 0;
            }
        } else {
            flyingSoundTimer = 0;
            propellerRot = approachDegrees(propellerRot, 0.0F, 15.0F);
        }
        if (!world.isRemote) {
            dataManager.set(HOLOGRAM_ENTITY_ID, hologram == null ? -1 : hologram.getEntityId());
            if (stopScanningFor > 0) {
                stopScanningFor--;
            }
        }
    }

    private float approach(float value, float target, float step) {
        if (value < target) {
            return Math.min(target, value + step);
        }
        return value > target ? Math.max(target, value - step) : value;
    }

    private float approachDegrees(float value, float target, float step) {
        float delta = MathHelper.wrapDegrees(target - value);
        return value + MathHelper.clamp(delta, -step, step);
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, net.minecraft.block.state.IBlockState state, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.block.Block blockIn) {
    }

    public float getGroundProgress(float partialTick) {
        return (prevGroundProgress + (groundProgress - prevGroundProgress) * partialTick) * 0.2F;
    }

    public float getBeamProgress(float partialTick) {
        return (prevBeamProgress + (beamProgress - prevBeamProgress) * partialTick) * 0.2F;
    }

    public float getHologramProgress(float partialTick) {
        return (prevHologramProgress + (hologramProgress - prevHologramProgress) * partialTick) * 0.2F;
    }

    public float getPropellerAngle(float partialTick) {
        return prevPropellerRot + (propellerRot - prevPropellerRot) * partialTick;
    }

    public void setScanningId(int id) {
        dataManager.set(SCANNING_ID, id);
    }

    public int getScanningId() {
        return dataManager.get(SCANNING_ID);
    }

    public Entity getScanningMob() {
        return getScanningId() == -1 ? null : world.getEntityByID(getScanningId());
    }

    public boolean showingHologram() {
        return dataManager.get(SHOWING_HOLOGRAM);
    }

    public void setShowingHologram(boolean showingHologram) {
        dataManager.set(SHOWING_HOLOGRAM, showingHologram);
    }

    public void setHologramPos(BlockPos pos) {
        dataManager.set(HOLOGRAM_Y, pos == null ? -1 : pos.getY());
        if (pos != null) {
            dataManager.set(HOLOGRAM_X, pos.getX());
            dataManager.set(HOLOGRAM_Z, pos.getZ());
        }
    }

    public BlockPos getHologramPos() {
        int y = dataManager.get(HOLOGRAM_Y);
        return y < 0 ? null : new BlockPos(dataManager.get(HOLOGRAM_X), y, dataManager.get(HOLOGRAM_Z));
    }

    public void setHologramEntity(Entity entity) {
        hologramUUID = entity == null ? null : entity.getUniqueID();
        dataManager.set(HOLOGRAM_ENTITY_ID, entity == null ? -1 : entity.getEntityId());
    }

    public Entity getHologramEntity() {
        if (world.isRemote) {
            int id = dataManager.get(HOLOGRAM_ENTITY_ID);
            return id == -1 ? null : world.getEntityByID(id);
        }
        if (hologramUUID == null) {
            return null;
        }
        for (Entity entity : world.loadedEntityList) {
            if (hologramUUID.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
    }

    public Vec3d getBeamEndPosition(float partialTicks) {
        Entity scanning = getScanningMob();
        if (scanning != null) {
            float f = (float) Math.abs(Math.sin((ticksExisted + partialTicks) * 0.1F));
            return scanning.getPositionVector().add(0.0D, scanning.height * f, 0.0D);
        }
        BlockPos pos = getHologramPos();
        return pos == null ? getPositionVector().add(0.0D, -3.0D, 0.0D) : new Vec3d(pos).add(0.5D, 0.5D, 0.5D);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.NOTOR_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ACSoundRegistry.NOTOR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.NOTOR_DEATH;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int count = rand.nextInt(2) + (lootingModifier > 0 && rand.nextInt(3) < lootingModifier ? 1 : 0);
        if (count > 0) {
            entityDropItem(new ItemStack(ACItemRegistry.NOTOR_GIZMO.item(), count), 0.0F);
        }
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return super.isPotionApplicable(effect) && effect.getPotion() != ACEffectRegistry.MAGNETIZING;
    }

    @Override
    public boolean canDespawn() {
        return !hasCustomName();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("StopScanningTime", stopScanningFor);
        compound.setBoolean("ShowingHologram", showingHologram());
        if (hologramUUID != null) {
            compound.setUniqueId("HologramUUID", hologramUUID);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        stopScanningFor = compound.getInteger("StopScanningTime");
        setShowingHologram(compound.getBoolean("ShowingHologram"));
        if (compound.hasUniqueId("HologramUUID")) {
            hologramUUID = compound.getUniqueId("HologramUUID");
        }
    }

    private boolean isScanTarget(EntityLivingBase entity) {
        return entity.isEntityAlive() && !entity.isInvisible() && !(entity instanceof NotorEntity);
    }

    private void lookAt(Entity entity) {
        getLookHelper().setLookPositionWithEntity(entity, 180.0F, 90.0F);
        double x = entity.posX - posX;
        double z = entity.posZ - posZ;
        rotationYaw = -((float) MathHelper.atan2(x, z)) * 180.0F / (float) Math.PI;
        renderYawOffset = rotationYaw;
    }

    private Vec3d groundPosition(Vec3d airPosition) {
        BlockPos ground = new BlockPos(airPosition);
        while (ground.getY() > 1 && world.getBlockState(ground).getMaterial() == Material.AIR) {
            ground = ground.down();
        }
        return new Vec3d(ground.getX() + 0.5D, ground.getY() + 1.0D, ground.getZ() + 0.5D);
    }

    private Vec3d findFlightPos() {
        float maxRot = collidedHorizontally || collidedVertically ? 360.0F : 40.0F;
        float xRot = (rand.nextFloat() - 0.5F) * maxRot * 0.25F;
        float yRot = (rand.nextFloat() - 0.5F) * maxRot;
        Vec3d look = getLookVec().scale(6 + rand.nextInt(6)).rotatePitch((float) Math.toRadians(xRot)).rotateYaw((float) Math.toRadians(yRot));
        Vec3d target = getPositionVector().add(look);
        Vec3d ground = groundPosition(target);
        Vec3d heightAdjusted = new Vec3d(target.x, ground.y + 3.0D + rand.nextInt(4), target.z);
        RayTraceResult result = world.rayTraceBlocks(getPositionEyes(1.0F), heightAdjusted, false, true, false);
        return result == null ? heightAdjusted : result.hitVec;
    }

    private class FlightMoveHelper extends EntityMoveHelper {
        private final NotorEntity notor;

        private FlightMoveHelper(NotorEntity notor) {
            super(notor);
            this.notor = notor;
        }

        @Override
        public void onUpdateMoveHelper() {
            boolean gravity = true;
            if (notor.getScanningId() != -1 || notor.getHologramEntity() != null) {
                gravity = false;
                float angle = (notor.renderYawOffset + 90.0F) * 0.017453292F;
                float radius = (float) Math.sin(notor.ticksExisted * 0.2F) * 2.0F;
                notor.motionX += MathHelper.sin((float) Math.PI + angle) * radius * 0.01D;
                notor.motionZ += MathHelper.cos(angle) * radius * 0.01D;
            }
            if (action == EntityMoveHelper.Action.MOVE_TO) {
                double dx = posX - notor.posX;
                double dy = posY - notor.posY;
                double dz = posZ - notor.posZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist < notor.width) {
                    action = EntityMoveHelper.Action.WAIT;
                    notor.motionX *= 0.5D;
                    notor.motionY *= 0.5D;
                    notor.motionZ *= 0.5D;
                } else {
                    gravity = false;
                    notor.motionX += dx / dist * speed * 0.05D;
                    notor.motionY += dy / dist * speed * 0.05D;
                    notor.motionZ += dz / dist * speed * 0.05D;
                    notor.rotationYaw = -((float) MathHelper.atan2(notor.motionX, notor.motionZ)) * 180.0F / (float) Math.PI;
                    notor.renderYawOffset = notor.rotationYaw;
                }
            }
            notor.setNoGravity(!gravity);
        }
    }

    private class FlightAI extends EntityAIBase {
        private double x;
        private double y;
        private double z;

        private FlightAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (isRiding() || onGround && rand.nextInt(45) != 0) {
                return false;
            }
            Vec3d target = findFlightPos();
            x = target.x;
            y = target.y;
            z = target.z;
            return true;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !onGround && getDistanceSq(x, y, z) > 5.0D;
        }

        @Override
        public void startExecuting() {
            moveHelper.setMoveTo(x, y, z, 1.0D);
        }

        @Override
        public void updateTask() {
            if (collidedHorizontally || collidedVertically && !onGround || getDistanceSq(x, y, z) < 3.0D) {
                Vec3d target = findFlightPos();
                x = target.x;
                y = target.y;
                z = target.z;
            }
            moveHelper.setMoveTo(x, y, z, 1.0D);
        }

        @Override
        public void resetTask() {
            getNavigator().clearPath();
        }
    }

    private class ScanAI extends EntityAIBase {
        private EntityLivingBase scanTarget;
        private int scanTime;

        private ScanAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            if (rand.nextInt(300) != 0 && world.getTotalWorldTime() % 10L != 0L || hologramUUID != null || stopScanningFor > 0) {
                return false;
            }
            List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(25.0D), NotorEntity.this::isScanTarget);
            scanTarget = null;
            for (EntityLivingBase entity : list) {
                if ((scanTarget == null || getDistanceSq(entity) < getDistanceSq(scanTarget) || !(scanTarget instanceof EntityPlayer) && entity instanceof EntityPlayer) && canEntityBeSeen(entity)) {
                    scanTarget = entity;
                }
            }
            return scanTarget != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return scanTarget != null && scanTarget.isEntityAlive() && canEntityBeSeen(scanTarget) && getDistance(scanTarget) <= 40.0F && scanTime < maxScanTime() && hologramUUID == null;
        }

        @Override
        public void startExecuting() {
            getNavigator().clearPath();
            scanTime = 0;
            setScanningId(-1);
        }

        @Override
        public void updateTask() {
            double dist = getDistance(scanTarget);
            lookAt(scanTarget);
            if (dist > 8.0D) {
                moveHelper.setMoveTo(scanTarget.posX, scanTarget.posY + scanTarget.height + 1.0D, scanTarget.posZ, 1.2D);
                if (dist > 15.0D) {
                    setScanningId(-1);
                }
            } else {
                getNavigator().clearPath();
                setScanningId(scanTarget.getEntityId());
                scanTime++;
            }
        }

        @Override
        public void resetTask() {
            if (scanTime >= maxScanTime() && scanTarget != null && scanTarget.isEntityAlive()) {
                setHologramEntity(scanTarget);
                setShowingHologram(false);
                stopScanningFor = rand.nextInt(300) + 300;
                playSound(ACSoundRegistry.HOLOGRAM_STOP, 0.7F, 1.0F);
            }
            setScanningId(-1);
        }

        private int maxScanTime() {
            return world.getDifficulty() == EnumDifficulty.PEACEFUL ? 40 : 100;
        }
    }

    private class HologramAI extends EntityAIBase {
        private EntityMob monster;
        private Vec3d moveTarget;
        private int checkForMonsterTime;
        private int hologramTime;

        private HologramAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            return hologramUUID != null && getHologramEntity() != null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return shouldExecute() && (monster == null || monster.isEntityAlive() && monster.getDistance(NotorEntity.this) < 40.0F);
        }

        @Override
        public void startExecuting() {
            checkForMonsterTime = 0;
            hologramTime = 0;
            monster = null;
            moveTarget = null;
        }

        @Override
        public void updateTask() {
            Entity hologram = getHologramEntity();
            if (hologram == null) {
                return;
            }
            findMonster();
            if (monster == null || !monster.isEntityAlive() || !hasNoTarget(monster)) {
                if (moveTarget == null || moveTarget.squareDistanceTo(getPositionVector()) < 16.0D) {
                    moveTarget = randomAwayFrom(hologram.getPositionVector());
                }
                if (moveTarget != null) {
                    moveHelper.setMoveTo(moveTarget.x, moveTarget.y, moveTarget.z, 1.2D);
                }
                setShowingHologram(false);
                return;
            }
            double dist = monster.getDistance(NotorEntity.this);
            if (hologramTime < 100) {
                if (dist < 8.0D && canEntityBeSeen(monster)) {
                    getNavigator().clearPath();
                    if (getHologramPos() == null) {
                        setHologramPos(findHologramPos(monster, hologram.height));
                    }
                    BlockPos pos = getHologramPos();
                    Vec3d stareAt = pos == null ? getPositionEyes(1.0F) : new Vec3d(pos).add(0.5D, 0.5D, 0.5D);
                    getLookHelper().setLookPosition(stareAt.x, stareAt.y, stareAt.z, 180.0F, 90.0F);
                    monster.getLookHelper().setLookPosition(stareAt.x, stareAt.y, stareAt.z, 180.0F, 90.0F);
                    monster.getNavigator().clearPath();
                    setShowingHologram(true);
                    hologramTime++;
                } else {
                    moveHelper.setMoveTo(monster.posX, monster.posY + monster.height + 1.0D, monster.posZ, 1.2D);
                }
            } else {
                setShowingHologram(false);
                if (hologram instanceof EntityPlayer && !((EntityPlayer) hologram).isCreative()) {
                    monster.setAttackTarget((EntityLivingBase) hologram);
                    monster.getNavigator().tryMoveToEntityLiving((EntityLivingBase) hologram, 1.2D);
                    if (monster.getDistance(hologram) < Math.min(monster.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue() - 15.0D, 10.0D)) {
                        setHologramEntity(null);
                    }
                } else {
                    setHologramEntity(null);
                }
            }
        }

        @Override
        public void resetTask() {
            setScanningId(-1);
            setHologramPos(null);
            setShowingHologram(false);
            setHologramEntity(null);
            stopScanningFor += rand.nextInt(100) + 100;
            monster = null;
        }

        private void findMonster() {
            if (checkForMonsterTime-- > 0 && monster != null && monster.isEntityAlive()) {
                return;
            }
            checkForMonsterTime = 20 + rand.nextInt(10);
            AxisAlignedBB box = getEntityBoundingBox().grow(30.0D, 12.0D, 30.0D);
            List<EntityMob> list = world.getEntitiesWithinAABB(EntityMob.class, box, mob -> mob instanceof IMob && mob.getDistance(NotorEntity.this) > 5.0F && !mob.isRiding() && hasNoTarget(mob));
            list.sort(Comparator.comparingDouble(NotorEntity.this::getDistanceSq));
            monster = list.isEmpty() ? null : list.get(0);
        }

        private boolean hasNoTarget(Entity entity) {
            return !(entity instanceof EntityMob) || ((EntityMob) entity).getAttackTarget() == null || !((EntityMob) entity).getAttackTarget().isEntityAlive();
        }

        private Vec3d randomAwayFrom(Vec3d from) {
            for (int i = 0; i < 10; i++) {
                Vec3d vec = getPositionVector().subtract(from).normalize().scale(12.0D + rand.nextInt(16)).rotateYaw((rand.nextFloat() - 0.5F) * 1.4F);
                Vec3d target = getPositionVector().add(vec.x, rand.nextInt(9) - 2.0D, vec.z);
                RayTraceResult ray = world.rayTraceBlocks(getPositionEyes(1.0F), target, false, true, false);
                if (ray == null) {
                    return target;
                }
            }
            return findFlightPos();
        }

        private BlockPos findHologramPos(EntityLivingBase monster, float hologramHeight) {
            BlockPos set = monster.getPosition();
            for (int i = 0; i < 15; i++) {
                BlockPos pos = monster.getPosition().add(rand.nextInt(10) - 5, (int) (monster.height + 3.0F), rand.nextInt(10) - 5);
                while (world.isAirBlock(pos) && pos.getY() > 1) {
                    pos = pos.down();
                }
                pos = pos.up((int) Math.max(1.0F, hologramHeight));
                Vec3d vec = new Vec3d(pos).add(0.5D, 0.5D, 0.5D);
                if (world.rayTraceBlocks(monster.getPositionEyes(1.0F), vec, false, true, false) == null && world.rayTraceBlocks(getPositionEyes(1.0F), vec, false, true, false) == null) {
                    set = pos;
                    break;
                }
            }
            return set;
        }
    }
}
