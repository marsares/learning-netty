package com.marsares.org.learning_netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * Created by marsares on 18/4/14.
 */
public class LogEventBroadcaster {
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private String path;

    public LogEventBroadcaster(InetSocketAddress address, String path) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(new LogEventEncoder(address));
        this.path=path;
    }
    public void run()throws IOException{
        Channel channel = bootstrap.bind(0).syncUninterruptibly().channel();
        long pointer=0;
        for(;;){
            File file=new File(path);
            long len=file.length();
            if(len<pointer){
                pointer=len;
            }else{
                RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
                randomAccessFile.seek(pointer);
                String line;
                while((line=randomAccessFile.readLine())!=null){
                    channel.write(new LogEvent(null,-1,file.getAbsolutePath(),line));
                }
                channel.flush();
                pointer=randomAccessFile.getFilePointer();
                randomAccessFile.close();
            }
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                Thread.interrupted();
                break;
            }
        }
    }
    public void stop(){
        group.shutdownGracefully();
    }
    public static void main(String[]args)throws Exception{
        int port=4096;
        String path=System.getProperty("user.dir")+"/log.txt";
        LogEventBroadcaster broadcaster=new LogEventBroadcaster(new InetSocketAddress("255.255.255.255",port),path);
        try{
            broadcaster.run();
        }finally {
            broadcaster.stop();
        }
    }
}
