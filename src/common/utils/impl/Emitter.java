package common.utils.impl;

import java.io.IOException;
import java.net.*;

public class Emitter {
    private final int port;
    private final String groupIp;
    private final DatagramSocket socket;

    public Emitter(int port, String destination) {
        this.port = port;
        this.groupIp = destination;
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Emitter send(MessageDTO<T> message) {
        try {
            byte[] buf = message.objectToByteArray();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(groupIp), port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public void close() {
        socket.close();
    }
}
