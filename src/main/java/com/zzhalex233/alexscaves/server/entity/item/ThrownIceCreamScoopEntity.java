package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ThrownIceCreamScoopEntity extends EntityThrowable {
    public ThrownIceCreamScoopEntity(World worldIn) {
        super(worldIn);
    }

    public ThrownIceCreamScoopEntity(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public ThrownIceCreamScoopEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
            if (result.entityHit instanceof EntityLivingBase) {
                ((EntityLivingBase) result.entityHit).curePotionEffects(new ItemStack(Items.MILK_BUCKET));
            }
        }
        if (!world.isRemote) {
            world.setEntityState(this, (byte) 3);
            setDead();
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, ((double) rand.nextFloat() - 0.5D) * 0.08D, ((double) rand.nextFloat() - 0.5D) * 0.08D, ((double) rand.nextFloat() - 0.5D) * 0.08D, Item.getIdFromItem(ACItemRegistry.VANILLA_ICE_CREAM_SCOOP.item()));
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }
}
