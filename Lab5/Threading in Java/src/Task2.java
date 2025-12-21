import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

// The point is to estimate how many producer produce and consumer consume within 30 seconds

// Item with value + priority
// Implements Comparable<Item> so that items can be sorted automatically by priority in the queue
class Item implements Comparable<Item> {
    int value;
    int priority;

    public Item(int value, int priority) {
        this.value = value;
        this.priority = priority;
    }

    // Higher priority comes first
    @Override
    // The queue now compares this new item’s priority with the existing item using
    // compareTo()
    public int compareTo(Item other) {
        return Integer.compare(other.priority, this.priority);
    }

    @Override
    // just display it
    public String toString() {
        return "(Value: " + value + ", Priority:" + priority + ")";
    }
}

// Producer thread
class Producer extends Thread {
    private final BlockingQueue<Item> queue;
    private final AtomicInteger producedCount;
    private final String name;
    private final Random random = new Random();

    public Producer(String name, BlockingQueue<Item> queue, AtomicInteger producedCount) {
        this.name = name;
        this.queue = queue;
        this.producedCount = producedCount;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // random value and priority from 1 to 100
                int value = random.nextInt(100) + 1;
                int priority = random.nextInt(100) + 1;

                // pass to Item class to store it in queue and then apply ascending sort based
                // on priority
                Item item = new Item(value, priority);
                queue.put(item); // Add data and blocks if full
                producedCount.incrementAndGet();

                System.out.println(name + " produced: " + item + " | Buffer: " + queue);
                Thread.sleep(random.nextInt(200));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Consumer thread
class Consumer implements Runnable {
    private final BlockingQueue<Item> queue;
    private final AtomicInteger consumedCount;
    private final String name;
    private final Random random = new Random();

    public Consumer(String name, BlockingQueue<Item> queue, AtomicInteger consumedCount) {
        this.name = name;
        this.queue = queue;
        this.consumedCount = consumedCount;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Item item = queue.take(); // remove data and blocks if empty
                consumedCount.incrementAndGet();

                System.out.println(name + " consumed: " + item + " | Buffer: " + queue);
                Thread.sleep(random.nextInt(300));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Main class
public class Task2 {
    public static void main(String[] args) throws InterruptedException {

        // Shared buffer with capacity 10
        BlockingQueue<Item> queue = new PriorityBlockingQueue<>(10);

        // Counters
        AtomicInteger producer1Count = new AtomicInteger(0);
        AtomicInteger producer2Count = new AtomicInteger(0);
        AtomicInteger consumer1Count = new AtomicInteger(0);
        AtomicInteger consumer2Count = new AtomicInteger(0);
        AtomicInteger consumer3Count = new AtomicInteger(0);

        // Producers
        Producer p1 = new Producer("Producer-1", queue, producer1Count);
        Producer p2 = new Producer("Producer-2", queue, producer2Count);

        // Consumers
        Thread c1 = new Thread(new Consumer("Consumer-1", queue, consumer1Count));
        Thread c2 = new Thread(new Consumer("Consumer-2", queue, consumer2Count));
        Thread c3 = new Thread(new Consumer("Consumer-3", queue, consumer3Count));

        // Start threads
        p1.start();
        p2.start();
        c1.start();
        c2.start();
        c3.start();

        // Run for 30 seconds
        Thread.sleep(30_000);

        // Stop threads
        p1.interrupt();
        p2.interrupt();
        c1.interrupt();
        c2.interrupt();
        c3.interrupt();

        // Wait for threads to finish
        p1.join();
        p2.join();
        c1.join();
        c2.join();
        c3.join();

        // Display statistics
        System.out.println("------------------ STATISTICS ------------------");
        System.out.println("Producer-1 produced: " + producer1Count.get() + " items");
        System.out.println("Producer-2 produced: " + producer2Count.get() + " items");
        System.out.println("Consumer-1 consumed: " + consumer1Count.get() + " items");
        System.out.println("Consumer-2 consumed: " + consumer2Count.get() + " items");
        System.out.println("Consumer-3 consumed: " + consumer3Count.get() + " items");
        System.out.println("Remaining items in buffer: " + queue.size());
        System.out.println("------------------------------------------------");
    }
}
