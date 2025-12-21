import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class FileThread extends Thread {
    private File file;
    // AtomicInteger is thread-safe, meaning multiple threads can safely addAndGet()
    // values without conflicts.
    private AtomicInteger totalWords;
    private AtomicInteger totalLines;
    private AtomicInteger totalChars;

    public FileThread(File file, AtomicInteger totalWords, AtomicInteger totalLines, AtomicInteger totalChars) {
        this.file = file;
        this.totalWords = totalWords;
        this.totalLines = totalLines;
        this.totalChars = totalChars;
    }

    @Override
    public void run() {
        // initialize the start, words, lines and characters
        long start = System.currentTimeMillis();
        int words = 0;
        int lines = 0;
        int characters = 0;

        // use try catch to avoid interruption
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // use buffered reader to read the whole line
            String line;
            while ((line = br.readLine()) != null) {
                lines++;
                characters += +line.length();
                words += line.trim().isEmpty() ? 0 : line.split("\\s").length;

            }
            // Update shared totals
            totalWords.addAndGet(words);
            totalLines.addAndGet(lines);
            totalChars.addAndGet(characters);
            long end = System.currentTimeMillis();
            long timeSpent = end - start;
            System.out.println(
                    Thread.currentThread().getName() + " processed " + file.getName() +
                            ": " + words + " words, " +
                            lines + " lines, " +
                            characters + " characters in " +
                            timeSpent + "ms");
        } catch (FileNotFoundException e) {
            System.out.println("Error reading " + file.getName());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
