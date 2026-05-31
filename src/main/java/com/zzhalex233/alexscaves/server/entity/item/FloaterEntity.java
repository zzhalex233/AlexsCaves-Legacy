package com.zzhalex233.alexscaves.server.entity.item;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class FloaterEntity extends Entity {
    private int timeOutOfWater;

    public FloaterEntity(World world) {
        super(world);
        setSize(0.98F, 0.98F);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!hasNoGravity()) {
            motionY -= 0.04D;
        }
        if (isInWater()) {
            motionY += 0.2D;
            if (world.isRemote) {
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX, posY + height * 0.5D, posZ, 0.0D, 0.0D, 0.0D);
            }
            timeOutOfWater = 0;
        } else if (!world.isRemote && timeOutOfWater++ > 5) {
            world.setEntityState(this, (byte) 3);
            setDead();
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
        motionX *= 0.9D;
        motionY *= 0.9D;
        motionZ *= 0.9D;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            ItemStack stack = new ItemStack(ACItemRegistry.FLOATER.item());
            for (int i = 0; i < 10 + rand.nextInt(4); ++i) {
                world.spawnParticle(EnumParticleTypes.ITEM_CRACK, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, (rand.nextDouble() - 0.5D) * 0.3D, (rand.nextDouble() - 0.5D) * 0.3D, (rand.nextDouble() - 0.5D) * 0.3D, Item.getIdFromItem(stack.getItem()), stack.getMetadata());
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return !world.isRemote && player.startRiding(this) || world.isRemote;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (isPassenger(passenger)) {
            passenger.setPosition(posX, posY + 0.7F + passenger.getYOffset(), posZ);
            passenger.fallDistance = 0.0F;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public ItemStack getPickedResult(net.minecraft.util.math.RayTraceResult target) {
        return new ItemStack(ACItemRegistry.FLOATER.item());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        timeOutOfWater = compound.getInteger("TimeOutOfWater");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("TimeOutOfWater", timeOutOfWater);
    }
}
