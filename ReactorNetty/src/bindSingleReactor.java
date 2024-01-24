/**
 * Netty Code
 * Single Thread Reactor Module
 */
public void bindSingleReactor(int port) {
    EventLoopGroup reactorGroup = new NioEventLoopGroup();
    try {
        /**
         * start single thread reactor
          */
        ServerBootstrap server_bootstrap = new ServerBootstrap();
        server_bootstrap.group(reactorGroup, reactorGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast();
                // 此处省略相关代码
            }
        });
        Channel ch = server_bootstrap.bind(port).sync().channel();
        ch.closeFuture().sync();
    } catch(InterruptedException e) {
        e.printStackTrace();
    } finally {
        reactorGroup.shutdownGracefully();
    }
}