package com.zzhalex233.alexscaves.server.entity.living;

import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class DinosaurEntity extends EntityTameable {
    private static final DataParameter<Boolean> DANCING = EntityDataManager.createKey(DinosaurEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(DinosaurEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ALT_SKIN = EntityDataManager.createKey(DinosaurEntity.class, DataSerializers.VARINT);
    public float prevDanceProgress;
    public float danceProgress;
    public float prevSitProgress;
    public float sitProgress;

    protected DinosaurEntity(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(DANCING, false);
        dataManager.register(COMMAND, 0);
        dataManager.register(ALT_SKIN, 0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        prevDanceProgress = danceProgress;
        prevSitProgress = sitProgress;
        danceProgress = approach(danceProgress, isDancing() ? 5.0F : 0.0F, 1.0F);
        sitProgress = approach(sitProgress, isSitting() ? maxSitTicks() : 0.0F, 1.0F);
    }

    protected float maxSitTicks() {
        return 10.0F;
    }

    public boolean isDancing() {
        return dataManager.get(DANCING);
    }

    public void setDancing(boolean dancing) {
        dataManager.set(DANCING, dancing);
    }

    public int getCommand() {
        return dataManager.get(COMMAND);
    }

    public void setCommand(int command) {
        dataManager.set(COMMAND, MathHelper.clamp(command, 0, 2));
        setSitting(command == 1);
    }

    public int getAltSkin() {
        return dataManager.get(ALT_SKIN);
    }

    public void setAltSkin(int skin) {
        dataManager.set(ALT_SKIN, MathHelper.clamp(skin, 0, 2));
    }

    public float getDanceProgress(float partialTicks) {
        return (prevDanceProgress + (danceProgress - prevDanceProgress) * partialTicks) * 0.2F;
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) / maxSitTicks();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Command", getCommand());
        compound.setInteger("AltSkin", getAltSkin());
        compound.setBoolean("Dancing", isDancing());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setCommand(compound.getInteger("Command"));
        setAltSkin(compound.getInteger("AltSkin"));
        setDancing(compound.getBoolean("Dancing"));
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (isTamed() && isOwner(player) && !player.isSneaking()) {
            if (stack.getItem() == ACItemRegistry.AMBER_CURIOSITY.item()) {
                setAltSkin(1);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return true;
            }
            if (stack.getItem() == ACItemRegistry.TECTONIC_SHARD.item()) {
                setAltSkin(2);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return true;
            }
            setCommand((getCommand() + 1) % 3);
            return true;
        }
        return super.processInteract(player, hand);
    }

    protected static float approach(float current, float target, float step) {
        if (current < target) {
            return Math.min(target, current + step);
        }
        if (current > target) {
            return Math.max(target, current - step);
        }
        return current;
    }
}
