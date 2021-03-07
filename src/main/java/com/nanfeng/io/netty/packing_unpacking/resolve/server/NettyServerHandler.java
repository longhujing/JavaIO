package com.nanfeng.io.netty.packing_unpacking.resolve.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private int counter;

    public NettyServerHandler() {
        this.counter = 0;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        log.info("Read From Client: {}, counter: {}", message, ++counter);

        String resp = "QUERY TIME ORDER".equalsIgnoreCase(message) ? LocalDateTime.now().toString() : "BAD REQUEST";
        ByteBuf response = Unpooled.copiedBuffer((resp + System.getProperty("line.separator")).getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
