package com.snippetmanager.module3;

import javax.swing.*;
import java.awt.*;

public class Module3MainWindow extends JFrame {
    private JTabbedPane tabbedPane;
    private RecommendationPanelPro recommendationPanel;
    private AnalyticsDashboardPro analyticsDashboard;
    private TagVisualization tagVisualization;
    private MetricsDashboard metricsDashboard;

    public Module3MainWindow() {
        setTitle("Smart Code Snippet Manager - Module 3: Recommendations & Analytics [Professional Edition]");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        createMenu();
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Create panels
        recommendationPanel = new RecommendationPanelPro();
        analyticsDashboard = new AnalyticsDashboardPro();
        tagVisualization = new TagVisualization();
        metricsDashboard = new MetricsDashboard();

        // Add tabs with icons
        tabbedPane.addTab("🎯 Recommendations", null, recommendationPanel, "Get smart recommendations");
        tabbedPane.addTab("📊 Analytics", null, analyticsDashboard, "View professional analytics");
        tabbedPane.addTab("📈 Metrics", null, metricsDashboard, "View recommendation quality metrics");
        tabbedPane.addTab("🔗 Tag Network", null, tagVisualization, "Visualize tag relationships");

        add(tabbedPane);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.addActionListener(e -> refreshAll());

        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> showSettings());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(settingsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // View Menu
        JMenu viewMenu = new JMenu("View");

        JMenuItem recommendationsView = new JMenuItem("Recommendations");
        recommendationsView.addActionListener(e -> tabbedPane.setSelectedIndex(0));

        JMenuItem analyticsView = new JMenuItem("Analytics Dashboard");
        analyticsView.addActionListener(e -> tabbedPane.setSelectedIndex(1));

        JMenuItem tagNetworkView = new JMenuItem("Tag Network");
        tagNetworkView.addActionListener(e -> tabbedPane.setSelectedIndex(2));

        viewMenu.add(recommendationsView);
        viewMenu.add(analyticsView);
        viewMenu.add(tagNetworkView);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());

        JMenuItem userGuideItem = new JMenuItem("User Guide");
        userGuideItem.addActionListener(e -> showUserGuide());

        helpMenu.add(userGuideItem);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void refreshAll() {
        JOptionPane.showMessageDialog(this,
                "Refreshing all data...",
                "Refresh",
                JOptionPane.INFORMATION_MESSAGE);

        // Trigger refresh on current panel
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == 1) { // Analytics dashboard
            // The dashboard has its own refresh mechanism
        }
    }

    private void showSettings() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setSize(400, 300);
        settingsDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Data File Path:"));
        JTextField pathField = new JTextField("data/snippets.csv");
        panel.add(pathField);

        panel.add(new JLabel("Default Result Limit:"));
        JSpinner limitSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
        panel.add(limitSpinner);

        panel.add(new JLabel("Auto-refresh:"));
        JCheckBox autoRefreshBox = new JCheckBox("Enable");
        panel.add(autoRefreshBox);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            String path = pathField.getText();
            recommendationPanel.setDataFilePath(path);
            analyticsDashboard.setDataFilePath(path);
            tagVisualization.setDataFilePath(path);
            settingsDialog.dispose();
            JOptionPane.showMessageDialog(this, "Settings saved successfully");
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> settingsDialog.dispose());

        panel.add(saveBtn);
        panel.add(cancelBtn);

        settingsDialog.add(panel);
        settingsDialog.setVisible(true);
    }

    private void showAbout() {
        String message = "Smart Code Snippet Manager\n" +
                "Module 3: Recommendations & Analytics\n\n" +
                "Version: 1.0\n" +
                "Student: [Your Name]\n\n" +
                "Features:\n" +
                "- Tag-based recommendations using Graph DSA\n" +
                "- Snippet similarity using BFS/DFS algorithms\n" +
                "- Real-time analytics dashboard\n" +
                "- Tag network visualization\n\n" +
                "Technologies:\n" +
                "- C++ Backend (Graph, HashMap, BFS/DFS)\n" +
                "- Java Swing GUI\n" +
                "- Process-based Integration\n\n" +
                "DSA Course Project - Semester 3";

        JOptionPane.showMessageDialog(this,
                message,
                "About Module 3",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUserGuide() {
        String guide = "USER GUIDE - Module 3\n\n" +
                "RECOMMENDATIONS TAB:\n" +
                "1. Enter a tag name to get related snippet recommendations\n" +
                "2. Enter a snippet ID to find similar snippets\n" +
                "3. Adjust result limit using the spinner\n" +
                "4. Export results to a file\n\n" +
                "ANALYTICS TAB:\n" +
                "1. View top used snippets\n" +
                "2. See trending tags\n" +
                "3. Analyze language distribution\n" +
                "4. Click 'Refresh' to update data\n" +
                "5. Export analytics report\n\n" +
                "TAG NETWORK TAB:\n" +
                "1. Select a tag to visualize its network\n" +
                "2. See connected tags and relationships\n" +
                "3. Explore tag co-occurrence patterns\n\n" +
                "For technical support, contact your instructor.";

        JTextArea textArea = new JTextArea(guide);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "User Guide",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Module3MainWindow window = new Module3MainWindow();
            window.setVisible(true);
        });
    }
}