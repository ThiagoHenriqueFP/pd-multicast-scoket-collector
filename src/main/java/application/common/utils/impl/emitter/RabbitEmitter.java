package application.common.utils.impl.emitter;

import application.common.utils.dto.MessageDTO;
import application.common.utils.interfaces.Emitter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitEmitter implements Emitter {
    private static final ConnectionFactory factory = new ConnectionFactory();
    private final Connection connection;
    private final String exchangeName = "DRONE_DATA";
    private final Channel channel;

    public RabbitEmitter() {
        factory.setHost("0.tcp.sa.ngrok.io");
        factory.setPort(15411);

        factory.setUsername("guest");
        factory.setPassword("guest");

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, "topic", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Emitter send(MessageDTO<T> message) {
        try {
            System.out.println("INFO: sending to rabbit queue");
            String topic = message.topic().replaceAll("/", ".");
            System.out.println(topic);
            channel.basicPublish(
                    exchangeName,
                    topic,
                    null,
                    message.objectToByteArray()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
