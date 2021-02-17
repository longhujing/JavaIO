package com.nanfeng.io.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author nanfeng
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        log.info("[NettyServerHandler] Receive Msg From Client, msg => {}", buf.toString(StandardCharsets.UTF_8));

        // 模拟耗时操作, 异步解决
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String content = "Netty Server Execute Task-1, Thread: " + Thread.currentThread().getName();
            ctx.writeAndFlush(Unpooled.copiedBuffer(content.getBytes(StandardCharsets.UTF_8)));
        });

        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String content = "Netty Server Execute Task-2, Thread: " + Thread.currentThread().getName();
            ctx.writeAndFlush(Unpooled.copiedBuffer(content.getBytes(StandardCharsets.UTF_8)));
        });

        // 定时任务
        ctx.channel().eventLoop().schedule(() -> {
            String content = "This Is Schedule Task, Thread: " + Thread.currentThread().getName();
            ctx.writeAndFlush(Unpooled.copiedBuffer(content.getBytes(StandardCharsets.UTF_8)));
        }, 5, TimeUnit.SECONDS);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Already Receive Msg".getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
