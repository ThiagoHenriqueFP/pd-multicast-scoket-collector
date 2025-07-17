package application.common.utils.impl;

import application.common.utils.dto.QueryMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FIleManager {
    private static final String FILE_NAME = ".db_storage";
    public static final File file = new File(FILE_NAME);

    private static final ExecutorService ex = Executors.newSingleThreadExecutor();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();


    public static void createFile() {
        writeLock.lock();
        new File(FILE_NAME);
        writeLock.unlock();
    }

    public static List<QueryMessage> read() throws IOException {
        readLock.lock();
        if (!file.exists())
            return new ArrayList<>();

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            List<QueryMessage> list = (List<QueryMessage>) ois.readObject();
            ois.close();
            return list;
        } catch (EOFException e ){
            return new ArrayList<>();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            readLock.unlock();
        }
    }

    private static void write(List<QueryMessage> list) throws IOException {
        writeLock.lock();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(list);
            oos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock();
        }
    }

    public static void insert(String message) {
        System.out.println("INFO: Inserting message");
        ex.submit(() -> {
            try {
                List<QueryMessage> list = read();
                list.add(new QueryMessage(message));
                write(list);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

