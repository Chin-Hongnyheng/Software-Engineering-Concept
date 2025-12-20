public class BankAccount {

    private double balance = 1000.0;

    //allow only 1 thread execute at a time
    public synchronized void withdraw(double amount) {

        if (balance >= amount) {
            balance = balance - amount;
            System.out.println(Thread.currentThread().getName()
                    + " withdraw " + amount);
        } else {
            System.out.println(Thread.currentThread().getName()
                    + " - Insufficient funds");
        }
    }

    public static void main(String[] args) {

        BankAccount account = new BankAccount();

        Thread t1 = new Thread(() -> account.withdraw(600), "Thread-1");
        Thread t2 = new Thread(() -> account.withdraw(500), "Thread-2");

        t1.start();
        t2.start();
    }
}
