package common.enums;

public enum Group {
    GATEWAY("224.0.0.2"),
    LEFT_SERVER("224.0.0.3"),
    RIGHT_SERVER("224.0.0.4");

    private String ip;

    Group(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
