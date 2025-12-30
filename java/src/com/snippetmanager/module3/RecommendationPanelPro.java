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

    // Modern Color Scheme
    private static final Color PRIMARY_BLUE = new Color(66, 133, 244);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_MUTED = new Color(108, 117, 125);
    private static final Color CONTENT_BG = new Color(248, 249, 250);
    private static final Color CONSOLE_BG = new Color(30, 33, 36);
    private static final Color CONSOLE_TEXT = new Color(171, 178, 191);
    private static final Color BUTTON_GREEN = new Color(40, 167, 69);
    private static final Color BUTTON_ORANGE = new Color(255, 152, 0);
    private static final Color ACCENT_BLUE = new Color(52, 152, 219);
    private static final Color BORDER_LIGHT = new Color(222, 226, 230);
    private static final Color HOVER_BG = new Color(232, 240, 254);

    // Helper method to get the executable path
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

    public RecommendationPanelPro() {
        setLayout(new BorderLayout(0, 0));
        setBackground(CONTENT_BG);
        setPreferredSize(new Dimension(1400, 900));

        // Main content with split view
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(750);
        mainSplitPane.setResizeWeight(0.60);
        mainSplitPane.setBorder(BorderFactory.createEmptyBorder());
        mainSplitPane.setBackground(CONTENT_BG);

        // Left side - Input controls and recommendations
        JPanel leftPanel = createLeftPanel();
        mainSplitPane.setLeftComponent(leftPanel);

        // Right side - Code view panel
        codeViewPanel = createCodeViewPanel();
        mainSplitPane.setRightComponent(codeViewPanel);

        add(mainSplitPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createLeftPanel() {
    JPanel panel = new JPanel(new BorderLayout(20, 20));  // Increased spacing between components in the left panel
    panel.setBackground(CONTENT_BG);
    panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); // Increased outer padding to make the left panel bigger

    // Control Panel Card
    JPanel controlCard = createControlPanel();
    panel.add(controlCard, BorderLayout.NORTH);

    // Results area
    resultsPanel = new JPanel();
    resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
    resultsPanel.setBackground(CONTENT_BG);

    // Wrap resultsPanel in a container that forces it to start from the top
    JPanel scrollContainer = new JPanel(new BorderLayout());
    scrollContainer.setBackground(CONTENT_BG);
    scrollContainer.add(resultsPanel, BorderLayout.NORTH);

    JScrollPane scrollPane = new JScrollPane(scrollContainer);
    scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                    "📋 Recommendations",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 13),  // Reduced font size for the title
                    TEXT_DARK),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));  // Reduced padding around the scroll pane

    // Ensure the scrollbar is always available and smooth
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.setBackground(CONTENT_BG);
    scrollPane.getViewport().setBackground(CONTENT_BG);
    scrollPane.setPreferredSize(new Dimension(0, 1200));  // Reduced the height to make the box smaller
    scrollPane.setMinimumSize(new Dimension(0, 1100));  // Reduced minimum height

    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}


    private JPanel createControlPanel() {
    JPanel card = new JPanel(new BorderLayout(5, 5)); // Reduced border gap
    card.setBackground(CARD_BG);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Reduced padding

    // Header
    JLabel header = new JLabel("🎯 Smart Recommendation Engine");
    header.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Reduced font size
    header.setForeground(TEXT_DARK);

    JLabel subtitle = new JLabel("Find related snippets based on tags using graph algorithms");
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Reduced font size
    subtitle.setForeground(TEXT_MUTED);
    subtitle.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0)); // Reduced bottom margin

    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    headerPanel.add(header, BorderLayout.NORTH);
    headerPanel.add(subtitle, BorderLayout.SOUTH);

    // Form Panel
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(CARD_BG);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 3, 6, 3); // Reduced insets
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    // Tag Input
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0;
    JLabel tagLabel = new JLabel("🏷️ Search by Tag");
    tagLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Reduced font size
    formPanel.add(tagLabel, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    tagInputField = new JTextField();
    tagInputField.setPreferredSize(new Dimension(0, 25)); // Reduced height
    tagInputField.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Reduced font size
    tagInputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    formPanel.add(tagInputField, gbc);

    // Language Filter
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0;
    JLabel langLabel = new JLabel("💻 Language");
    langLabel.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Reduced font size
    formPanel.add(langLabel, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    languageFilterCombo = new JComboBox<>(new String[]{"All", "C++", "Java", "Python", "JavaScript", "Go", "Rust"});
    languageFilterCombo.setPreferredSize(new Dimension(0, 25)); // Reduced height
    languageFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Reduced font size
    languageFilterCombo.setBackground(CARD_BG);
    formPanel.add(languageFilterCombo, gbc);

    // Result Limit
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0;
    JLabel limitLabel = new JLabel("📊 Result Limit");
    limitLabel.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Reduced font size
    formPanel.add(limitLabel, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10, 1, 50, 1);
    resultLimitSpinner = new JSpinner(spinnerModel);
    resultLimitSpinner.setPreferredSize(new Dimension(0, 25)); // Reduced height
    resultLimitSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Reduced font size
    JSpinner.NumberEditor editor = new JSpinner.NumberEditor(resultLimitSpinner, "#");
    resultLimitSpinner.setEditor(editor);
    formPanel.add(resultLimitSpinner, gbc);

    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Reduced gap between buttons
    buttonPanel.setBackground(CARD_BG);

    recommendBtn = new RoundedButton("🔍 Get Recommendations", PRIMARY_BLUE, TEXT_LIGHT, 20); // Reduced font size
    recommendBtn.setPreferredSize(new Dimension(160, 30)); // Reduced button size
    recommendBtn.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Reduced font size
    recommendBtn.addActionListener(e -> getRecommendations());

    JButton clearBtn = new RoundedButton("Clear", new Color(108, 117, 125), TEXT_LIGHT, 18); // Reduced font size
    clearBtn.setPreferredSize(new Dimension(90, 40)); // Reduced button size
    clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Reduced font size
    clearBtn.addActionListener(e -> {
        tagInputField.setText("");
        resultsPanel.removeAll();
        resultsPanel.revalidate();
        resultsPanel.repaint();
        codeArea.setText("// Select a recommendation to view code here");
        codeInfoLabel.setText("Select a recommendation to view code");
        copyBtn.setEnabled(false);
    });

    buttonPanel.add(recommendBtn);
    buttonPanel.add(clearBtn);

    card.add(headerPanel, BorderLayout.NORTH);
    card.add(formPanel, BorderLayout.CENTER);
    card.add(buttonPanel, BorderLayout.SOUTH);

    return card;
}

    private JPanel createCodeViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        // Card container
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Header
        JLabel header = new JLabel("💻 Code View");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(TEXT_DARK);

        // Info section
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_BLUE, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        codeInfoLabel = new JLabel("Select a recommendation to view code");
        codeInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        codeInfoLabel.setForeground(TEXT_MUTED);
        infoPanel.add(codeInfoLabel, BorderLayout.CENTER);

        // Code display area
        codeArea = new JTextArea("// Select a recommendation to view code here");
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        codeArea.setEditable(false);
        codeArea.setLineWrap(true);
        codeArea.setWrapStyleWord(true);
        codeArea.setBackground(CONSOLE_BG);
        codeArea.setForeground(CONSOLE_TEXT);
        codeArea.setCaretColor(CONSOLE_TEXT);
        codeArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        codeScrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 53, 60), 1));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);

        copyBtn = new RoundedButton("📋 Copy Code", BUTTON_GREEN, TEXT_LIGHT, 20);
        copyBtn.setPreferredSize(new Dimension(140, 38));
        copyBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

        // Assemble card
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.SOUTH);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(codeScrollPane, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    private void displayCodeView(RecommendationItem item) {
        currentSelectedItem = item;

        // Update info label
        codeInfoLabel.setText(String.format(
                "<html><b>Algorithm:</b> %s | <b>Tags:</b> %s | <b>Language:</b> %s | <b>Score:</b> %.2f</html>",
                item.title, item.tags.replace(";", ", "), item.language, item.score));

        // Update code area
        String formattedCode = item.code.replace("\\n", "\n").replace("\\t", "\t");
        codeArea.setText(formattedCode);
        codeArea.setCaretPosition(0);

        // Enable copy button
        copyBtn.setEnabled(true);
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        panel.setBackground(new Color(52, 58, 70));
        panel.setPreferredSize(new Dimension(0, 35));

        statusLabel = new JLabel("🟢 Ready to search...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_LIGHT);

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
                List<RecommendationItem> recommendations = searchTag(tag, limit, language);

                if (recommendations.isEmpty()) {
                    statusLabel.setText("🔍 No exact match found. Searching for related tags...");
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

        pb.directory(getDataDirectory());
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
                    String[] parts = line.split("\\|", 6);
                    if (parts.length >= 5) {
                        RecommendationItem item = new RecommendationItem(
                                parts[0], parts[1], Double.parseDouble(parts[2]),
                                parts[3], parts[4], parts.length > 5 ? parts[5] : "");

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

        ProcessBuilder pb = new ProcessBuilder(getExecutablePath(),
                "top_snippets", String.valueOf(limit * 3));
        pb.directory(getDataDirectory());
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
                        if (tags.contains(searchTag) ||
                                searchTag.contains(tags.split(";")[0]) ||
                                levenshteinDistance(searchTag, tags.split(";")[0]) <= 2) {
                            RecommendationItem item = new RecommendationItem(
                                    parts[0], parts[1], Double.parseDouble(parts[2]),
                                    parts[3], parts.length > 4 ? parts[4] : "Unknown",
                                    parts.length > 5 ? parts[5] : "");
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

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++)
            dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++)
            dp[0][j] = j;
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
            JPanel noResultPanel = new JPanel(new GridBagLayout());
            noResultPanel.setBackground(CONTENT_BG);
            JLabel noResults = new JLabel("<html><center>❌ No recommendations found for tag: <b>" + tag
                    + "</b><br><br>Try different tags or check spelling</center></html>");
            noResults.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            noResults.setForeground(TEXT_MUTED);
            noResultPanel.add(noResults);
            resultsPanel.add(noResultPanel);
        } else {
            int rank = 1;
            for (RecommendationItem item : items) {
                resultsPanel.add(createRecommendationCard(item, rank++));
                resultsPanel.add(Box.createVerticalStrut(10));
            }
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
        JPanel card = new JPanel(new BorderLayout(10, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        card.setBackground(CARD_BG);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setPreferredSize(new Dimension(680, 130));
        card.setMinimumSize(new Dimension(500, 130));

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                displayCodeView(item);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(HOVER_BG);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(CARD_BG);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)));
            }
        });

        // Title section
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setOpaque(false);

        JLabel rankLabel = new JLabel(String.format("#%d", rank));
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rankLabel.setForeground(PRIMARY_BLUE);
        rankLabel.setPreferredSize(new Dimension(35, 20));

        JLabel titleLabel = new JLabel(item.title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_DARK);

        JLabel scoreLabel = new JLabel(String.format("%.2f", item.score));
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        scoreLabel.setForeground(BUTTON_GREEN);
        scoreLabel.setBackground(new Color(220, 248, 228));
        scoreLabel.setOpaque(true);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        titlePanel.add(rankLabel, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(scoreLabel, BorderLayout.EAST);

        // Details section
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 0));
        detailsPanel.setOpaque(false);

        // Create scrollable tags area
        JTextArea tagsArea = new JTextArea("🏷️ " + item.tags.replace(";", "\n🏷️ "));
        tagsArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tagsArea.setForeground(TEXT_MUTED);
        tagsArea.setBackground(CONTENT_BG);
        tagsArea.setEditable(false);
        tagsArea.setLineWrap(true);
        tagsArea.setWrapStyleWord(true);
        tagsArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JScrollPane tagsScrollPane = new JScrollPane(tagsArea);
        tagsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tagsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tagsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JLabel langLabel = new JLabel(item.language);
        langLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        langLabel.setForeground(TEXT_LIGHT);
        langLabel.setBackground(BUTTON_ORANGE);
        langLabel.setOpaque(true);
        langLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        detailsPanel.add(tagsScrollPane, BorderLayout.CENTER);
        detailsPanel.add(langLabel, BorderLayout.EAST);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }

    // Custom rounded button
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

    // Helper class for recommendation items
    private static class RecommendationItem {
        String id, title, tags, language, code;
        double score;

        RecommendationItem(String id, String title, double score, String tags, String language, String code) {
            this.id = id;
            this.title = title;
            this.score = score;
            this.tags = tags;
            this.language = language;
            this.code = code;
        }
    }

    public void setDataFilePath(String path) {
        // Data file path setting - can be extended for future use
    }
}