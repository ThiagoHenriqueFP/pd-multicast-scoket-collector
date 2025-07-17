package application.common.utils.dto;

import application.common.utils.interfaces.ConvertToByteArray;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public record QueryMessage(
        Long timestamp,
        String message
) implements Serializable, ConvertToByteArray {
    public QueryMessage(String message) {
        this(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), message);
    }

    @Override
    public String toString() {
        return "QueryMessage{" +
                "timestamp=" + LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC) +
                ", message='" + message + '\'' +
                '}';
    }
}
