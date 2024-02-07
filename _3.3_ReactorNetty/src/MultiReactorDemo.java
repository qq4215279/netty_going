import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * MultiReactorDemo
 *
 * @author liuzhen
 * @version 1.0.0 2024/1/25 11:25
 */
public class MultiReactorDemo {
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
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(acceptorGroup, nioGroup)
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptorGroup.shutdownGracefully();
            nioGroup.shutdownGracefully();
        }
    }
}
