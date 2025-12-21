import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Task3 {
    // return a result (which thread processed which URL)
    // Callable is like Runnable, but it returns a value
    static class DownloadPage implements Callable<String> {
        private final String url;
        private final Random random = new Random();

        public DownloadPage(String url) {
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            int downloadTime = 1 + random.nextInt(5); // random download time is 1-5 seconds
            Thread.sleep(downloadTime * 1000); // simulate download
            return Thread.currentThread().getName() + " processed " + url + " in " + downloadTime + " ms";
        }
    }

    static void runCrawler(ExecutorService pool, List<String> urls, String poolName) throws InterruptedException {
        System.out.println("Running with " + poolName);

        // marks the start of the simulation to measure total execution time
        long startTime = System.currentTimeMillis();

        //Create a DownloadPage(url) task and Submit it to the thread pool using pool.submit()
        List<Future<String>> futures = new ArrayList<>();
        for (String url : urls) {
            futures.add(pool.submit(new DownloadPage(url)));
        }

        // Loop through each Future in the order of submission.
        for (Future<String> f : futures) {
            try {
                // If the task finishes → returns the result (the string from DownloadPage.call()
                System.out.println(f.get(10, TimeUnit.SECONDS)); // max 10 sec per page
            } catch (TimeoutException e) {
                System.out.println("Timeout occurred for a page");
                f.cancel(true);
            } catch (ExecutionException e) {
                System.out.println("Error processing page: " + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + " ms");

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws InterruptedException {
        // Simulate 20 URLs
        List<String> urls = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            urls.add("page" + i);
        }

        // FixedThreadPool
        //Creates a pool with a fixed number of threads
        // Only 5 threads run at the same time
        // If more tasks come in, they wait in a queue until a thread is free
        ExecutorService fixedPool = Executors.newFixedThreadPool(5);
        runCrawler(fixedPool, urls, "FixedThreadPool");

        // CachedThreadPool
        // Creates a pool that creates new threads as needed
        // If threads are idle, it reuses them
        // If 10 threads are needed for 20 tasks, it will create 10 threads automatically and run in parallel
        // If some threads finish early → new tasks can reuse them so the number of threads can grow up to 20 if all tasks arrive at once
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        runCrawler(cachedPool, urls, "CachedThreadPool");

        // SingleThreadExecutor
        // Only 1 thread exists in the pool
        // All tasks run sequentially, one after another like Round Robin
        ExecutorService singlePool = Executors.newSingleThreadExecutor();
        runCrawler(singlePool, urls, "SingleThreadExecutor");
    }

}
