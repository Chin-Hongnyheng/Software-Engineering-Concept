import java.util.LinkedList;
import java.util.Queue;

public class BoundedBuffer {
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity;

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    // Producer adds item
    public synchronized void produce(int item) throws InterruptedException {
        while (buffer.size() == capacity) { // buffer full → wait
            wait();
        }
        buffer.add(item);
        System.out.println(Thread.currentThread().getName() + " produced: " + item);
        notify(); // wake up one waiting thread
    }

    // Consumer removes item
    public synchronized int consume() throws InterruptedException {
        while (buffer.isEmpty()) { // buffer empty → wait
            wait();
        }
        int item = buffer.remove();
        System.out.println(Thread.currentThread().getName() + " consumed: " + item);
        notify(); // wake up one waiting thread
        return item;
    }
}
