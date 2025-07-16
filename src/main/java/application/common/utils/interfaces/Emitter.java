package application.common.utils.interfaces;

import application.common.utils.dto.MessageDTO;

public interface Emitter {
    <T> Emitter send(MessageDTO<T> message);
    void close();
}
