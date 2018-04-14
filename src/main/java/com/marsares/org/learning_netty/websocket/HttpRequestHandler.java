package com.marsares.org.learning_netty.websocket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.RandomAccessFile;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private String wsUri;

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if (wsUri.equalsIgnoreCase(msg.getUri())) {
            ctx.fireChannelRead(msg.retain());
        } else {
            if (HttpHeaders.is100ContinueExpected(msg)) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(response);
            }
            RandomAccessFile file=new RandomAccessFile(System.getProperty("user.dir")+"/index.html","rw");
            HttpResponse response = new DefaultHttpResponse(msg.getProtocolVersion(),HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/html; charset=UTF-8");
            boolean keepAlive=HttpHeaders.isKeepAlive(msg);
            if(keepAlive){
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);
            if(ctx.pipeline().get(SslHandler.class)==null){
                ctx.write(new DefaultFileRegion(file.getChannel(),0,file.length()));
            }else{
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture future=ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if(!keepAlive){
                future.addListener(ChannelFutureListener.CLOSE);
            }
            file.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        cause.printStackTrace();
        ctx.close();
    }
}
