package com.marsares.org.learning_netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;

/**
 * Created by marsares on 18/4/14.
 */
public class SecureChatServer extends ChatServer {
    private SSLContext context;
    public SecureChatServer(SSLContext context){
        this.context=context;
    }
    @Override
    protected ChannelInitializer<Channel>createInitializer(ChannelGroup group){
        return new SecureChatServerInitializer(group,context);
    }
    private static SSLContext getSslContext() {
        return null;
    }
    public static void main(String[]args){
        SSLContext context=getSslContext();
        final SecureChatServer server = new SecureChatServer(context);
        ChannelFuture future = server.start(new InetSocketAddress(4096));
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                server.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }
}
