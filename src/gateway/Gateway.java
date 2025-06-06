package gateway;

import common.enums.BalanceLoaderTypes;
import common.enums.Group;
import common.exception.CouldNotSendException;
import common.utils.impl.*;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Gateway {
    private final List<String> serversIp;
    private BalanceLoaderTypes type = BalanceLoaderTypes.ROUND_ROBIN;
    private int roundRobinIndex = 0;

    public Gateway(int port) {
        try {
            this.serversIp = new ArrayList<>();
            FIleManager.createFile();
            new Receiver(port, Group.GATEWAY, this::distribute);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private void distribute(MessageDTO msg) {
        try {
            if (msg.retrieve()) {
                System.out.println("IFO: Syncing with " + msg.ip());
                List<QueryMessage> list;
                try {
                    list = FIleManager.read();
                } catch (EOFException e) {
                    list = new ArrayList<>();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                new Emitter(5504, msg.ip())
                        .send(new MessageDTO<>(list));
            } else if (!msg.ack()) {
                switch (this.type) {
                    case LRT -> lrt(msg);
                    default -> rr(msg);
                }
            } else {
                System.out.println("INFO: Adding server " + msg.ip());
                this.addAddress(msg.ip());
            }

        } catch (CouldNotSendException e) {
            e.printStackTrace();
        }
    }

    public void useLRT(boolean value) {
        if (value) setType(BalanceLoaderTypes.LRT);
        else setType(BalanceLoaderTypes.ROUND_ROBIN);
    }


    private void lrt(MessageDTO msg) throws CouldNotSendException {
        System.out.println("WARN: Using Round Robin strategy until the LRT is implemented");
        this.rr(msg);
    }

    private void rr(MessageDTO msg) throws CouldNotSendException {
        if (!this.serversIp.isEmpty()) {
            System.out.println("INFO: Sending to: " + serversIp.get(this.roundRobinIndex));
            new Emitter(5504, serversIp.get(this.roundRobinIndex)).send(msg);
            this.roundRobinIndex = (roundRobinIndex + 1) % serversIp.size();
        } else {
            System.out.println("WARN: Discarding package, due no servers connected");
        }
    }

    private void setType(BalanceLoaderTypes type) {
        this.type = type;
    }

    private void addAddress(String ip) {
        if (!this.serversIp.contains(ip))
            this.serversIp.add(ip);
    }
}
