package application.client;

import application.common.enums.Group;
import application.common.utils.impl.*;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class Client {
    private final Emitter emitter = new Emitter(5504, Group.GATEWAY.getIp());
    private final List<QueryMessage> messages = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Client() {
        new Thread(() -> {
            try {
                new Receiver(5504, "224.0.0.14", this::store);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }).start();

        scheduler.scheduleAtFixedRate(this::sync, 0, 5, TimeUnit.SECONDS);
    }

    private void store(MessageDTO<List<QueryMessage>> msg) {
        this.messages.addAll(msg.message());
    }

    private void sync() {
        this.emitter.send(new MessageDTO<>("224.0.0.14", true));
    }

    public List<QueryMessage> readFile(UnaryOperator<List<QueryMessage>> op) {
        List<QueryMessage> messages = this.readFile();
        return op.apply(messages);
    }

    public List<QueryMessage> readFile() {
        return this.messages;
    }

    public void shutdown() {
        this.scheduler.shutdownNow();
    }
}
