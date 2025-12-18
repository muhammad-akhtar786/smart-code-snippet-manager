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
    private String dataFilePath = "Data/sample_snippets.csv";

    // Helper method to get the executable path
    private String getExecutablePath() {
        String userDir = System.getProperty("user.dir");
        String projectRoot = userDir.contains("java") ? userDir.substring(0, userDir.indexOf("java")) : userDir;
        return new File(projectRoot, "cpp/module3/module3.exe").getAbsolutePath();
    }

    public TagVisualization() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel headerLabel = new JLabel("Tag Network Visualization");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        // Control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.WEST);

        // Visualization canvas
        canvas = new VisualizationCanvas();
        add(canvas, BorderLayout.CENTER);

        // Info panel
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.EAST);

        // Status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Controls"));
        panel.setPreferredSize(new Dimension(250, 0));

        // Tag input
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("Select Tag:"), BorderLayout.NORTH);

        tagInputField = new JTextField();
        tagInputField.addActionListener(e -> visualizeTagNetwork());
        inputPanel.add(tagInputField, BorderLayout.CENTER);

        visualizeBtn = new JButton("Visualize Network");
        visualizeBtn.addActionListener(e -> visualizeTagNetwork());
        inputPanel.add(visualizeBtn, BorderLayout.SOUTH);

        // Options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));

        JCheckBox showLabelsCheck = new JCheckBox("Show Labels", true);
        showLabelsCheck.addActionListener(e -> canvas.setShowLabels(showLabelsCheck.isSelected()));

        JCheckBox animateCheck = new JCheckBox("Animate", false);
        animateCheck.addActionListener(e -> canvas.setAnimate(animateCheck.isSelected()));

        optionsPanel.add(showLabelsCheck);
        optionsPanel.add(Box.createVerticalStrut(5));
        optionsPanel.add(animateCheck);

        // Actions
        JPanel actionsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> canvas.clear());

        JButton exportBtn = new JButton("Export Image");
        exportBtn.addActionListener(e -> exportVisualization());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> visualizeTagNetwork());

        actionsPanel.add(clearBtn);
        actionsPanel.add(exportBtn);
        actionsPanel.add(refreshBtn);

        panel.add(Box.createVerticalStrut(10));
        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(optionsPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(actionsPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Tag Information"));
        panel.setPreferredSize(new Dimension(250, 0));

        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(infoArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.BLUE);
        panel.add(statusLabel);
        return panel;
    }

    private void visualizeTagNetwork() {
        String tag = tagInputField.getText().trim();

        if (tag.isEmpty()) {
            showError("Please enter a tag");
            return;
        }

        setStatus("Loading tag network for: " + tag + "...");

        new Thread(() -> {
            try {
                // Get tag co-occurrence data
                List<String[]> cooccurrence = getTagCooccurrence(tag);

                // Get tag info
                Map<String, Integer> tagInfo = getTrendingTags(20);

                SwingUtilities.invokeLater(() -> {
                    canvas.visualizeNetwork(tag, cooccurrence, tagInfo);
                    displayTagInfo(tag, cooccurrence);
                    setStatus("Visualized network for: " + tag);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> showError("Failed to visualize: " + e.getMessage()));
            }
        }).start();
    }

    private List<String[]> getTagCooccurrence(String tag) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "tag_cooccur", tag, dataFilePath);
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
        ProcessBuilder pb = new ProcessBuilder(getExecutablePath(), "trending_tags", dataFilePath,
                String.valueOf(n));
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
        sb.append("Tag: ").append(tag).append("\n\n");
        sb.append("Connected Tags:\n");
        sb.append("=".repeat(30)).append("\n\n");

        if (cooccurrence.isEmpty()) {
            sb.append("No connections found.\n");
        } else {
            for (int i = 0; i < Math.min(10, cooccurrence.size()); i++) {
                String[] data = cooccurrence.get(i);
                sb.append(String.format("%d. %s (freq: %s)\n", i + 1, data[0], data[1]));
            }
        }

        infoArea.setText(sb.toString());
    }

    private void exportVisualization() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Visualization");
        fileChooser.setSelectedFile(new File("tag_network.png"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                canvas.exportToImage(fileChooser.getSelectedFile());
                setStatus("Exported to: " + fileChooser.getSelectedFile().getName());
            } catch (Exception e) {
                showError("Export failed: " + e.getMessage());
            }
        }
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.BLUE);
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setForeground(Color.RED);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setDataFilePath(String path) {
        this.dataFilePath = path;
    }
}

class VisualizationCanvas extends JPanel {
    private String centerTag;
    private List<String[]> connections;
    private Map<String, Integer> tagFrequencies;
    private boolean showLabels = true;
    private boolean animate = false;
    private Map<String, Point> nodePositions = new HashMap<>();

    public VisualizationCanvas() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
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

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        // Center node
        nodePositions.put(centerTag, new Point(centerX, centerY));

        // Arrange connected nodes in a circle
        int radius = Math.min(width, height) / 3;
        int numNodes = Math.min(connections.size(), 15); // Limit to 15 for clarity

        for (int i = 0; i < numNodes; i++) {
            String tag = connections.get(i)[0];
            double angle = 2 * Math.PI * i / numNodes;
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            nodePositions.put(tag, new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (centerTag == null || connections == null) {
            String msg = "Select a tag to visualize its network";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.setColor(Color.GRAY);
            g2d.drawString(msg, x, y);
            return;
        }

        // Draw edges
        Point center = nodePositions.get(centerTag);
        if (center != null) {
            g2d.setColor(new Color(200, 200, 200));
            for (String[] conn : connections) {
                String tag = conn[0];
                Point p = nodePositions.get(tag);
                if (p != null) {
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
            int nodeSize = Math.min(30 + freq * 2, 60);

            // Color based on whether it's center or connected
            if (tag.equals(centerTag)) {
                g2d.setColor(new Color(66, 133, 244)); // Blue
            } else {
                g2d.setColor(new Color(52, 168, 83)); // Green
            }

            g2d.fillOval(p.x - nodeSize / 2, p.y - nodeSize / 2, nodeSize, nodeSize);

            // Draw border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(p.x - nodeSize / 2, p.y - nodeSize / 2, nodeSize, nodeSize);

            // Draw label
            if (showLabels) {
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(tag);
                g2d.drawString(tag, p.x - labelWidth / 2, p.y + nodeSize / 2 + 15);
            }
        }
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
    }

    public void exportToImage(File file) throws Exception {
        // Simple implementation - could be enhanced with actual image export
        JOptionPane.showMessageDialog(this,
                "Image export functionality would save the visualization as PNG.\n" +
                        "This is a placeholder for the actual implementation.",
                "Export",
                JOptionPane.INFORMATION_MESSAGE);
    }
}