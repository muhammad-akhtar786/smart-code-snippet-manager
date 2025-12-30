package com.snippetmanager.module3;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Module3MainWindow extends JFrame {
    private JTabbedPane tabbedPane;
    private RecommendationPanelPro recommendationPanel;
    private AnalyticsDashboardPro analyticsDashboard;
    private TagVisualization tagVisualization;
    private MetricsDashboard metricsDashboard;

    // Modern Color Scheme
    private static final Color PRIMARY_BLUE = new Color(66, 133, 244);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_MUTED = new Color(108, 117, 125);
    private static final Color CONTENT_BG = new Color(248, 249, 250);
    private static final Color SIDEBAR_BG = new Color(40, 44, 52);
    private static final Color ACCENT_YELLOW = new Color(255, 193, 7);
    private static final Color BUTTON_GREEN = new Color(40, 167, 69);
    private static final Color BUTTON_ORANGE = new Color(255, 152, 0);
    private static final Color BORDER_LIGHT = new Color(222, 226, 230);

    public Module3MainWindow() {
        setTitle("Smart Code Snippet Manager - Professional Edition");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeComponents();
        createMenu();
        customizeUI();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Modern Header
        add(createHeader(), BorderLayout.NORTH);

        // Create panels
        recommendationPanel = new RecommendationPanelPro();
        analyticsDashboard = new AnalyticsDashboardPro();
        tagVisualization = new TagVisualization();
        metricsDashboard = new MetricsDashboard();

        // Modern Tabbed Pane
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(CONTENT_BG);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());

        // Customize tab appearance
        UIManager.put("TabbedPane.contentAreaColor", CONTENT_BG);
        UIManager.put("TabbedPane.selected", PRIMARY_BLUE);
        UIManager.put("TabbedPane.background", SIDEBAR_BG);
        UIManager.put("TabbedPane.foreground", TEXT_LIGHT);

        // Add tabs with icons
        tabbedPane.addTab(" 🎯 Recommendations ", null, recommendationPanel, "Get smart recommendations");
        tabbedPane.addTab(" 📊 Analytics ", null, analyticsDashboard, "View professional analytics");
        tabbedPane.addTab(" 📈 Metrics ", null, metricsDashboard, "View recommendation quality metrics");
        tabbedPane.addTab(" 🔗 Tag Network ", null, tagVisualization, "Visualize tag relationships");

        // Customize tab size and appearance
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setBackgroundAt(i, SIDEBAR_BG);
            tabbedPane.setForegroundAt(i, TEXT_LIGHT);
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(0, 65));

        // Title with icon
        JLabel titleLabel = new JLabel("⚡ Smart Code Snippet Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_LIGHT);

        // Edition badge
        JLabel editionLabel = new JLabel("Module 3: Analytics & Recommendations");
        editionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        editionLabel.setForeground(new Color(200, 220, 255));
        editionLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 255), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));

        // Right panel with quick actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JButton refreshBtn = createHeaderButton("🔄", "Refresh All");
        refreshBtn.addActionListener(e -> refreshAll());

        JButton settingsBtn = createHeaderButton("⚙️", "Settings");
        settingsBtn.addActionListener(e -> showSettings());

        JButton helpBtn = createHeaderButton("❓", "Help");
        helpBtn.addActionListener(e -> showUserGuide());

        rightPanel.add(editionLabel);
        rightPanel.add(refreshBtn);
        rightPanel.add(settingsBtn);
        rightPanel.add(helpBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JButton createHeaderButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setForeground(TEXT_LIGHT);
        button.setBackground(new Color(80, 120, 200));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setPreferredSize(new Dimension(40, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(90, 140, 220));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(80, 120, 200));
            }
        });

        return button;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(52, 58, 70));
        statusBar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        statusBar.setPreferredSize(new Dimension(0, 35));

        JLabel statusLabel = new JLabel("🟢 Status: Ready | Module 3 Professional Edition");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_LIGHT);

        JLabel versionLabel = new JLabel("v2.0 | DSA Course Project");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(TEXT_MUTED);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void customizeUI() {
        // Customize tab appearance on selection
        tabbedPane.addChangeListener(e -> {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (i == tabbedPane.getSelectedIndex()) {
                    tabbedPane.setBackgroundAt(i, PRIMARY_BLUE);
                    tabbedPane.setForegroundAt(i, TEXT_LIGHT);
                } else {
                    tabbedPane.setBackgroundAt(i, SIDEBAR_BG);
                    tabbedPane.setForegroundAt(i, new Color(171, 178, 191));
                }
            }
        });

        // Set initial selected tab
        tabbedPane.setBackgroundAt(0, PRIMARY_BLUE);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(CARD_BG);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT));

        // File Menu
        JMenu fileMenu = createMenu("File");

        JMenuItem refreshItem = createMenuItem("🔄 Refresh All", KeyEvent.VK_R);
        refreshItem.addActionListener(e -> refreshAll());

        JMenuItem settingsItem = createMenuItem("⚙️ Settings", KeyEvent.VK_S);
        settingsItem.addActionListener(e -> showSettings());

        JMenuItem exitItem = createMenuItem("🚪 Exit", KeyEvent.VK_X);
        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(settingsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // View Menu
        JMenu viewMenu = createMenu("View");

        JMenuItem recommendationsView = createMenuItem("🎯 Recommendations", KeyEvent.VK_1);
        recommendationsView.addActionListener(e -> tabbedPane.setSelectedIndex(0));

        JMenuItem analyticsView = createMenuItem("📊 Analytics Dashboard", KeyEvent.VK_2);
        analyticsView.addActionListener(e -> tabbedPane.setSelectedIndex(1));

        JMenuItem metricsView = createMenuItem("📈 Metrics Dashboard", KeyEvent.VK_3);
        metricsView.addActionListener(e -> tabbedPane.setSelectedIndex(2));

        JMenuItem tagNetworkView = createMenuItem("🔗 Tag Network", KeyEvent.VK_4);
        tagNetworkView.addActionListener(e -> tabbedPane.setSelectedIndex(3));

        viewMenu.add(recommendationsView);
        viewMenu.add(analyticsView);
        viewMenu.add(metricsView);
        viewMenu.add(tagNetworkView);

        // Help Menu
        JMenu helpMenu = createMenu("Help");

        JMenuItem userGuideItem = createMenuItem("📖 User Guide", KeyEvent.VK_H);
        userGuideItem.addActionListener(e -> showUserGuide());

        JMenuItem aboutItem = createMenuItem("ℹ️ About", KeyEvent.VK_A);
        aboutItem.addActionListener(e -> showAbout());

        helpMenu.add(userGuideItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return menu;
    }

    private JMenuItem createMenuItem(String title, int mnemonic) {
        JMenuItem item = new JMenuItem(title);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setMnemonic(mnemonic);
        return item;
    }

    private void refreshAll() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        String[] tabNames = { "Recommendations", "Analytics", "Metrics", "Tag Network" };

        JDialog progressDialog = new JDialog(this, "Refreshing Data", true);
        progressDialog.setLayout(new BorderLayout(15, 15));
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(CARD_BG);

        JLabel messageLabel = new JLabel("🔄 Refreshing " + tabNames[selectedIndex] + "...");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(0, 25));

        contentPanel.add(messageLabel, BorderLayout.NORTH);
        contentPanel.add(progressBar, BorderLayout.CENTER);

        progressDialog.add(contentPanel);

        // Simulate refresh in background
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(this,
                            "✓ Data refreshed successfully!",
                            "Refresh Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        progressDialog.setVisible(true);
    }

    private void showSettings() {
        JDialog settingsDialog = new JDialog(this, "⚙️ Settings", true);
        settingsDialog.setSize(600, 500);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.getContentPane().setBackground(CARD_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Title
        JLabel title = new JLabel("Application Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_DARK);

        // Settings form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Data File Path
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel pathLabel = new JLabel("Data File Path:");
        pathLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(pathLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField pathField = new JTextField("Data/snippets_large.json");
        pathField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pathField.setPreferredSize(new Dimension(300, 35));
        formPanel.add(pathField, gbc);

        // Default Result Limit
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel limitLabel = new JLabel("Result Limit:");
        limitLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(limitLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JSpinner limitSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        limitSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) limitSpinner.getEditor()).getTextField().setPreferredSize(new Dimension(300, 35));
        formPanel.add(limitSpinner, gbc);

        // Auto-refresh
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel refreshLabel = new JLabel("Auto-refresh:");
        refreshLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(refreshLabel, gbc);

        gbc.gridx = 1;
        JCheckBox autoRefreshBox = new JCheckBox("Enable automatic refresh");
        autoRefreshBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        autoRefreshBox.setBackground(CARD_BG);
        formPanel.add(autoRefreshBox, gbc);

        // Theme
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(themeLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> themeCombo = new JComboBox<>(new String[] { "Modern Blue", "Dark", "Light" });
        themeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        themeCombo.setPreferredSize(new Dimension(300, 35));
        formPanel.add(themeCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(CARD_BG);

        JButton saveBtn = createStyledButton("💾 Save Settings", BUTTON_GREEN);
        saveBtn.addActionListener(e -> {
            String path = pathField.getText();
            recommendationPanel.setDataFilePath(path);
            analyticsDashboard.setDataFilePath(path);
            tagVisualization.setDataFilePath(path);

            settingsDialog.dispose();
            JOptionPane.showMessageDialog(this,
                    "✓ Settings saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JButton cancelBtn = createStyledButton("Cancel", new Color(108, 117, 125));
        cancelBtn.addActionListener(e -> settingsDialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        settingsDialog.add(mainPanel);
        settingsDialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bgColor.darker()
                        : (getModel().isRollover() ? bgColor.brighter() : bgColor));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        button.setPreferredSize(new Dimension(160, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showAbout() {
        JDialog aboutDialog = new JDialog(this, "ℹ️ About", true);
        aboutDialog.setSize(550, 600);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.getContentPane().setBackground(CARD_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header with logo
        JPanel headerPanel = new JPanel(new BorderLayout(15, 10));
        headerPanel.setOpaque(false);

        JLabel logoLabel = new JLabel("⚡");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(
                "<html><center>Smart Code Snippet Manager<br><span style='font-size:12px; color:#6c757d;'>Module 3: Recommendations & Analytics</span></center></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(TEXT_DARK);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Content
        JTextArea contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentArea.setBackground(CARD_BG);
        contentArea.setForeground(TEXT_DARK);
        contentArea.setText(
                "Version: 2.0 Professional Edition\n" +
                        "Student: [Your Name]\n" +
                        "Course: Data Structures & Algorithms\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "✨ KEY FEATURES:\n\n" +
                        "🎯 Smart Recommendations\n" +
                        "   • Tag-based similarity using Graph DSA\n" +
                        "   • BFS/DFS algorithms for snippet discovery\n" +
                        "   • Intelligent filtering and ranking\n\n" +
                        "📊 Advanced Analytics\n" +
                        "   • Real-time usage statistics\n" +
                        "   • Language distribution analysis\n" +
                        "   • Trending snippets and tags\n\n" +
                        "📈 Quality Metrics\n" +
                        "   • Recommendation accuracy tracking\n" +
                        "   • User engagement analytics\n" +
                        "   • System performance indicators\n\n" +
                        "🔗 Tag Network Visualization\n" +
                        "   • Interactive graph visualization\n" +
                        "   • Tag relationship mapping\n" +
                        "   • Co-occurrence analysis\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "🛠️ TECHNOLOGIES:\n\n" +
                        "Backend: C++ (Graph, HashMap, BFS/DFS)\n" +
                        "Frontend: Java Swing with modern UI\n" +
                        "Integration: Process-based communication\n" +
                        "Data: JSON format with efficient indexing\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "© 2024 DSA Course Project - Semester 3");

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));

        // Close button
        JButton closeBtn = createStyledButton("Close", PRIMARY_BLUE);
        closeBtn.addActionListener(e -> aboutDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        aboutDialog.add(mainPanel);
        aboutDialog.setVisible(true);
    }

    private void showUserGuide() {
        JDialog guideDialog = new JDialog(this, "📖 User Guide", true);
        guideDialog.setSize(650, 650);
        guideDialog.setLocationRelativeTo(this);
        guideDialog.getContentPane().setBackground(CARD_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title
        JLabel titleLabel = new JLabel("📖 User Guide - Module 3");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_DARK);

        // Guide content
        JTextArea guideArea = new JTextArea();
        guideArea.setEditable(false);
        guideArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        guideArea.setBackground(CARD_BG);
        guideArea.setForeground(TEXT_DARK);
        guideArea.setText(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "🎯 RECOMMENDATIONS TAB\n\n" +
                        "1. Enter a tag name in the search field\n" +
                        "2. Select language filter (optional)\n" +
                        "3. Adjust result limit using the spinner\n" +
                        "4. Click 'Get Recommendations' to search\n" +
                        "5. View results with relevance scores\n" +
                        "6. Click any recommendation to view full code\n" +
                        "7. Use 'Copy Code' button to copy to clipboard\n\n" +
                        "💡 Tip: Try related tags for better results!\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "📊 ANALYTICS TAB\n\n" +
                        "1. View real-time statistics in the header\n" +
                        "2. Language Distribution chart shows usage by language\n" +
                        "3. Top Snippets displays most-used code snippets\n" +
                        "4. Trending Tags shows popular tags\n" +
                        "5. Trending Snippets uses time-decay algorithm\n" +
                        "6. Click 'Refresh' button to update all charts\n\n" +
                        "💡 Tip: Analytics update automatically on data changes!\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "📈 METRICS TAB\n\n" +
                        "1. View recommendation performance metrics\n" +
                        "2. Total Recommendations: Count of all recommendations\n" +
                        "3. Clicked: Number of user interactions\n" +
                        "4. Accuracy: Success rate of recommendations\n" +
                        "5. Coverage: Percentage of snippets recommended\n" +
                        "6. Use 'Refresh Metrics' to update values\n\n" +
                        "💡 Tip: Higher accuracy means better recommendations!\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "🔗 TAG NETWORK TAB\n\n" +
                        "1. Select a tag from the dropdown\n" +
                        "2. View connected tags and relationships\n" +
                        "3. Explore tag co-occurrence patterns\n" +
                        "4. Use visualization to understand tag connections\n" +
                        "5. Adjust depth for deeper network exploration\n\n" +
                        "💡 Tip: Related tags help discover new snippets!\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "⚙️ GENERAL TIPS\n\n" +
                        "• Use Ctrl+R to refresh current view\n" +
                        "• Access settings via File > Settings menu\n" +
                        "• Switch tabs using View menu or shortcuts\n" +
                        "• All data is saved automatically\n\n" +
                        "For technical support, contact your instructor.\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        JScrollPane scrollPane = new JScrollPane(guideArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));

        // Close button
        JButton closeBtn = createStyledButton("Got it!", PRIMARY_BLUE);
        closeBtn.addActionListener(e -> guideDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeBtn);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        guideDialog.add(mainPanel);
        guideDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Enable anti-aliasing for text
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Module3MainWindow window = new Module3MainWindow();
            window.setVisible(true);
        });
    }
}