package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class CinderBrickEntity extends EntityThrowable {
    public CinderBrickEntity(World worldIn) {
        super(worldIn);
    }

    public CinderBrickEntity(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public CinderBrickEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 2.0F + rand.nextInt(2));
        }
        if (!world.isRemote) {
            world.setEntityState(this, (byte) 3);
            if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = result.getBlockPos();
                IBlockState state = world.getBlockState(pos);
                if (!state.getBlock().isAir(state, world, pos) && state.getBlock().getExplosionResistance(world, pos, null, null) < 5.0F) {
                    world.destroyBlock(pos, true);
                }
            }
            setDead();
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                world.spawnParticle(net.minecraft.util.EnumParticleTypes.ITEM_CRACK, posX, posY, posZ, ((double) rand.nextFloat() - 0.5D) * 0.08D, ((double) rand.nextFloat() - 0.5D) * 0.08D, ((double) rand.nextFloat() - 0.5D) * 0.08D, Item.getIdFromItem(ACItemRegistry.CINDER_BRICK.item()));
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.03F;
    }
}
