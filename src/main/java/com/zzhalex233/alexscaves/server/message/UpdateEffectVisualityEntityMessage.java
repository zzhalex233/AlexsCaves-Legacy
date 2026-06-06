package com.zzhalex233.alexscaves.server.message;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.misc.ACSoundRegistry;
import com.zzhalex233.alexscaves.server.potion.ACEffectRegistry;
import com.zzhalex233.alexscaves.server.potion.IrradiatedEffect;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateEffectVisualityEntityMessage implements IMessage {
    private int entityId;
    private int fromEntityId;
    private int potionType;
    private int duration;
    private boolean remove;

    public UpdateEffectVisualityEntityMessage() {
    }

    public UpdateEffectVisualityEntityMessage(int entityId, int fromEntityId, int potionType, int duration) {
        this(entityId, fromEntityId, potionType, duration, false);
    }

    public UpdateEffectVisualityEntityMessage(int entityId, int fromEntityId, int potionType, int duration, boolean remove) {
        this.entityId = entityId;
        this.fromEntityId = fromEntityId;
        this.potionType = potionType;
        this.duration = duration;
        this.remove = remove;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        fromEntityId = buf.readInt();
        potionType = buf.readInt();
        duration = buf.readInt();
        remove = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(fromEntityId);
        buf.writeInt(potionType);
        buf.writeInt(duration);
        buf.writeBoolean(remove);
    }

    public static class Handler implements IMessageHandler<UpdateEffectVisualityEntityMessage, IMessage> {
        @Override
        public IMessage onMessage(UpdateEffectVisualityEntityMessage message, MessageContext ctx) {
            AlexsCaves.PROXY.runOnClientThread(() -> {
                EntityPlayer player = AlexsCaves.PROXY.getClientSidePlayer();
                if (player != null) {
                    apply(player, message);
                }
            });
            return null;
        }
    }

    private static void apply(EntityPlayer player, UpdateEffectVisualityEntityMessage message) {
        Entity entity = player.world.getEntityByID(message.entityId);
        Entity from = player.world.getEntityByID(message.fromEntityId);
        if (!(entity instanceof EntityLivingBase) || from == null || entity.getDistance(from) >= 32.0F) {
            return;
        }
        EntityLivingBase living = (EntityLivingBase) entity;
        Potion potion = null;
        int amplifier = 0;
        switch (message.potionType) {
            case 0:
                potion = ACEffectRegistry.IRRADIATED;
                break;
            case 1:
                potion = ACEffectRegistry.BUBBLED;
                player.world.playSound(entity.posX, entity.posY, entity.posZ, ACSoundRegistry.SEA_STAFF_BUBBLE, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
                break;
            case 2:
                potion = ACEffectRegistry.MAGNETIZING;
                break;
            case 3:
                potion = ACEffectRegistry.STUNNED;
                break;
            case 4:
                potion = ACEffectRegistry.IRRADIATED;
                amplifier = IrradiatedEffect.BLUE_LEVEL;
                break;
            default:
                break;
        }
        if (potion != null) {
            if (message.remove) {
                living.removePotionEffect(potion);
            } else {
                living.addPotionEffect(new PotionEffect(potion, message.duration, amplifier));
            }
        }
    }
}
