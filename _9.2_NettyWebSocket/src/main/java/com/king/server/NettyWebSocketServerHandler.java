package com.king.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * NettyWebSocketServerHandler
 * @Description: 处理消息的handler
 */
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    // TODO: 用于记录和管理所有客户端的channle
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // TODO: 获取客户端传输过来的消息
        String content = msg.text();
        System.out.println("Received data: " + content);
        // TODO: 向客户端发送数据
        clients.writeAndFlush(new TextWebSocketFrame("This is server, received data: " + content));
    }

    /**
     * 当客户端连接服务端之后（打开连接）获取客户端的channle
     * 并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    /**
     * remove handler
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client shut, channle's long id is " + ctx.channel().id().asLongText());
        System.out.println("Client shut, channle's short id is " + ctx.channel().id().asShortText());
    }
}