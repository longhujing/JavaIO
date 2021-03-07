package com.nanfeng.io.netty.packing_unpacking.problem.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private int counter;

    private byte[] req;

    public NettyClientHandler() {
        this.counter = 0;
        this.req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 100; i++) {
            ByteBuf msg = Unpooled.copiedBuffer(req);
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] resp = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(resp);

        String message = new String(resp, StandardCharsets.UTF_8);
        log.info("Receive From Server: {}, counter: {}", message, ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Occur An Error, " + cause.getMessage());
        ctx.close();
    }

}
