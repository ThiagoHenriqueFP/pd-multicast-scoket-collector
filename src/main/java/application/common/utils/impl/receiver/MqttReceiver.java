package application.common.utils.impl.receiver;

import application.common.enums.Topic;
import application.common.utils.interfaces.ActionReceiver;
import application.common.utils.interfaces.Receiver;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.UUID;

public class MqttReceiver implements Receiver {
    private static final String brokerUrl = "tcp://broker.emqx.io:1883";
    private MqttClient client;
    private String clientId;


    public MqttReceiver(String topic) throws MqttException {
        this.clientId = UUID.randomUUID().toString();
        this.client = new MqttClient(brokerUrl, clientId, new MqttDefaultFilePersistence("persistence/"));
        MqttConnectOptions op = new MqttConnectOptions();
        op.setCleanSession(false);
        client.connect(op);
        client.setCallback(new MqttCallback() {

            public void connectionLost(Throwable c) {
                System.out.println("[Consumer] connection Lost: " + c.getMessage());
            }

            public void messageArrived(String top, MqttMessage message) {
                System.out.println("[Consumer] message arrived: " + new String(message.getPayload()));
            }

            public void deliveryComplete(IMqttDeliveryToken token) {}
        });

        client.subscribe(topic, 1);
    }

    public MqttReceiver(Topic topic) throws MqttException {
        this.clientId = UUID.randomUUID().toString();
        this.client = new MqttClient(brokerUrl, clientId, new MqttDefaultFilePersistence("persistence/"));
        MqttConnectOptions op = new MqttConnectOptions();
        op.setCleanSession(false);
        client.connect(op);
        client.setCallback(new MqttCallback() {

            public void connectionLost(Throwable c) {
                System.out.println("[Consumer] connection Lost: " + c.getMessage());
            }

            public void messageArrived(String top, MqttMessage message) {
                System.out.println("[Consumer] message arrived: " + new String(message.getPayload()));
            }

            public void deliveryComplete(IMqttDeliveryToken token) {}
        });

        client.subscribe(topic.getTopic(), 1);
    }

    @Override
    public void start(ActionReceiver action) {

    }
}
