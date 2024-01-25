package com.king.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * NettyWebSocketServerInitialzer子处理器类
 */
public class NettyWebSocketServerInitialzer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        /**
         * websocket基于http协议,所以要有http编解码器
         * 服务端编解码器用HttpServerCodec
         */
        pipeline.addLast(new HttpServerCodec());
        // TODO: 针对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        // TODO: 针对数据聚合的支持
        pipeline.addLast(new HttpObjectAggregator(1024*32));
        /**
         * 针对websocket服务器处理的协议
         * 用于指定给客户端连接访问的路由: /websocket
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
        // TODO: 自定义的handler
        pipeline.addLast(new NettyWebSocketServerHandler());
    }
}