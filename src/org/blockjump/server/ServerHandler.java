package org.blockjump.server;

import org.blockjump.server.objects.Connection;
import org.blockjump.server.packets.PacketHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class ServerHandler extends SimpleChannelHandler {

    private PacketHandler packetHandler;

    public ServerHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        String message = "";
        while (buf.readable()) {
            message += (char) buf.readByte();
        }
        packetHandler.addProcess(new Connection(e.getChannel(), message));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        Channel ch = e.getChannel();
        ch.close();
    }

}
