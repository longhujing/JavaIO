package com.nanfeng.io.bio.server;

import com.nanfeng.io.bio.common.dto.BioMessage;
import com.nanfeng.io.bio.common.util.BioMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author nanfeng
 */
@Slf4j
public class SocketHandler implements Runnable {

    private final Socket socket;
    private final ConcurrentLinkedQueue<Socket> clientSockets;

    public SocketHandler(Socket socket, ConcurrentLinkedQueue<Socket> clientSockets) {
        this.socket = socket;
        this.clientSockets = clientSockets;
    }

    @Override
    public void run() {
        while (true) {
            try {
                readMsg(this.socket);
            } catch (IOException e) {
                e.printStackTrace();
                clientSockets.remove(socket);
                break;
            }
        }
    }

    private void readMsg(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        BioMessage bioMessage = BioMessageUtils.readMessage(inputStream);

        String host = bioMessage.getHeader().getHost();
        Integer port = bioMessage.getHeader().getPort();
        String nickName = bioMessage.getHeader().getNickName();
        log.info("[SocketHandler] Receive Message From host: {}, port: {}, user: {}", host, port, nickName);
        log.info("[SocketHandler] Message: {}", bioMessage);

        dispatchToAllClients(socket, bioMessage);
    }

    private void dispatchToAllClients(Socket socket, BioMessage message) throws IOException {
        String content = message.getContent();

        if (StringUtils.isEmpty(content)) {
            log.info("[SocketHandler] Receive Empty Message !!!");
            return;
        }

        for (Socket client : clientSockets) {
            if (Objects.equals(socket, client)) {
                continue;
            }

            OutputStream outputStream = client.getOutputStream();
            BioMessageUtils.sendMessage(outputStream, message);
        }
    }

}
