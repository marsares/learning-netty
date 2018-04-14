package com.marsares.org.learning_netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * Created by marsares on 18/4/14.
 */
public class LogEventMonitor {
    private EventLoopGroup group;
    private Bootstrap bootstrap;

    public LogEventMonitor(InetSocketAddress address){
        group=new NioEventLoopGroup();
        bootstrap=new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST,true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline=ch.pipeline();
                        pipeline.addLast(new LogEventDecoder());
                        pipeline.addLast(new LogEventHandler());
                    }
                }).localAddress(address);
    }
    public Channel bind(){
        return bootstrap.bind().syncUninterruptibly().channel();
    }
    public void stop(){
        group.shutdownGracefully();
    }
    public static void main(String[]args)throws InterruptedException{
        LogEventMonitor monitor=new LogEventMonitor(new InetSocketAddress(4096));
        try{
            Channel channel=monitor.bind();
            System.out.println("LogEventMonitor running");
            channel.closeFuture().sync();
        }finally{
            monitor.stop();
        }
    }
}
