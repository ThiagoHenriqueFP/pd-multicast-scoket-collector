package application.common.utils.impl.emitter;

import application.common.utils.dto.MessageDTO;
import application.common.utils.interfaces.Emitter;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.util.UUID;

public class MqttEmitter implements Emitter {
    private static final String brokerUrl = "tcp://127.0.0.1:1883";
    private MqttClient client;
    private String clientId;

    public MqttEmitter() {
        this.clientId = UUID.randomUUID().toString();
        try {
            this.client =
                    new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            client.connect(options);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Emitter send(MessageDTO<T> message) {
        try {
            byte[] bytes = message.objectToByteArray();
            MqttMessage mqttMessage = new MqttMessage(bytes);
            mqttMessage.setQos(1);
            client.publish(message.topic(), mqttMessage);
        } catch (IOException | MqttPersistenceException e) {
            throw new RuntimeException(e);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        return this;
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
