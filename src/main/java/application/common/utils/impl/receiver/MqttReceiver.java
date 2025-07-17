package application.common.utils.impl.receiver;

import application.common.enums.Topic;
import application.common.utils.dto.MessageDTO;
import application.common.utils.interfaces.ActionReceiver;
import application.common.utils.interfaces.Receiver;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.IOException;
import java.util.UUID;

public class MqttReceiver implements Receiver {
    private static final String brokerUrl = "tcp://0.tcp.sa.ngrok.io:13590";
    private MqttClient client;
    private String clientId;

    public MqttReceiver(Topic topic, ActionReceiver action) throws MqttException {
        this.clientId = UUID.randomUUID().toString();
        this.client = new MqttClient(brokerUrl, clientId, new MqttDefaultFilePersistence("persistence/"));
        MqttConnectOptions op = new MqttConnectOptions();
        op.setCleanSession(false);
        op.setUserName("user1");
        op.setPassword("user1".toCharArray());
        client.connect(op);
        client.setCallback(new MqttCallback() {

            public void connectionLost(Throwable c) {
                System.out.println("[Consumer] connection Lost: " + c.getMessage());
                c.printStackTrace();
            }

            public void messageArrived(String top, MqttMessage message) {
                MessageDTO decoded = MessageDTO.fromBytes(message.getPayload());
                try {
                    System.out.println("INFO: From " + topic);
                    action.execute(decoded);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        Runnable run = () -> {
            try {
                client.subscribe(topic.getTopic(), 1);
                System.out.println("INFO: Running mqtt receiver");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        };

        run.run();
    }

    @Override
    public void start(ActionReceiver action) {
    }

    @Override
    public void close() {
        try {
            this.client.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
