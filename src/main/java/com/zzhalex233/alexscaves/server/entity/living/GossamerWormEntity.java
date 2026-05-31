package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GossamerWormEntity extends BucketableWaterMob {
    private static final int TRAIL_LENGTH = 32;
    private static final int PART_COUNT = 5;
    private final float[][] trail = new float[TRAIL_LENGTH][3];
    private final int[] partIds = new int[] {-1, -1, -1, -1, -1};
    private float prevSquishProgress;
    private float squishProgress;
    private float prevFishPitch;
    private float fishPitch;
    private int swimChangeCooldown;
    private int fleeFor;
    private BlockPos hurtPos;
    private double swimVecX;
    private double swimVecY;
    private double swimVecZ;

    public GossamerWormEntity(World world) {
        super(world);
        setSize(0.8F, 0.8F);
        experienceValue = 1;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new AvoidHurtAI());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.08D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevSquishProgress = squishProgress;
        prevFishPitch = fishPitch;
        updateTrail();
        boolean grounded = onGround && !isInWater();
        if (grounded && squishProgress < 5.0F) {
            squishProgress++;
        } else if (!grounded && squishProgress > 0.0F) {
            squishProgress--;
        }
        if (!world.isRemote) {
            if (isInWater()) {
                setAir(300);
                updateSwimmingVector();
            } else {
                handleDryingOut();
            }
        }
        if (isInWater()) {
            motionX += swimVecX * 0.025D;
            motionY += swimVecY * 0.02D - 0.01D;
            motionZ += swimVecZ * 0.025D;
            if (collidedHorizontally) {
                motionY += 0.05D;
            }
            motionX *= 0.82D;
            motionY *= 0.82D;
            motionZ *= 0.82D;
            double horizontal = Math.sqrt(motionX * motionX + motionZ * motionZ);
            if (horizontal > 0.02D) {
                rotationYaw = (float) (-Math.atan2(motionX, motionZ) * 57.2957763671875D);
                renderYawOffset = rotationYaw;
                fishPitch = (float) (-Math.atan2(motionY, horizontal) * 57.2957763671875D);
            }
        } else {
            fishPitch *= 0.8F;
        }
        if (fleeFor > 0) {
            fleeFor--;
        }
        if (!world.isRemote) {
            tickMultipart();
        }
    }

    private void updateTrail() {
        for (int i = TRAIL_LENGTH - 1; i > 0; i--) {
            trail[i][0] = trail[i - 1][0];
            trail[i][1] = trail[i - 1][1];
            trail[i][2] = trail[i - 1][2];
        }
        trail[0][0] = rotationYaw;
        trail[0][1] = fishPitch;
        trail[0][2] = (float) posY;
    }

    private void tickMultipart() {
        GossamerWormPartEntity tail1 = ensurePart(0, this, 1.1F, 0.5F);
        GossamerWormPartEntity tail2 = ensurePart(1, tail1, 1.1F, 0.5F);
        GossamerWormPartEntity tail3 = ensurePart(2, tail2, 1.0F, 0.5F);
        GossamerWormPartEntity tail4 = ensurePart(3, tail3, 0.8F, 0.5F);
        GossamerWormPartEntity tail5 = ensurePart(4, tail4, 0.6F, 0.5F);
        movePart(tail1, this, 5, 1.0D);
        movePart(tail2, tail1, 10, 0.9D);
        movePart(tail3, tail2, 15, 0.8D);
        movePart(tail4, tail3, 20, 0.7D);
        movePart(tail5, tail4, 25, 0.6D);
    }

    private GossamerWormPartEntity ensurePart(int index, Entity connectedTo, float sizeXZ, float sizeY) {
        Entity entity = partIds[index] == -1 ? null : world.getEntityByID(partIds[index]);
        GossamerWormPartEntity part;
        if (entity instanceof GossamerWormPartEntity && !entity.isDead) {
            part = (GossamerWormPartEntity) entity;
        } else {
            part = new GossamerWormPartEntity(world, this, connectedTo, index, sizeXZ, sizeY);
            part.setPosition(posX, posY, posZ);
            world.spawnEntity(part);
            partIds[index] = part.getEntityId();
        }
        part.setParentId(getEntityId());
        part.setConnectedId(connectedTo.getEntityId());
        part.setPartIndex(index);
        return part;
    }

    private void movePart(GossamerWormPartEntity part, Entity connectedTo, int pointer, double distance) {
        if (part != null && connectedTo != null) {
            part.setToTransformation(connectedTo, new Vec3d(0.0D, 0.0D, -distance), getTrailTransformation(pointer, 1, 1.0F), getTrailTransformation(pointer, 0, 1.0F));
        }
    }

    private void updateSwimmingVector() {
        if (--swimChangeCooldown > 0) {
            return;
        }
        swimChangeCooldown = 20 + rand.nextInt(40);
        if (fleeFor > 0 && hurtPos != null) {
            Vec3d away = getPositionVector().subtract(new Vec3d(hurtPos).add(0.5D, 0.5D, 0.5D)).normalize();
            swimVecX = away.x * 1.6D;
            swimVecY = away.y * 0.8D;
            swimVecZ = away.z * 1.6D;
            return;
        }
        double angle = rand.nextDouble() * Math.PI * 2.0D;
        swimVecX = Math.cos(angle) * 0.7D;
        swimVecY = rand.nextDouble() * 0.7D - 0.2D;
        swimVecZ = Math.sin(angle) * 0.7D;
    }

    private void handleDryingOut() {
        setAir(getAir() - 1);
        if (onGround) {
            motionX += (rand.nextFloat() * 2.0F - 1.0F) * 0.08F;
            motionY += 0.18D;
            motionZ += (rand.nextFloat() * 2.0F - 1.0F) * 0.08F;
        }
        if (getAir() <= -20) {
            setAir(0);
            attackEntityFrom(DamageSource.DROWN, 2.0F);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean hurt = super.attackEntityFrom(source, amount);
        if (hurt) {
            hurtPos = getPosition();
            fleeFor = 80;
        }
        return hurt;
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        removeParts();
    }

    @Override
    public void setDead() {
        super.setDead();
        removeParts();
    }

    private void removeParts() {
        if (world.isRemote) {
            return;
        }
        for (int id : partIds) {
            Entity entity = id == -1 ? null : world.getEntityByID(id);
            if (entity instanceof GossamerWormPartEntity) {
                entity.setDead();
            }
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (!world.isRemote) {
            tickMultipart();
        }
        return livingdata;
    }

    public float getSquishProgress(float partialTicks) {
        return (prevSquishProgress + (squishProgress - prevSquishProgress) * partialTicks) * 0.2F;
    }

    public float getFishPitch(float partialTicks) {
        return prevFishPitch + (fishPitch - prevFishPitch) * partialTicks;
    }

    public float getTrailTransformation(int pointer, int index, float partialTicks) {
        int from = MathHelper.clamp(pointer, 0, TRAIL_LENGTH - 1);
        int to = MathHelper.clamp(pointer - 1, 0, TRAIL_LENGTH - 1);
        return trail[from][index] + (trail[to][index] - trail[from][index]) * partialTicks;
    }

    @Override
    protected Item getBucketItem() {
        return ACItemRegistry.GOSSAMER_WORM_BUCKET.item();
    }

    @Override
    protected SoundEvent getBucketFillSound() {
        return SoundEvents.ITEM_BUCKET_FILL;
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
        return isInWater() && isNotColliding() && posY < world.getSeaLevel() - 25;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GOSSAMER_WORM_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ACSoundRegistry.GOSSAMER_WORM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GOSSAMER_WORM_DEATH;
    }

    private class AvoidHurtAI extends EntityAIBase {
        private AvoidHurtAI() {
            setMutexBits(1);
        }

        @Override
        public boolean shouldExecute() {
            return fleeFor > 0 && hurtPos != null;
        }

        @Override
        public void updateTask() {
            updateSwimmingVector();
        }
    }
}
