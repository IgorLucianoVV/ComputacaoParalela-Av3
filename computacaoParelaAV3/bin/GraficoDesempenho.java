import java.awt.*;
import java.util.*;
import javax.swing.*;

public class GraficoDesempenho extends JPanel {
    private final Map<String, Map<String, Long>> data;
    private final String titulo;

    public GraficoDesempenho(Map<String, Map<String, Long>> data, String titulo) {
        this.data = data;
        this.titulo = titulo;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int margin = 50;
        int numSamples = data.keySet().size();
        long maxTime = data.values().stream().flatMap(m -> m.values().stream()).max(Long::compare).orElse(1L);
        int xScale = (width - 2 * margin) / numSamples;
        double yScale = (double) (height - 2 * margin) / maxTime;
        Color[] colors = { Color.RED, Color.BLUE, Color.GREEN };
        int x = margin;

        // Gerar gráfico de barras para cada método
        for (String sample : data.keySet()) {
            Map<String, Long> methodData = data.getOrDefault(sample, new HashMap<>());
            int i = 0;
            for (String method : methodData.keySet()) {
                long time = methodData.getOrDefault(method, 0L);
                int barHeight = (int) (time * yScale);
                g2d.setColor(colors[i % colors.length]);
                int barWidth = xScale / methodData.size();
                g2d.fillRect(x + i * barWidth, height - margin - barHeight, barWidth, barHeight);
                i++;
            }
            x += xScale;
        }

        // Eixos
        g2d.setColor(Color.BLACK);
        g2d.drawLine(margin, height - margin, width - margin, height - margin);
        g2d.drawLine(margin, height - margin, margin, margin);

        int xLabel = margin;
        for (String sample : data.keySet()) {
            g2d.drawString(sample, xLabel + xScale / 2 - 10, height - margin + 20);
            xLabel += xScale;
        }

        for (int i = 0; i <= 10; i++) {
            int yLabel = height - margin - i * (height - 2 * margin) / 10;
            g2d.drawString(String.valueOf(i * (int) (maxTime / 10)), margin - 40, yLabel);
        }

        g2d.drawString(titulo, width / 2 - 50, 20);
    }
}
