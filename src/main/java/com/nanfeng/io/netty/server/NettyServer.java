package com.nanfeng.io.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nanfeng
 */
@Slf4j
public class NettyServer {

    /**
     * 处理连接请求线程组
     */
    private final EventLoopGroup bossGroup;

    /**
     * 处理客户端业务线程组
     */
    private final EventLoopGroup workerGroup;

    /**
     * 服务器启动对象
     */
    private final ServerBootstrap serverBootstrap;

    /**
     * bossGroup 和 workerGroup线程数为机器核数 * 2
     * 然而bossGroup只是处理客户端连接，大部分情况下并不需要这么多的线程，因此可以手动设置线程数 new NioEventLoopGroup(int n);
     *
     * @param port
     * @throws InterruptedException
     */
    public NettyServer(int port) throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            serverBootstrap = new ServerBootstrap()
                    // 设置线程组
                    .group(bossGroup, workerGroup)
                    // 设置服务器通道类型
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列最大等待连接数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 设置连接保持活跃
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 设置workerGroup的处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });

            log.info("[NettyServer] Netty Server Is Ready!");

            // 绑定指定端口且同步
            ChannelFuture future = serverBootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
            log.info("[NettyServer] Netty Server Closed");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer(8080);
    }

}
