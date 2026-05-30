package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrilocarisEntity extends BucketableWaterMob {
    private int swimChangeCooldown;
    private double swimVecX;
    private double swimVecY;
    private double swimVecZ;
    private int lastStepSoundTick = -20;

    public TrilocarisEntity(World world) {
        super(world);
        setSize(0.9F, 0.4F);
        experienceValue = 2;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(2, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!world.isRemote) {
            updateSwimmingVector();
        }
        if (isInWater()) {
            motionX += swimVecX * 0.03D;
            motionY += swimVecY * 0.02D;
            motionZ += swimVecZ * 0.03D;
            if (collidedHorizontally) {
                motionY += 0.03D;
            }
            rotationYaw = (float) (-Math.atan2(motionX, motionZ) * (180D / Math.PI));
            renderYawOffset = rotationYaw;
        } else if (onGround) {
            motionX *= 0.65D;
            motionZ *= 0.65D;
        }
    }

    private void updateSwimmingVector() {
        if (--swimChangeCooldown > 0 && isInWater()) {
            return;
        }
        swimChangeCooldown = 20 + rand.nextInt(40);
        double angle = rand.nextDouble() * Math.PI * 2.0D;
        swimVecX = Math.cos(angle) * 0.6D;
        swimVecY = rand.nextDouble() * 0.6D - 0.25D;
        swimVecZ = Math.sin(angle) * 0.6D;
    }

    @Override
    public boolean attackEntityAsMob(net.minecraft.entity.Entity entity) {
        boolean attacked = super.attackEntityAsMob(entity);
        if (attacked && entity instanceof EntityLivingBase) {
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0F);
        }
        return attacked;
    }

    @Override
    protected Item getBucketItem() {
        return ACItemRegistry.TRILOCARIS_BUCKET.item();
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
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 4;
    }

    @Override
    public boolean getCanSpawnHere() {
        return isInWater() && isNotColliding();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TRILOCARIS_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TRILOCARIS_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block block) {
        if (ticksExisted - lastStepSoundTick > 10) {
            lastStepSoundTick = ticksExisted;
            playSound(ACSoundRegistry.TRILOCARIS_STEP, 0.2F, 1.0F);
        }
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int count = rand.nextInt(2);
        Item drop = isBurning() ? ACItemRegistry.COOKED_TRILOCARIS_TAIL.item() : ACItemRegistry.TRILOCARIS_TAIL.item();
        for (int i = 0; i < count; i++) {
            dropItem(drop, 1);
        }
    }
}
