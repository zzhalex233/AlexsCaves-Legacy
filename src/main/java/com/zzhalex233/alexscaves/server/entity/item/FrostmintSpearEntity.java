package com.zzhalex233.alexscaves.server.entity.item;

import java.util.List;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.FrostmintSpearItem;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class FrostmintSpearEntity extends EntityArrow {
    private boolean exploded;

    public FrostmintSpearEntity(World worldIn) {
        super(worldIn);
        setDamage(6.0D);
    }

    public FrostmintSpearEntity(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        setDamage(6.0D);
    }

    public FrostmintSpearEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setDamage(6.0D);
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        if (!exploded && ticksExisted > 1) {
            exploded = true;
            frostPulse();
        }
        super.onHit(raytraceResultIn);
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        super.arrowHit(living);
        FrostmintSpearItem.applyFrostmintFreeze(living, 100);
    }

    private void frostPulse() {
        if (!world.isRemote) {
            AxisAlignedBB area = getEntityBoundingBox().grow(2.0D);
            List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, area, entity -> entity != shootingEntity);
            for (EntityLivingBase living : nearby) {
                FrostmintSpearItem.applyFrostmintFreeze(living, 60);
            }
            world.setEntityState(this, (byte) 4);
            world.playSound(null, posX, posY, posZ, ACSoundRegistry.FROSTMINT_SPEAR_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            for (int i = 0; i < 24; i++) {
                world.spawnParticle(EnumParticleTypes.SNOWBALL, posX + (rand.nextDouble() - 0.5D) * 2.0D, posY + rand.nextDouble(), posZ + (rand.nextDouble() - 0.5D) * 2.0D, 0.0D, 0.02D, 0.0D);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ACItemRegistry.FROSTMINT_SPEAR.item());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Exploded", exploded);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        exploded = compound.getBoolean("Exploded");
    }
}
