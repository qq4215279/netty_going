/**
 * Netty Code
 * Multi Thread Reactor Module
 */
public void bindMultiReactor(int port) {
    EventLoopGroup acceptorGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup nioGroup = new NioEventLoopGroup();
    try {
        /**
         * start multi thread reactor
         */
        ServerBootstrap server_bootstrap = new ServerBootstrap();
        server_bootstrap.group(acceptorGroup, nioGroup)
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
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        acceptorGroup.shutdownGracefully();
        nioGroup.shutdownGracefully();
    }
}