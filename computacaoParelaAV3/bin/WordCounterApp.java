import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class WordCounterApp extends JFrame {
    private final JTextField wordField;
    private final JButton executeButton;
    private final JPanel graphPanel;

    public WordCounterApp() {
        setTitle("Word Counter Comparison");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Palavra a Contar:"));
        wordField = new JTextField();
        inputPanel.add(wordField);

        executeButton = new JButton("Executar");
        inputPanel.add(new JLabel(""));
        inputPanel.add(executeButton);

        add(inputPanel, BorderLayout.NORTH);

        graphPanel = new JPanel();
        add(graphPanel, BorderLayout.CENTER);

        executeButton.addActionListener(new ExecuteAction());
    }

    private class ExecuteAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<File> files = new ArrayList<>();
            files.add(new File("data\\DonQuixote-388208.txt"));
            files.add(new File("data\\Dracula-165307.txt"));
            files.add(new File("data\\MobyDick-217452.txt"));

            String targetWord = wordField.getText().trim();

            if (targetWord.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha o campo da palavra.");
                return;
            }

            try {
                Map<String, Map<String, Long>> results = new LinkedHashMap<>();

                for (File file : files) {
                    long startTime = System.currentTimeMillis();
                    int serialCount = SerialCPU.countWordOccurrences(file, targetWord);
                    long endTime = System.currentTimeMillis();
                    addResult(results, file.getName(), "SerialCPU", endTime - startTime);

                    startTime = System.currentTimeMillis();
                    int parallelCount = ParallelCPU.countWordOccurrences(file, targetWord,
                            Runtime.getRuntime().availableProcessors());
                    endTime = System.currentTimeMillis();
                    addResult(results, file.getName(), "ParallelCPU", endTime - startTime);

                    startTime = System.currentTimeMillis();
                    int gpuCount = ParallelGPU.countWordOccurrences(file.getAbsolutePath(), targetWord);
                    endTime = System.currentTimeMillis();
                    addResult(results, file.getName(), "ParallelGPU", endTime - startTime);
                }

                graphPanel.removeAll();
                graphPanel.add(new GraficoDesempenho(results, "Comparação de Desempenho"));
                graphPanel.revalidate();
                graphPanel.repaint();

            } catch (IOException | InterruptedException | ExecutionException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao processar: " + ex.getMessage());
            }
        }

        private void addResult(Map<String, Map<String, Long>> results, String fileName, String method, long time) {
            results.putIfAbsent(fileName, new HashMap<>());
            results.get(fileName).put(method, time);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordCounterApp app = new WordCounterApp();
            app.setVisible(true);
        });
    }
}
