package com.marsares.org.learning_netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * Created by marsares on 18/4/14.
 */
public class ChatServer {
    private ChannelGroup group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private EventLoopGroup workerGroup=new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture start(InetSocketAddress address){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(workerGroup).channel(NioServerSocketChannel.class).childHandler(createInitializer(group));
        ChannelFuture future = bootstrap.bind(address).syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    public void destroy(){
        if(channel!=null){
            channel.close();
        }
        group.close();
        workerGroup.shutdownGracefully();
    }

    protected ChannelInitializer<Channel>createInitializer(ChannelGroup group){
        return new ChatServerInitializer(group);
    }

    public static void main(String[]args){
        final ChatServer server = new ChatServer();
        ChannelFuture future = server.start(new InetSocketAddress(2048));
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                server.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }
}
