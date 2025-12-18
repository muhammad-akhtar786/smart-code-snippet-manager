package com.snippetmanager.module3;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class AnalyticsDashboardPro extends JPanel {
    private JLabel totalSnippetsLabel;
    private JLabel totalTagsLabel;
    private JPanel chartsPanel;
    private JLabel statusLabel;
    private String dataFilePath = "Data/sample_snippets.csv";

    // Helper method to get the executable path
    private String getExecutablePath() {
        String userDir = System.getProperty("user.dir");
        String projectRoot = userDir.contains("java") ? userDir.substring(0, userDir.indexOf("java")) : userDir;
        return new File(projectRoot, "cpp/module3/module3.exe").getAbsolutePath();
    }

    public AnalyticsDashboardPro() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content with charts
        chartsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        chartsPanel.setBackground(new Color(245, 245, 245));
        chartsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        add(chartsPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        // Load initial data
        loadStatistics();
        refreshAllAnalytics();
    }

    private void loadStatistics() {
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "stats", dataFilePath);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                int totalSnippets = 0;
                int totalTags = 0;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // FIX: Parse pipe-separated format from C++ backend (total_snippets|520)
                        if (line.contains("total_snippets|")) {
                            String[] parts = line.split("\\|");
                            if (parts.length > 1) {
                                try {
                                    totalSnippets = Integer.parseInt(parts[1].trim());
                                } catch (NumberFormatException e) {
                                    totalSnippets = 0;
                                }
                            }
                        } else if (line.contains("total_tags|")) {
                            String[] parts = line.split("\\|");
                            if (parts.length > 1) {
                                try {
                                    totalTags = Integer.parseInt(parts[1].trim());
                                } catch (NumberFormatException e) {
                                    totalTags = 0;
                                }
                            }
                        }
                    }
                }
                process.waitFor();

                final int snippets = totalSnippets;
                final int tags = totalTags;
                SwingUtilities.invokeLater(() -> {
                    totalSnippetsLabel.setText("Total Snippets: " + snippets);
                    totalTagsLabel.setText("Total Tags: " + tags);
                });
            } catch (Exception e) {
                e.printStackTrace(); // Show errors for debugging
            }
        }).start();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new LineBorder(new Color(70, 130, 180), 2, true));
        panel.setPreferredSize(new Dimension(0, 80));

        JLabel headerLabel = new JLabel("📊 Advanced Analytics Dashboard");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(25, 25, 112));

        // Stats cards
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        statsPanel.setOpaque(false);

        totalSnippetsLabel = createStatLabel("Total Snippets: 0", new Color(70, 130, 180));
        totalTagsLabel = createStatLabel("Total Tags: 0", new Color(34, 139, 34));

        statsPanel.add(totalSnippetsLabel);
        statsPanel.add(totalTagsLabel);

        panel.add(headerLabel, BorderLayout.WEST);
        panel.add(statsPanel, BorderLayout.EAST);

        return panel;
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(color);
        return label;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EtchedBorder());
        statusLabel = new JLabel("Loading analytics...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void refreshAllAnalytics() {
        new Thread(() -> {
            try {
                chartsPanel.removeAll();

                // Language Distribution
                chartsPanel.add(createLanguageDistributionChart());

                // Top Snippets
                chartsPanel.add(createTopSnippetsChart());

                // Trending Tags
                chartsPanel.add(createTrendingTagsChart());

                // Trending Snippets (time-decay)
                chartsPanel.add(createTrendingSnippetsChart());

                chartsPanel.revalidate();
                chartsPanel.repaint();

                setStatus("✓ Analytics updated successfully");

            } catch (Exception e) {
                setStatus("✗ Error: " + e.getMessage());
            }
        }).start();
    }

    private JPanel createLanguageDistributionChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(new LineBorder(new Color(100, 149, 237), 2),
                "💻 Language Distribution", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(25, 25, 112)));
        panel.setBackground(Color.WHITE);

        try {
            // Execute C++ program to get language distribution
            ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "lang_dist", dataFilePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            Map<String, Integer> langDist = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean capturing = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("LANG_DIST_START")) {
                        capturing = true;
                    } else if (line.equals("LANG_DIST_END")) {
                        capturing = false;
                    } else if (capturing) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 2) {
                            langDist.put(parts[0], Integer.parseInt(parts[1]));
                        }
                    }
                }
            }
            process.waitFor();

            // Create bar chart visualization
            JTextArea chart = new JTextArea();
            chart.setEditable(false);
            chart.setFont(new Font("Monospaced", Font.PLAIN, 11));
            chart.setBackground(Color.WHITE);

            StringBuilder chartText = new StringBuilder();
            int maxCount = langDist.values().stream().max(Integer::compare).orElse(1);

            for (Map.Entry<String, Integer> entry : langDist.entrySet()) {
                int barLength = (int) (entry.getValue() * 20.0 / maxCount);
                String bar = "█".repeat(barLength);
                chartText.append(String.format("%-8s %s %d\n", entry.getKey() + ":", bar, entry.getValue()));
            }

            chart.setText(chartText.toString());
            panel.add(new JScrollPane(chart), BorderLayout.CENTER);

        } catch (Exception e) {
            JLabel error = new JLabel("Error loading chart: " + e.getMessage());
            error.setForeground(Color.RED);
            panel.add(error, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createTopSnippetsChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(new LineBorder(new Color(34, 139, 34), 2),
                "⭐ Top Used Snippets", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(25, 25, 112)));
        panel.setBackground(Color.WHITE);

        try {
            ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "top_snippets", dataFilePath, "8");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            List<String> topSnippets = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean capturing = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("TOP_SNIPPETS_START")) {
                        capturing = true;
                    } else if (line.equals("TOP_SNIPPETS_END")) {
                        capturing = false;
                    } else if (capturing) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 3) {
                            topSnippets.add(parts[1] + " (" + parts[2] + " uses)");
                        }
                    }
                }
            }
            process.waitFor();

            JTextArea list = new JTextArea();
            list.setEditable(false);
            list.setFont(new Font("Arial", Font.PLAIN, 11));
            list.setBackground(Color.WHITE);

            StringBuilder text = new StringBuilder();
            int rank = 1;
            for (String snippet : topSnippets) {
                text.append(String.format("%d. %s\n", rank++, snippet));
            }

            list.setText(text.toString());
            panel.add(new JScrollPane(list), BorderLayout.CENTER);

        } catch (Exception e) {
            JLabel error = new JLabel("Error: " + e.getMessage());
            error.setForeground(Color.RED);
            panel.add(error, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createTrendingTagsChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(new LineBorder(new Color(178, 34, 34), 2),
                "🔥 Trending Tags", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(25, 25, 112)));
        panel.setBackground(Color.WHITE);

        try {
            ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "trending_tags", dataFilePath, "10");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            Map<String, Integer> tags = new LinkedHashMap<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean capturing = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("TRENDING_TAGS_START")) {
                        capturing = true;
                    } else if (line.equals("TRENDING_TAGS_END")) {
                        capturing = false;
                    } else if (capturing) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 2) {
                            tags.put(parts[0], Integer.parseInt(parts[1]));
                        }
                    }
                }
            }
            process.waitFor();

            JTextArea chart = new JTextArea();
            chart.setEditable(false);
            chart.setFont(new Font("Monospaced", Font.PLAIN, 10));
            chart.setBackground(Color.WHITE);

            StringBuilder chartText = new StringBuilder();
            int maxCount = tags.values().stream().max(Integer::compare).orElse(1);

            for (Map.Entry<String, Integer> entry : tags.entrySet()) {
                int barLength = (int) (entry.getValue() * 15.0 / maxCount);
                String bar = "▓".repeat(barLength);
                chartText.append(String.format("%-12s %s %d\n", entry.getKey() + ":", bar, entry.getValue()));
            }

            chart.setText(chartText.toString());
            panel.add(new JScrollPane(chart), BorderLayout.CENTER);

        } catch (Exception e) {
            JLabel error = new JLabel("Error: " + e.getMessage());
            error.setForeground(Color.RED);
            panel.add(error, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createTrendingSnippetsChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(new LineBorder(new Color(255, 140, 0), 2),
                "📈 Trending Snippets (Time-Decay)", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(25, 25, 112)));
        panel.setBackground(Color.WHITE);

        try {
            ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "trending_snippets", dataFilePath, "8");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            List<String> snippets = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean capturing = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("TRENDING_SNIPPETS_START")) {
                        capturing = true;
                    } else if (line.equals("TRENDING_SNIPPETS_END")) {
                        capturing = false;
                    } else if (capturing) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 2) {
                            double score = Double.parseDouble(parts[2]);
                            snippets.add(String.format("%-30s [Score: %.2f]", parts[1], score));
                        }
                    }
                }
            }
            process.waitFor();

            JTextArea list = new JTextArea();
            list.setEditable(false);
            list.setFont(new Font("Arial", Font.PLAIN, 10));
            list.setBackground(Color.WHITE);

            StringBuilder text = new StringBuilder();
            int rank = 1;
            for (String snippet : snippets) {
                text.append(String.format("%d. %s\n", rank++, snippet));
            }

            list.setText(text.toString());
            panel.add(new JScrollPane(list), BorderLayout.CENTER);

        } catch (Exception e) {
            JLabel error = new JLabel("Error: " + e.getMessage());
            error.setForeground(Color.RED);
            panel.add(error, BorderLayout.CENTER);
        }

        return panel;
    }

    public void setDataFilePath(String path) {
        this.dataFilePath = path;
    }
}
