package com.zzhalex233.alexscaves.server.inventory;

import com.zzhalex233.alexscaves.server.block.entity.NuclearFurnaceTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class NuclearFurnaceContainer extends Container {
    private final IInventory furnace;
    private int lastWaste;
    private int lastBarrelTime;
    private int lastFissionTime;
    private int lastCookTime;
    private int lastMaxCookTime;

    public NuclearFurnaceContainer(InventoryPlayer inventory, IInventory furnace) {
        this.furnace = furnace;
        addSlotToContainer(new Slot(furnace, 0, 67, 17) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return !FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty();
            }
        });
        addSlotToContainer(new FilterSlot(furnace, 1, 67, 53, false));
        addSlotToContainer(new FilterSlot(furnace, 2, 37, 53, true));
        addSlotToContainer(new NuclearFurnaceResultSlot(inventory.player, furnace, 3, 127, 35));
        addSlotToContainer(new NuclearFurnaceResultSlot(inventory.player, furnace, 4, 37, 17));
        for (int i = 0; i < 3; i++) for (int j = 0; j < 9; j++) {
            addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        }
        for (int k = 0; k < 9; k++) {
            addSlotToContainer(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return furnace.isUsableByPlayer(playerIn);
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, furnace);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : listeners) {
            if (lastWaste != furnace.getField(0)) listener.sendWindowProperty(this, 0, furnace.getField(0));
            if (lastBarrelTime != furnace.getField(1)) listener.sendWindowProperty(this, 1, furnace.getField(1));
            if (lastFissionTime != furnace.getField(2)) listener.sendWindowProperty(this, 2, furnace.getField(2));
            if (lastCookTime != furnace.getField(3)) listener.sendWindowProperty(this, 3, furnace.getField(3));
            if (lastMaxCookTime != furnace.getField(4)) listener.sendWindowProperty(this, 4, furnace.getField(4));
        }
        lastWaste = furnace.getField(0);
        lastBarrelTime = furnace.getField(1);
        lastFissionTime = furnace.getField(2);
        lastCookTime = furnace.getField(3);
        lastMaxCookTime = furnace.getField(4);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        furnace.setField(id, data);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index == 3 || index == 4) {
                if (!mergeItemStack(stack, 5, 41, true)) return ItemStack.EMPTY;
                slot.onSlotChange(stack, copy);
            } else if (index > 4) {
                if (!FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty()) {
                    if (!mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
                } else if (NuclearFurnaceTileEntity.isFuel(stack)) {
                    if (!mergeItemStack(stack, 1, 2, false)) return ItemStack.EMPTY;
                } else if (NuclearFurnaceTileEntity.isBarrel(stack)) {
                    if (!mergeItemStack(stack, 2, 3, false)) return ItemStack.EMPTY;
                } else if (index < 32) {
                    if (!mergeItemStack(stack, 32, 41, false)) return ItemStack.EMPTY;
                } else if (!mergeItemStack(stack, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 5, 41, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
            if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
            slot.onTake(playerIn, stack);
        }
        return copy;
    }

    public float getWasteScale() {
        return furnace.getField(0) / (float) NuclearFurnaceTileEntity.MAX_WASTE;
    }

    public float getBarrelScale() {
        return furnace.getField(1) / (float) NuclearFurnaceTileEntity.MAX_BARRELING_TIME;
    }

    public float getFissionScale() {
        return furnace.getField(2) / (float) NuclearFurnaceTileEntity.getMaxFissionTime();
    }

    public float getCookScale() {
        int max = furnace.getField(4);
        return max <= 0 ? 0.0F : furnace.getField(3) / (float) max;
    }

    private static class FilterSlot extends Slot {
        private final boolean barrel;

        FilterSlot(IInventory inventory, int index, int x, int y, boolean barrel) {
            super(inventory, index, x, y);
            this.barrel = barrel;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return barrel ? NuclearFurnaceTileEntity.isBarrel(stack) : NuclearFurnaceTileEntity.isFuel(stack);
        }
    }
}
