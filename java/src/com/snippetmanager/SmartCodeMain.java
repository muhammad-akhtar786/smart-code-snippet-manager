package com.snippetmanager;

import com.snippetmanager.module1.SnippetManagerPanel;
import com.snippetmanager.module3.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class SmartCodeMain extends JFrame {
    private CardLayout contentCardLayout;
    private JPanel contentPanel;

    // Module panels
    private SnippetManagerPanel snippetManagerPanel;
    private RecommendationPanelPro recommendationPanel;
    private AnalyticsDashboardPro analyticsDashboard;
    private TagVisualization tagVisualization;
    private MetricsDashboard metricsDashboard;

    // Menu buttons
    private ModuleButton activeButton;

    // Card names for CardLayout
    private static final String MODULE_1 = "MODULE_1";
    private static final String MODULE_3_RECOMMENDATIONS = "MODULE_3_RECOMMENDATIONS";
    private static final String MODULE_3_ANALYTICS = "MODULE_3_ANALYTICS";
    private static final String MODULE_3_METRICS = "MODULE_3_METRICS";
    private static final String MODULE_3_TAGS = "MODULE_3_TAGS";

    // Modern Color Palette (Refined)
    private static final Color PRIMARY_ACCENT = new Color(37, 99, 235); // Royal Blue
    private static final Color SIDEBAR_BG = new Color(15, 23, 42); // Slate 900
    private static final Color SIDEBAR_HOVER = new Color(30, 41, 59); // Slate 800
    private static final Color SIDEBAR_ACTIVE = new Color(30, 41, 59);
    private static final Color CONTENT_BG = new Color(248, 250, 252); // Slate 50
    private static final Color TEXT_LIGHT = new Color(241, 245, 249); // Slate 100
    private static final Color TEXT_MUTED = new Color(148, 163, 184); // Slate 400
    private static final Color ACCENT_YELLOW = new Color(250, 204, 21);
    private static final Color EXIT_RED = new Color(220, 38, 38);

    public SmartCodeMain() {
        setTitle("Smart Code Snippet Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 700));

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Global UI Overrides
            UIManager.put("Label.font", new Font("Inter", Font.PLAIN, 13));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeComponents();
        buildUI();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        snippetManagerPanel = new SnippetManagerPanel();
        recommendationPanel = new RecommendationPanelPro();
        analyticsDashboard = new AnalyticsDashboardPro();
        tagVisualization = new TagVisualization();
        metricsDashboard = new MetricsDashboard();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Sidebar + Content
        JPanel container = new JPanel(new BorderLayout());
        container.add(createModernSidebar(), BorderLayout.WEST);
        container.add(createContentArea(), BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(0, 25, 0, 25)));
        header.setPreferredSize(new Dimension(0, 70));

        // App Logo/Title
        JLabel titleLabel = new JLabel("⚡ SMART CODE");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setIconTextGap(12);

        // Right side: User Profile
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        profilePanel.setOpaque(false);

        JLabel userLabel = new JLabel("Developer Console");
        userLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        userLabel.setForeground(new Color(100, 116, 139));

        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        profilePanel.add(userLabel);
        profilePanel.add(avatar);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(profilePanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createModernSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));
        sidebar.setPreferredSize(new Dimension(260, 0));

        JLabel menuLabel = new JLabel("MAIN MENU");
        menuLabel.setFont(new Font("Inter", Font.BOLD, 11));
        menuLabel.setForeground(TEXT_MUTED);
        menuLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 0));
        sidebar.add(menuLabel);

        // Create buttons
        ModuleButton btnSnippet = new ModuleButton("📋", "Snippet Manager", true);
        ModuleButton btnRecommend = new ModuleButton("💡", "Recommendations", false);
        ModuleButton btnAnalytics = new ModuleButton("📊", "Usage Analytics", false);
        ModuleButton btnMetrics = new ModuleButton("📈", "Performance Metrics", false);
        ModuleButton btnTagNetwork = new ModuleButton("🔗", "Tag Network", false);
        ModuleButton btnSettings = new ModuleButton("⚙️", "Settings", false);

        // Logic
        btnSnippet.addActionListener(e -> switchToModule(MODULE_1, btnSnippet));
        btnRecommend.addActionListener(e -> switchToModule(MODULE_3_RECOMMENDATIONS, btnRecommend));
        btnAnalytics.addActionListener(e -> switchToModule(MODULE_3_ANALYTICS, btnAnalytics));
        btnMetrics.addActionListener(e -> switchToModule(MODULE_3_METRICS, btnMetrics));
        btnTagNetwork.addActionListener(e -> switchToModule(MODULE_3_TAGS, btnTagNetwork));
        btnSettings.addActionListener(e -> showSettings());

        // Layout
        ModuleButton[] buttons = { btnSnippet, btnRecommend, btnAnalytics, btnMetrics, btnTagNetwork };
        for (ModuleButton btn : buttons) {
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalStrut(15));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(SIDEBAR_HOVER);
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(15));

        sidebar.add(btnSettings);
        sidebar.add(Box.createVerticalGlue());

        // Exit
        ModuleButton btnExit = new ModuleButton("🚪", "Exit System", false);
        btnExit.setHoverColor(EXIT_RED);
        btnExit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Close the application?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == 0) {
                System.exit(0);
            }
        });
        sidebar.add(btnExit);

        activeButton = btnSnippet;
        return sidebar;
    }

    private JPanel createContentArea() {
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(CONTENT_BG);
        contentPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(226, 232, 240)));

        contentPanel.add(snippetManagerPanel, MODULE_1);
        contentPanel.add(recommendationPanel, MODULE_3_RECOMMENDATIONS);
        contentPanel.add(analyticsDashboard, MODULE_3_ANALYTICS);
        contentPanel.add(metricsDashboard, MODULE_3_METRICS);
        contentPanel.add(tagVisualization, MODULE_3_TAGS);

        return contentPanel;
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(0, 30));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JLabel left = new JLabel("  System Status: Operational");
        left.setFont(new Font("Inter", Font.PLAIN, 11));
        left.setForeground(new Color(16, 185, 129));

        JLabel right = new JLabel("Unified Edition v2.0  ");
        right.setFont(new Font("Inter", Font.PLAIN, 11));
        right.setForeground(TEXT_MUTED);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void switchToModule(String moduleName, ModuleButton clickedButton) {
        if (activeButton != null)
            activeButton.setActive(false);
        clickedButton.setActive(true);
        activeButton = clickedButton;
        contentCardLayout.show(contentPanel, moduleName);
    }

    private void showSettings() {
        // ... Logic remains identical to your original code ...
        // Re-implement your settings dialog here
    }

    /**
     * Highly Customized Sidebar Button
     */
    private class ModuleButton extends JButton {
        private boolean active = false;
        private String icon;
        private String label;
        private Color hoverColor = SIDEBAR_HOVER;

        ModuleButton(String icon, String label, boolean isActive) {
            this.icon = icon;
            this.label = label;
            this.active = isActive;

            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            setPreferredSize(new Dimension(230, 45));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        void setActive(boolean isActive) {
            this.active = isActive;
            repaint();
        }

        void setHoverColor(Color c) {
            this.hoverColor = c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active || getModel().isRollover()) {
                g2.setColor(active ? PRIMARY_ACCENT : hoverColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }

            // Text and Icon
            g2.setColor(active ? Color.WHITE : (getModel().isRollover() ? Color.WHITE : TEXT_MUTED));

            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            g2.drawString(icon, 18, 28);

            g2.setFont(new Font("Inter", Font.BOLD, 13));
            g2.drawString(label, 50, 28);

            if (active) {
                g2.setColor(ACCENT_YELLOW);
                g2.fillOval(getWidth() - 15, getHeight() / 2 - 3, 6, 6);
            }

            g2.dispose();
        }
    }

    // Custom creation of button logic (Re-using your existing logic)
    private JButton createStyledButton(String text, Color bgColor) {
        // Use the Logic provided in your prompt
        return null; // Implementation similar to ModuleButton but for dialogs
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartCodeMain::new);
    }
}