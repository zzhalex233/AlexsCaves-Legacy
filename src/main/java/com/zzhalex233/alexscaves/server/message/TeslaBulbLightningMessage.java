package com.zzhalex233.alexscaves.server.message;

import com.zzhalex233.alexscaves.AlexsCaves;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TeslaBulbLightningMessage implements IMessage {
    private Vec3d from;
    private Vec3d to;

    public TeslaBulbLightningMessage() {
    }

    public TeslaBulbLightningMessage(Vec3d from, Vec3d to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        from = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        to = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(from.x);
        buf.writeDouble(from.y);
        buf.writeDouble(from.z);
        buf.writeDouble(to.x);
        buf.writeDouble(to.y);
        buf.writeDouble(to.z);
    }

    public static class Handler implements IMessageHandler<TeslaBulbLightningMessage, IMessage> {
        @Override
        public IMessage onMessage(TeslaBulbLightningMessage message, MessageContext ctx) {
            AlexsCaves.PROXY.spawnTeslaBulbLightning(null, message.from, message.to);
            return null;
        }
    }
}
