package com.nanfeng.io.nio.client;

import com.alibaba.fastjson.JSON;
import com.nanfeng.io.common.IoMessage;
import com.nanfeng.io.common.IoMessageHeader;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author nanfeng
 */
@Slf4j
public class NioClient {

    private final SocketChannel socketChannel;
    private final ByteBuffer buffer;
    private final IoMessageHeader header;

    public NioClient(String host, int port, String nickName) throws IOException {
        this.socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        this.buffer = ByteBuffer.allocate(1024);
        this.header = new IoMessageHeader(host, port, nickName);

        socketChannel.configureBlocking(false);

        log.info("[NioClient] Client Start Successfully!, listen host: {}, port: {}", host, port);
    }

    public static void main(String[] args) throws IOException {
        NioClient client = new NioClient("127.0.0.1", 8080, "xiaoming");

        new Thread(() -> {
            try {
                client.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        client.sendMessage();
    }

    public void sendMessage() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String input = reader.readLine();
            if ("QUIT".equalsIgnoreCase(input)) {
                break;
            }

            IoMessage message = new IoMessage(header, input);

            buffer.clear();
            buffer.put(JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8));
            buffer.flip();
            socketChannel.write(buffer);

        }
    }

    public void listen() throws IOException {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        while (true) {
            int len = this.socketChannel.read(readBuffer);
            if (len <= 0) {
                continue;
            }

            readBuffer.flip();
            String jsonData = new String(readBuffer.array(), 0, len);
            IoMessage message = JSON.parseObject(jsonData, IoMessage.class);
            readBuffer.clear();

            System.out.printf("%s Said: %s%n", message.getHeader().getNickName(), message.getContent());
        }
    }

}
