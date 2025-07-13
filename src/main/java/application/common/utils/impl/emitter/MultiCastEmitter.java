package application.common.utils.impl.emitter;

import application.common.utils.dto.MessageDTO;
import application.common.utils.interfaces.Emitter;

import java.io.IOException;
import java.net.*;

public class MultiCastEmitter implements Emitter {
    private final int port;
    private final String groupIp;
    private final DatagramSocket socket;

    public MultiCastEmitter(int port, String destination) {
        this.port = port;
        this.groupIp = destination;
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
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
