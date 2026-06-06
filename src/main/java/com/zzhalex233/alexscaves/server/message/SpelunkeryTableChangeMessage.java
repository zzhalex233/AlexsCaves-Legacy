package com.zzhalex233.alexscaves.server.message;

import com.zzhalex233.alexscaves.server.inventory.SpelunkeryTableContainer;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SpelunkeryTableChangeMessage implements IMessage {
    private boolean pass;

    public SpelunkeryTableChangeMessage() {
    }

    public SpelunkeryTableChangeMessage(boolean pass) {
        this.pass = pass;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pass = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(pass);
    }

    public static class Handler implements IMessageHandler<SpelunkeryTableChangeMessage, IMessage> {
        @Override
        public IMessage onMessage(SpelunkeryTableChangeMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (player.openContainer instanceof SpelunkeryTableContainer) {
                    ((SpelunkeryTableContainer) player.openContainer).onMessageFromScreen(player, message.pass);
                }
            });
            return null;
        }
    }
}
