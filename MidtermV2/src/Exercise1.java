import java.util.Scanner;

public class Exercise1 {
    private int counter = 1;
    private boolean goingUpward = true;
    private boolean turnOfThread1 = true;

    public static void main(String[] args) {

        Exercise1 ex1 = new Exercise1();
        ex1.startThreads();
    }

    public void startThreads() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter maximum number of counter: ");
        int n = scan.nextInt();

        Thread th1 = new Thread(() -> printNumber(true, "Thread1", n));
        Thread th2 = new Thread(() -> printNumber(false, "Thread2", n));

        th1.start();
        th2.start();

        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void printNumber(boolean isThread1, String name, int number) {
        while (true) {
            try {
                while (turnOfThread1 != isThread1) {
                    wait();
                }

                if (!goingUpward && counter == 1) {
                    notifyAll();
                    break;
                }

                System.out.println(name + ": " + counter);

                if (goingUpward) {
                    counter++;
                    if (counter > number) {
                        goingUpward = false;
                        counter = number - 1;
                    }
                } else {
                    counter--;
                }

                turnOfThread1 = !turnOfThread1;
                notifyAll();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}