import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

class Philosopher extends Thread {
    private final int id;
    private final ReentrantLock leftFork;
    private final ReentrantLock rightFork;
    private int eatCount = 0;
    private final int maxEat;

    public Philosopher(int id, ReentrantLock leftFork, ReentrantLock rightFork, int maxEat) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.maxEat = maxEat;
    }

    @Override
    public void run() {
        try {
            while (eatCount < maxEat) {
                // Thinking
                System.out.println("Philosopher " + id + " is THINKING");
                Thread.sleep((int)(Math.random() * 1000));

                // Hungry
                System.out.println("Philosopher " + id + " is HUNGRY");

                // Try to pick up forks with timeout to avoid deadlock
                boolean leftAcquired = leftFork.tryLock(500, TimeUnit.MILLISECONDS);
                if (leftAcquired) {
                    try {
                        boolean rightAcquired = rightFork.tryLock(500, TimeUnit.MILLISECONDS);
                        if (rightAcquired) {
                            try {
                                // Eating
                                System.out.println("Philosopher " + id + " is EATING (" + (eatCount + 1) + ")");
                                eatCount++;
                                Thread.sleep((int)(Math.random() * 1000));
                            } finally {
                                rightFork.unlock();
                            }
                        }
                    } finally {
                        leftFork.unlock();
                    }
                }
                // If timeout occurs, philosopher goes back to thinking and retries
            }
            System.out.println("Philosopher " + id + " finished eating " + eatCount + " times");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class DiningPhilosophers {
    public static void main(String[] args) throws InterruptedException {
        int numPhilosophers = 5;
        int maxEat = 3;
        ReentrantLock[] forks = new ReentrantLock[numPhilosophers];
        Philosopher[] philosophers = new Philosopher[numPhilosophers];

        // Initialize forks
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new ReentrantLock();
        }

        // Initialize philosophers
        for (int i = 0; i < numPhilosophers; i++) {
            ReentrantLock leftFork = forks[i];
            ReentrantLock rightFork = forks[(i + 1) % numPhilosophers];
            philosophers[i] = new Philosopher(i, leftFork, rightFork, maxEat);
            philosophers[i].start();
        }

        // Run simulation for 2 minutes
        Thread.sleep(120_000);

        // Interrupt all philosophers to stop simulation
        for (Philosopher p : philosophers) {
            p.interrupt();
        }
    }
}
