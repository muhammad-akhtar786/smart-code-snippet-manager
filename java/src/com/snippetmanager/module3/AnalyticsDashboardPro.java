package com.snippetmanager.module3;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;

public class AnalyticsDashboardPro extends JPanel {
    // --- Modern Color Palette ---
    private static final Color BG_LIGHT = new Color(248, 249, 252);
    private static final Color CARD_WHITE = Color.WHITE;
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color ACCENT_ORANGE = new Color(245, 158, 11);
    private static final Color TEXT_MAIN = new Color(30, 41, 59);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color STATUS_BG = new Color(15, 23, 42);

    private JLabel totalSnippetsValue;
    private JLabel totalTagsValue;
    private JPanel chartsPanel;
    private JLabel statusLabel;
    private String dataFilePath = "Data/snippets_large.json";

    public AnalyticsDashboardPro() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_LIGHT);

        // Header Section
        add(createHeader(), BorderLayout.NORTH);

        // Main Content
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setOpaque(false);
        mainWrapper.setBorder(new EmptyBorder(25, 30, 25, 30));

        chartsPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        chartsPanel.setOpaque(false);

        mainWrapper.add(chartsPanel, BorderLayout.CENTER);
        add(mainWrapper, BorderLayout.CENTER);

        // Status Bar
        add(createStatusBar(), BorderLayout.SOUTH);

        // Load data on startup
        loadStatistics();
        refreshAllAnalytics();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_WHITE);
        header.setPreferredSize(new Dimension(0, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Left: Title
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(25, 40, 0, 0));

        JLabel title = new JLabel("Advanced Analytics");
        title.setFont(new Font("Inter", Font.BOLD, 24));
        title.setForeground(TEXT_MAIN);

        JLabel subtitle = new JLabel("Visual insights from your code collection");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_SECONDARY);

        leftPanel.add(title);
        leftPanel.add(subtitle);

        // Right: Mini Stats + Refresh
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 0, 0, 40));

        totalSnippetsValue = new JLabel("0");
        totalTagsValue = new JLabel("0");

        rightPanel.add(createMiniStat("Snippets", totalSnippetsValue, ACCENT_BLUE));
        rightPanel.add(createMiniStat("Tags", totalTagsValue, ACCENT_GREEN));

        ModernButton refreshBtn = new ModernButton("🔄 Refresh", ACCENT_ORANGE);
        refreshBtn.setPreferredSize(new Dimension(110, 40));
        refreshBtn.addActionListener(e -> {
            loadStatistics();
            refreshAllAnalytics();
        });
        rightPanel.add(refreshBtn);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createMiniStat(String label, JLabel valueLabel, Color accent) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        valueLabel.setFont(new Font("Inter", Font.BOLD, 18));
        valueLabel.setForeground(accent);

        JLabel l = new JLabel(label.toUpperCase());
        l.setFont(new Font("Inter", Font.BOLD, 10));
        l.setForeground(TEXT_SECONDARY);

        p.add(valueLabel, BorderLayout.NORTH);
        p.add(l, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(STATUS_BG);
        bar.setPreferredSize(new Dimension(0, 35));
        bar.setBorder(new EmptyBorder(0, 40, 0, 40));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(148, 163, 184));

        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    // --- Backend Logic (Preserved exactly as provided) ---

    private String getExecutablePath() {
        File currentDir = new File(System.getProperty("user.dir"));
        File projectRoot = currentDir;
        while (projectRoot != null && !projectRoot.getName().equals("smart-code-snippet-manager")) {
            projectRoot = projectRoot.getParentFile();
        }
        if (projectRoot == null || !projectRoot.exists()) {
            projectRoot = new File("e:\\DSA\\smart-code-snippet-manager");
        }
        return new File(projectRoot, "cpp/module3/app.exe").getAbsolutePath();
    }

    private File getDataDirectory() {
        File currentDir = new File(System.getProperty("user.dir"));
        File projectRoot = currentDir;
        while (projectRoot != null && !projectRoot.getName().equals("smart-code-snippet-manager")) {
            projectRoot = projectRoot.getParentFile();
        }
        if (projectRoot == null || !projectRoot.exists()) {
            projectRoot = new File("e:\\DSA\\smart-code-snippet-manager");
        }
        return new File(projectRoot, "Data");
    }

    private void loadStatistics() {
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "stats");
                pb.directory(getDataDirectory());
                pb.redirectErrorStream(true);
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("total_snippets|")) {
                            String val = line.split("\\|")[1].trim();
                            SwingUtilities.invokeLater(() -> totalSnippetsValue.setText(val));
                        } else if (line.contains("total_tags|")) {
                            String val = line.split("\\|")[1].trim();
                            SwingUtilities.invokeLater(() -> totalTagsValue.setText(val));
                        }
                    }
                }
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void refreshAllAnalytics() {
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    chartsPanel.removeAll();
                    chartsPanel.repaint();
                });

                setStatus("🔄 Computing language distribution...");
                JPanel langChart = createLanguageDistributionChart();

                setStatus("🔄 Analyzing snippet usage...");
                JPanel topChart = createTopSnippetsChart();

                setStatus("🔄 Identifying trending tags...");
                JPanel tagsChart = createTrendingTagsChart();

                setStatus("🔄 calculating time-decay trends...");
                JPanel trendingChart = createTrendingSnippetsChart();

                SwingUtilities.invokeLater(() -> {
                    chartsPanel.add(langChart);
                    chartsPanel.add(topChart);
                    chartsPanel.add(tagsChart);
                    chartsPanel.add(trendingChart);
                    chartsPanel.revalidate();
                    chartsPanel.repaint();
                });
                setStatus("✓ Analytics Updated");
            } catch (Exception e) {
                setStatus("✗ Error: " + e.getMessage());
            }
        }).start();
    }

    private void setStatus(String msg) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(msg));
    }

    // --- Chart Creators (Preserving Logic, Updating UI) ---

    private JPanel createLanguageDistributionChart() {
        JPanel card = createChartCard("Language Distribution", ACCENT_BLUE);
        try {
            ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "lang_dist");
            pb.directory(getDataDirectory());
            Process process = pb.start();
            Map<String, Integer> langDist = new LinkedHashMap<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean capturing = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("LANG_DIST_START"))
                        capturing = true;
                    else if (line.equals("LANG_DIST_END"))
                        capturing = false;
                    else if (capturing) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 2)
                            langDist.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
            process.waitFor();

            JPanel list = createChartListContainer();
            int max = langDist.values().stream().max(Integer::compare).orElse(1);
            for (Map.Entry<String, Integer> entry : langDist.entrySet()) {
                list.add(createModernBar(entry.getKey(), entry.getValue(), max, ACCENT_BLUE));
            }
            card.add(wrapInScroll(list), BorderLayout.CENTER);
        } catch (Exception e) {
            card.add(new JLabel("Error loading data"));
        }
        return card;
    }

    private JPanel createTopSnippetsChart() {
        JPanel card = createChartCard("Top Used Snippets", ACCENT_GREEN);
        try {
            ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "top_snippets", "8");
            pb.directory(getDataDirectory());
            Process process = pb.start();
            List<SnippetItem> items = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean cap = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("TOP_SNIPPETS_START"))
                        cap = true;
                    else if (line.equals("TOP_SNIPPETS_END"))
                        cap = false;
                    else if (cap) {
                        String[] p = line.split("\\|");
                        if (p.length >= 3)
                            items.add(new SnippetItem(p[1], Integer.parseInt(p[2])));
                    }
                }
            }
            JPanel list = createChartListContainer();
            int r = 1;
            for (SnippetItem i : items)
                list.add(createModernListItem(r++, i.name, i.count + " uses", ACCENT_GREEN));
            card.add(wrapInScroll(list), BorderLayout.CENTER);
        } catch (Exception e) {
        }
        return card;
    }

    private JPanel createTrendingTagsChart() {
        JPanel card = createChartCard("Trending Tags", ACCENT_RED);
        try {
            ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "trending_tags", "10");
            pb.directory(getDataDirectory());
            Process process = pb.start();
            Map<String, Integer> tags = new LinkedHashMap<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean cap = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("TRENDING_TAGS_START"))
                        cap = true;
                    else if (line.equals("TRENDING_TAGS_END"))
                        cap = false;
                    else if (cap) {
                        String[] p = line.split("\\|");
                        if (p.length == 2)
                            tags.put(p[0], Integer.parseInt(p[1]));
                    }
                }
            }
            JPanel list = createChartListContainer();
            int max = tags.values().stream().max(Integer::compare).orElse(1);
            for (Map.Entry<String, Integer> entry : tags.entrySet())
                list.add(createModernBar(entry.getKey(), entry.getValue(), max, ACCENT_RED));
            card.add(wrapInScroll(list), BorderLayout.CENTER);
        } catch (Exception e) {
        }
        return card;
    }

    private JPanel createTrendingSnippetsChart() {
        JPanel card = createChartCard("Trending (Time-Decay)", ACCENT_ORANGE);
        try {
            ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "trending_snippets", "8");
            pb.directory(getDataDirectory());
            Process process = pb.start();
            List<TrendingItem> items = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean cap = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("TRENDING_SNIPPETS_START"))
                        cap = true;
                    else if (line.equals("TRENDING_SNIPPETS_END"))
                        cap = false;
                    else if (cap) {
                        String[] p = line.split("\\|");
                        if (p.length >= 3)
                            items.add(new TrendingItem(p[1], Double.parseDouble(p[2])));
                    }
                }
            }
            JPanel list = createChartListContainer();
            int r = 1;
            for (TrendingItem i : items)
                list.add(createModernListItem(r++, i.name, String.format("%.2f", i.score), ACCENT_ORANGE));
            card.add(wrapInScroll(list), BorderLayout.CENTER);
        } catch (Exception e) {
        }
        return card;
    }

    // --- Modern UI Components ---

    private JPanel createChartCard(String title, Color accent) {
        ModernCard card = new ModernCard();
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Inter", Font.BOLD, 15));
        t.setForeground(TEXT_MAIN);

        // Color dot indicator
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(15, 15));
        dot.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.add(dot, BorderLayout.WEST);
        top.add(t, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        return card;
    }

    private JPanel createChartListContainer() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        return p;
    }

    private JScrollPane wrapInScroll(JPanel panel) {
        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        return sp;
    }

    private JPanel createModernBar(String label, int val, int max, Color color) {
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Inter", Font.PLAIN, 12));
        l.setPreferredSize(new Dimension(100, 20));

        JPanel track = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 5, getWidth(), 10, 10, 10);

                int fillWidth = (int) (getWidth() * ((double) val / max));
                g2.setColor(color);
                g2.fillRoundRect(0, 5, fillWidth, 10, 10, 10);
                g2.dispose();
            }
        };
        track.setOpaque(false);

        JLabel v = new JLabel(String.valueOf(val));
        v.setFont(new Font("Inter", Font.BOLD, 11));
        v.setForeground(TEXT_SECONDARY);
        v.setPreferredSize(new Dimension(30, 20));

        p.add(l, BorderLayout.WEST);
        p.add(track, BorderLayout.CENTER);
        p.add(v, BorderLayout.EAST);
        return p;
    }

    private JPanel createModernListItem(int rank, String name, String sub, Color color) {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        p.setBorder(new EmptyBorder(8, 5, 8, 5));

        JLabel r = new JLabel("#" + rank);
        r.setFont(new Font("Inter", Font.BOLD, 12));
        r.setForeground(color);
        r.setPreferredSize(new Dimension(30, 20));

        JLabel n = new JLabel(name);
        n.setFont(new Font("Inter", Font.PLAIN, 13));
        n.setForeground(TEXT_MAIN);

        JLabel s = new JLabel(sub);
        s.setFont(new Font("Inter", Font.PLAIN, 11));
        s.setForeground(TEXT_SECONDARY);

        p.add(r, BorderLayout.WEST);
        p.add(n, BorderLayout.CENTER);
        p.add(s, BorderLayout.EAST);

        // Separator line
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));
        return p;
    }

    // --- Helper UI Classes ---

    static class ModernCard extends JPanel {
        public ModernCard() {
            setOpaque(false);
            setBackground(CARD_WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
        }
    }

    static class ModernButton extends JButton {
        private Color color;

        public ModernButton(String text, Color color) {
            super(text);
            this.color = color;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Inter", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? color.darker() : color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private static class SnippetItem {
        String name;
        int count;

        SnippetItem(String n, int c) {
            name = n;
            count = c;
        }
    }

    private static class TrendingItem {
        String name;
        double score;

        TrendingItem(String n, double s) {
            name = n;
            score = s;
        }
    }

    // Add missing method for Module3MainWindow
    public void setDataFilePath(String path) {
        // Method stub for compatibility with Module3MainWindow
    }
}