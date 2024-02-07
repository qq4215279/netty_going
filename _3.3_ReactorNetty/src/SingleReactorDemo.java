import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * SingleReactorDemo
 *
 * @author liuzhen
 * @version 1.0.0 2024/1/25 11:26
 */
public class SingleReactorDemo {
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
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(reactorGroup, reactorGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast();
                            // 此处省略相关代码
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            reactorGroup.shutdownGracefully();
        }
    }
}
