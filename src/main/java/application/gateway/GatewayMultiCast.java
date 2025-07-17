package application.gateway;

import application.common.enums.BalanceLoaderTypes;
import application.common.enums.Group;
import application.common.exception.CouldNotSendException;
import application.common.utils.dto.MessageDTO;
import application.common.utils.dto.QueryMessage;
import application.common.utils.impl.*;
import application.common.utils.impl.emitter.MultiCastEmitter;
import application.common.utils.impl.receiver.MultiCastReceiver;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class GatewayMultiCast {
    private final List<String> serversIp;
    private BalanceLoaderTypes type = BalanceLoaderTypes.ROUND_ROBIN;
    private int roundRobinIndex = 0;

    public GatewayMultiCast(int port) {
        try {
            this.serversIp = new ArrayList<>();
            FIleManager.createFile();
            new MultiCastReceiver(port, Group.GATEWAY, this::distribute);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private void distribute(MessageDTO msg) {
        try {
            if (msg.retrieve()) {
                System.out.println("INFO: Syncing with " + msg.ip());
                List<QueryMessage> list;
                try {
                    list = FIleManager.read();
                } catch (EOFException e) {
                    list = new ArrayList<>();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                new MultiCastEmitter(5504, msg.ip())
                        .send(new MessageDTO<>(list));
            } else if (!msg.ack()) {
                switch (this.type) {
                    case LRT -> lrt(msg);
                    default -> rr(msg);
                }
            } else {
                System.out.println("INFO: Adding application.server " + msg.ip());
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
            new MultiCastEmitter(5504, serversIp.get(this.roundRobinIndex)).send(msg);
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
