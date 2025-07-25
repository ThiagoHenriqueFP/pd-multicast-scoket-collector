package application.gateway;

import application.common.enums.Topic;
import application.common.utils.dto.MessageDTO;
import application.common.utils.impl.FIleManager;
import application.common.utils.impl.emitter.MqttEmitter;
import application.common.utils.impl.emitter.RabbitEmitter;
import application.common.utils.impl.receiver.MqttReceiver;

import application.common.utils.interfaces.Emitter;
import org.eclipse.paho.client.mqttv3.MqttException;

public class GatewayIndirect {
    Emitter mqttEmitter = new MqttEmitter();
    Emitter rabbitEmitter = new RabbitEmitter();

    public GatewayIndirect(Topic topic) {
        try {
            new MqttReceiver(topic, this::action);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private void action(MessageDTO<String> message) {
        System.out.println(message.message());
        FIleManager.insert(message.message());
        sendToRegions(message);
    }

    private <T> void sendToRegions(MessageDTO<T> message) {
        if (message.message() instanceof String) {
            String str = (String) message.message();
            char separator = str.split("pa")[1].charAt(0);
            str = "[" + str.replaceAll("[\\,\\;\\#\\-]", " | ") + "]";
            mqttEmitter.send(new MessageDTO<>(str, Topic.fromSeparator(separator)));
            rabbitEmitter.send(new MessageDTO<>(str, Topic.fromSeparator(separator)));
        }
    }
}
