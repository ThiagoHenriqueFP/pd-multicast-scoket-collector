package application.common.utils.interfaces;

import application.common.utils.dto.MessageDTO;

import java.io.IOException;

public interface ActionReceiver {
    void execute(MessageDTO message) throws IOException;
}
