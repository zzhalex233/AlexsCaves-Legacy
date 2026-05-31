package com.zzhalex233.alexscaves.server.entity.item;

import java.util.List;

import com.zzhalex233.alexscaves.server.entity.living.CaramelCubeEntity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MeltedCaramelEntity extends Entity {
    private int despawnsIn = 40;
    private int prevDespawnsIn;
    private final float yRenderOffset = rand.nextFloat() * 0.05F;

    public MeltedCaramelEntity(World world) {
        super(world);
        setSize(0.99F, 0.1F);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        prevDespawnsIn = despawnsIn;
        if (despawnsIn > 0) {
            despawnsIn--;
        } else if (!world.isRemote) {
            setDead();
        }
        BlockPos below = getPosition().down();
        if (!world.isRemote && !world.getBlockState(below).isSideSolid(world, below, EnumFacing.UP)) {
            setDead();
        }
        slowEntities();
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.2D;
        motionY *= 0.2D;
        motionZ *= 0.2D;
    }

    public void setDespawnsIn(int despawnsIn) {
        this.despawnsIn = despawnsIn;
    }

    public float getDespawnTime(float partialTicks) {
        return prevDespawnsIn + (despawnsIn - prevDespawnsIn) * partialTicks;
    }

    public float getYRenderOffset() {
        return yRenderOffset;
    }

    private void slowEntities() {
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox());
        for (EntityLivingBase entity : entities) {
            if (!isOnSameTeam(entity) && !(entity instanceof CaramelCubeEntity)) {
                entity.motionX *= 0.25D;
                entity.motionZ *= 0.25D;
                if (entity.onGround && entity.motionY > 0.0D) {
                    entity.motionY *= 0.05D;
                }
            }
        }
    }

    public static Vec3d getGroundBelowPosition(World world, Vec3d in) {
        BlockPos pos = new BlockPos(in);
        while (pos.getY() > 0 && world.getBlockState(pos).getMaterial() == Material.AIR) {
            pos = pos.down();
        }
        AxisAlignedBB box = world.getBlockState(pos).getCollisionBoundingBox(world, pos);
        double top = box == null ? 0.0D : box.maxY;
        return new Vec3d(in.x, pos.getY() + top + 0.02D, in.z);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        despawnsIn = compound.getInteger("DespawnsIn");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("DespawnsIn", despawnsIn);
    }
}
