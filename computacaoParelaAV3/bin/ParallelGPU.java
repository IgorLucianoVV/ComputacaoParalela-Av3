// Simulação de ParallelGPU com Threads para contagem paralelizada
class ParallelGPU {
    public static int countWordOccurrences(String filePath, String targetWord)
            throws IOException, InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Callable<Integer>> tasks = new ArrayList<>();
        int totalOccurrences = 0;

        // Ler o arquivo em partes e criar tarefas para cada parte (simulação do
        // paralelo)
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String currentLine = line.toLowerCase();
                // Criar uma tarefa para contar as palavras na linha
                tasks.add(() -> countWordsInLine(currentLine, targetWord));
            }
        }

        // Submeter as tarefas para execução paralela
        List<Future<Integer>> futures = executor.invokeAll(tasks);

        // Agregar os resultados
        for (Future<Integer> future : futures) {
            totalOccurrences += future.get();
        }

        executor.shutdown();
        return totalOccurrences;
    }

    private static int countWordsInLine(String line, String targetWord) {
        int count = 0;
        String[] words = line.split("\\W+");
        for (String word : words) {
            if (word.equals(targetWord.toLowerCase())) {
                count++;
            }
        }
        return count;
    }
}
