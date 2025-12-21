import java.util.Random;

public class BankAccount {
    private int balance;

    public BankAccount(int balance) {
        this.balance = balance;
    }

    // Non-synchronized
    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    // Synchronized
    public synchronized void synDeposit(int amount) {
        balance += amount;
    }

    public synchronized void synWithdraw(int amount) {
        balance -= amount;
    }

    public int getBalance() {
        return balance;
    }
}

class Transaction extends Thread {
    private BankAccount account;
    private Random random = new Random();
    private boolean synchronization;
    private StringBuffer history;

    public Transaction(BankAccount account, boolean synchronization, StringBuffer history) {
        this.account = account;
        this.synchronization = synchronization;
        this.history = history;
    }

    @Override
    public void run() {
        // generate 100 transaction
        for (int i = 0; i < 100; i++) {
            int amount = random.nextInt(50) + 1; // random transaction from 1 to 50
            boolean deposit = random.nextBoolean();
            String threadName = Thread.currentThread().getName();

            // Test synchronized version which avoid race condition
            if (synchronization) {
                if (deposit) {
                    account.synDeposit(amount);
                    history.append(
                            threadName + " deposits $" + amount + " | balance: " + account.getBalance() + "\n");
                } else {
                    account.synWithdraw(amount);
                    history.append(
                            threadName + " withdraw $" + amount + " | balance: " + account.getBalance() + "\n");
                }
            }
            // for unsynchronized version which can lead a race condition
            else {
                if (deposit) {
                    account.deposit(amount);
                    history.append(
                            threadName + " deposits $" + amount + " | balance: " + account.getBalance() + "\n");
                } else {
                    account.withdraw(amount);
                    history.append(
                            threadName + " withdraw $" + amount + " | balance: " + account.getBalance() + "\n");
                }
            }
        }
    }
}
