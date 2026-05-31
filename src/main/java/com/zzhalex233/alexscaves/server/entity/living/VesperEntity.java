package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.entity.util.UnderzealotSacrifice;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VesperEntity extends EntityMob implements UnderzealotSacrifice {
    public static final int ANIMATION_NONE = 0;
    public static final int ANIMATION_BITE = 1;
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(VesperEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HANGING = EntityDataManager.createKey(VesperEntity.class, DataSerializers.BOOLEAN);

    private int animation = ANIMATION_NONE;
    private int animationTick;
    private float prevFlyProgress;
    private float flyProgress;
    private float prevSleepProgress;
    private float sleepProgress;
    private float prevGroundProgress = 5.0F;
    private float groundProgress = 5.0F;
    private float prevFlightPitch;
    private float flightPitch;
    private float prevFlightRoll;
    private float flightRoll;
    private int timeFlying;
    private int timeHanging;
    private int groundedFor;
    private int lastTargetId = -1;
    private int flapSoundTime;
    private boolean isBeingSacrificed;
    private int sacrificeTime;

    public VesperEntity(World world) {
        super(world);
        setSize(0.9F, 1.25F);
        stepHeight = 1.0F;
        experienceValue = 5;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FLYING, false);
        dataManager.register(HANGING, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new VesperAttackAI());
        tasks.addTask(2, new FlyAndHangAI());
        tasks.addTask(3, new GroundWanderAI());
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new VesperTargetAI(EntityPlayer.class, 20));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(52.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevFlyProgress = flyProgress;
        prevSleepProgress = sleepProgress;
        prevGroundProgress = groundProgress;
        prevFlightPitch = flightPitch;
        prevFlightRoll = flightRoll;
        flyProgress = approach(flyProgress, isFlying() ? 5.0F : 0.0F);
        sleepProgress = approach(sleepProgress, isHanging() ? 5.0F : 0.0F);
        groundProgress = approach(groundProgress, onGround ? 5.0F : 0.0F);
        super.onLivingUpdate();
        if (animation != ANIMATION_NONE && ++animationTick >= 15) {
            setAnimation(ANIMATION_NONE);
        }
        updateFlightState();
        tickRotation((float) (motionY * -114.59156D));
        if (groundedFor > 0) {
            groundedFor--;
        }
        EntityLivingBase target = getAttackTarget();
        if (!world.isRemote && target != null && target.isEntityAlive() && target.getEntityId() != lastTargetId) {
            lastTargetId = target.getEntityId();
            playSound(ACSoundRegistry.VESPER_SCREAM, 3.0F, 1.0F);
        } else if (target == null || !target.isEntityAlive()) {
            lastTargetId = -1;
        }
        if (isBeingSacrificed && isRiding() && !world.isRemote) {
            if (--sacrificeTime < 10) {
                world.setEntityState(this, (byte) 61);
            }
            if (sacrificeTime < 0) {
                if (getRidingEntity() instanceof UnderzealotEntity) {
                    UnderzealotEntity underzealot = (UnderzealotEntity) getRidingEntity();
                    underzealot.postSacrifice(this);
                    underzealot.triggerIdleDigging();
                }
                dismountRidingEntity();
                ForsakenEntity forsaken = new ForsakenEntity(world);
                forsaken.copyLocationAndAnglesFrom(this);
                forsaken.setAnimation(ForsakenEntity.ANIMATION_SUMMON);
                world.spawnEntity(forsaken);
                playSound(ACSoundRegistry.FORSAKEN_SPAWN, 8.0F, 1.0F);
                setDead();
            }
        }
    }

    private void updateFlightState() {
        if (isHanging()) {
            setFlying(false);
            if (canHangFrom(posAbove())) {
                motionX *= 0.1D;
                motionY = Math.min(0.08D, motionY * 0.3D + 0.08D);
                motionZ *= 0.1D;
            } else if (!world.isRemote) {
                setHanging(false);
                setFlying(true);
            }
            timeHanging++;
        } else {
            timeHanging = 0;
        }
        if (isFlying()) {
            setNoGravity(true);
            timeFlying++;
            if (!world.isRemote && ++flapSoundTime >= 10) {
                flapSoundTime = 0;
                playSound(ACSoundRegistry.VESPER_FLAP, 0.7F, getSoundPitch());
            }
            if (!world.isRemote && groundedFor > 0) {
                setFlying(false);
            }
        } else {
            setNoGravity(false);
            timeFlying = 0;
            flapSoundTime = 0;
        }
    }

    private float approach(float value, float target) {
        if (value < target) {
            return Math.min(target, value + 1.0F);
        }
        return value > target ? Math.max(target, value - 1.0F) : value;
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

    private void flyToward(double x, double y, double z, double speed) {
        setFlying(true);
        setHanging(false);
        getNavigator().clearPath();
        Vec3d delta = new Vec3d(x - posX, y - posY, z - posZ);
        double length = Math.sqrt(delta.x * delta.x + delta.y * delta.y + delta.z * delta.z);
        if (length > 0.001D) {
            Vec3d add = delta.scale(speed * 0.05D / length);
            motionX = (motionX + add.x) * 0.95D;
            motionY = (motionY + add.y) * 0.95D - 0.01D;
            motionZ = (motionZ + add.z) * 0.95D;
            rotationYaw = approachAngle(rotationYaw, (float) (-Math.atan2(add.x, add.z) * 180.0D / Math.PI), 8.0F);
            renderYawOffset = rotationYaw;
        }
    }

    private float approachAngle(float value, float target, float step) {
        return value + MathHelper.clamp(MathHelper.wrapDegrees(target - value), -step, step);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (isFlying() || isHanging()) {
            moveRelative(strafe, vertical, forward, 0.02F);
            move(MoverType.SELF, motionX, motionY, motionZ);
            motionX *= 0.91D;
            motionY *= 0.91D;
            motionZ *= 0.91D;
            return;
        }
        super.travel(strafe, vertical, forward);
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
        if (!isFlying() && !isHanging()) {
            super.updateFallState(y, onGroundIn, state, pos);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 61) {
            for (int i = 0; i < 4; i++) {
                world.spawnParticle(net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, 0.04D, 0.0D);
            }
        } else if (id >= 90 && id <= 91) {
            animation = id - 90;
            animationTick = 0;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void setAnimation(int animation) {
        this.animation = animation;
        animationTick = 0;
        world.setEntityState(this, (byte) (90 + animation));
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int guano = rand.nextInt(3);
        for (int i = 0; i < guano; i++) {
            dropItem(ACItemRegistry.GUANO.item(), 1);
        }
        int wings = rand.nextInt(2 + lootingModifier);
        if (wings > 0) {
            entityDropItem(new ItemStack(ACItemRegistry.VESPER_WING.item(), wings), 0.0F);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Flying", isFlying());
        compound.setBoolean("Hanging", isHanging());
        compound.setInteger("GroundedFor", groundedFor);
        compound.setBoolean("BeingSacrificed", isBeingSacrificed);
        compound.setInteger("SacrificeTime", sacrificeTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setFlying(compound.getBoolean("Flying"));
        setHanging(compound.getBoolean("Hanging"));
        groundedFor = compound.getInteger("GroundedFor");
        isBeingSacrificed = compound.getBoolean("BeingSacrificed");
        sacrificeTime = compound.getInteger("SacrificeTime");
    }

    public boolean isFlying() {
        return dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        dataManager.set(FLYING, flying);
    }

    public boolean isHanging() {
        return dataManager.get(HANGING);
    }

    public void setHanging(boolean hanging) {
        dataManager.set(HANGING, hanging);
    }

    public float getFlightPitch(float partialTick) {
        return prevFlightPitch + (flightPitch - prevFlightPitch) * partialTick;
    }

    public float getFlightRoll(float partialTick) {
        return prevFlightRoll + (flightRoll - prevFlightRoll) * partialTick;
    }

    public float getSleepProgress(float partialTick) {
        return (prevSleepProgress + (sleepProgress - prevSleepProgress) * partialTick) * 0.2F;
    }

    public float getFlyProgress(float partialTick) {
        return (prevFlyProgress + (flyProgress - prevFlyProgress) * partialTick) * 0.2F;
    }

    public float getGroundProgress(float partialTick) {
        return (prevGroundProgress + (groundProgress - prevGroundProgress) * partialTick) * 0.2F;
    }

    public int getAnimation() {
        return animation;
    }

    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void triggerSacrificeIn(int time) {
        isBeingSacrificed = true;
        sacrificeTime = time;
    }

    @Override
    public boolean isValidSacrifice(int distanceFromGround) {
        return distanceFromGround < (isHanging() ? 3 : 9);
    }

    public BlockPos posAbove() {
        return new BlockPos(posX, getEntityBoundingBox().maxY + 0.1D, posZ);
    }

    public boolean canHangFrom(BlockPos pos) {
        return world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.DOWN) && world.isAirBlock(pos.down()) && world.isAirBlock(pos.down(2));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isHanging() ? ACSoundRegistry.VESPER_QUIET_IDLE : ACSoundRegistry.VESPER_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.VESPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.VESPER_DEATH;
    }

    @Override
    public int getTalkInterval() {
        return isHanging() ? 80 : 140;
    }

    private class VesperAttackAI extends EntityAIBase {
        private Vec3d orbitFrom;
        private int orbitTime;
        private int maxOrbitTime;
        private boolean clockwise;

        private VesperAttackAI() {
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            return target != null && target.isEntityAlive() && !isRiding();
        }

        @Override
        public void startExecuting() {
            orbitTime = 0;
            maxOrbitTime = 80;
            orbitFrom = null;
            clockwise = rand.nextBoolean();
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target == null || !target.isEntityAlive()) {
                return;
            }
            if (groundedFor <= 0) {
                if (isHanging()) {
                    setHanging(false);
                }
                setFlying(true);
            } else {
                setFlying(false);
            }
            double distance = getDistance(target);
            float reach = width + target.width;
            getLookHelper().setLookPositionWithEntity(target, 180.0F, 30.0F);
            if (orbitFrom == null) {
                if (isFlying()) {
                    flyToward(target.posX, target.posY + target.height * 0.65D, target.posZ, 2.5D);
                } else {
                    getNavigator().tryMoveToEntityLiving(target, 1.0D);
                }
            } else if (orbitTime < maxOrbitTime && groundedFor <= 0) {
                orbitTime++;
                float zoom = 1.0F - orbitTime / (float) maxOrbitTime;
                Vec3d orbit = orbitAround(3.0F + zoom * 5.0F).add(0.0D, 4.0D + zoom * 3.0D, 0.0D);
                flyToward(orbit.x, orbit.y, orbit.z, 2.5D);
                getLookHelper().setLookPosition(orbit.x, orbit.y, orbit.z, 180.0F, 30.0F);
            } else {
                orbitTime = 0;
                orbitFrom = null;
            }
            if (distance < reach + 0.5D && animation == ANIMATION_NONE) {
                setAnimation(ANIMATION_BITE);
            }
            if (animation == ANIMATION_BITE && animationTick == 8 && canEntityBeSeen(target) && distance < reach + 1.0D) {
                if (target.attackEntityFrom(DamageSource.causeMobDamage(VesperEntity.this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue())) {
                    target.knockBack(VesperEntity.this, 0.25F, posX - target.posX, posZ - target.posZ);
                }
                playSound(ACSoundRegistry.VESPER_SCREAM, 0.8F, getSoundPitch());
                maxOrbitTime = 60 + rand.nextInt(80);
                orbitTime = 0;
                orbitFrom = target.getPositionEyes(1.0F);
            }
        }

        private Vec3d orbitAround(float distance) {
            float angle = (float) Math.toRadians((clockwise ? -orbitTime : orbitTime) * 9.0F);
            return orbitFrom.add(distance * MathHelper.sin(angle), 0.0D, distance * MathHelper.cos(angle));
        }
    }

    private class FlyAndHangAI extends EntityAIBase {
        private Vec3d target;
        private boolean wantsToHang;
        private int hangCheckIn;

        private FlyAndHangAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (isRiding() || getAttackTarget() != null && getAttackTarget().isEntityAlive() || isHanging() || groundedFor > 0) {
                return false;
            }
            if (!isFlying() && rand.nextInt(70) != 0) {
                return false;
            }
            wantsToHang = timeFlying > 300;
            target = wantsToHang ? findHangPosition() : null;
            if (target == null) {
                target = findFlightPosition();
            }
            return target != null;
        }

        @Override
        public void startExecuting() {
            setFlying(true);
            setHanging(false);
            hangCheckIn = 0;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return target != null && isFlying() && groundedFor <= 0 && getDistanceSq(target.x, target.y, target.z) > 3.0D && (!wantsToHang || !isHanging());
        }

        @Override
        public void updateTask() {
            flyToward(target.x, target.y, target.z, 1.0D);
            if (wantsToHang && --hangCheckIn < 0) {
                hangCheckIn = 5 + rand.nextInt(5);
                if (canHangFrom(posAbove())) {
                    setHanging(true);
                    setFlying(false);
                }
            }
            if (isFlying() && onGround && timeFlying > 40) {
                setFlying(false);
            }
        }

        @Override
        public void resetTask() {
            target = null;
            wantsToHang = false;
        }
    }

    private class GroundWanderAI extends EntityAIBase {
        private double x;
        private double y;
        private double z;

        private GroundWanderAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (isFlying() || isHanging() || getAttackTarget() != null && getAttackTarget().isEntityAlive() || rand.nextInt(15) != 0) {
                return false;
            }
            Vec3d pos = getPositionVector().add(rand.nextInt(17) - 8, rand.nextInt(5) - 2, rand.nextInt(17) - 8);
            x = pos.x;
            y = pos.y;
            z = pos.z;
            return true;
        }

        @Override
        public void startExecuting() {
            getNavigator().tryMoveToXYZ(x, y, z, 1.0D);
        }
    }

    private class VesperTargetAI extends EntityAINearestAttackableTarget<EntityPlayer> {
        private final float flyingRange;

        private VesperTargetAI(Class<EntityPlayer> classTarget, float flyingRange) {
            super(VesperEntity.this, classTarget, 10, true, true, null);
            this.flyingRange = flyingRange;
        }

        @Override
        protected AxisAlignedBB getTargetableArea(double targetDistance) {
            AxisAlignedBB box = getEntityBoundingBox();
            if (isHanging()) {
                return new AxisAlignedBB(box.minX - 2.0D, 0.0D, box.minZ - 2.0D, box.maxX + 2.0D, box.maxY + 1.0D, box.maxZ + 2.0D);
            }
            return box.grow(flyingRange, flyingRange, flyingRange);
        }
    }

    private Vec3d findFlightPosition() {
        int range = 13;
        Vec3d target = getPositionVector().add(rand.nextInt(range * 2 + 1) - range, 0.0D, rand.nextInt(range * 2 + 1) - range);
        BlockPos ground = groundPosition(new BlockPos(target));
        if (world.canSeeSky(ground.up())) {
            target = new Vec3d(target.x, ground.getY() + 4 + rand.nextInt(3), target.z);
        } else {
            BlockPos ceiling = ground.up(2);
            while (ceiling.getY() < world.getHeight() && world.isAirBlock(ceiling)) {
                ceiling = ceiling.up();
            }
            target = new Vec3d(target.x, ground.getY() + Math.max(2.0D, (ceiling.getY() - ground.getY()) * (0.3D + rand.nextDouble() * 0.5D)), target.z);
        }
        RayTraceResult hit = world.rayTraceBlocks(getPositionEyes(1.0F), target, false, true, false);
        return hit == null ? target : hit.hitVec;
    }

    private Vec3d findHangPosition() {
        for (int i = 0; i < 15; i++) {
            BlockPos pos = getPosition().add(rand.nextInt(15) - 7, 0, rand.nextInt(15) - 7);
            if (!world.isBlockLoaded(pos) || !world.isAirBlock(pos)) {
                continue;
            }
            while (pos.getY() < world.getHeight() && world.isAirBlock(pos)) {
                pos = pos.up();
            }
            if (pos.getY() > posY - 1.0D && canHangFrom(pos) && hasLineOfSightTo(pos)) {
                return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            }
        }
        return null;
    }

    private BlockPos groundPosition(BlockPos air) {
        BlockPos pos = air;
        while (pos.getY() > 1 && world.isAirBlock(pos)) {
            pos = pos.down();
        }
        return pos;
    }

    private boolean hasLineOfSightTo(BlockPos pos) {
        RayTraceResult hit = world.rayTraceBlocks(getPositionEyes(1.0F), new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), false, true, false);
        return hit == null || hit.typeOfHit == RayTraceResult.Type.MISS || pos.equals(hit.getBlockPos());
    }
}
