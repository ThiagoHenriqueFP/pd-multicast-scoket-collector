package application.common.utils.interfaces;

import application.common.utils.impl.MessageDTO;

import java.io.IOException;

public interface ActionReceiver {
    void execute(MessageDTO message) throws IOException;
}
