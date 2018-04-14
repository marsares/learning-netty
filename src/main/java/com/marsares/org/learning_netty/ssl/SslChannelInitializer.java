package com.marsares.org.learning_netty.ssl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class SslChannelInitializer extends ChannelInitializer<Channel> {
    SSLContext context;
    boolean client;
    boolean startTls;

    public SslChannelInitializer(SSLContext context, boolean client, boolean startTls) {
        this.context = context;
        this.client = client;
        this.startTls = startTls;
    }

    protected void initChannel(Channel channel) throws Exception {
        SSLEngine engine=context.createSSLEngine();
        engine.setUseClientMode(client);
        channel.pipeline().addFirst("ssl",new SslHandler(engine,startTls));
    }
}
