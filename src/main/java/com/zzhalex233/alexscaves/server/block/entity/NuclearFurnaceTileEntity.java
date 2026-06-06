package com.zzhalex233.alexscaves.server.block.entity;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.entity.item.NuclearExplosionEntity;

import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class NuclearFurnaceTileEntity extends TileEntity implements ITickable, ISidedInventory {
    public static final int MAX_BARRELING_TIME = 100;
    public static final int MAX_WASTE = 1000;
    private static final int[] SLOTS_FOR_UP = {0};
    private static final int[] SLOTS_FOR_DOWN = {3, 4};
    private static final int[] SLOTS_FOR_LEFT = {2};
    private static final int[] SLOTS_FOR_RIGHT = {1};

    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    private int barrelTime;
    private int currentWaste;
    private int fissionTime;
    private int cookTime;
    private int maxCookTime;
    private EntityPlayer lastInteractedWithPlayer;

    @Override
    public void update() {
        if (world == null) {
            return;
        }
        if (world.isRemote) {
            spawnClientParticles();
            if (isUndergoingFission()) {
                AlexsCaves.PROXY.playNuclearFurnaceSound(this);
            }
            return;
        }
        boolean dirty = false;
        ItemStack input = items.get(0);
        ItemStack rod = items.get(1);
        ItemStack barrel = items.get(2);
        ItemStack result = getSmeltingResult(input);
        if (!input.isEmpty() && !result.isEmpty() && canFitInResultSlot(result, 3)) {
            maxCookTime = 20;
            if (fissionTime <= 0) {
                if (!rod.isEmpty() && isFuel(rod)) {
                    fissionTime = getMaxFissionTime();
                    rod.shrink(1);
                    currentWaste += getWastePerBarrel();
                    dirty = true;
                } else {
                    cookTime = 0;
                }
            } else if (++cookTime >= maxCookTime) {
                input.shrink(1);
                mergeResult(result.copy(), 3);
                cookTime = 0;
                dirty = true;
            }
        } else if (cookTime != 0) {
            cookTime = 0;
            dirty = true;
        }
        if (fissionTime > 0) {
            fissionTime--;
            dirty = true;
        }
        if (currentWaste >= getWastePerBarrel() && !barrel.isEmpty() && isBarrel(barrel) && canFitInResultSlot(new ItemStack(ACBlockRegistry.WASTE_DRUM.block()), 4)) {
            if (++barrelTime >= MAX_BARRELING_TIME) {
                float prev = getCriticality();
                barrel.shrink(1);
                currentWaste -= getWastePerBarrel();
                mergeResult(new ItemStack(ACBlockRegistry.WASTE_DRUM.block()), 4);
                barrelTime = 0;
                if (prev >= 3 && getCriticality() <= 2 && lastInteractedWithPlayer != null) {
                    lastInteractedWithPlayer.addExperience(10);
                }
            }
            dirty = true;
        } else if (barrelTime != 0) {
            barrelTime = 0;
            dirty = true;
        }
        if (currentWaste >= MAX_WASTE) {
            destroyWhileCritical(true);
            dirty = true;
        }
        if (dirty) {
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        }
    }

    private void spawnClientParticles() {
        if (getCriticality() >= 2 && world.rand.nextFloat() < 0.2F) {
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 1.0D, pos.getY() + 1.2D, pos.getZ() + 1.0D, 0.0D, 0.08D, 0.0D);
        } else if (isUndergoingFission() && world.rand.nextFloat() < 0.15F) {
            world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() + 1.0D, pos.getY() + 1.0D, pos.getZ() + 1.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    public void destroyWhileCritical(boolean nuke) {
        if (world == null || world.isRemote) {
            return;
        }
        currentWaste = 0;
        BlockPos origin = getPos();
        for (int x = 0; x <= 1; x++) for (int y = 0; y <= 1; y++) for (int z = 0; z <= 1; z++) {
            world.setBlockToAir(origin.add(x, y, z));
        }
        for (int i = 0; i < MAX_WASTE / getWastePerBarrel(); i++) {
            EntityFallingBlock falling = new EntityFallingBlock(world, origin.getX() + 1.0D, origin.getY() + 1.0D, origin.getZ() + 1.0D, ACBlockRegistry.UNREFINED_WASTE.block().getDefaultState());
            falling.motionX = world.rand.nextGaussian() * 0.35D;
            falling.motionY = 0.25D + world.rand.nextDouble() * 0.35D;
            falling.motionZ = world.rand.nextGaussian() * 0.35D;
            world.spawnEntity(falling);
        }
        if (nuke) {
            NuclearExplosionEntity explosion = new NuclearExplosionEntity(world);
            explosion.setPosition(origin.getX() + 1.0D, origin.getY() + 0.5D, origin.getZ() + 1.0D);
            explosion.setNukeSize(0.75F);
            world.spawnEntity(explosion);
        }
    }

    private ItemStack getSmeltingResult(ItemStack input) {
        return input.isEmpty() ? ItemStack.EMPTY : FurnaceRecipes.instance().getSmeltingResult(input);
    }

    private void mergeResult(ItemStack stack, int slot) {
        ItemStack existing = items.get(slot);
        if (existing.isEmpty()) {
            items.set(slot, stack);
        } else {
            existing.grow(stack.getCount());
        }
    }

    private boolean canFitInResultSlot(ItemStack stack, int slot) {
        ItemStack existing = items.get(slot);
        return existing.isEmpty() || ItemStack.areItemsEqual(existing, stack) && ItemStack.areItemStackTagsEqual(existing, stack) && existing.getCount() + stack.getCount() <= Math.min(existing.getMaxStackSize(), getInventoryStackLimit());
    }

    public static boolean isFuel(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ACBlockRegistry.URANIUM_ROD.item();
    }

    public static boolean isBarrel(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ACBlockRegistry.METAL_BARREL.item();
    }

    public boolean isUndergoingFission() {
        return fissionTime > 0;
    }

    public int getCriticality() {
        float scale = getWasteScale();
        return scale >= 0.8F ? 3 : scale >= 0.6F ? 2 : scale >= 0.35F ? 1 : 0;
    }

    public float getWasteScale() {
        return currentWaste / (float) MAX_WASTE;
    }

    public static int getMaxFissionTime() {
        return 1280;
    }

    private static int getWastePerBarrel() {
        return MAX_WASTE / 10;
    }

    public void onPlayerUse(EntityPlayer player) {
        lastInteractedWithPlayer = player;
    }

    @Override
    public int getSizeInventory() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(items, index, count);
        if (!stack.isEmpty()) markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(items, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) stack.setCount(getInventoryStackLimit());
        markDirty();
    }

    @Override
    public String getName() {
        return "container.alexscaves.nuclear_furnace";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("tile.alexscaves.nuclear_furnace.name");
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
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
        return (index == 0 && !getSmeltingResult(stack).isEmpty()) || (index == 1 && isFuel(stack)) || (index == 2 && isBarrel(stack));
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0: return currentWaste;
            case 1: return barrelTime;
            case 2: return fissionTime;
            case 3: return cookTime;
            case 4: return maxCookTime;
            default: return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0: currentWaste = value; break;
            case 1: barrelTime = value; break;
            case 2: fissionTime = value; break;
            case 3: cookTime = value; break;
            case 4: maxCookTime = value; break;
            default: break;
        }
    }

    @Override
    public int getFieldCount() {
        return 5;
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) return SLOTS_FOR_DOWN;
        EnumFacing facing = world.getBlockState(pos).getValue(com.zzhalex233.alexscaves.server.block.NuclearFurnaceBlock.FACING);
        if (side == facing.rotateY()) return SLOTS_FOR_LEFT;
        if (side == facing.rotateYCCW()) return SLOTS_FOR_RIGHT;
        return SLOTS_FOR_UP;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == 3 || index == 4;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, items);
        compound.setInteger("Waste", currentWaste);
        compound.setInteger("CookTime", cookTime);
        compound.setInteger("MaxCookTime", maxCookTime);
        compound.setInteger("FissionTime", fissionTime);
        compound.setInteger("BarrelTime", barrelTime);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        items = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, items);
        currentWaste = compound.getInteger("Waste");
        cookTime = compound.getInteger("CookTime");
        maxCookTime = compound.getInteger("MaxCookTime");
        fissionTime = compound.getInteger("FissionTime");
        barrelTime = compound.getInteger("BarrelTime");
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
    }
}
