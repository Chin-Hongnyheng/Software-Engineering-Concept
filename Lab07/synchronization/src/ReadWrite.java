import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWrite {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private String data = "Initial Data";

    public String readData() {
        rwLock.readLock().lock();
        try {
            System.out.println("Reading: " + data);
            Thread.sleep(1000); // Simulate read operation
            return data;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void writeData(String newData) {
        rwLock.writeLock().lock();
        try {
            System.out.println("Writing: " + newData);
            Thread.sleep(2000); // Simulate write operation
            this.data = newData;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static void main(String[] args) {
        ReadWrite example = new ReadWrite();
        // Multiple readers

        for (int i = 0; i < 3; i++) {
            new Thread(example::readData).start();
        }
        // One writer
        new Thread(() -> example.writeData("Updated Data")).start();
    }
}