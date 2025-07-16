package application;

import application.common.enums.Topic;
import application.common.utils.dto.MessageDTO;
import application.common.utils.impl.emitter.MqttEmitter;
import application.common.utils.interfaces.Emitter;
import application.drone.Drone;
import application.drone.DroneMqtt;


public class DroneMain {
    public static void main(String[] args) {
        //Emitter emitter = new MultiCastEmitter();
        Emitter emitter = new MqttEmitter();
        Drone droneSul = new DroneMqtt(emitter, Topic.fromTopic(Topic.SOUTH));
        Drone droneNorth = new DroneMqtt(emitter, Topic.fromTopic(Topic.NORTH));
        Drone droneWest = new DroneMqtt(emitter, Topic.fromTopic(Topic.WEST));
        Drone droneEast = new DroneMqtt(emitter, Topic.fromTopic(Topic.EAST));
        droneSul.startDrone();
        droneNorth.startDrone();
        droneWest.startDrone();
        droneEast.startDrone();
    }
}