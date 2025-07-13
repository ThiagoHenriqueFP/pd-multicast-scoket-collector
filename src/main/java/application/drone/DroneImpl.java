package application.drone;

import application.common.utils.impl.emitter.MultiCastEmitter;
import application.common.utils.dto.MessageDTO;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DroneImpl implements Drone {
    private final char separator;
    private final LocalDateTime stop;
    private final Position position;
    private final MultiCastEmitter multiCastEmitter;
    private final ScheduledExecutorService executor;

    public DroneImpl(Position position, String ip, int port, char separator) {
        this.position = position;
        this.separator = separator;
        this.stop = LocalDateTime.now().plusSeconds(15);
        this.multiCastEmitter = new MultiCastEmitter(port, ip);
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(LocalDateTime.now() + ": " + message);
        this.multiCastEmitter.send(new MessageDTO(message));
    }

    private String collectData() {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMaximumFractionDigits(2);
        String pressure = df.format(
                Math.min(
                        Math.random() * 1000 % 300
                        , 104)
        ) + "pa";
        String temperature = df.format(Math.min(
                Math.random() * 100 % 45
                , 10.00)
        ) + "°C";
        String radiation = df.format(Math.random() * 10) + "mSv";
        String humidity = df.format(
                Math.min(Math.random() * 100, 40)
        ) + "%";

        return pressure + separator + radiation + separator + temperature + separator + humidity;
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
            this.sendMessage(collectData());
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
