package com.zzhalex233.alexscaves.server.block.entity;

import com.zzhalex233.alexscaves.server.block.AbyssalAltarBlock;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneBaseEntity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.UUID;

public class AbyssalAltarTileEntity extends TileEntity implements ITickable, IInventory {
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private ItemStack displayCopyStack = ItemStack.EMPTY;
    private ItemStack popStack = ItemStack.EMPTY;
    private EntityLivingBase lastInteracter;
    private UUID placingPlayer;
    private float itemAngle;
    private float slideProgress;
    private float prevSlideProgress;
    private int popDelay;
    private long lastInteractionTime;
    private boolean slideImpulse;

    @Override
    public void update() {
        if (world == null) {
            return;
        }
        if (world.isRemote) {
            ItemStack stack = getStackInSlot(0);
            if (!ItemStack.areItemStacksEqual(stack, displayCopyStack) || slideImpulse) {
                prevSlideProgress = slideProgress;
                if (slideProgress > 0.0F) {
                    slideProgress--;
                } else {
                    slideImpulse = false;
                    displayCopyStack = stack.copy();
                }
            }
            return;
        }
        if (!popStack.isEmpty() && popDelay++ > 6) {
            ItemStack drop = popStack.copy();
            Vec3d angle = new Vec3d(0.0D, 0.0D, 1.0D).rotateYaw(itemAngle * ((float) Math.PI / 180F));
            EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5D + angle.x, pos.getY() + 1.0D, pos.getZ() + 0.5D + angle.z, drop);
            if (lastInteracter instanceof EntityPlayer && ((EntityPlayer) lastInteracter).inventory.addItemStackToInventory(drop.copy())) {
                lastInteracter.onItemPickup(entityItem, drop.getCount());
            } else if (lastInteracter instanceof DeepOneBaseEntity) {
                ((DeepOneBaseEntity) lastInteracter).setHeldItem(net.minecraft.util.EnumHand.MAIN_HAND, drop.copy());
            } else {
                entityItem.setDefaultPickupDelay();
                world.spawnEntity(entityItem);
            }
            popStack = ItemStack.EMPTY;
            lastInteracter = null;
            markUpdated();
        }
    }

    public void onEntityInteract(EntityLivingBase entity, boolean flip) {
        displayCopyStack = getStackInSlot(0).copy();
        if (world != null && !world.isRemote && world.getBlockState(pos).getBlock() instanceof AbyssalAltarBlock) {
            if (flip && world.getBlockState(pos).getValue(AbyssalAltarBlock.ACTIVE)) {
                world.setBlockState(pos, world.getBlockState(pos).withProperty(AbyssalAltarBlock.ACTIVE, false), 3);
            } else if (!flip && entity instanceof DeepOneBaseEntity) {
                world.setBlockState(pos, world.getBlockState(pos).withProperty(AbyssalAltarBlock.ACTIVE, true), 3);
            }
        }
        Vec3d vec = entity.getPositionVector().subtract(new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D));
        itemAngle = MathHelper.wrapDegrees((float) (MathHelper.atan2(vec.x, vec.z) * (180F / (float) Math.PI)));
        lastInteracter = entity;
        popDelay = 0;
        lastInteractionTime = world == null ? 0L : world.getTotalWorldTime();
        if (entity instanceof EntityPlayer) {
            placingPlayer = entity.getUniqueID();
        }
        resetSlideAnimation();
        markUpdated();
    }

    public boolean queueItemDrop(ItemStack stack) {
        if (!popStack.isEmpty()) {
            return false;
        }
        popStack = stack.copy();
        popDelay = 0;
        markUpdated();
        return true;
    }

    public void resetSlideAnimation() {
        prevSlideProgress = 5.0F;
        slideProgress = 5.0F;
        slideImpulse = true;
    }

    public float getSlideProgress(float partialTicks) {
        return (prevSlideProgress + (slideProgress - prevSlideProgress) * partialTicks) * 0.2F;
    }

    public float getItemAngle() {
        return itemAngle;
    }

    public ItemStack getDisplayStack() {
        return displayCopyStack;
    }

    public long getLastInteractionTime() {
        return lastInteractionTime;
    }

    public UUID getPlacingPlayer() {
        return placingPlayer;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos, pos.add(1, 2, 1));
    }

    @Override
    public int getSizeInventory() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return getStackInSlot(0).isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(items, index, count);
        if (!stack.isEmpty()) {
            markUpdated();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStackHelper.getAndRemove(items, index);
        if (!stack.isEmpty()) {
            markUpdated();
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markUpdated();
    }

    @Override
    public String getName() {
        return "container.alexscaves.abyssal_altar";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("tile.alexscaves.abyssal_altar.name");
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return world != null && world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        items.set(0, ItemStack.EMPTY);
        markUpdated();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, items);
        if (!popStack.isEmpty()) {
            compound.setTag("PopStack", popStack.writeToNBT(new NBTTagCompound()));
        }
        if (!displayCopyStack.isEmpty()) {
            compound.setTag("DisplayStack", displayCopyStack.writeToNBT(new NBTTagCompound()));
        }
        if (placingPlayer != null) {
            compound.setUniqueId("PlayerUUID", placingPlayer);
        }
        compound.setFloat("Angle", itemAngle);
        compound.setFloat("SlideAmount", slideProgress);
        compound.setFloat("PrevSlideAmount", prevSlideProgress);
        compound.setLong("LastInteractionTime", lastInteractionTime);
        compound.setBoolean("SlideImpulse", slideImpulse);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        items = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, items);
        popStack = compound.hasKey("PopStack", 10) ? new ItemStack(compound.getCompoundTag("PopStack")) : ItemStack.EMPTY;
        displayCopyStack = compound.hasKey("DisplayStack", 10) ? new ItemStack(compound.getCompoundTag("DisplayStack")) : getStackInSlot(0).copy();
        placingPlayer = compound.hasUniqueId("PlayerUUID") ? compound.getUniqueId("PlayerUUID") : null;
        itemAngle = compound.getFloat("Angle");
        slideProgress = compound.getFloat("SlideAmount");
        prevSlideProgress = compound.getFloat("PrevSlideAmount");
        lastInteractionTime = compound.getLong("LastInteractionTime");
        slideImpulse = compound.getBoolean("SlideImpulse");
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (world != null) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    public void markUpdated() {
        markDirty();
        if (world != null) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }
}
