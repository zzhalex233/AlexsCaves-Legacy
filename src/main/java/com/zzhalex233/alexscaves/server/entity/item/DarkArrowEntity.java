package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class DarkArrowEntity extends EntityArrow {
    private static final DataParameter<Float> SHADOW_ARROW_DAMAGE = EntityDataManager.createKey(DarkArrowEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> PERFECT_SHOT = EntityDataManager.createKey(DarkArrowEntity.class, DataSerializers.BOOLEAN);
    private float fadeOut;
    private float prevFadeOut;
    private float arrowRed;
    private float prevArrowRed;
    private boolean startFading;

    public DarkArrowEntity(World worldIn) {
        super(worldIn);
    }

    public DarkArrowEntity(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        pickupStatus = PickupStatus.DISALLOWED;
    }

    public DarkArrowEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SHADOW_ARROW_DAMAGE, 0.0F);
        dataManager.register(PERFECT_SHOT, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevArrowRed = arrowRed;
        prevFadeOut = fadeOut;
        if (inGround) {
            startFading = true;
        }
        if (startFading) {
            noClip = true;
            motionX *= 0.7D;
            motionY *= 0.7D;
            motionZ *= 0.7D;
            if (fadeOut++ > 5.0F && !world.isRemote) {
                setDead();
            }
        }
        if (isPerfectShot() && arrowRed < 1.0F) {
            arrowRed = Math.min(arrowRed + 0.15F, 1.0F);
        }
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        Entity shooter = shootingEntity;
        float damage = getShadowArrowDamage() * (isPerfectShot() ? 2.0F : 1.0F);
        if ((shooter == null || living != shooter && !living.isOnSameTeam(shooter)) && !startFading && living.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, shooter), damage)) {
            startFading = true;
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            startFading = true;
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ACItemRegistry.DREADBOW.item());
    }

    public float getShadowArrowDamage() {
        return dataManager.get(SHADOW_ARROW_DAMAGE);
    }

    public void setShadowArrowDamage(float damage) {
        dataManager.set(SHADOW_ARROW_DAMAGE, damage);
    }

    public boolean isPerfectShot() {
        return dataManager.get(PERFECT_SHOT);
    }

    public void setPerfectShot(boolean perfectShot) {
        dataManager.set(PERFECT_SHOT, perfectShot);
    }

    public float getFadeOut(float partialTicks) {
        return prevFadeOut + (fadeOut - prevFadeOut) * partialTicks;
    }

    public float getArrowRed(float partialTicks) {
        return prevArrowRed + (arrowRed - prevArrowRed) * partialTicks;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("ShadowArrowDamage", getShadowArrowDamage());
        compound.setBoolean("PerfectShot", isPerfectShot());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setShadowArrowDamage(compound.getFloat("ShadowArrowDamage"));
        setPerfectShot(compound.getBoolean("PerfectShot"));
    }
}
