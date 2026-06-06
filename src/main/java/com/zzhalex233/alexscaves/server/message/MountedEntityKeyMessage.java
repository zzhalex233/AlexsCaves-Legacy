package com.zzhalex233.alexscaves.server.message;

import com.zzhalex233.alexscaves.server.entity.util.KeybindUsingMount;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MountedEntityKeyMessage implements IMessage {
    private int mountId;
    private int playerId;
    private int type;

    public MountedEntityKeyMessage() {
    }

    public MountedEntityKeyMessage(int mountId, int playerId, int type) {
        this.mountId = mountId;
        this.playerId = playerId;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mountId = buf.readInt();
        playerId = buf.readInt();
        type = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(mountId);
        buf.writeInt(playerId);
        buf.writeInt(type);
    }

    public static class Handler implements IMessageHandler<MountedEntityKeyMessage, IMessage> {
        @Override
        public IMessage onMessage(MountedEntityKeyMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                Entity mount = player.world.getEntityByID(message.mountId);
                Entity keyPresser = player.world.getEntityByID(message.playerId);
                if (mount instanceof KeybindUsingMount && keyPresser != null && keyPresser.getRidingEntity() == mount) {
                    ((KeybindUsingMount) mount).onKeyPacket(keyPresser, message.type);
                }
            });
            return null;
        }
    }
}
