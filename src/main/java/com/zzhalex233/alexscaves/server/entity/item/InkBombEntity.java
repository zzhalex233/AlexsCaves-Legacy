package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class InkBombEntity extends EntityThrowable {
    private static final DataParameter<Boolean> GLOWING_BOMB = EntityDataManager.createKey(InkBombEntity.class, DataSerializers.BOOLEAN);

    public InkBombEntity(World worldIn) {
        super(worldIn);
    }

    public InkBombEntity(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public InkBombEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(GLOWING_BOMB, false);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
            if (result.entityHit instanceof EntityLivingBase) {
                applyInkEffects((EntityLivingBase) result.entityHit);
            }
        }
        if (!world.isRemote) {
            world.setEntityState(this, (byte) 3);
            spawnInkCloud();
            setDead();
        }
    }

    private void spawnInkCloud() {
        EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(world, posX, posY + 0.2D, posZ);
        cloud.setOwner(getThrower());
        cloud.setParticle(isGlowingBomb() ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SMOKE_NORMAL);
        cloud.setColor(isGlowingBomb() ? 0x66FFF0 : 0x101010);
        cloud.setRadius(2.0F);
        cloud.setDuration(60);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
        cloud.addEffect(new PotionEffect(MobEffects.BLINDNESS, 100));
        if (isGlowingBomb()) {
            cloud.addEffect(new PotionEffect(MobEffects.GLOWING, 300));
        }
        world.spawnEntity(cloud);
        AxisAlignedBB area = new AxisAlignedBB(posX - 2.0D, posY - 2.0D, posZ - 2.0D, posX + 2.0D, posY + 2.0D, posZ + 2.0D);
        for (EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
            if (living.getDistanceSq(this) <= 4.0D) {
                applyInkEffects(living);
            }
        }
    }

    private void applyInkEffects(EntityLivingBase living) {
        living.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100));
        if (isGlowingBomb()) {
            living.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 300));
        }
        if (!(living instanceof EntityPlayer) || !((EntityPlayer) living).capabilities.isCreativeMode) {
            living.removePotionEffect(MobEffects.NIGHT_VISION);
            living.removePotionEffect(MobEffects.WATER_BREATHING);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            Item item = isGlowingBomb() ? ACItemRegistry.GLOW_INK_BOMB.item() : ACItemRegistry.INK_BOMB.item();
            for (int i = 0; i < 8; ++i) {
                world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, ((double) rand.nextFloat() - 0.5D) * 0.08D, ((double) rand.nextFloat() - 0.5D) * 0.08D, ((double) rand.nextFloat() - 0.5D) * 0.08D, Item.getIdFromItem(item));
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public boolean isGlowingBomb() {
        return dataManager.get(GLOWING_BOMB);
    }

    public void setGlowingBomb(boolean glowing) {
        dataManager.set(GLOWING_BOMB, glowing);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("GlowingBomb", isGlowingBomb());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setGlowingBomb(compound.getBoolean("GlowingBomb"));
    }

    @Override
    protected float getGravityVelocity() {
        return 0.03F;
    }
}
