package com.marsares.org.learning_netty.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;

/**
 * Created by marsares on 18/4/14.
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf buf = msg.content();
        int i=buf.indexOf(0, buf.readableBytes(), LogEvent.SEPARATOR);
        String filename=buf.slice(0,i).toString(CharsetUtil.UTF_8);
        String logMsg=buf.slice(i+1,buf.readableBytes()).toString(CharsetUtil.UTF_8);
        LogEvent event = new LogEvent(msg.sender(),System.currentTimeMillis(),filename,logMsg);
        out.add(event);
    }
}
