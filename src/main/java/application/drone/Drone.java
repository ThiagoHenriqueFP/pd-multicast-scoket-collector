package application.drone;

import application.common.utils.dto.MessageDTO;

public interface Drone {
    void startDrone();
    void sendMessage(MessageDTO message);
}
