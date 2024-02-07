package netty.king;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.channels.FileChannel;

public class HelloNettyServer {

    /**
     * netty-4.1.48.Final 源码
     *  1. buffer [netty-buffer]  ->  io.netty.buffer
     *  2. codec [netty-codec]  ->  io.netty.handler.codec
     *  3. common [netty-common]  ->  io.netty.util
     *  4. handler [netty-handler]  ->    io.netty.handler
     *  5. resolver [netty-resolver]  ->  io.netty.resolver
     *  6. transport [netty-transport]  ->  io.netty.bootstrap  &&  io.netty.channel
     *
     * netty-4.1.48.Final.jar
     * io.netty.bootstrap  ->      transport [netty-transport]
     * io.netty.buffer     ->      buffer [netty-buffer]
     * io.netty.channel    ->      transport [netty-transport]
     * io.netty.handler    ->      handler [netty-handler]
     * io.netty.internal   ->
     * io.netty.resolver   ->      resolver [netty-resolver]
     * io.netty.util       ->      common [netty-common]
     *
     * 3：
     * Handle
     *
     * 4：
     * ByteBuf ByteBufHolder(DefaultByteBufHolder) ByteBufAllocator(AbstractByteBufAllocator UnpooledByteBufAllocator PooledByteBufAllocator)
     * CompositeByteBuf ByteBufUtil
     * AbstractByteBuf Unpooled  ReferenceCounted  FileChannel
     *
     * 5:
     * Channel LocalChannel  EmbeddedChannel  ChannelPipeline  ChannelConfig
     *
     * 6:
     * Channel: ChannelPipeline(DefaultChannelPipeline)  ChannelFuture  ChannelPromise  EventLoop  ByteBuf
     * ChannelHandler(ChannelInboundHandler  ChannelOutboundHandler  ChannelHandlerAdapter(ChannelInboundHandlerAdapter ChannelOutboundHandlerAdapter))
     * ChannelHandlerContext
     * EventLoopGroup  EventExecutorGroup(DefaultEventExecutorGroup)
     *
     * 7:
     * ReferenceCountUtil  ChannelOutboundHandler  MessageToByteEncoder  MessageToMessageEncoder
     * ChannelInboundHandler  ByteToMessageDecoder  ReplayingDecoder  MessageToMessageDecoder
     * ByteToMessageCodec  MessageToMessageCodec  CombinedChannelDuplexHandler
     *
     * 8.
     * AbstractBootstrap(Bootstrap  ServerBootstrap)
     * ChannelOption
     *
     */



    private int port;

    /**
     * @param port
     */
    public HelloNettyServer(int port) {
        this.port = port;
    }

    /**
     * @throws Exception
     */
    public void run() throws Exception {
        //


        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new HelloNettyServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("开始建立Netty服务器...");
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new HelloNettyServer(port).run();
    }
}
