package com.king.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

/**
 * Netty Push Message Server
 */
public class NettyPushMessageServer {
    // TODO: define logger
    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * init
     */
    private void init() {
        logger.info("Now is starting WebSocket Server...");
        /**
         * { bossGroup, workerGroup } thread
         */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new NettyPushMessageChannelInitializer());
            Channel channel = bootstrap.bind(8086).sync().channel();
            logger.info("WebSocket server starts successfully: " + channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("Runtime error: " + e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("WebSocket server is closed.");
        }
    }

    /**
     * main entry
     * @param args
     */
    public static void main( String[] args ) {
        System.out.println("NettyPushMessageServer: init");
        new NettyPushMessageServer().init();
    }
}