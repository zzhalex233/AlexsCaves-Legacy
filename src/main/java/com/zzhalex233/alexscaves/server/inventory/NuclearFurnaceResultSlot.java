package com.zzhalex233.alexscaves.server.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotFurnaceOutput;

public class NuclearFurnaceResultSlot extends SlotFurnaceOutput {
    public NuclearFurnaceResultSlot(EntityPlayer player, IInventory inventory, int slotIndex, int xPosition, int yPosition) {
        super(player, inventory, slotIndex, xPosition, yPosition);
    }
}
