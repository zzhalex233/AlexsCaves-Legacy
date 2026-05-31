package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.entity.util.UnderzealotSacrifice;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GloomothEntity extends EntityLiving implements UnderzealotSacrifice {
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(GloomothEntity.class, DataSerializers.BOOLEAN);
    private float prevFlyProgress;
    private float flyProgress;
    private float prevFlapAmount;
    private float flapAmount;
    private float prevFlightPitch;
    private float flightPitch;
    private float prevFlightRoll;
    private float flightRoll;
    private int flapTime;
    private int refreshLightPosIn;
    private boolean isBeingSacrificed;
    private int sacrificeTime;
    public BlockPos lightPos;

    public GloomothEntity(World world) {
        super(world);
        setSize(0.8F, 0.7F);
        experienceValue = 2;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FLYING, false);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new FindLightAI());
        tasks.addTask(2, new FlightAI());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
    }

    @Override
    public void onLivingUpdate() {
        prevFlyProgress = flyProgress;
        prevFlapAmount = flapAmount;
        prevFlightPitch = flightPitch;
        prevFlightRoll = flightRoll;
        flyProgress = approach(flyProgress, isFlying() ? 5.0F : 0.0F);
        double horizontal = Math.sqrt(motionX * motionX + motionZ * motionZ);
        flapAmount = approach(flapAmount, horizontal > 0.05D ? 5.0F : 0.0F);
        super.onLivingUpdate();
        if (isFlying()) {
            setNoGravity(true);
            if (flapAmount > 0.0F && !world.isRemote && --flapTime <= 0) {
                flapTime = 8 + rand.nextInt(10);
                playSound(ACSoundRegistry.GLOOMOTH_FLAP, 0.7F, 1.0F);
            }
        } else {
            setNoGravity(false);
        }
        if (!world.isRemote && lightPos != null && --refreshLightPosIn < 0) {
            refreshLightPosIn = 40 + rand.nextInt(100);
            if (getDistanceSqToCenter(lightPos) >= 256.0D || world.getLight(lightPos) <= 0 || !isLightSource(world.getBlockState(lightPos))) {
                lightPos = null;
            }
        }
        if (isBeingSacrificed && !world.isRemote) {
            if (--sacrificeTime < 10) {
                world.setEntityState(this, (byte) 61);
            }
            if (sacrificeTime < 0) {
                if (isRiding() && getRidingEntity() instanceof UnderzealotEntity) {
                    UnderzealotEntity underzealot = (UnderzealotEntity) getRidingEntity();
                    underzealot.postSacrifice(this);
                    underzealot.triggerIdleDigging();
                }
                dismountRidingEntity();
                WatcherEntity watcher = new WatcherEntity(world);
                watcher.copyLocationAndAnglesFrom(this);
                world.spawnEntity(watcher);
                playSound(ACSoundRegistry.WATCHER_SPAWN, 8.0F, 1.0F);
                setDead();
            }
        }
        tickRotation((float) (motionY * -143.23945D));
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 61) {
            for (int i = 0; i < 4; i++) {
                world.spawnParticle(net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, 0.04D, 0.0D);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    private float approach(float value, float target) {
        if (value < target) {
            return Math.min(target, value + 1.0F);
        }
        return value > target ? Math.max(target, value - 0.5F) : value;
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
        getNavigator().clearPath();
        Vec3d delta = new Vec3d(x - posX, y - posY, z - posZ);
        double length = Math.sqrt(delta.x * delta.x + delta.y * delta.y + delta.z * delta.z);
        if (length > 0.001D) {
            Vec3d add = delta.scale(speed * 0.05D / length);
            motionX = (motionX + add.x) * 0.95D;
            motionY = (motionY + add.y) * 0.95D - 0.01D;
            motionZ = (motionZ + add.z) * 0.95D;
            rotationYaw = approachAngle(rotationYaw, (float) (-Math.atan2(add.x, add.z) * 180.0D / Math.PI), 10.0F);
            renderYawOffset = rotationYaw;
        }
    }

    private float approachAngle(float value, float target, float step) {
        return value + MathHelper.clamp(MathHelper.wrapDegrees(target - value), -step, step);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (isFlying()) {
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
    }

    @Override
    protected void playStepSound(BlockPos pos, Block block) {
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int dust = rand.nextInt(3) + rand.nextInt(1 + lootingModifier);
        if (dust > 0) {
            entityDropItem(new ItemStack(ACItemRegistry.MOTH_DUST.item(), dust), 0.0F);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Flying", isFlying());
        compound.setBoolean("BeingSacrificed", isBeingSacrificed);
        compound.setInteger("SacrificeTime", sacrificeTime);
        if (lightPos != null) {
            compound.setLong("LightPos", lightPos.toLong());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setFlying(compound.getBoolean("Flying"));
        isBeingSacrificed = compound.getBoolean("BeingSacrificed");
        sacrificeTime = compound.getInteger("SacrificeTime");
        if (compound.hasKey("LightPos")) {
            lightPos = BlockPos.fromLong(compound.getLong("LightPos"));
        }
    }

    public boolean isFlying() {
        return dataManager.get(FLYING);
    }

    public void setFlying(boolean flying) {
        dataManager.set(FLYING, flying);
    }

    public float getFlapAmount(float partialTick) {
        return (prevFlapAmount + (flapAmount - prevFlapAmount) * partialTick) * 0.2F;
    }

    public float getFlyProgress(float partialTick) {
        return (prevFlyProgress + (flyProgress - prevFlyProgress) * partialTick) * 0.2F;
    }

    public float getFlightPitch(float partialTick) {
        return prevFlightPitch + (flightPitch - prevFlightPitch) * partialTick;
    }

    public float getFlightRoll(float partialTick) {
        return prevFlightRoll + (flightRoll - prevFlightRoll) * partialTick;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public void triggerSacrificeIn(int time) {
        isBeingSacrificed = true;
        sacrificeTime = time;
    }

    @Override
    public boolean isValidSacrifice(int distanceFromGround) {
        return distanceFromGround < 4;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GLOOMOTH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GLOOMOTH_DEATH;
    }

    private boolean isLightSource(IBlockState state) {
        return state.getLightValue(world, new BlockPos(this)) > 0 || state.isSideSolid(world, new BlockPos(this), EnumFacing.UP) && state.getLightValue() > 0;
    }

    private class FindLightAI extends EntityAIBase {
        private BlockPos target;

        private FindLightAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            if (lightPos != null || rand.nextInt(60) != 0) {
                return false;
            }
            target = findLight();
            return target != null && hasLineOfSightTo(target);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return target != null && lightPos == null && getDistanceSqToCenter(target) > 2.0D;
        }

        @Override
        public void updateTask() {
            if (target != null) {
                flyToward(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.0D);
                if (getDistanceSqToCenter(target) < 3.0D) {
                    lightPos = target;
                }
            }
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
            if (isRiding() || onGround && !isFlying() && rand.nextInt(4) != 0) {
                return false;
            }
            Vec3d target = generatePosition();
            if (target == null) {
                return false;
            }
            x = target.x;
            y = target.y;
            z = target.z;
            return true;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return isFlying() && getDistanceSq(x, y, z) > 3.0D;
        }

        @Override
        public void startExecuting() {
            setFlying(true);
        }

        @Override
        public void updateTask() {
            if (collidedHorizontally || collidedVertically && !onGround || getDistanceSq(x, y, z) > 100.0D) {
                Vec3d target = generatePosition();
                if (target != null) {
                    x = target.x;
                    y = target.y;
                    z = target.z;
                }
            }
            flyToward(x, y, z, lightPos == null ? 1.0D : 1.1D);
        }
    }

    private Vec3d generatePosition() {
        if (lightPos != null) {
            float angle = rand.nextFloat() * (float) Math.PI * 2.0F;
            float distance = 1.0F + rand.nextFloat() * 4.0F;
            return new Vec3d(lightPos.getX() + 0.5D + MathHelper.sin(angle) * distance, lightPos.getY() + 0.5D + rand.nextFloat() * 2.0F, lightPos.getZ() + 0.5D + MathHelper.cos(angle) * distance);
        }
        BlockPos pos = getPosition().add(rand.nextInt(11) - 5, 0, rand.nextInt(11) - 5);
        BlockPos ground = groundPosition(pos);
        Vec3d target = new Vec3d(pos.getX() + 0.5D, ground.getY() + 4 + rand.nextInt(3), pos.getZ() + 0.5D);
        RayTraceResult hit = world.rayTraceBlocks(getPositionEyes(1.0F), target, false, true, false);
        return hit == null ? target : hit.hitVec;
    }

    private BlockPos groundPosition(BlockPos pos) {
        while (pos.getY() > 1 && world.isAirBlock(pos)) {
            pos = pos.down();
        }
        return pos;
    }

    private BlockPos findLight() {
        BlockPos origin = getPosition();
        BlockPos best = null;
        int bestLight = 0;
        int radius = 16;
        for (BlockPos pos : BlockPos.getAllInBox(origin.add(-radius, -8, -radius), origin.add(radius, 8, radius))) {
            if (world.isBlockLoaded(pos)) {
                int light = world.getLight(pos);
                if (light > bestLight && isLightSource(world.getBlockState(pos))) {
                    bestLight = light;
                    best = pos.toImmutable();
                }
            }
        }
        return best;
    }

    private boolean hasLineOfSightTo(BlockPos pos) {
        RayTraceResult hit = world.rayTraceBlocks(getPositionEyes(1.0F), new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), false, true, false);
        return hit == null || hit.typeOfHit == RayTraceResult.Type.MISS || pos.equals(hit.getBlockPos());
    }
}
