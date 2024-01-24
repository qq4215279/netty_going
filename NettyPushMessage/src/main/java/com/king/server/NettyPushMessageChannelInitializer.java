package com.king.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Netty Push Message ChannelInitializer
 */
public class NettyPushMessageChannelInitializer extends ChannelInitializer<SocketChannel> {
    /**
     * init Channel
     * @param ch
     */
    @Override
    protected void initChannel(SocketChannel ch) {
        // TODO: 设置log监听器,并且日志级别为debug,方便观察运行流程
        ch.pipeline().addLast("logging", new LoggingHandler("DEBUG"));
        // TODO: 设置编解码器
        ch.pipeline().addLast("http-codec", new HttpServerCodec());
        // TODO: 设置聚合器
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        // TODO: 用于大数据流的分区传输
        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        //TODO: 自定义业务 handler
        ch.pipeline().addLast("handler", new NettyPushMessageHandler());
    }
}