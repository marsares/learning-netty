package com.marsares.org.learning_netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Created by marsares on 18/4/14.
 */
public class SecureChatServerInitializer extends ChatServerInitializer {
    private SSLContext context;
    public SecureChatServerInitializer(ChannelGroup group,SSLContext context) {
        super(group);
        this.context=context;
    }
    @Override
    protected void initChannel(Channel ch)throws Exception{
        super.initChannel(ch);
        SSLEngine engine=context.createSSLEngine();
        engine.setUseClientMode(false);
        ch.pipeline().addFirst(new SslHandler(engine));
    }
}
