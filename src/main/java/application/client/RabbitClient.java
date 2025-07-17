package application.client;

import application.common.utils.dto.MessageDTO;
import application.common.utils.impl.receiver.RabbitReceiver;
import application.common.utils.interfaces.ActionReceiver;
import application.common.utils.interfaces.Receiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class RabbitClient {
    private final Receiver receiver;
    private final List<String> messages = new ArrayList<>();

    public RabbitClient(List<String> topics, ActionReceiver action) {
        this.receiver = new RabbitReceiver(topics);
        Runnable run = () -> this.receiver.start(it -> store(it, action));
        run.run();
    }

    private void store(MessageDTO<String> msg, ActionReceiver action) throws IOException {
        this.messages.add(msg.message());
        action.execute(msg);
    }

    public List<String> readFile(UnaryOperator<List<String>> op) {
        List<String> messages = this.readFile();
        return op.apply(messages);
    }

    public List<String> readFile() {
        return this.messages;
    }

    public void shutdown() {
        this.receiver.close();
    }
}
