package org.blockjump.server.objects;

import org.jboss.netty.channel.Channel;

public class Connection {

    private Channel channel;
    private String  buffer;

    public Connection(Channel channel, String buf) {
        this.channel = channel;
        this.buffer = buf;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getBuffer() {
        return buffer;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

}
