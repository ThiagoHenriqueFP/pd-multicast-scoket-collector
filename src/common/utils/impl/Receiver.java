package common.utils.impl;

import common.enums.Group;
import common.utils.interfaces.ActionReceiver;

import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Receiver {
    private final MulticastSocket socket;

    public Receiver(int port, Group ip, ActionReceiver action) throws SocketException {
        try {
            this.socket = new MulticastSocket(port);
            InetSocketAddress remoteAddress = new InetSocketAddress(ip.getIp(), port);
            NetworkInterface networkInterface = NetworkInterface.getByName("wlo1");
            this.socket.joinGroup(remoteAddress, networkInterface);
            this.start(action);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Receiver(int port, String ip, ActionReceiver action) throws SocketException {
        try {
            this.socket = new MulticastSocket(port);
            InetSocketAddress remoteAddress = new InetSocketAddress(ip, port);
            NetworkInterface networkInterface = NetworkInterface.getByName("wlo1");
            this.socket.joinGroup(remoteAddress, networkInterface);
            this.start(action);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void start(ActionReceiver action) {
        System.out.println("INFO: Starting receiver");
        Runnable run = () -> {
            byte[] buffer = new byte[8192];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                try {
                    this.socket.receive(packet);
                    MessageDTO dto = MessageDTO.fromDatagram(packet);
                    if (!(dto.message() instanceof List<?>)) {
                        System.out.println("INFO: " + LocalDateTime.now() + " - " + packet.getAddress() + ": " + dto.message());
                    }
                    action.execute(dto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        run.run();
    }
}
