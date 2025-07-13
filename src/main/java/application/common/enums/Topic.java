package application.common.enums;

public enum Topic {
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west");

    private String topic;

    Topic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
