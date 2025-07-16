package application;

import application.common.enums.Topic;
import application.gateway.GatewayIndirect;

public class GatewayMain {
    public static void main(String[] args) {
        new GatewayIndirect(Topic.DRONE);
    }
}
