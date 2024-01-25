package netty.king.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoNettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channel active... ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server channel read....");
        //ctx.write(msg);
        /*try {
            System.out.println(ctx.channel().remoteAddress() + "->Server : " + msg);
            ctx.write("Server write : " + msg);
            ctx.flush();
        } finally {
            ReferenceCountUtil.release(msg);
        }*/

        String result = "server to client!";
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(result.getBytes());
        ctx.channel().writeAndFlush(buf);

        /*ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes("\r\n".getBytes());
        ctx.channel().writeAndFlush(buf2);*/

        /*ByteBuf in = (ByteBuf)msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        ctx.write(in);*/
        System.out.println("Server received: " + msg);

        System.out.println("==========");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channel read complete.");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}