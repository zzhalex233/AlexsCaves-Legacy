package com.zzhalex233.alexscaves.server.inventory;

import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.CaveInfoItem;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpelunkeryTableContainer extends Container {
    private static final String TUTORIAL_KEY = "alexscaves_spelunkery_tutorial_complete";
    private static final String PERSISTED_NBT_TAG = "PlayerPersisted";

    private final World world;
    private final BlockPos pos;
    public final IInventory input = new InventoryBasic("spelunkery_table", false, 2) {
        @Override
        public void markDirty() {
            SpelunkeryTableContainer.this.onCraftMatrixChanged(this);
            super.markDirty();
        }
    };
    private final IInventory result = new InventoryCraftResult();

    public SpelunkeryTableContainer(InventoryPlayer playerInventory, World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
        this.addSlotToContainer(new Slot(input, 0, 50, 143) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ACItemRegistry.CAVE_TABLET.item();
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return input.getStackInSlot(1).isEmpty();
            }

            @Override
            public ItemStack onTake(EntityPlayer player, ItemStack stack) {
                play(ACSoundRegistry.SPELUNKERY_TABLE_TABLET_REMOVE);
                return super.onTake(player, stack);
            }

            @Override
            public void putStack(ItemStack stack) {
                boolean inserted = getStack().isEmpty() && !stack.isEmpty();
                super.putStack(stack);
                if (inserted) {
                    play(ACSoundRegistry.SPELUNKERY_TABLE_TABLET_INSERT);
                }
            }
        });
        this.addSlotToContainer(new Slot(input, 1, 70, 143) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.PAPER;
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return input.getStackInSlot(0).isEmpty();
            }

            @Override
            public ItemStack onTake(EntityPlayer player, ItemStack stack) {
                play(ACSoundRegistry.SPELUNKERY_TABLE_PAPER_REMOVE);
                return super.onTake(player, stack);
            }

            @Override
            public void putStack(ItemStack stack) {
                boolean inserted = getStack().isEmpty() && !stack.isEmpty();
                super.putStack(stack);
                if (inserted) {
                    play(ACSoundRegistry.SPELUNKERY_TABLE_PAPER_INSERT);
                }
            }
        });
        this.addSlotToContainer(new Slot(result, 2, 142, 143) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTake(EntityPlayer player, ItemStack stack) {
                play(ACSoundRegistry.SPELUNKERY_TABLE_CODEX_REMOVE);
                stack.getItem().onCreated(stack, world, player);
                return super.onTake(player, stack);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 24 + j * 18, 174 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(playerInventory, k, 24 + k * 18, 232));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return world.getBlockState(pos).getBlock() == ACBlockRegistry.SPELUNKERY_TABLE.block()
                && playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != result && super.canMergeSlot(stack, slotIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index == 2) {
                stack.getItem().onCreated(stack, world, playerIn);
                if (!this.mergeItemStack(stack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, copy);
            } else if (index != 0 && index != 1) {
                if (stack.getItem() == Items.PAPER) {
                    if (!this.mergeItemStack(stack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() == ACItemRegistry.CAVE_TABLET.item() && !this.mergeItemStack(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (stack.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, stack);
        }
        return copy;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!world.isRemote) {
            this.clearContainer(playerIn, world, input);
            this.clearContainer(playerIn, world, result);
        }
    }

    public void onMessageFromScreen(EntityPlayer player, boolean pass) {
        ItemStack tablet = this.getSlot(0).getStack().copy();
        this.getSlot(0).getStack().shrink(1);
        if (pass && !tablet.isEmpty()) {
            if (this.getSlot(1).getStack().getItem() == Items.PAPER) {
                this.getSlot(1).getStack().shrink(1);
            }
            String caveBiome = CaveInfoItem.getCaveBiome(tablet);
            if (caveBiome != null && !caveBiome.isEmpty()) {
                setupResultSlot(CaveInfoItem.create(ACItemRegistry.CAVE_CODEX.item(), caveBiome), player);
            }
            setTutorialComplete(player, true);
        } else if (!world.isRemote) {
            world.playSound(null, pos, ACSoundRegistry.SPELUNKERY_TABLE_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        this.detectAndSendChanges();
    }

    private void setupResultSlot(ItemStack stack, EntityPlayer player) {
        ItemStack existing = result.getStackInSlot(2);
        if (existing.isEmpty()) {
            result.setInventorySlotContents(2, stack);
        } else if (ItemStack.areItemsEqual(existing, stack) && ItemStack.areItemStackTagsEqual(existing, stack) && existing.getCount() + stack.getCount() <= existing.getMaxStackSize()) {
            existing.grow(stack.getCount());
            result.setInventorySlotContents(2, existing);
        } else if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
    }

    public int getHighlightColor() {
        ItemStack stack = this.getSlot(0).getStack();
        String caveBiome = CaveInfoItem.getCaveBiome(stack);
        if (stack.getItem() != ACItemRegistry.CAVE_TABLET.item() || caveBiome == null) {
            return -1;
        }
        int index = java.util.Arrays.asList(CaveInfoItem.CAVE_BIOMES).indexOf(caveBiome);
        int[] colors = {0X7BA0A3, 0X8C7044, 0XA0DE30, 0X1E6BB8, 0X826BA3, 0XFF5FA2};
        return index < 0 ? -1 : colors[index];
    }

    public static void setTutorialComplete(EntityPlayer player, boolean done) {
        NBTTagCompound playerData = player.getEntityData();
        NBTTagCompound data = playerData.getCompoundTag(PERSISTED_NBT_TAG);
        data.setBoolean(TUTORIAL_KEY, done);
        playerData.setTag(PERSISTED_NBT_TAG, data);
    }

    public static boolean hasCompletedTutorial(EntityPlayer player) {
        return player.getEntityData().getCompoundTag(PERSISTED_NBT_TAG).getBoolean(TUTORIAL_KEY);
    }

    private void play(net.minecraft.util.SoundEvent sound) {
        if (!world.isRemote) {
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }
}
