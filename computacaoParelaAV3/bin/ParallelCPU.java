import java.io.*;
import java.util.concurrent.*;

public class ParallelCPU {
    public static int countWordOccurrences(File file, String targetWord, int numThreads)
            throws IOException, ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int totalOccurrences = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            BlockingQueue<Future<Integer>> futures = new LinkedBlockingQueue<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String currentLine = line.toLowerCase();
                futures.add(executor.submit(() -> {
                    int count = 0;
                    String[] words = currentLine.split("\\W+");
                    for (String word : words) {
                        if (word.equals(targetWord.toLowerCase())) {
                            count++;
                        }
                    }
                    return count;
                }));
            }

            for (Future<Integer> future : futures) {
                totalOccurrences += future.get();
            }
        } finally {
            executor.shutdown();
        }

        return totalOccurrences;
    }
}
