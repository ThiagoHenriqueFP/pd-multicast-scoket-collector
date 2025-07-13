package application;

import application.common.enums.Group;
import application.drone.Drone;
import application.drone.DroneImpl;
import application.drone.Position;

public class DroneMain {
    public static void main(String[] args) {
        Drone droneSul = new DroneImpl(Position.SOUTH, Group.GATEWAY.getIp(), 5504, ';');
        Drone droneNorth = new DroneImpl(Position.NORTH, Group.GATEWAY.getIp(), 5504, '-');
        Drone droneWest = new DroneImpl(Position.NORTH, Group.GATEWAY.getIp(), 5504, ',');
        Drone droneEast = new DroneImpl(Position.NORTH, Group.GATEWAY.getIp(), 5504, '#');
        droneSul.startDrone();
        droneNorth.startDrone();
        droneWest.startDrone();
        droneEast.startDrone();
    }
}