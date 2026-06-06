package com.zzhalex233.alexscaves.server.message;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.item.UpdatesStackTags;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateItemTagMessage implements IMessage {
    private int entityId;
    private ItemStack itemStackFrom = ItemStack.EMPTY;

    public UpdateItemTagMessage() {
    }

    public UpdateItemTagMessage(int entityId, ItemStack itemStackFrom) {
        this.entityId = entityId;
        this.itemStackFrom = itemStackFrom.copy();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        itemStackFrom = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeItemStack(buf, itemStackFrom);
    }

    public static class ServerHandler implements IMessageHandler<UpdateItemTagMessage, IMessage> {
        @Override
        public IMessage onMessage(UpdateItemTagMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> updateHeldItem(player, message));
            return null;
        }
    }

    public static class ClientHandler implements IMessageHandler<UpdateItemTagMessage, IMessage> {
        @Override
        public IMessage onMessage(UpdateItemTagMessage message, MessageContext ctx) {
            AlexsCaves.PROXY.runOnClientThread(() -> {
                EntityPlayer player = AlexsCaves.PROXY.getClientSidePlayer();
                if (player != null) {
                    updateHeldItem(player, message);
                }
            });
            return null;
        }
    }

    private static void updateHeldItem(EntityPlayer player, UpdateItemTagMessage message) {
        Entity holder = player.world.getEntityByID(message.entityId);
        if (holder instanceof EntityLivingBase && !message.itemStackFrom.isEmpty()) {
            EntityLivingBase living = (EntityLivingBase) holder;
            ItemStack source = message.itemStackFrom;
            for (EnumHand hand : EnumHand.values()) {
                ItemStack held = living.getHeldItem(hand);
                if (!held.isEmpty() && held.getItem() == source.getItem() && held.getItem() instanceof UpdatesStackTags) {
                    ((UpdatesStackTags) held.getItem()).updateTagFromServer(holder, held, source.getTagCompound());
                    return;
                }
            }
        }
    }
}
