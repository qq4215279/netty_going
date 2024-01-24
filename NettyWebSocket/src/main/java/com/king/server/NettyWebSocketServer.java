package com.king.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty WebSocket Server {
 */
public class NettyWebSocketServer {
    /**
     * main entry
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println( "NettyWebSocketServer: main()" );
        /*
         * 定义 {boss, worker} 线程组
         */
        // TODO: 主线程组 - 用于接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // TODO: 工作线程组 - 负责IO交互工作
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // TODO: netty服务器的创建,辅助工具类,用于服务器通道的一系列配置
            ServerBootstrap server = new ServerBootstrap();
            // TODO: 绑定两个线程组
            server.group(bossGroup, workerGroup)
                    // TODO: 指定NIO的模式
                    .channel(NioServerSocketChannel.class)
                    // TODO: 用于处理workerGroup子处理器
                    .childHandler(new NettyWebSocketServerInitialzer());
            // TODO: 启动server,并且设置8086为启动的端口号,同时启动方式为同步
            ChannelFuture future = server.bind(8086).sync();
            //TODO: 监听关闭的channel,设置为同步方式
            future.channel().closeFuture().sync();
        } finally {
            // TODO: 退出线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}