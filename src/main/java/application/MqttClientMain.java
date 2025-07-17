package application;

import application.client.RabbitClient;
import application.common.enums.Topic;
import application.common.utils.dto.MessageDTO;
import application.common.utils.impl.receiver.MqttReceiver;
import application.common.utils.interfaces.Receiver;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Arrays;
import java.util.Scanner;

public class MqttClientMain {
    private static int index = 0;
    private static final double[] pressureBufferMqtt = new double[20];
    private static final double[] radiationBufferMqtt = new double[20];
    private static final double[] humidityBufferMqtt = new double[20];
    private static final double[] temperatureBufferMqtt = new double[20];

    private static final String menu = "\n" +
            "┌─────────────────┬───┬──────────┐\n" +
            "│ Listen          │ 1 │ <region> │\n" +
            "│ Listen All      │ 2 │          │\n" +
            "│ Exit            │ 0 │          │\n" +
            "└─────────────────┴───┴──────────┘\n" +
            "\n";

    private static void mqttMetrics(MessageDTO<String> dto) {
        String raw = dto.message().replaceAll("[\\[\\]]", "");
        String[] splitRaw = raw.split(" \\| ");

        double pressure = Double.parseDouble(splitRaw[0].split("pa")[0]);
        double radiation = Double.parseDouble(splitRaw[1].split("mSv")[0]);
        double temperature = Double.parseDouble(splitRaw[2].split("°C")[0]);
        double humidity = Double.parseDouble(splitRaw[3].split("%")[0]);

        pressureBufferMqtt[index] = pressure;
        humidityBufferMqtt[index] = humidity;
        radiationBufferMqtt[index] = radiation;
        temperatureBufferMqtt[index] = temperature;

        index = (index + 1) % 20;

        double pressureAvg = Arrays
                .stream(pressureBufferMqtt)
                .reduce(0f, (subTotal, next) -> subTotal + next) / 20;
        double radiationAvg = Arrays
                .stream(radiationBufferMqtt)
                .reduce(0f, (subTotal, next) -> subTotal + next) / 20;
        double humidityAvg = Arrays
                .stream(humidityBufferMqtt)
                .reduce(0f, (subTotal, next) -> subTotal + next) / 20;
        double temperatureAvg = Arrays
                .stream(temperatureBufferMqtt)
                .reduce(0f, (subTotal, next) -> subTotal + next) / 20;

        System.out.println(menu);
        System.out.println("\rAVG Pressure: " + pressureAvg + "pa");
        System.out.println("\rAVG Radiation: " + radiationAvg + "mSv");
        System.out.println("\rAVG Humidity: " + humidityAvg + "%");
        System.out.println("\rAVG Temperature: " + temperatureAvg + "°C");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Receiver receiver = null;
        while (true) {
            System.out.println(menu);
            switch (sc.nextInt()) {
                case 1 -> {
                    receiver = null;
                    System.out.println("Region [NORTH=1, SOUTH=2, EAST=3, WEST=4]: ");
                    int n = sc.nextInt();
                    try {
                        switch (n) {
                            case 1 -> receiver = new MqttReceiver(Topic.NORTH, MqttClientMain::mqttMetrics);
                            case 2 -> receiver = new MqttReceiver(Topic.SOUTH, MqttClientMain::mqttMetrics);
                            case 3 -> receiver = new MqttReceiver(Topic.EAST, MqttClientMain::mqttMetrics);
                            default -> receiver = new MqttReceiver(Topic.WEST, MqttClientMain::mqttMetrics);
                        }
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                case 2 -> {
                    if (receiver != null) {
                        receiver.close();
                    }

                    try {
                        receiver = new MqttReceiver(Topic.REGION_ALL, MqttClientMain::mqttMetrics);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }

                default -> {
                    System.out.println("exiting");
                    System.exit(0);
                }
            }
        }

    }


}
