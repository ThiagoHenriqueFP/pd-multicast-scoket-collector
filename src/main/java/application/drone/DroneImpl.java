package application.drone;

import application.common.utils.dto.MessageDTO;
import application.common.utils.interfaces.Emitter;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DroneImpl extends DroneAbstract implements Drone {
    private final char separator;
    private final LocalDateTime stop;
    private final Emitter multiCastEmitter;
    private final ScheduledExecutorService executor;

    public DroneImpl(Emitter emitter, char separator) {
        this.separator = separator;
        this.stop = LocalDateTime.now().plusSeconds(15);
        this.multiCastEmitter = emitter;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void sendMessage(MessageDTO message) {
        System.out.println(LocalDateTime.now() + ": " + message);
        this.multiCastEmitter.send(message);
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
                multiCastEmitter.close();
                System.exit(0);
            }
        };
    }
}
