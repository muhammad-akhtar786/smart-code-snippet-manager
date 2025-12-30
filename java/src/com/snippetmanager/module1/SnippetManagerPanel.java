package com.snippetmanager.module1;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetManagerPanel extends JPanel {

    private JTextArea outputArea;
    private JTextField searchField;
    private JList<String> recentSearchesList;
    private DefaultListModel<String> recentModel;
    private String backendPath;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    private final String SEARCH_PAGE = "SEARCH";
    private final String ADD_PAGE = "ADD";
    private final String UPDATE_PAGE = "UPDATE";
    private final String DELETE_PAGE = "DELETE";

    private RoundedTextField addTitleField, updateTitleField, deleteTitleField, addTagsField, updateTagsField;
    private JTextArea addCodeArea, updateCodeArea, deleteCodeArea;
    private JComboBox<String> languageDropdown, updateLanguageDropdown;

    // --- Modern Color Palette (Consistent with Main Frame) ---
    public static final Color PRIMARY_ACCENT = new Color(37, 99, 235); // Royal Blue
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(15, 23, 42); // Slate 900
    public static final Color TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    public static final Color CONTENT_BG = new Color(248, 250, 252); // Slate 50
    public static final Color CONSOLE_BG = new Color(15, 23, 42); // Slate 900
    public static final Color CONSOLE_TEXT = new Color(226, 232, 240); // Slate 200
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // Slate 200
    public static final Color BUTTON_SUCCESS = new Color(16, 185, 129);
    public static final Color BUTTON_DANGER = new Color(239, 68, 68);

    public SnippetManagerPanel() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);
        loadBackendPath();

        if (!validateBackend()) {
            // Logic preserved from original
        }

        initializeGUI();
        setupRecentSearches();

        // Register Autocomplete Logic (Remains identical to original)
        setupAutocomplete(searchField);
        if (updateTitleField != null)
            setupAutocomplete(updateTitleField);
        if (deleteTitleField != null)
            setupAutocomplete(deleteTitleField);
    }

    // --- Backend Logic (UNCHANGED) ---
    private void loadBackendPath() {
        // Try multiple paths to find app.exe
        backendPath = Paths.get("cpp", "module1").toAbsolutePath().toString() + File.separator;
    }

    private boolean validateBackend() {
        File dir = new File(backendPath);
        return dir.exists() && new File(dir, "app.exe").exists();
    }

    private void setupAutocomplete(JTextField field) {
        if (field == null)
            return;
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    String input = field.getText();
                    if (input.isEmpty())
                        return;
                    List<String> matches = getMatchesFromJson(input);
                    if (!matches.isEmpty()) {
                        String suggestion = matches.get(0);
                        if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                            String completion = suggestion.substring(input.length());
                            field.setText(input + completion);
                            field.setCaretPosition(input.length());
                            field.moveCaretPosition(field.getText().length());
                            field.setSelectionStart(input.length());
                            field.setSelectionEnd(field.getText().length());
                        }
                    }
                }
            }
        });
    }

    private List<String> getMatchesFromJson(String prefix) {
        List<String> matches = new ArrayList<>();
        try {
            Path jsonPath = Paths.get("Data", "snippets_large.json");
            String content = Files.readString(jsonPath);
            Pattern pattern = Pattern.compile("\"(.*?)\"\\s*:");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                String key = matcher.group(1);
                if (key.toLowerCase().startsWith(prefix.toLowerCase()))
                    matches.add(key);
            }
        } catch (Exception e) {
        }
        return matches;
    }

    // --- GUI Initialization (MODERNIZED) ---
    private void initializeGUI() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);

        mainContentPanel.add(createSearchPage(), SEARCH_PAGE);
        mainContentPanel.add(createAddPage(), ADD_PAGE);
        mainContentPanel.add(createUpdatePage(), UPDATE_PAGE);
        mainContentPanel.add(createDeletePage(), DELETE_PAGE);

        JPanel consolePanel = createConsolePanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainContentPanel, consolePanel);
        splitPane.setDividerLocation(0.68);
        splitPane.setResizeWeight(0.68);
        splitPane.setDividerSize(3);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createConsolePanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CONSOLE_BG);
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("OUTPUT CONSOLE");
        title.setFont(new Font("Inter", Font.BOLD, 11));
        title.setForeground(new Color(100, 116, 139));
        title.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));

        outputArea = new JTextArea();
        outputArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputArea.setBackground(CONSOLE_BG);
        outputArea.setForeground(CONSOLE_TEXT);
        outputArea.setLineWrap(true);
        outputArea.setText("$ System initialized. Ready for input...");

        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        container.add(title, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    private JPanel createSearchPage() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(CONTENT_BG);
        container.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Header Section
        JLabel welcome = new JLabel("Welcome back, Developer");
        welcome.setFont(new Font("Inter", Font.BOLD, 22));
        welcome.setForeground(TEXT_DARK);
        gbc.gridy = 0;
        container.add(welcome, gbc);

        JLabel sub = new JLabel("Search your local snippet library or create new ones below.");
        sub.setFont(new Font("Inter", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 30, 0);
        container.add(sub, gbc);

        // Quick Actions Grid
        JPanel actions = new JPanel(new GridLayout(1, 3, 15, 0));
        actions.setOpaque(false);
        actions.add(new QuickActionCard("Add New", "Create snippet", "➕", e -> switchPage(ADD_PAGE)));
        actions.add(new QuickActionCard("Update", "Edit existing", "✏️", e -> switchPage(UPDATE_PAGE)));
        actions.add(new QuickActionCard("Delete", "Remove code", "🗑️", e -> switchPage(DELETE_PAGE)));

        gbc.gridy = 2;
        container.add(actions, gbc);

        // Search Section
        JPanel searchBox = new JPanel(new BorderLayout(15, 0));
        searchBox.setOpaque(false);
        searchBox.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));

        searchField = new RoundedTextField();
        searchField.setPreferredSize(new Dimension(0, 50));
        searchField.setToolTipText("Enter snippet title...");

        RoundedButton searchBtn = new RoundedButton("Search Library", PRIMARY_ACCENT, Color.WHITE, 12);
        searchBtn.setPreferredSize(new Dimension(140, 50));
        searchBtn.addActionListener(e -> performSearch());

        searchBox.add(searchField, BorderLayout.CENTER);
        searchBox.add(searchBtn, BorderLayout.EAST);

        gbc.gridy = 3;
        container.add(searchBox, gbc);

        // Recent List
        recentModel = new DefaultListModel<>();
        recentSearchesList = new JList<>(recentModel);
        recentSearchesList.setCellRenderer(new RecentSearchRenderer());
        recentSearchesList.setBackground(CARD_BG);

        JScrollPane listScroll = new JScrollPane(recentSearchesList);
        listScroll.setPreferredSize(new Dimension(0, 200));
        listScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(listScroll, gbc);

        return container;
    }

    private JPanel createAddPage() {
        addTitleField = new RoundedTextField();
        addCodeArea = new JTextArea(10, 20);
        return createFormPage("➕ Add New Snippet", "Create", addTitleField, addCodeArea,
                e -> submitForm("ADD", addTitleField, addCodeArea));
    }

    private JPanel createUpdatePage() {
        updateTitleField = new RoundedTextField();
        updateCodeArea = new JTextArea(10, 20);
        return createFormPage("✏️ Update Snippet", "Save Changes", updateTitleField, updateCodeArea,
                e -> submitForm("UPDATE", updateTitleField, updateCodeArea));
    }

    private JPanel createDeletePage() {
        deleteTitleField = new RoundedTextField();
        deleteCodeArea = new JTextArea(10, 20);
        return createFormPage("🗑️ Delete Snippet", "Delete", deleteTitleField, deleteCodeArea,
                e -> submitForm("DELETE", deleteTitleField, deleteCodeArea));
    }

    // --- Template Form Creator to avoid redundancy ---
    private JPanel createFormPage(String title, String btnText, RoundedTextField titleField, JTextArea codeArea,
            ActionListener action) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(CONTENT_BG);
        container.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel head = new JLabel(title);
        head.setFont(new Font("Inter", Font.BOLD, 20));
        gbc.gridy = 0;
        card.add(head, gbc);

        // Fields
        titleField.setPreferredSize(new Dimension(0, 40));
        gbc.gridy = 1;
        card.add(new JLabel("Snippet Title"), gbc);
        gbc.gridy = 2;
        card.add(titleField, gbc);

        codeArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(codeArea);
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        card.add(scroll, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        RoundedButton cancel = new RoundedButton("Cancel", Color.LIGHT_GRAY, Color.BLACK, 10);
        cancel.addActionListener(e -> switchPage(SEARCH_PAGE));
        RoundedButton submit = new RoundedButton(btnText, PRIMARY_ACCENT, Color.WHITE, 10);
        submit.addActionListener(action);

        buttons.add(cancel);
        buttons.add(submit);
        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(buttons, gbc);

        container.add(card, BorderLayout.CENTER);
        return container;
    }

    // --- Backend Wrapper ---
    private void sendCommand(String command, String titleAndLang, String code) {
        try {
            // Find the project root directory
            File currentDir = new File(System.getProperty("user.dir"));
            File projectRoot = currentDir;
            while (projectRoot != null && !projectRoot.getName().equals("smart-code-snippet-manager")) {
                projectRoot = projectRoot.getParentFile();
            }
            if (projectRoot == null || !projectRoot.exists()) {
                projectRoot = new File("e:\\DSA\\smart-code-snippet-manager");
            }

            File dataDir = new File(projectRoot, "Data");
            if (!dataDir.exists())
                dataDir.mkdirs();

            Path inputPath = dataDir.toPath().resolve("input.txt");
            Path outputPath = dataDir.toPath().resolve("output.txt");

            // Fix: Look for app.exe in cpp/module1 directory
            Path exePath = new File(projectRoot, "cpp/module1/app.exe").toPath().toAbsolutePath();

            // FIXED: Format must be COMMAND|TITLE|LANGUAGE on FIRST line, then CODE on
            // following lines
            try (BufferedWriter writer = Files.newBufferedWriter(inputPath)) {
                // First line: COMMAND|TITLE|LANGUAGE (all on one line with pipes)
                writer.write(command.toUpperCase() + "|" + titleAndLang);
                // Code on remaining lines (if present)
                if (!code.isEmpty()) {
                    writer.write("\n" + code);
                }
            }

            outputArea.setText("> Executing " + command + " on " + titleAndLang + "...");

            if (exePath.toFile().exists()) {
                Process process = new ProcessBuilder(exePath.toString())
                        .directory(dataDir).redirectErrorStream(true).start();
                process.waitFor();

                if (outputPath.toFile().exists()) {
                    outputArea.setText(Files.readString(outputPath));
                }
            } else {
                outputArea.setText("Error: app.exe not found at " + exePath);
            }
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void performSearch() {
        String title = searchField.getText().trim();
        if (!title.isEmpty()) {
            if (!recentModel.contains(title))
                recentModel.add(0, title);
            sendCommand("SEARCH", title + "|Unknown", "");
        }
    }

    private void switchPage(String pageName) {
        cardLayout.show(mainContentPanel, pageName);
    }

    private void submitForm(String command, RoundedTextField titleField, JTextArea codeArea) {
        String title = titleField.getText().trim();
        sendCommand(command, title + "|Java", codeArea.getText().trim());
        titleField.setText("");
        codeArea.setText("");
        switchPage(SEARCH_PAGE);
    }

    private void setupRecentSearches() {
        recentModel.addElement("Binary Search");
        recentModel.addElement("Quick Sort");
    }

    // --- Custom Modern UI Components ---

    static class QuickActionCard extends JPanel {
        public QuickActionCard(String title, String sub, String icon, ActionListener al) {
            setLayout(new BorderLayout());
            setBackground(SnippetManagerPanel.CARD_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SnippetManagerPanel.BORDER_COLOR),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));

            JLabel iconLbl = new JLabel(icon);
            iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

            JPanel textPart = new JPanel(new GridLayout(2, 1));
            textPart.setOpaque(false);
            JLabel t = new JLabel(title);
            t.setFont(new Font("Inter", Font.BOLD, 14));
            JLabel s = new JLabel(sub);
            s.setFont(new Font("Inter", Font.PLAIN, 11));
            s.setForeground(SnippetManagerPanel.TEXT_MUTED);
            textPart.add(t);
            textPart.add(s);

            add(iconLbl, BorderLayout.WEST);
            add(textPart, BorderLayout.CENTER);

            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    al.actionPerformed(null);
                }

                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(241, 245, 249));
                }

                public void mouseExited(MouseEvent e) {
                    setBackground(SnippetManagerPanel.CARD_BG);
                }
            });
        }
    }

    static class RoundedButton extends JButton {
        private Color bg, fg;
        private int r;

        public RoundedButton(String t, Color bg, Color fg, int r) {
            super(t);
            this.bg = bg;
            this.fg = fg;
            this.r = r;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(fg);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Inter", Font.BOLD, 13));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? bg.darker() : (getModel().isRollover() ? bg.brighter() : bg));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    static class RoundedTextField extends JTextField {
        public RoundedTextField() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            setFont(new Font("Inter", Font.PLAIN, 14));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(SnippetManagerPanel.BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    static class RecentSearchRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean isS, boolean chf) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, i, isS, chf);
            lbl.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            lbl.setText("🔍  " + v.toString());
            lbl.setBackground(isS ? new Color(241, 245, 249) : Color.WHITE);
            lbl.setForeground(SnippetManagerPanel.TEXT_DARK);
            return lbl;
        }
    }
}