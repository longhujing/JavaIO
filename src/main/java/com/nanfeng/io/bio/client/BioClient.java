package com.nanfeng.io.bio.client;

import com.nanfeng.io.bio.common.util.BioMessageUtils;
import com.nanfeng.io.common.IoMessage;
import com.nanfeng.io.common.IoMessageHeader;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author nanfeng
 */
@Slf4j
public class BioClient {

    private final IoMessageHeader header;
    private final Socket socket;

    public BioClient(String host, int port, String nickName) throws IOException {
        this.socket = new Socket(host, port);
        this.header = new IoMessageHeader(host, port, nickName);
    }

    public static void main(String[] args) throws IOException {

        String host = "127.0.0.1";
        int port = 8080;
        String nickName = "xiaoming";

        BioClient client = new BioClient(host, port, nickName);

        new Thread(() -> {
            try {
                client.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        client.sendMessage();
    }

    private void sendMessage() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = reader.readLine();
            if ("QUIT".equalsIgnoreCase(line)) {
                break;
            }

            IoMessage message = new IoMessage(header, line);
            BioMessageUtils.sendMessage(socket.getOutputStream(), message);
        }
    }

    private void listen() throws IOException {
        while (true) {
            ObjectInputStream ois = new ObjectInputStream(this.socket.getInputStream());
            try {
                IoMessage message = (IoMessage) ois.readObject();
                System.out.println(String.format("%s Said: %s", message.getHeader().getNickName(), message.getContent()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                log.error("[BioClient] Read Message From Server Failed !!!");
            }
        }
    }

}
