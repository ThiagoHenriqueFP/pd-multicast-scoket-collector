package application;

import application.client.RabbitClient;
import application.common.enums.Topic;
import application.common.utils.dto.MessageDTO;
import application.common.utils.impl.receiver.MqttReceiver;
import application.common.utils.interfaces.Receiver;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RabbitClientMain {
    private static final List<Double> pressureBufferRabbit = new ArrayList<>();
    private static final List<Double> radiationBufferRabbit = new ArrayList<>();
    private static final List<Double> humidityBufferRabbit = new ArrayList<>();
    private static final List<Double> temperatureBufferRabbit = new ArrayList<>();

    private static final List<String> outputBuffer = new ArrayList<>();

    private static final String menu = "\n" +
            "┌─────────────────┬───┬──────────┐\n" +
            "│ Read all        │ 1 │          │\n" +
            "│ Read all (desc) │ 2 │          │\n" +
            "│ Tail            │ 3 │ <n>      │\n" +
            "│ Head            │ 4 │ <n>      │\n" +
            "│ Count           │ 5 │          │\n" +
            "│ Exit            │ 0 │          │\n" +
            "└─────────────────┴───┴──────────┘\n" +
            "\n";

    private static void rabbitMetrics(MessageDTO<String> dto) {
        String raw = dto.message().replaceAll("[\\[\\]]", "");
        String[] splitRaw = raw.split(" \\| ");

        double pressure = Double.parseDouble(splitRaw[0].split("pa")[0]);
        double radiation = Double.parseDouble(splitRaw[1].split("mSv")[0]);
        double temperature = Double.parseDouble(splitRaw[2].split("°C")[0]);
        double humidity = Double.parseDouble(splitRaw[3].split("%")[0]);

        pressureBufferRabbit.add(pressure);
        humidityBufferRabbit.add(humidity);
        radiationBufferRabbit.add(radiation);
        temperatureBufferRabbit.add(temperature);

        double pressureAvg = pressureBufferRabbit.stream()
                .reduce(0d, (subTotal, next) -> subTotal + next) / pressureBufferRabbit.size();
        double radiationAvg = radiationBufferRabbit.stream()
                .reduce(0d, (subTotal, next) -> subTotal + next) / radiationBufferRabbit.size();
        double humidityAvg = humidityBufferRabbit.stream()
                .reduce(0d, (subTotal, next) -> subTotal + next) / humidityBufferRabbit.size();
        double temperatureAvg = temperatureBufferRabbit.stream()
                .reduce(0d, (subTotal, next) -> subTotal + next) / temperatureBufferRabbit.size();


        System.out.println(menu);
        System.out.println("\rAVG Pressure: " + pressureAvg + "pa");
        System.out.println("\rAVG Radiation: " + radiationAvg + "mSv");
        System.out.println("\rAVG Humidity: " + humidityAvg + "%");
        System.out.println("\rAVG Temperature: " + temperatureAvg + "°C");
    }

    public static void main(String[] args) {
        RabbitClient c = new RabbitClient(List.of("region.north", "region.south", "region.east", "region.west"), RabbitClientMain::rabbitMetrics);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println(menu);
            switch (sc.nextInt()) {
                case 1 -> {
                    System.out.println("Importing data....");
                    System.out.println(c.readFile());
                }
                case 2 -> {
                    System.out.println(c.readFile(List::reversed));
                }
                case 3 -> {
                    System.out.println("Limit tail to: ");
                    int n = sc.nextInt();
                    System.out.println(c.readFile((list) -> list.reversed().subList(0, n)));
                }
                case 4 -> {
                    System.out.println("Limit head to: ");
                    int n = sc.nextInt();
                    System.out.println(c.readFile((list) -> list.subList(0, n)));
                }

                case 5 -> System.out.println(c.readFile().size());

                default -> {
                    System.out.println("exiting");
                    System.exit(0);
                }
            }
        }

    }


}
