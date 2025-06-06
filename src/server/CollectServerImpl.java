package server;

import common.enums.Group;
import common.utils.impl.*;

import java.io.*;
import java.net.SocketException;

public class CollectServerImpl implements CollectServer {
    private final Group group;

    public CollectServerImpl(Group group, int port) throws IOException {
        this.group = group;
        try {
            this.register(port);
            new Receiver(port, group, (MessageDTO msg) -> this.insert((String) msg.message()));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(int port) {
        System.out.println("INFO: Registering");
        MessageDTO<String> dto = new MessageDTO<>("", this.group.getIp(), true);
        new Emitter(port, Group.GATEWAY.getIp()).send(dto);
    }

    private void insert(String message) {
       FIleManager.insert(message);
    }

}
