import common.enums.Group;
import server.CollectServerImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ServerMain {
    public static void main(String[] args) {
        File file = new File(".db_storage");
        ObjectOutputStream fos;
        try {
            fos = new ObjectOutputStream(new FileOutputStream(file, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            try {
                new CollectServerImpl(Group.LEFT_SERVER, 5504);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                new CollectServerImpl(Group.RIGHT_SERVER, 5504);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
