package com.example;

public class Prime extends Thread {
    private final int start;
    private final Integer end;

    private volatile boolean paused = false;
    private volatile boolean stopped = false;

    private final PrimeListener listener;

    public interface PrimeListener {
        void onPrimeFound(int prime);

        void onProgress(double progress);
    }

    public Prime(int start, Integer end, PrimeListener listener) {
        this.start = start;
        this.end = end;
        this.listener = listener;
    }

    @Override
    public void run() {
        int n = start;

        // If bounded, pre-count how many primes exist in [start..end]
        int totalPrimes = -1;
        if (end != null) {
            totalPrimes = countPrimesInRange(start, end);
            if (totalPrimes == 0) {
                // nothing to generate — ensure progress shows complete and return
                listener.onProgress(1.0);
                return;
            }
        }

        int primesFound = 0;

        while (!stopped) {

            if (end != null && n > end) {
                break;
            }

            // Pause logic (check before expensive work)
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

            if (isPrime(n)) {
                primesFound++;
                listener.onPrimeFound(n);

                // If bounded, compute progress based on primes found
                if (end != null) {
                    double progress = (double) primesFound / (double) totalPrimes;
                    if (progress > 1.0)
                        progress = 1.0;
                    listener.onProgress(progress);
                }

                // Sleep after reporting a prime
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    if (stopped)
                        return;
                }

                // If we already found all primes (safety), break
                if (end != null && primesFound >= totalPrimes) {
                    // ensure progress is exactly 100%
                    listener.onProgress(1.0);
                    break;
                }
            } else {
                // Non-prime: still advance n (no progress update)
            }

            n++;
        }

        // If loop finished normally (not stopped) and bounded, ensure final 100%
        if (!stopped && end != null && primesFound >= totalPrimes) {
            listener.onProgress(1.0);
        }
    }

    // Helper: count primes in [a..b] inclusive
    private int countPrimesInRange(int a, int b) {
        int count = 0;
        for (int i = Math.max(2, a); i <= b; i++) {
            if (isPrime(i))
                count++;
        }
        return count;
    }

    private boolean isPrime(int n) {
        if (n < 2)
            return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    public synchronized void pauseThread() {
        paused = true;
    }

    public synchronized void resumeThread() {
        paused = false;
        // Wake up the waiting thread(s)
        notifyAll();
    }

    public void stopThread() {
        stopped = true;
        this.interrupt(); // wake if sleeping or waiting
    }
}
