package application.common.utils.impl.receiver;

import application.common.utils.dto.MessageDTO;
import application.common.utils.dto.QueryMessage;
import application.common.utils.interfaces.ActionReceiver;
import application.common.utils.interfaces.Receiver;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RabbitReceiver implements Receiver {
    private static final ConnectionFactory factory = new ConnectionFactory();
    private final Connection connection;
    private final String exchangeName = "DRONE_DATA";
    private final String queueName;
    private final Channel channel;
    public RabbitReceiver(List<String> topics) {
        factory.setHost("0.tcp.sa.ngrok.io");
        factory.setPort(15411);
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, "topic", true);
            queueName = channel.queueDeclare("", true, false, false, null).getQueue();

            for (String topic : topics)
                channel.queueBind(queueName, exchangeName, topic);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void start(ActionReceiver action) {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            MessageDTO<String> dto = MessageDTO.fromBytes(delivery.getBody());
            action.execute(dto);
        };

        try {
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
