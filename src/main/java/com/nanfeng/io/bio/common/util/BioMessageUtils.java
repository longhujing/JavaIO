package com.nanfeng.io.bio.common.util;

import com.nanfeng.io.bio.common.BioException;
import com.nanfeng.io.common.IoMessage;
import com.nanfeng.io.common.IoMessageHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author nanfeng
 */
public class BioMessageUtils {

    public static IoMessage readMessage(InputStream inputStream) throws IOException {
        if (Objects.isNull(inputStream)) {
            throw new BioException("Can Not Read Message Of Null !!!");
        }

        ObjectInputStream ois = new ObjectInputStream(inputStream);
        try {
            return (IoMessage) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        throw new BioException("Can Not Read Message");
    }

    public static void sendMessage(OutputStream outputStream, IoMessageHeader header, String content) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(new IoMessage(header, content));
    }

    public static void sendMessage(OutputStream outputStream, IoMessage message) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(message);
    }
}
