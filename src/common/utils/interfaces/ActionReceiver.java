package common.utils.interfaces;

import common.utils.impl.MessageDTO;

import java.io.IOException;

public interface ActionReceiver {
    void execute(MessageDTO message) throws IOException;
}
