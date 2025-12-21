import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class Task1 {
    public static void main(String[] args) throws Exception {
        // Shared totals of each thread
        AtomicInteger totalWords = new AtomicInteger(0);
        AtomicInteger totalLines = new AtomicInteger(0);
        AtomicInteger totalChars = new AtomicInteger(0);

        System.out.println("Scanning files in folder: bunchOfFiles...");
        System.out.println("--------------------------------- RESULTS -------------------------------------");
        // create 3 files
        File file1 = new File(
                "C:\\Users\\User\\Documents\\I3\\Software Engineering Concept\\Lab5\\Threading in Java\\src\\BunchOfFiles\\file1.txt");
        File file2 = new File(
                "C:\\Users\\User\\Documents\\I3\\Software Engineering Concept\\Lab5\\Threading in Java\\src\\BunchOfFiles\\file2.txt");
        File file3 = new File(
                "C:\\Users\\User\\Documents\\I3\\Software Engineering Concept\\Lab5\\Threading in Java\\src\\BunchOfFiles\\file3.txt");

        // Create 1 Thread version and 2 Runnable version
        FileThread t1 = new FileThread(file1, totalWords, totalLines, totalChars);

        Thread r1 = new Thread(new FileRunnable(file2, totalWords, totalLines, totalChars));
        Thread r2 = new Thread(new FileRunnable(file3, totalWords, totalLines, totalChars));

        // Start the timer
        long startTime = System.currentTimeMillis();

        t1.start();
        r1.start();
        r2.start();

        // wait for thread to finish
        t1.join();
        r1.join();
        r2.join();

        // End the timer
        long endTime = System.currentTimeMillis();
        long timeSpent = endTime - startTime;
        
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("Total files processed: 3");
        System.out.println("Total word(s): " + totalWords.get());
        System.out.println("Total line(s): " + totalLines.get());
        System.out.println("Total character(s): " + totalChars.get());
        System.out.println("Total processing time: " + timeSpent + " ms");
    }
}
