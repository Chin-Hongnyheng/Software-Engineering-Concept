public class Main {
    public static void main(String[] args) {
        BoundedBuffer buffer = new BoundedBuffer(5); // buffer size = 5

        // Multiple producers and consumers
        Producer p1 = new Producer(buffer, "Producer-1");
        Producer p2 = new Producer(buffer, "Producer-2");
        Consumer c1 = new Consumer(buffer, "Consumer-1");
        Consumer c2 = new Consumer(buffer, "Consumer-2");

        p1.start();
        p2.start();
        c1.start();
        c2.start();
    }
}
