package com.snippetmanager.module3;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class MetricsDashboard extends JPanel {
    // --- Modern Color Palette (Synced with Module 1) ---
    private static final Color BG_LIGHT = new Color(248, 249, 252);
    private static final Color CARD_WHITE = Color.WHITE;
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color ACCENT_PURPLE = new Color(139, 92, 246);
    private static final Color ACCENT_ORANGE = new Color(245, 158, 11);
    private static final Color TEXT_MAIN = new Color(30, 41, 59);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color STATUS_BG = new Color(15, 23, 42);

    private MetricCard[] metricCards = new MetricCard[4];
    private JLabel statusLabel;

    public MetricsDashboard() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_LIGHT);

        // Header Section
        add(createHeader(), BorderLayout.NORTH);

        // Main Grid Panel
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setOpaque(false);
        mainWrapper.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel metricsGrid = new JPanel(new GridLayout(2, 2, 25, 25));
        metricsGrid.setOpaque(false);

        // Initialize Cards with modern colors
        metricCards[0] = new MetricCard("Total Recommendations", "0", "recommendations made", ACCENT_BLUE);
        metricCards[1] = new MetricCard("Clicked Recommendations", "0", "user interactions", ACCENT_GREEN);
        metricCards[2] = new MetricCard("Recommendation Accuracy", "0%", "success rate", ACCENT_PURPLE);
        metricCards[3] = new MetricCard("Snippet Coverage", "0%", "overall coverage", ACCENT_ORANGE);

        for (MetricCard card : metricCards)
            metricsGrid.add(card);

        mainWrapper.add(metricsGrid, BorderLayout.CENTER);

        // Info Banner at bottom of grid
        mainWrapper.add(createInfoBanner(), BorderLayout.SOUTH);

        add(mainWrapper, BorderLayout.CENTER);

        // Status Bar
        add(createStatusBar(), BorderLayout.SOUTH);

        loadMetrics();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_WHITE);
        header.setPreferredSize(new Dimension(0, 90));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(20, 40, 0, 0));

        JLabel title = new JLabel("Analytics Dashboard");
        title.setFont(new Font("Inter", Font.BOLD, 22));
        title.setForeground(TEXT_MAIN);

        JLabel subtitle = new JLabel("Real-time performance indicators for the recommendation engine");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_SECONDARY);

        leftPanel.add(title);
        leftPanel.add(subtitle);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 25));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 0, 0, 40));

        ModernButton refreshBtn = new ModernButton("Refresh Data", ACCENT_BLUE);
        refreshBtn.setPreferredSize(new Dimension(140, 40));
        refreshBtn.addActionListener(e -> loadMetrics());

        rightPanel.add(refreshBtn);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createInfoBanner() {
        ModernCard banner = new ModernCard();
        banner.setLayout(new BorderLayout(20, 0));
        banner.setBorder(new EmptyBorder(15, 25, 15, 25));
        banner.setBackground(new Color(239, 246, 255)); // Soft blue tint

        JLabel infoText = new JLabel(
                "<html><b>Pro Tip:</b> Accuracy and Coverage metrics are calculated based on the last 100 user queries. Higher coverage leads to better snippet discoverability.</html>");
        infoText.setFont(new Font("Inter", Font.PLAIN, 12));
        infoText.setForeground(new Color(30, 64, 175));

        banner.add(new JLabel("💡"), BorderLayout.WEST);
        banner.add(infoText, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(30, 0, 0, 0));
        wrapper.add(banner, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(STATUS_BG);
        bar.setPreferredSize(new Dimension(0, 35));
        bar.setBorder(new EmptyBorder(0, 40, 0, 40));

        statusLabel = new JLabel("System Ready");
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(148, 163, 184));

        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    // --- Backend Integration (Unchanged Logic) ---

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

    private void loadMetrics() {
        statusLabel.setText("🔄 Synchronizing metrics with backend...");
        for (MetricCard card : metricCards)
            card.startLoading();

        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "metrics");
                pb.directory(getDataDirectory());
                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("total_recommendations|"))
                            updateCard(0, line.split("\\|")[1], false);
                        else if (line.contains("clicked_count|"))
                            updateCard(1, line.split("\\|")[1], false);
                        else if (line.contains("accuracy|"))
                            updateCard(2, line.split("\\|")[1], true);
                        else if (line.contains("coverage|"))
                            updateCard(3, line.split("\\|")[1], true);
                    }
                }
                process.waitFor();
                SwingUtilities.invokeLater(
                        () -> statusLabel.setText("✓ Data synchronized at " + new java.util.Date().toString()));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> statusLabel.setText("✗ Backend Error: " + e.getMessage()));
            }
        }).start();
    }

    private void updateCard(int index, String value, boolean isPct) {
        String val = isPct && !value.endsWith("%") ? value + "%" : value;
        SwingUtilities.invokeLater(() -> metricCards[index].setValue(val.trim()));
    }

    // --- UI Custom Components ---

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

    private static class MetricCard extends ModernCard {
        private JLabel valLabel;
        private Color accent;

        public MetricCard(String title, String initialVal, String subtitle, Color accent) {
            this.accent = accent;
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel tLabel = new JLabel(title.toUpperCase());
            tLabel.setFont(new Font("Inter", Font.BOLD, 12));
            tLabel.setForeground(TEXT_SECONDARY);

            valLabel = new JLabel(initialVal);
            valLabel.setFont(new Font("Inter", Font.BOLD, 42));
            valLabel.setForeground(TEXT_MAIN);

            JLabel sLabel = new JLabel(subtitle);
            sLabel.setFont(new Font("Inter", Font.PLAIN, 12));
            sLabel.setForeground(TEXT_SECONDARY);

            // Left accent line
            JPanel accentBar = new JPanel();
            accentBar.setBackground(accent);
            accentBar.setPreferredSize(new Dimension(4, 0));

            JPanel content = new JPanel(new GridLayout(3, 1, 0, 5));
            content.setOpaque(false);
            content.add(tLabel);
            content.add(valLabel);
            content.add(sLabel);

            add(content, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(252, 253, 255));
                }

                public void mouseExited(MouseEvent e) {
                    setBackground(CARD_WHITE);
                }
            });
        }

        public void setValue(String val) {
            valLabel.setText(val);
            valLabel.setForeground(accent);
        }

        public void startLoading() {
            valLabel.setText("...");
            valLabel.setForeground(TEXT_SECONDARY);
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
}