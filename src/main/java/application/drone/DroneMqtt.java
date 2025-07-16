package application.drone;

import application.common.enums.Topic;
import application.common.utils.dto.MessageDTO;
import application.common.utils.interfaces.Emitter;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DroneMqtt extends DroneAbstract implements Drone {
    private final char separator;
    private final LocalDateTime stop;
    private final Emitter emitter;
    private final ScheduledExecutorService executor;

    public DroneMqtt(Emitter emitter, char separator) {
        this.separator = separator;
        this.stop = LocalDateTime.now().plusMinutes(4);
        this.emitter = emitter;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void sendMessage(MessageDTO message) {
        System.out.println(LocalDateTime.now() + ": " + message.message());
        MessageDTO topicAdded = new MessageDTO(
                message.message(),
                Topic.DRONE
        );
        this.emitter.send(topicAdded);
    }

    private String collectData() {
        return getString(separator);
    }

    @Override
    public void startDrone() {
        try {
            Runnable call = () -> this.sendMessageProcessor().run();

            this.executor.schedule(call, 500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable sendMessageProcessor() {
        return () -> {
            this.sendMessage(new MessageDTO<>(collectData()));
            if (LocalDateTime.now().isBefore(stop)) {
                int random = (int) (Math.random() * 10 % 5);
                this.executor.schedule(() -> sendMessageProcessor().run(), random, TimeUnit.SECONDS);
            } else {
                emitter.close();
                System.exit(0);
            }
        };
    }
}
