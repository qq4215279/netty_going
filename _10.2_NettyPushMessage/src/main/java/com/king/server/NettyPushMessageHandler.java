package com.king.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.Date;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

/**
 * Netty Push Message Server Handler
 */
public class NettyPushMessageHandler extends SimpleChannelInboundHandler<Object> {
    // TODO: define logger
    private final Logger logger=Logger.getLogger(this.getClass());
    // TODO: WebSocket Handshaker
    private WebSocketServerHandshaker handShaker;

    /**
     * channelRead0
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("Received message: " + msg);
        if(msg instanceof FullHttpRequest) {
            // TODO: 以http请求形式接入,实际使用WebSocket方式
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if(msg instanceof  WebSocketFrame) {
            // TODO: 处理 WebSocket客户端消息
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {}
    }

    /**
     * channelActive
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO: 添加连接
        logger.debug("Client join in connection: " + ctx.channel());
        NettyPushMessageChannelSupervise.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO: 断开连接
        logger.debug("Client shuts connection: " + ctx.channel());
        NettyPushMessageChannelSupervise.removeChannel(ctx.channel());
    }

    /**
     * channelReadComplete
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * handlerWebSocketFrame
     * @param ctx
     * @param frame
     */
    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        // TODO: 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handShaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // TODO: 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // TODO: 本例程仅支持文本消息,不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.debug("Supports text message only, does not support binary message.");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }
        // TODO: 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        logger.debug("Server received: " + request);
        TextWebSocketFrame tws = new TextWebSocketFrame("client id(" + ctx.channel().id() + ") " + request + ", server pushes at " + new Date().toString() + ".");
        // TODO: 群发消息
        NettyPushMessageChannelSupervise.sendToAll(tws);
    }

    /**
     * handleHttpRequest
     * 唯一的一次http请求,用于创建WebSocket
     * @param ctx
     * @param req
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // TODO: Upgrade为WebSocket方式,过滤掉get/Post
        if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
            // TODO: 若不是WebSocket方式,则将req(BAD_REQUEST)返回给客户端
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8086/pushmsg", null, false);
        handShaker = wsFactory.newHandshaker(req);
        if (handShaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * sendHttpResponse
     * 拒绝不合法的请求,并返回错误信息
     * @param ctx
     * @param req
     * @param res
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // TODO: 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // TODO: 如果是非Keep-Alive则关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
}