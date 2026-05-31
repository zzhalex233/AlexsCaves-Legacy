package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class LimestoneSpearEntity extends EntityArrow {
    private boolean dealtDamage;

    public LimestoneSpearEntity(World worldIn) {
        super(worldIn);
        setDamage(4.0D);
    }

    public LimestoneSpearEntity(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        setDamage(4.0D);
    }

    public LimestoneSpearEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setDamage(4.0D);
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        if (!dealtDamage && ticksExisted > 1) {
            dealtDamage = true;
            world.playSound(null, posX, posY, posZ, ACSoundRegistry.LIMESTONE_SPEAR_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        super.onHit(raytraceResultIn);
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ACItemRegistry.LIMESTONE_SPEAR.item());
    }
}
