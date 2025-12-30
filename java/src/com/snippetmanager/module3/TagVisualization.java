package com.snippetmanager.module3;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class TagVisualization extends JPanel {
    private JTextField tagInputField;
    private JButton visualizeBtn;
    private VisualizationCanvas canvas;
    private JTextArea infoArea;
    private JLabel statusLabel;
    private String dataFilePath = "Data/snippets_large.json";
    private JCheckBox showLabelsCheck;
    private JCheckBox animateCheck;

    // Modern Color Scheme
    private static final Color PRIMARY_BLUE = new Color(66, 133, 244);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_MUTED = new Color(108, 117, 125);
    private static final Color CONTENT_BG = new Color(248, 249, 250);
    private static final Color BUTTON_GREEN = new Color(40, 167, 69);
    private static final Color BUTTON_ORANGE = new Color(255, 152, 0);
    private static final Color BUTTON_RED = new Color(244, 67, 54);
    private static final Color BORDER_LIGHT = new Color(222, 226, 230);
    private static final Color CANVAS_BG = new Color(250, 251, 252);

    private String getExecutablePath() {
        File currentDir = new File(System.getProperty("user.dir"));
        File projectRoot = currentDir;

        while (projectRoot != null && !projectRoot.getName().equals("smart-code-snippet-manager")) {
            projectRoot = projectRoot.getParentFile();
        }

        if (projectRoot == null || !projectRoot.exists()) {
            projectRoot = new File("e:\\DSA\\smart-code-snippet-manager");
        }

        File exe = new File(projectRoot, "cpp/module3/app.exe");
        return exe.getAbsolutePath();
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

    public TagVisualization() {
        setLayout(new BorderLayout(0, 0));
        setBackground(CONTENT_BG);

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content area
        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBackground(CONTENT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // Control panel (left)
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.WEST);

        // Visualization canvas (center)
        canvas = new VisualizationCanvas();
        mainPanel.add(canvas, BorderLayout.CENTER);

        // Info panel (right)
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        // Title section
        JPanel titlePanel = new JPanel(new BorderLayout(0, 8));
        titlePanel.setOpaque(false);

        JLabel headerLabel = new JLabel("🔗 Tag Network Visualization");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(TEXT_DARK);

        JLabel subtitle = new JLabel("Explore tag relationships and co-occurrence patterns");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);

        titlePanel.add(headerLabel, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);

        // Legend
        JPanel legendPanel = createLegendPanel();

        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(legendPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panel.setOpaque(false);

        panel.add(createLegendItem("●", PRIMARY_BLUE, "Center Tag"));
        panel.add(createLegendItem("●", BUTTON_GREEN, "Connected Tags"));

        return panel;
    }

    private JPanel createLegendItem(String symbol, Color color, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setOpaque(false);

        JLabel symbolLabel = new JLabel(symbol);
        symbolLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        symbolLabel.setForeground(color);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textLabel.setForeground(TEXT_MUTED);

        item.add(symbolLabel);
        item.add(textLabel);

        return item;
    }

    private JPanel createControlPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setPreferredSize(new Dimension(280, 0));

        // Search Section
        JLabel searchLabel = new JLabel("🔍 Search Tag Network");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_DARK);
        searchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tagInputField = new RoundedTextField();
        tagInputField.setPreferredSize(new Dimension(0, 40));
        tagInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tagInputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        tagInputField.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagInputField.addActionListener(e -> visualizeTagNetwork());

        visualizeBtn = new RoundedButton("🔗 Visualize Network", PRIMARY_BLUE, TEXT_LIGHT, 20);
        visualizeBtn.setPreferredSize(new Dimension(0, 42));
        visualizeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        visualizeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        visualizeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        visualizeBtn.addActionListener(e -> visualizeTagNetwork());

        // Options Section
        JLabel optionsLabel = new JLabel("⚙️ Display Options");
        optionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        optionsLabel.setForeground(TEXT_DARK);
        optionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        showLabelsCheck = new JCheckBox("Show tag labels");
        showLabelsCheck.setSelected(true);
        showLabelsCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showLabelsCheck.setBackground(CARD_BG);
        showLabelsCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        showLabelsCheck.addActionListener(e -> canvas.setShowLabels(showLabelsCheck.isSelected()));

        animateCheck = new JCheckBox("Enable animation");
        animateCheck.setSelected(false);
        animateCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        animateCheck.setBackground(CARD_BG);
        animateCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        animateCheck.addActionListener(e -> canvas.setAnimate(animateCheck.isSelected()));

        // Actions Section
        JLabel actionsLabel = new JLabel("🎬 Actions");
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actionsLabel.setForeground(TEXT_DARK);
        actionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton clearBtn = new RoundedButton("Clear Canvas", new Color(108, 117, 125), TEXT_LIGHT, 20);
        clearBtn.setPreferredSize(new Dimension(0, 38));
        clearBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearBtn.addActionListener(e -> {
            canvas.clear();
            tagInputField.setText("");
            infoArea.setText("Canvas cleared. Enter a tag to visualize its network.");
        });

        RoundedButton exportBtn = new RoundedButton("📥 Export Image", BUTTON_GREEN, TEXT_LIGHT, 20);
        exportBtn.setPreferredSize(new Dimension(0, 38));
        exportBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportBtn.addActionListener(e -> exportVisualization());

        RoundedButton refreshBtn = new RoundedButton("🔄 Refresh", BUTTON_ORANGE, TEXT_LIGHT, 20);
        refreshBtn.setPreferredSize(new Dimension(0, 38));
        refreshBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshBtn.addActionListener(e -> visualizeTagNetwork());

        // Assembly
        card.add(searchLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(tagInputField);
        card.add(Box.createVerticalStrut(10));
        card.add(visualizeBtn);
        card.add(Box.createVerticalStrut(25));

        card.add(optionsLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(showLabelsCheck);
        card.add(Box.createVerticalStrut(5));
        card.add(animateCheck);
        card.add(Box.createVerticalStrut(25));

        card.add(actionsLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(clearBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(exportBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(refreshBtn);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createInfoPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setPreferredSize(new Dimension(300, 0));

        // Header
        JLabel infoLabel = new JLabel("ℹ️ Tag Information");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoLabel.setForeground(TEXT_DARK);

        // Info area
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setBackground(CANVAS_BG);
        infoArea.setForeground(TEXT_DARK);
        infoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoArea.setText(
                "Select a tag to view its network information.\n\nConnected tags and their frequencies will appear here.");

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));

        card.add(infoLabel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        panel.setBackground(new Color(52, 58, 70));
        panel.setPreferredSize(new Dimension(0, 35));

        statusLabel = new JLabel("🟢 Ready to visualize tag networks");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_LIGHT);

        JLabel helpLabel = new JLabel("Tip: Try tags like 'sorting', 'graph', or 'dynamic-programming'");
        helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        helpLabel.setForeground(TEXT_MUTED);

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(helpLabel, BorderLayout.EAST);

        return panel;
    }

    private void visualizeTagNetwork() {
        String tag = tagInputField.getText().trim();

        if (tag.isEmpty()) {
            showError("Please enter a tag to visualize");
            return;
        }

        setStatus("🔄 Loading tag network for: " + tag + "...");
        visualizeBtn.setEnabled(false);

        new Thread(() -> {
            try {
                List<String[]> cooccurrence = getTagCooccurrence(tag);
                Map<String, Integer> tagInfo = getTrendingTags(20);

                SwingUtilities.invokeLater(() -> {
                    canvas.visualizeNetwork(tag, cooccurrence, tagInfo);
                    displayTagInfo(tag, cooccurrence);
                    setStatus("✓ Successfully visualized network for: " + tag);
                    visualizeBtn.setEnabled(true);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showError("Failed to visualize: " + e.getMessage());
                    visualizeBtn.setEnabled(true);
                });
            }
        }).start();
    }

    private List<String[]> getTagCooccurrence(String tag) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "tag_cooccur", tag);
        pb.directory(getDataDirectory());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        List<String[]> results = new ArrayList<>();
        boolean capturing = false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("TAG_COOCCUR_START")) {
                    capturing = true;
                } else if (line.equals("TAG_COOCCUR_END")) {
                    capturing = false;
                } else if (capturing) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 2) {
                        results.add(parts);
                    }
                }
            }
        }

        process.waitFor();
        return results;
    }

    private Map<String, Integer> getTrendingTags(int n) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "trending_tags", String.valueOf(n));
        pb.directory(getDataDirectory());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        Map<String, Integer> results = new HashMap<>();
        boolean capturing = false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("TRENDING_TAGS_START")) {
                    capturing = true;
                } else if (line.equals("TRENDING_TAGS_END")) {
                    capturing = false;
                } else if (capturing) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 2) {
                        results.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
        }

        process.waitFor();
        return results;
    }

    private void displayTagInfo(String tag, List<String[]> cooccurrence) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════\n");
        sb.append("  TAG: ").append(tag.toUpperCase()).append("\n");
        sb.append("═══════════════════════════════\n\n");
        sb.append("Connected Tags:\n");
        sb.append("───────────────────────────────\n\n");

        if (cooccurrence.isEmpty()) {
            sb.append("⚠ No connections found.\n\n");
            sb.append("This tag might be:\n");
            sb.append("• New to the system\n");
            sb.append("• Rarely used\n");
            sb.append("• Spelled incorrectly\n");
        } else {
            int displayCount = Math.min(15, cooccurrence.size());
            for (int i = 0; i < displayCount; i++) {
                String[] data = cooccurrence.get(i);
                String emoji = i < 3 ? "🔥" : "●";
                sb.append(String.format("%s %2d. %-20s (%3s)\n",
                        emoji, i + 1, data[0], data[1]));
            }

            sb.append("\n");
            sb.append("───────────────────────────────\n");
            sb.append(String.format("Total Connections: %d\n", cooccurrence.size()));
            sb.append(String.format("Displayed: %d\n", displayCount));
        }

        infoArea.setText(sb.toString());
        infoArea.setCaretPosition(0);
    }

    private void exportVisualization() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Visualization");
        fileChooser.setSelectedFile(new File("tag_network.png"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                canvas.exportToImage(fileChooser.getSelectedFile());
                setStatus("✓ Exported to: " + fileChooser.getSelectedFile().getName());
                JOptionPane.showMessageDialog(this,
                        "Visualization exported successfully!\n\nSaved to: "
                                + fileChooser.getSelectedFile().getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showError("Export failed: " + e.getMessage());
            }
        }
    }

    private void setStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(TEXT_LIGHT);
        });
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("✗ Error: " + message);
            statusLabel.setForeground(new Color(255, 100, 100));
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    public void setDataFilePath(String path) {
        this.dataFilePath = path;
    }

    // Custom Components
    static class RoundedButton extends JButton {
        private Color bgColor, fgColor;
        private int radius;

        public RoundedButton(String text, Color bg, Color fg, int radius) {
            super(text);
            this.bgColor = bg;
            this.fgColor = fg;
            this.radius = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(fg);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? bgColor.darker()
                    : (getModel().isRollover() ? bgColor.brighter() : bgColor));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(fgColor);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    static class RoundedTextField extends JTextField {
        private int radius = 12;

        public RoundedTextField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

class VisualizationCanvas extends JPanel {
    private String centerTag;
    private List<String[]> connections;
    private Map<String, Integer> tagFrequencies;
    private boolean showLabels = true;
    private boolean animate = false;
    private Map<String, Point> nodePositions = new HashMap<>();

    // Modern Colors
    private static final Color PRIMARY_BLUE = new Color(66, 133, 244);
    private static final Color BUTTON_GREEN = new Color(40, 167, 69);
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_MUTED = new Color(108, 117, 125);
    private static final Color CANVAS_BG = new Color(250, 251, 252);
    private static final Color EDGE_COLOR = new Color(200, 210, 220);

    public VisualizationCanvas() {
        setBackground(CANVAS_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
    }

    public void visualizeNetwork(String tag, List<String[]> cooccurrence, Map<String, Integer> frequencies) {
        this.centerTag = tag;
        this.connections = cooccurrence;
        this.tagFrequencies = frequencies;
        calculateNodePositions();
        repaint();
    }

    private void calculateNodePositions() {
        nodePositions.clear();

        if (centerTag == null || connections == null)
            return;

        int width = getWidth() - 40;
        int height = getHeight() - 40;
        int centerX = width / 2 + 20;
        int centerY = height / 2 + 20;

        // Center node
        nodePositions.put(centerTag, new Point(centerX, centerY));

        // Arrange connected nodes in a circle
        int radius = Math.min(width, height) / 3;
        int numNodes = Math.min(connections.size(), 15);

        for (int i = 0; i < numNodes; i++) {
            String tag = connections.get(i)[0];
            double angle = 2 * Math.PI * i / numNodes - Math.PI / 2; // Start from top
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            nodePositions.put(tag, new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (centerTag == null || connections == null) {
            drawEmptyState(g2d);
            g2d.dispose();
            return;
        }

        // Draw edges with gradient
        Point center = nodePositions.get(centerTag);
        if (center != null) {
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < Math.min(connections.size(), 15); i++) {
                String tag = connections.get(i)[0];
                Point p = nodePositions.get(tag);
                if (p != null) {
                    // Gradient color based on connection strength
                    int freq = Integer.parseInt(connections.get(i)[1]);
                    int alpha = Math.min(100 + freq * 10, 200);
                    g2d.setColor(new Color(EDGE_COLOR.getRed(), EDGE_COLOR.getGreen(),
                            EDGE_COLOR.getBlue(), alpha));
                    g2d.drawLine(center.x, center.y, p.x, p.y);
                }
            }
        }

        // Draw nodes
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            String tag = entry.getKey();
            Point p = entry.getValue();

            // Node size based on frequency
            int freq = tagFrequencies.getOrDefault(tag, 1);
            int nodeSize = Math.min(35 + freq * 2, 65);

            // Shadow
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillOval(p.x - nodeSize / 2 + 2, p.y - nodeSize / 2 + 2, nodeSize, nodeSize);

            // Color based on whether it's center or connected
            if (tag.equals(centerTag)) {
                g2d.setColor(PRIMARY_BLUE);
            } else {
                g2d.setColor(BUTTON_GREEN);
            }

            g2d.fillOval(p.x - nodeSize / 2, p.y - nodeSize / 2, nodeSize, nodeSize);

            // Border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(p.x - nodeSize / 2, p.y - nodeSize / 2, nodeSize, nodeSize);

            // Label
            if (showLabels) {
                g2d.setColor(TEXT_DARK);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(tag);

                // Label background
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRoundRect(p.x - labelWidth / 2 - 6, p.y + nodeSize / 2 + 8,
                        labelWidth + 12, fm.getHeight() + 4, 8, 8);

                // Label text
                g2d.setColor(TEXT_DARK);
                g2d.drawString(tag, p.x - labelWidth / 2, p.y + nodeSize / 2 + 22);
            }

            // Frequency badge for center node
            if (tag.equals(centerTag)) {
                g2d.setColor(new Color(255, 193, 7));
                g2d.fillOval(p.x + nodeSize / 3, p.y - nodeSize / 3, 20, 20);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2d.drawString("★", p.x + nodeSize / 3 + 5, p.y - nodeSize / 3 + 15);
            }
        }

        g2d.dispose();
    }

    private void drawEmptyState(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Icon
        g2d.setColor(TEXT_MUTED);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        String icon = "🔗";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(icon, centerX - fm.stringWidth(icon) / 2, centerY - 30);

        // Message
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.setColor(TEXT_DARK);
        String msg = "Select a tag to visualize its network";
        fm = g2d.getFontMetrics();
        g2d.drawString(msg, centerX - fm.stringWidth(msg) / 2, centerY + 40);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2d.setColor(TEXT_MUTED);
        String hint = "Enter a tag name in the search field and click 'Visualize Network'";
        fm = g2d.getFontMetrics();
        g2d.drawString(hint, centerX - fm.stringWidth(hint) / 2, centerY + 65);
    }

    public void clear() {
        centerTag = null;
        connections = null;
        nodePositions.clear();
        repaint();
    }

    public void setShowLabels(boolean show) {
        this.showLabels = show;
        repaint();
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
        // Animation implementation could be added here
    }

    public void exportToImage(File file) throws Exception {
        // Create buffered image
        int width = getWidth();
        int height = getHeight();
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();
        paint(g2d);
        g2d.dispose();

        // Save to file
        javax.imageio.ImageIO.write(image, "PNG", file);
    }
}