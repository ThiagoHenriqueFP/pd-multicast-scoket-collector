package application;

import application.client.Client;
import application.common.enums.Topic;
import application.common.utils.dto.MessageDTO;
import application.common.utils.impl.receiver.MqttReceiver;
import application.common.utils.interfaces.Receiver;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientMain {
    private static int index = 0;
    private static double[] pressureBuffer = new double[20];
    private static double[] radiationBuffer = new double[20];
    private static double[] humidityBuffer = new double[20];
    private static double[] temperatureBuffer = new double[20];

    private static void mqttMetrics(MessageDTO<String> dto) {
        String raw = dto.message().replaceAll("[\\[\\]]", "");
        String[] splitRaw = raw.split(" \\| ");

        double pressure = Double.parseDouble(splitRaw[0].split("pa")[0]);
        double radiation = Double.parseDouble(splitRaw[1].split("mSv")[0]);
        double temperature = Double.parseDouble(splitRaw[2].split("°C")[0]);
        double humidity = Double.parseDouble(splitRaw[3].split("%")[0]);

        pressureBuffer[index] = pressure;
        radiationBuffer[index] = radiation;
        humidityBuffer[index] = humidity;
        temperatureBuffer[index] = temperature;

        index = (index + 1) % 20;

        double pressureAvg = Arrays
                .stream(pressureBuffer)
                .reduce(0f, (subTotal, next) ->  subTotal + next )/20;
        double radiationAvg = Arrays
                .stream(radiationBuffer)
                .reduce(0f, (subTotal, next) -> subTotal + next )/20;
        double humidityAvg = Arrays
                .stream(humidityBuffer)
                .reduce(0f, (subTotal, next) -> subTotal + next )/20;
        double temperatureAvg = Arrays
                .stream(temperatureBuffer)
                .reduce(0f, (subTotal, next) -> subTotal + next )/20;


        System.out.println("\rAVG Pressure: " + pressureAvg + "pa");
        System.out.println("\rAVG Radiation: " + radiationAvg + "mSv");
        System.out.println("\rAVG Humidity: " + humidityAvg + "%");
        System.out.println("\rAVG Temperature: " + temperatureAvg + "°C");
    }

    public static void main(String[] args) {
        Client c = new Client();
        Scanner sc = new Scanner(System.in);
        Receiver receiver = null;

        while (true) {
            System.out.println("\n" +
                    "┌─────────────────┬───┬──────────┐\n" +
                    "│ Read all        │ 1 │          │\n" +
                    "│ Read all (desc) │ 2 │          │\n" +
                    "│ Tail            │ 3 │ <n>      │\n" +
                    "│ Head            │ 4 │ <n>      │\n" +
                    "│ Count           │ 5 │          │\n" +
                    "│ Listen          │ 6 │ <region> │\n" +
                    "│ Listen All      │ 7 │          │\n" +
                    "│ Exit            │ 0 │          │\n" +
                    "└─────────────────┴───┴──────────┘\n" +
                    "\n");
            switch (sc.nextInt()) {
                case 1 -> System.out.println(c.readFile());
                case 2 -> System.out.println(c.readFile(List::reversed));
                case 3 -> {
                    System.out.println("Limit tail to: ");
                    int n = sc.nextInt();
                    System.out.println(c.readFile((list) -> list.reversed().subList(0, n)));
                }
                case 4 -> {
                    System.out.println("Limit tail to: ");
                    int n = sc.nextInt();
                    System.out.println(c.readFile((list) -> list.subList(0, n)));
                }

                case 5 -> System.out.println(c.readFile().size());

                case 6 -> {
                    receiver = null;
                    System.out.println("Region [NORTH=1, SOUTH=2, EAST=3, WEST=4]: ");
                    int n = sc.nextInt();
                    try {
                        switch (n) {
                            case 1 -> receiver = new MqttReceiver(Topic.NORTH, ClientMain::mqttMetrics);
                            case 2 -> receiver = new MqttReceiver(Topic.SOUTH, ClientMain::mqttMetrics);
                            case 3 -> receiver = new MqttReceiver(Topic.EAST, ClientMain::mqttMetrics);
                            default -> receiver = new MqttReceiver(Topic.WEST, ClientMain::mqttMetrics);
                        }
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                case 7 -> {
                    if (receiver != null) {
                        receiver.close();
                    }

                    try {
                        receiver = new MqttReceiver(Topic.REGION_ALL, ClientMain::mqttMetrics);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }

                default -> {

                    System.out.println("exiting");
                    c.shutdown();
                    System.exit(0);
                }
            }
        }

    }


}
