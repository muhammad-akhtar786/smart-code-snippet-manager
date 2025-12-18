package com.snippetmanager.module3;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;

public class MetricsDashboard extends JPanel {
    private String dataFilePath = "Data/sample_snippets.csv";
    private JLabel[] metricValues = new JLabel[4];

    // Helper method to get the executable path
    private String getExecutablePath() {
        String userDir = System.getProperty("user.dir");
        String projectRoot = userDir.contains("java") ? userDir.substring(0, userDir.indexOf("java")) : userDir;
        return new File(projectRoot, "cpp/module3/module3.exe").getAbsolutePath();
    }

    public MetricsDashboard() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Metrics area
        JPanel metricsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        metricsPanel.setBackground(new Color(245, 245, 245));

        metricValues[0] = new JLabel("0");
        metricValues[1] = new JLabel("0");
        metricValues[2] = new JLabel("0%");
        metricValues[3] = new JLabel("0%");

        metricsPanel.add(createMetricCard("Total Recommendations", metricValues[0], new Color(70, 130, 180)));
        metricsPanel.add(createMetricCard("Clicked", metricValues[1], new Color(34, 139, 34)));
        metricsPanel.add(createMetricCard("Accuracy", metricValues[2], new Color(178, 34, 34)));
        metricsPanel.add(createMetricCard("Coverage", metricValues[3], new Color(255, 140, 0)));

        add(metricsPanel, BorderLayout.CENTER);

        loadMetrics();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new LineBorder(new Color(70, 130, 180), 2, true));
        panel.setPreferredSize(new Dimension(0, 80));

        JLabel headerLabel = new JLabel("📈 Recommendation Quality Metrics");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(25, 25, 112));

        JLabel descLabel = new JLabel("System performance and recommendation quality indicators");
        descLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        descLabel.setForeground(new Color(100, 100, 100));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(headerLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createMetricCard(String label, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(new CompoundBorder(
                new LineBorder(color, 3),
                new EmptyBorder(20, 20, 20, 20)));
        card.setBackground(Color.WHITE);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Arial", Font.PLAIN, 12));
        labelText.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 48));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(labelText, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadMetrics() {
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "metrics", dataFilePath);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // FIX: Parse pipe-separated format from C++ backend
                        if (line.contains("total_recommendations|")) {
                            String value = line.split("\\|")[1].trim();
                            updateMetricCard(0, value);
                        } else if (line.contains("clicked_count|")) {
                            String value = line.split("\\|")[1].trim();
                            updateMetricCard(1, value);
                        } else if (line.contains("accuracy|")) {
                            String value = line.split("\\|")[1].trim();
                            updateMetricCard(2, value);
                        } else if (line.contains("coverage|")) {
                            String value = line.split("\\|")[1].trim();
                            updateMetricCard(3, value);
                        }
                    }
                }
                process.waitFor();

            } catch (Exception e) {
                e.printStackTrace(); // Show errors for debugging
            }
        }).start();
    }

    private void updateMetricCard(int index, String value) {
        SwingUtilities.invokeLater(() -> {
            if (index < metricValues.length) {
                metricValues[index].setText(value);
            }
        });
    }
}
