public class Task4 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Non-Synchronized Version ===");
        runSimulation(false);

        System.out.println("\n=== Synchronized Version ===");
        runSimulation(true);
    }

    public static void runSimulation(boolean synchronization) throws InterruptedException {
        BankAccount account = new BankAccount(1000);
        StringBuffer history = new StringBuffer();
        Thread[] threads = new Thread[10];

        // Create 10 threads
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Transaction(account, synchronization, history), "Thread-" + (i + 1));
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            t.join();
        }

        // Display results
        System.out.println(history.toString());
        System.out.println("Final balance: $" + account.getBalance());
    }

    // Explain the result
    // Asynchronization = most likely get the race condition which multiple thread will run parallel which is not protected and error
    // Balance = 1000$
    // Example: T1 deposit 50$ -> Balance = 1050$
    //          T2 deposit 30$ -> Balance - 1030$
    // it should be 1080$ not 1030$ which lead to lost update

    // Synchronization = avoid race condition which each thread will run once at a time
    // Balance = 1000$
    // Example: T1 deposit 50$ -> Balance = 1050$
    //          T2 deposit 30$ -> Balance - 1080$
    // Give Final balance consistent no lost update, stable result, transaction are corrected applied   
}
