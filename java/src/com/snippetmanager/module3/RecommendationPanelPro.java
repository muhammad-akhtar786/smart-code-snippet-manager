package com.snippetmanager.module3;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class RecommendationPanelPro extends JPanel {
    private JTextField tagInputField;
    private JComboBox<String> languageFilterCombo;
    private JSpinner resultLimitSpinner;
    private JButton recommendBtn;
    private JPanel resultsPanel;
    private JLabel statusLabel;

    // Code view panel components
    private JPanel codeViewPanel;
    private JTextArea codeArea;
    private JLabel codeInfoLabel;
    private JButton copyBtn;
    private RecommendationItem currentSelectedItem;

    // Helper method to get the executable path
    private String getExecutablePath() {
        // Start from project root by finding the smart-code-snippet-manager directory
        File currentDir = new File(System.getProperty("user.dir"));
        File projectRoot = currentDir;

        // Walk up directory tree to find smart-code-snippet-manager
        while (projectRoot != null && !projectRoot.getName().equals("smart-code-snippet-manager")) {
            projectRoot = projectRoot.getParentFile();
        }

        // If not found by walking up, try relative path
        if (projectRoot == null || !projectRoot.exists()) {
            projectRoot = new File("e:\\DSA\\SmartCode_Snippet_Manager\\smart-code-snippet-manager");
        }

        File exe = new File(projectRoot, "cpp/module3/module3.exe");
        return exe.getAbsolutePath();
    }

    public RecommendationPanelPro() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content area with split view
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(500);
        mainSplitPane.setResizeWeight(0.5);

        // Left side - Input controls and recommendations
        JPanel leftPanel = new JPanel(new BorderLayout(15, 15));
        leftPanel.setBackground(new Color(245, 245, 245));

        // Input controls with scroll
        JPanel controlPanel = createControlPanel();
        JScrollPane controlScrollPane = new JScrollPane(controlPanel);
        controlScrollPane.setPreferredSize(new Dimension(350, 300));
        controlScrollPane.setBorder(new TitledBorder(new LineBorder(new Color(100, 149, 237), 2),
                "🔍 Search & Filter", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));
        leftPanel.add(controlScrollPane, BorderLayout.NORTH);

        // Results area
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(new TitledBorder(new LineBorder(new Color(70, 130, 180), 2),
                "📋 Recommendations", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        mainSplitPane.setLeftComponent(leftPanel);

        // Right side - Code view panel
        codeViewPanel = createCodeViewPanel();
        mainSplitPane.setRightComponent(codeViewPanel);

        add(mainSplitPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createCodeViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder(new LineBorder(new Color(70, 130, 180), 2),
                "💻 Code View", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(new Color(245, 245, 245));

        // Info section
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 8));
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(70, 130, 180), 1),
                new EmptyBorder(10, 10, 10, 10)));

        codeInfoLabel = new JLabel("Select a recommendation to view code");
        codeInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        codeInfoLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(codeInfoLabel);

        panel.add(infoPanel, BorderLayout.NORTH);

        // Code display area
        codeArea = new JTextArea("// Select a recommendation to view code here");
        codeArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        codeArea.setEditable(false);
        codeArea.setLineWrap(true);
        codeArea.setWrapStyleWord(true);
        codeArea.setBackground(new Color(30, 30, 30));
        codeArea.setForeground(new Color(144, 238, 144)); // Light green for code
        codeArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        codeScrollPane.setBorder(new LineBorder(new Color(100, 100, 100), 1));
        panel.add(codeScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));

        copyBtn = new JButton("📋 Copy Code");
        copyBtn.setEnabled(false);
        copyBtn.addActionListener(e -> {
            if (currentSelectedItem != null) {
                java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(
                        currentSelectedItem.code);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                JOptionPane.showMessageDialog(panel, "Code copied to clipboard!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(copyBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void displayCodeView(RecommendationItem item) {
        currentSelectedItem = item;

        // Update info label
        codeInfoLabel.setText(String.format("Algorithm: %s | Tags: %s | Language: %s | Score: %.2f",
                item.title, item.tags.replace(";", ", "), item.language, item.score));

        // Update code area - properly handle escape sequences
        String formattedCode = item.code.replace("\\n", "\n").replace("\\t", "\t");
        codeArea.setText(formattedCode);
        codeArea.setCaretPosition(0);

        // Enable copy button
        copyBtn.setEnabled(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new LineBorder(new Color(70, 130, 180), 2, true));
        panel.setPreferredSize(new Dimension(0, 70));

        JLabel headerLabel = new JLabel("🎯 Smart Recommendation Engine");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(new Color(25, 25, 112));
        panel.add(headerLabel, BorderLayout.WEST);

        JLabel descLabel = new JLabel("Find related snippets based on tags using graph algorithms");
        descLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        descLabel.setForeground(new Color(100, 100, 100));
        panel.add(descLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tag input
        JPanel tagPanel = createInputSection("🏷️ Search by Tag",
                "Enter a tag (e.g., 'algorithm', 'sorting')",
                new Color(70, 130, 180));
        tagInputField = new JTextField(20);
        tagInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tagInputField.setFont(new Font("Arial", Font.PLAIN, 13));
        tagInputField.setBorder(new LineBorder(new Color(100, 149, 237), 2));
        tagPanel.add(tagInputField);
        panel.add(tagPanel);
        panel.add(Box.createVerticalStrut(15));

        // Language filter
        JPanel langPanel = createInputSection("💻 Filter by Language",
                "Select a programming language",
                new Color(34, 139, 34));
        languageFilterCombo = new JComboBox<>(new String[] { "All", "C++", "Java", "Python" });
        languageFilterCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        languageFilterCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        langPanel.add(languageFilterCombo);
        panel.add(langPanel);
        panel.add(Box.createVerticalStrut(15));

        // Result limit
        JPanel limitPanel = createInputSection("📊 Result Limit",
                "Number of recommendations (1-50)",
                new Color(178, 34, 34));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10, 1, 50, 1);
        resultLimitSpinner = new JSpinner(spinnerModel);
        resultLimitSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        resultLimitSpinner.setFont(new Font("Arial", Font.PLAIN, 13));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(resultLimitSpinner, "#");
        resultLimitSpinner.setEditor(editor);
        limitPanel.add(resultLimitSpinner);
        panel.add(limitPanel);
        panel.add(Box.createVerticalStrut(20));

        // Get recommendations button
        recommendBtn = new JButton("🔍 GET RECOMMENDATIONS");
        recommendBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        recommendBtn.setFont(new Font("Arial", Font.BOLD, 14));
        recommendBtn.setBackground(new Color(220, 20, 60)); // Crimson red - highly visible
        recommendBtn.setForeground(Color.WHITE);
        recommendBtn.setOpaque(true);
        recommendBtn.setBorder(new RoundBorder(10));
        recommendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        recommendBtn.addActionListener(e -> getRecommendations());
        panel.add(recommendBtn);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createInputSection(String title, String hint, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);

        JLabel hintLabel = new JLabel(hint);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        hintLabel.setForeground(new Color(128, 128, 128));

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.add(titleLabel, BorderLayout.WEST);
        labelPanel.add(hintLabel, BorderLayout.EAST);

        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(8), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EtchedBorder());
        panel.setBackground(new Color(240, 248, 255));

        statusLabel = new JLabel("Ready to search...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 100, 100));

        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }

    private void getRecommendations() {
        String tag = tagInputField.getText().trim().toLowerCase();
        if (tag.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a tag!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int limit = (Integer) resultLimitSpinner.getValue();
        String language = (String) languageFilterCombo.getSelectedItem();

        recommendBtn.setEnabled(false);
        statusLabel.setText("🔄 Searching for recommendations (exact & related tags)...");

        new Thread(() -> {
            try {
                // First try exact match
                List<RecommendationItem> recommendations = searchTag(tag, limit, language);

                // If no results, try fuzzy/related search
                if (recommendations.isEmpty()) {
                    statusLabel.setText("🔍 No exact match found. Searching for related tags...");
                    // Try to find similar tags by searching for partial matches
                    recommendations = fuzzySearchTags(tag, limit, language);
                }

                final List<RecommendationItem> finalRecommendations = recommendations;
                SwingUtilities.invokeLater(() -> displayRecommendations(finalRecommendations, tag, language));

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("✗ Error: " + e.getMessage());
                    recommendBtn.setEnabled(true);
                });
            }
        }).start();
    }

    private List<RecommendationItem> searchTag(String tag, int limit, String language) throws Exception {
        List<RecommendationItem> recommendations = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder(getExecutablePath(),
                "rec_tag", tag, String.valueOf(limit));
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            boolean capturing = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("RECOMMENDATIONS_START")) {
                    capturing = true;
                } else if (line.equals("RECOMMENDATIONS_END")) {
                    capturing = false;
                } else if (capturing) {
                    String[] parts = line.split("\\|", 6); // Split into max 6 parts to preserve code with pipes
                    if (parts.length >= 5) {
                        RecommendationItem item = new RecommendationItem(
                                parts[0], // id
                                parts[1], // title
                                Double.parseDouble(parts[2]), // score
                                parts[3], // tags
                                parts[4], // language
                                parts.length > 5 ? parts[5] : "" // code
                        );

                        // Apply language filter
                        if ("All".equals(language) || language.equals(item.language)) {
                            recommendations.add(item);
                        }
                    }
                }
            }
        }
        process.waitFor();
        return recommendations;
    }

    private List<RecommendationItem> fuzzySearchTags(String searchTag, int limit, String language) throws Exception {
        List<RecommendationItem> recommendations = new ArrayList<>();

        // Get all top snippets and search through their tags
        ProcessBuilder pb = new ProcessBuilder(getExecutablePath(),
                "top_snippets", String.valueOf(limit * 3));
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            boolean capturing = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("TOP_SNIPPETS_START")) {
                    capturing = true;
                } else if (line.equals("TOP_SNIPPETS_END")) {
                    capturing = false;
                } else if (capturing) {
                    String[] parts = line.split("\\|", 6);
                    if (parts.length >= 4) {
                        String tags = parts[3].toLowerCase();
                        // Check if any tag contains or is similar to search term
                        if (tags.contains(searchTag) ||
                                searchTag.contains(tags.split(";")[0]) ||
                                levenshteinDistance(searchTag, tags.split(";")[0]) <= 2) {
                            RecommendationItem item = new RecommendationItem(
                                    parts[0], // id
                                    parts[1], // title
                                    Double.parseDouble(parts[2]), // score
                                    parts[3], // tags
                                    parts.length > 4 ? parts[4] : "Unknown", // language
                                    parts.length > 5 ? parts[5] : "" // code
                            );
                            if ("All".equals(language) || language.equals(item.language)) {
                                recommendations.add(item);
                                if (recommendations.size() >= limit)
                                    break;
                            }
                        }
                    }
                }
            }
        }
        process.waitFor();
        return recommendations;
    }

    // Calculate Levenshtein distance for fuzzy matching
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    private void displayRecommendations(List<RecommendationItem> items, String tag, String language) {
        resultsPanel.removeAll();

        if (items.isEmpty()) {
            JLabel noResults = new JLabel("No recommendations found for tag: " + tag);
            noResults.setForeground(Color.RED);
            noResults.setFont(new Font("Arial", Font.ITALIC, 12));
            resultsPanel.add(noResults);
        } else {
            int rank = 1;
            for (RecommendationItem item : items) {
                resultsPanel.add(createRecommendationCard(item, rank++));
                resultsPanel.add(Box.createVerticalStrut(10));
            }
            // Automatically display the first recommendation
            if (!items.isEmpty()) {
                displayCodeView(items.get(0));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();

        statusLabel.setText(String.format("✓ Found %d recommendation(s) for '%s'", items.size(), tag));
        recommendBtn.setEnabled(true);
    }

    private JPanel createRecommendationCard(RecommendationItem item, int rank) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(12, 12, 12, 12)));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add click listener to show code
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                displayCodeView(item);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(240, 248, 255));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        // Rank and title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel rankLabel = new JLabel(String.format("#%d", rank));
        rankLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rankLabel.setForeground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel(item.title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel scoreLabel = new JLabel(String.format("Score: %.2f", item.score));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 11));
        scoreLabel.setForeground(new Color(34, 139, 34));

        titlePanel.add(rankLabel, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(scoreLabel, BorderLayout.EAST);

        // Details
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setOpaque(false);

        JLabel idLabel = new JLabel("ID: " + item.id);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        idLabel.setForeground(new Color(100, 100, 100));

        JLabel tagsLabel = new JLabel("Tags: " + item.tags.replace(";", ", "));
        tagsLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        tagsLabel.setForeground(new Color(100, 100, 100));

        JLabel langLabel = new JLabel(String.format("[%s]", item.language));
        langLabel.setFont(new Font("Arial", Font.BOLD, 10));
        langLabel.setForeground(new Color(178, 34, 34));
        langLabel.setBackground(new Color(255, 240, 245));
        langLabel.setOpaque(true);
        langLabel.setBorder(new EmptyBorder(2, 5, 2, 5));

        JPanel langPanel = new JPanel(new BorderLayout());
        langPanel.setOpaque(false);
        langPanel.add(langLabel, BorderLayout.EAST);

        detailsPanel.add(idLabel, BorderLayout.WEST);
        detailsPanel.add(tagsLabel, BorderLayout.CENTER);
        detailsPanel.add(langPanel, BorderLayout.EAST);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }

    // Helper class for recommendation items
    private static class RecommendationItem {
        String id;
        String title;
        double score;
        String tags;
        String language;
        String code;

        RecommendationItem(String id, String title, double score, String tags, String language, String code) {
            this.id = id;
            this.title = title;
            this.score = score;
            this.tags = tags;
            this.language = language;
            this.code = code;
        }
    }

    // Custom rounded button border
    private static class RoundBorder extends AbstractBorder {
        private int radius;

        public RoundBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 10, 10, 10);
        }
    }

    public void setDataFilePath(String path) {
        // Data file path setting - can be extended for future use
    }
}
