package com.nanfeng.io.nio.server;

import com.alibaba.fastjson.JSON;
import com.nanfeng.io.common.IoMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author nanfeng
 */
@Slf4j
public class NioServer {

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;
    private final ByteBuffer buffer;
    private final ConcurrentLinkedQueue<SocketChannel> clientSockets;

    public NioServer(int port) throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.selector = Selector.open();
        buffer = ByteBuffer.allocate(1024);
        clientSockets = new ConcurrentLinkedQueue<>();

        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        log.info("[NioServer] Server Start Successfully!, port: {}", port);
    }

    public static void main(String[] args) throws IOException {
        NioServer server = new NioServer(8080);
        server.listen();
    }

    private void listen() throws IOException {

        try {
            while (true) {
                int count = selector.select();
                if (count <= 0) {
                    continue;
                }

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isAcceptable()) {
                        handleAccept(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        handleReadClient(selectionKey);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void handleAccept(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        clientSockets.add(socketChannel);
        log.info("[NioServer] Get New Client, host: {}, port: {}, Now Online Client Is: {}",
                socketChannel.socket().getInetAddress().getHostAddress(), socketChannel.socket().getPort(), clientSockets.size());
    }

    private void handleReadClient(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        int len = socketChannel.read(buffer);
        if (len <= 0) {
            return;
        }

        buffer.flip();
        String jsonData = new String(buffer.array(), 0, len);
        IoMessage ioMessage = JSON.parseObject(jsonData, IoMessage.class);
        buffer.clear();

        log.info("[NioServer] Receive Client Message: {}", ioMessage);

        dispatch(socketChannel, ioMessage);
    }

    private void dispatch(SocketChannel socketChannel, IoMessage message) throws IOException {
        for (SocketChannel channel : clientSockets) {
            if (Objects.equals(socketChannel, channel)) {
                continue;
            }

            ByteBuffer writeBuffer = ByteBuffer.wrap(JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8));
            channel.write(writeBuffer);
        }
    }

}
