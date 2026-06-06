package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class DepthChargeEntity extends EntityThrowable {
    private boolean hitGround;
    private int groundTime;

    public DepthChargeEntity(World worldIn) {
        super(worldIn);
    }

    public DepthChargeEntity(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public DepthChargeEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public void onUpdate() {
        if (hitGround) {
            super.onEntityUpdate();
            motionX = 0.0D;
            motionY = 0.0D;
            motionZ = 0.0D;
            if (!world.isRemote && groundTime++ > 30) {
                explode();
            }
        } else {
            super.onUpdate();
            if (collided) {
                hitGround = true;
            }
        }
        if (isInWater()) {
            for (int i = 0; i < 4; ++i) {
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX, posY + height * 0.5D, posZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (hitGround) {
            return;
        }
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 2.0F + rand.nextInt(2));
        }
        hitGround = true;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, ((double) rand.nextFloat() - 0.5D) * 0.08D, ((double) rand.nextFloat() - 0.5D) * 0.08D, ((double) rand.nextFloat() - 0.5D) * 0.08D, Item.getIdFromItem(ACItemRegistry.DEPTH_CHARGE.item()));
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05F;
    }

    private void explode() {
        world.setEntityState(this, (byte) 3);
        world.createExplosion(this, posX, posY + height * 0.5D, posZ, 2.0F, world.getGameRules().getBoolean("mobGriefing"));
        setDead();
    }
}
