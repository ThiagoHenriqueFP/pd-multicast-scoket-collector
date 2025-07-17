package application.common.utils.interfaces;

public interface Receiver {
    void start(ActionReceiver action);
    void close();
}
