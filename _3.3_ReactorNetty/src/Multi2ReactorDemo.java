import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Multi2ReactorDemo
 *
 * @author liuzhen
 * @version 1.0.0 2024/1/25 11:25
 */
public class Multi2ReactorDemo {
    /**
     * Netty Code
     * Multi Thread 2 Reactor Module
     */
    public void bindMulti2Reactor(int port) {
        EventLoopGroup acceptorGroup = new NioEventLoopGroup();
        NioEventLoopGroup nioGroup = new NioEventLoopGroup();
        try {
            /**
             * start multi 2 thread reactor
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
}
