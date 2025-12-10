package com.example;

public class Fibonacci extends Thread {

    private int startA;
    private int startB;
    private Integer end;
    private FibonacciListener listener;

    private volatile boolean paused = false;
    private volatile boolean stopped = false;

    public interface FibonacciListener {
        void onNumberFound(int number);

        void onProgress(double progress);
    }

    public Fibonacci(int startA, int startB, Integer end, FibonacciListener listener) {
        this.startA = startA;
        this.startB = startB;
        this.end = end;
        this.listener = listener;
    }

    @Override
    public void run() {

        // --- Count Fibonacci numbers first ---
        int total = countFibonacciInRange(startA, startB, end);

        if (total == 0) {
            listener.onProgress(1.0); // nothing to show
            return;
        }

        int count = 0;
        int a = startA;
        int b = startB;

        while (!stopped) {

            if (end != null && a > end) {
                break; // stop at bound
            }

            // Send number to UI
            listener.onNumberFound(a);

            count++;
            double progress = (double) count / total;
            if (progress > 1.0)
                progress = 1.0;
            listener.onProgress(progress);

            if (count >= total) {
                listener.onProgress(1.0);
                break;
            }

            // Next Fibonacci value
            int next = a + b;
            a = b;
            b = next;

            // Pause if requested
            synchronized (this) {
                while (paused && !stopped) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        if (stopped)
                            return;
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                if (stopped)
                    return;
            }
        }
    }

    // Count how many Fibonacci numbers ≤ end
    private int countFibonacciInRange(int a, int b, Integer end) {
        if (end == null)
            return -1; // unlimited Fibonacci has no progress end

        int x = a;
        int y = b;
        int total = 0;

        while (x <= end) {
            total++;
            int next = x + y;
            x = y;
            y = next;
        }

        return total;
    }

    // --- Controls ---
    public synchronized void pauseThread() {
        paused = true;
    }

    public synchronized void resumeThread() {
        paused = false;
        notifyAll();
    }

    public void stopThread() {
        stopped = true;
        this.interrupt();
    }
}
