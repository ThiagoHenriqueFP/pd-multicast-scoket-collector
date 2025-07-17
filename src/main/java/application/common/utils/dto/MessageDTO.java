package application.common.utils.dto;

import application.common.enums.Topic;
import application.common.utils.interfaces.ConvertToByteArray;

import java.io.*;

public record MessageDTO<T>(
        T message,
        String ip,
        boolean ack,
        boolean retrieve,
        String topic
) implements Serializable, ConvertToByteArray {

    public MessageDTO(T message) {
        this(message, "", false, false, "");
    }

    public MessageDTO(T message, String ip, boolean ack) {
        this(message, ip, ack, false, "");
    }

    public MessageDTO(String ip, boolean retrieve) {
        this(null, ip, false, retrieve, "");
    }

    public MessageDTO(T message, Topic topic) {
        this(message, null, false, false, topic.getTopic());
    }

    public static MessageDTO fromBytes(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object msg = ois.readObject();
            return (MessageDTO) msg;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
