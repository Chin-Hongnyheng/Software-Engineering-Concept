import java.util.Scanner;

public class Exercise2 {

    private int counter = 1;
    private boolean goingUpward = true;
    private int turn = 1;

    public static void main(String[] args) {
        Exercise2 ex2 = new Exercise2();
        ex2.startThreads();
    }

    public void startThreads() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter maximum number of counter: ");
        int n = scan.nextInt();

        Thread th1 = new Thread(() -> printNumber(1, "Thread1", n));
        Thread th2 = new Thread(() -> printNumber(2, "Thread2", n));
        Thread th3 = new Thread(() -> printNumber(3, "Thread3", n));

        th1.start();
        th2.start();
        th3.start();

        try {
            th1.join();
            th2.join();
            th3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    public synchronized void printNumber(int threadID, String name, int maxNumber) {
        while (true) {
            try {

                while (turn != threadID) {
                    wait();
                }

                System.out.println(name + ": " + counter);

                if (!goingUpward && counter == 1) {
                    notifyAll();
                    break;
                }

                if (goingUpward) {
                    counter++;
                    if (counter > maxNumber) {
                        goingUpward = false;
                        counter = maxNumber - 1;
                    }
                } else {
                    counter--;
                }

                turn = (threadID % 3) + 1;
                notifyAll();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
