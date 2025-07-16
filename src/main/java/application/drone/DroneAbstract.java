package application.drone;

import java.text.DecimalFormat;

public class DroneAbstract {
    String getString(char separator) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMaximumFractionDigits(2);
        String pressure = df.format(
                Math.min(
                        Math.random() * 1000 % 300
                        , 104)
        ) + "pa";
        String temperature = df.format(Math.max(
                Math.random() * 100 % 60
                , 10.00)
        ) + "°C";
        String radiation = df.format(Math.random() * 10) + "mSv";
        String humidity = df.format(
                Math.min(Math.random() * 100, 40)
        ) + "%";

        return pressure + separator + radiation + separator + temperature + separator + humidity;
    }

}
