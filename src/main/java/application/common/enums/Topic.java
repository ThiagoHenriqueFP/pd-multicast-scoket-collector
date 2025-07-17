package application.common.enums;

public enum Topic {
    ALL("#"),
    DRONE("drone"),
    NORTH("region/north"),
    SOUTH("region/south"),
    EAST("region/east"),
    WEST("region/west"),
    REGION_ALL("region/#");

    private final String topic;

    Topic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public static Topic fromSeparator(char separator) {
        return switch (separator) {
            case ';' -> Topic.SOUTH;
            case ',' -> Topic.EAST;
            case '-' -> Topic.NORTH;
            default -> Topic.WEST;
        };
    }

    public static char fromTopic(Topic topic) {
        return switch (topic) {
            case SOUTH -> ';';
            case EAST -> ',';
            case NORTH -> '-';
            default -> '#';
        };
    }
}
