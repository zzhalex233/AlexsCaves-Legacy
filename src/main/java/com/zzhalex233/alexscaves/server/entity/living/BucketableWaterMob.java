package com.zzhalex233.alexscaves.server.entity.living;

import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class BucketableWaterMob extends EntityWaterMob {
    private boolean fromBucket;

    protected BucketableWaterMob(World world) {
        super(world);
    }

    protected abstract Item getBucketItem();

    protected Item getPickupBucketItem() {
        return Items.BUCKET;
    }

    protected SoundEvent getBucketFillSound() {
        return SoundEvents.ITEM_BUCKET_FILL;
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (held.getItem() == getPickupBucketItem() && isEntityAlive()) {
            if (!world.isRemote) {
                ItemStack bucket = new ItemStack(getBucketItem());
                if (hasCustomName()) {
                    bucket.setStackDisplayName(getCustomNameTag());
                }
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                    if (held.isEmpty()) {
                        player.setHeldItem(hand, bucket);
                    } else if (!player.inventory.addItemStackToInventory(bucket)) {
                        player.dropItem(bucket, false);
                    }
                }
                playSound(getBucketFillSound(), 1.0F, 1.0F);
                setDead();
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("FromBucket", fromBucket);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        fromBucket = compound.getBoolean("FromBucket");
    }

    public void setFromBucket(boolean fromBucket) {
        this.fromBucket = fromBucket;
    }

    @Override
    protected boolean canDespawn() {
        return !fromBucket && !hasCustomName();
    }
}
