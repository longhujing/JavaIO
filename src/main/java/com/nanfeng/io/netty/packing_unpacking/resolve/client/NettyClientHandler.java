package com.nanfeng.io.netty.packing_unpacking.resolve.client;

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
            byte[] bytes = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes(StandardCharsets.UTF_8);
            ByteBuf msg = Unpooled.copiedBuffer(bytes);
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        log.info("Receive From Server: {}, counter: {}", message, ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Occur An Error, " + cause.getMessage());
        ctx.close();
    }

}