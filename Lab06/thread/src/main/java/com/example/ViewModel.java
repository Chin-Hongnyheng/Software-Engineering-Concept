package com.example;

import javafx.application.Platform;

public class ViewModel {

    private Prime thread1;
    private Fibonacci thread2;

    public void startGeneratingPrime(int start, Integer end, java.util.function.Consumer<String> outputHandler,
            java.util.function.Consumer<Double> progressHandler) {
        thread1 = new Prime(start, end, new Prime.PrimeListener() {
            @Override
            public void onPrimeFound(int prime) {
                Platform.runLater(() -> outputHandler.accept(String.valueOf(prime)));
            }

            @Override
            public void onProgress(double progress) {
                Platform.runLater(() -> progressHandler.accept(progress));
            }
        });
        thread1.setDaemon(true);
        thread1.start();
    }

    public void PrimePause() {
        if (thread1 != null)
            thread1.pauseThread();
    }

    public void PrimeResume() {
        if (thread1 != null)
            thread1.resumeThread();
    }

    public void PrimeStop() {
        if (thread1 != null)
            thread1.stopThread();
    }

    public void startGeneratingFibonacci(int startA, int startB, Integer end,
            java.util.function.Consumer<String> outputHandler, java.util.function.Consumer<Double> progressHandler) {
        thread2 = new Fibonacci(startA, startB, end, new Fibonacci.FibonacciListener() {
            @Override
            public void onNumberFound(int number) {
                Platform.runLater(() -> outputHandler.accept(String.valueOf(number)));
            }

            @Override
            public void onProgress(double progress) {
                Platform.runLater(() -> progressHandler.accept(progress));
            }
        });
        thread2.setDaemon(true);
        thread2.start();
    }

    public void FibonacciPause() {
        if (thread2 != null)
            thread2.pauseThread();
    }

    public void FibonacciResume() {
        if (thread2 != null)
            thread2.resumeThread();
    }

    public void FibonacciStop() {
        if (thread2 != null)
            thread2.stopThread();
    }

    public void restartPrime(int start, Integer end,
            java.util.function.Consumer<String> outputHandler,
            java.util.function.Consumer<Double> progressHandler) {
        PrimeStop();
        startGeneratingPrime(start, end, outputHandler, progressHandler);
    }

    public void restartFibonacci(int startA, int startB, Integer end,
            java.util.function.Consumer<String> outputHandler,
            java.util.function.Consumer<Double> progressHandler) {
        FibonacciStop();
        startGeneratingFibonacci(startA, startB, end, outputHandler, progressHandler);
    }
}