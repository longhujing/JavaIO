package com.nanfeng.io.bio.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author nanfeng
 */
@Slf4j
public class BioServer {

    private final ConcurrentLinkedQueue<Socket> clientSockets;
    private final ThreadPoolExecutor bioServerThreadPool;
    private final ServerSocket serverSocket;
    private final int port;

    public BioServer(int port) throws IOException {
        this.port = port;
        this.bioServerThreadPool = getBioServerThreadPool();
        this.clientSockets = new ConcurrentLinkedQueue<>();
        this.serverSocket = new ServerSocket();
        this.serverSocket.bind(new InetSocketAddress(this.port));

        log.info("[BioServer] BioServer Start Successfully, listen port: {}", this.port);
    }

    public static void main(String[] args) throws IOException {

        BioServer bioServer = new BioServer(8080);
        bioServer.listen();

    }

    private void listen() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            clientSockets.add(socket);
            log.info("[BioServer] Get Client, host: {}, port: {}", socket.getInetAddress().getHostAddress(), socket.getPort());
            log.info("[BioServer] Current Online People: {}", clientSockets.size());
            bioServerThreadPool.execute(new SocketHandler(socket, clientSockets));
        }
    }

    private ThreadPoolExecutor getBioServerThreadPool() {
        return new ThreadPoolExecutor(10,
                20,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1024),
                new ThreadFactoryBuilder().setNameFormat("bioServer-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

}
