
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeSnippetApp extends JFrame {

    private JTextArea outputArea;
    private JTextField searchField;
    private JList<String> recentSearchesList;
    private DefaultListModel<String> recentModel;
    private final Preferences prefs = Preferences.userNodeForPackage(CodeSnippetApp.class);
    private String backendPath;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private final String SEARCH_PAGE = "SEARCH";
    private final String ADD_PAGE = "ADD";
    private final String UPDATE_PAGE = "UPDATE";
    private final String DELETE_PAGE = "DELETE";
    private RoundedTextField addTitleField, updateTitleField, deleteTitleField;
    private JTextArea addCodeArea, updateCodeArea;
    private JComboBox<String> languageDropdown;
    private JComboBox<String> updateLanguageDropdown;

    private static final Color NAV_BG = new Color(52, 152, 219);
    private static final Color TEXT_DARK = Color.BLACK;
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color FIELD_BG = Color.WHITE;
    private static final Color BUTTON_GREEN = new Color(46, 204, 113);
    private static final Color BUTTON_ORANGE = new Color(243, 156, 18);
    private static final Color BUTTON_RED = new Color(231, 76, 60);
    private static final Color BUTTON_BLUE = new Color(52, 152, 219);

    public CodeSnippetApp() {
        loadBackendPath();
        if (!validateBackend()) {
            JOptionPane.showMessageDialog(null, "Backend files missing at: " + backendPath, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        initializeGUI();
        setupRecentSearches();

        // Register Autocomplete for Search, Update, and Delete fields
        setupAutocomplete(searchField);
        setupAutocomplete(updateTitleField);
        setupAutocomplete(deleteTitleField);
    }

    // --- AUTOCOMPLETE LOGIC (NO EXTERNAL LIBRARY NEEDED) ---
    private void setupAutocomplete(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Only trigger for letters, digits, or backspace
                if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    String input = field.getText();
                    if (input.isEmpty()) return;

                    List<String> matches = getMatchesFromJson(input);
                    if (!matches.isEmpty()) {
                        String suggestion = matches.get(0);
                        // Case-insensitive check
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
            String content = new String(Files.readAllBytes(Paths.get(backendPath + "snippets.json")));
            // Regex to find "KeyName": pattern in JSON
            Pattern pattern = Pattern.compile("\"(.*?)\"\\s*:");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String key = matcher.group(1);
                if (key.toLowerCase().startsWith(prefix.toLowerCase())) {
                    matches.add(key);
                }
            }
        } catch (Exception e) {
            // File not found or empty
        }
        return matches;
    }

    private void loadBackendPath() {
        backendPath = "G:\\dsaproject\\Smart Code Snippet\\C++Backend\\";
        prefs.put("backendPath", backendPath);
    }

    private boolean validateBackend() {
        File dir = new File(backendPath);
        return dir.exists() && new File(dir, "app.exe").exists();
    }

    private void initializeGUI() {
        setTitle("Smart Code Snippet Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Smart Code Snippet Manager", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(30, 144, 255));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        navPanel.setBackground(new Color(248, 249, 250));

        RoundedButton btnAdd = createNavButton("Add Snippet");
        RoundedButton btnUpdate = createNavButton("Update Snippet");
        RoundedButton btnDelete = createNavButton("Delete Snippet");
        RoundedButton btnSearch = createNavButton("Search");

        navPanel.add(btnAdd);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(btnUpdate);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(btnDelete);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(btnSearch);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.add(createSearchPage(), SEARCH_PAGE);
        mainContentPanel.add(createAddPage(), ADD_PAGE);
        mainContentPanel.add(createUpdatePage(), UPDATE_PAGE);
        mainContentPanel.add(createDeletePage(), DELETE_PAGE);

        cardLayout.show(mainContentPanel, SEARCH_PAGE);

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(45, 45, 45));
        outputArea.setForeground(Color.LIGHT_GRAY);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));

        JSplitPane leftMainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navPanel, mainContentPanel);
        leftMainSplit.setDividerLocation(280);
        leftMainSplit.setBorder(BorderFactory.createEmptyBorder());

        JSplitPane fullSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftMainSplit, outputScroll);
        fullSplit.setDividerLocation(0.7);
        add(fullSplit, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> switchPage(ADD_PAGE));
        btnUpdate.addActionListener(e -> switchPage(UPDATE_PAGE));
        btnDelete.addActionListener(e -> switchPage(DELETE_PAGE));
        btnSearch.addActionListener(e -> switchPage(SEARCH_PAGE));
    }

    private RoundedButton createNavButton(String text) {
        RoundedButton btn = new RoundedButton(text, NAV_BG, TEXT_LIGHT, 30);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(240, 55));
        btn.setMaximumSize(new Dimension(240, 55));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private void switchPage(String pageName) {
        cardLayout.show(mainContentPanel, pageName);
    }

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
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? bgColor.darker() : (getModel().isRollover() ? bgColor.brighter() : bgColor));
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
        private int radius = 15;
        public RoundedTextField() {
            setOpaque(false);
            setBorder(new RoundedBorder(radius));
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FIELD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedBorder implements Border {
        private int radius;
        RoundedBorder(int radius) { this.radius = radius; }
        public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius, radius/2, radius); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    private JPanel createSearchPage() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(Color.WHITE);
        searchField = new RoundedTextField();
        searchField.setPreferredSize(new Dimension(400, 45));
        RoundedButton searchBtn = new RoundedButton(" Search", BUTTON_BLUE, TEXT_LIGHT, 25);
        searchBtn.setPreferredSize(new Dimension(120, 45));
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setBackground(Color.WHITE);
        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(searchBtn, BorderLayout.EAST);
        panel.add(searchBar, BorderLayout.NORTH);
        recentModel = new DefaultListModel<>();
        recentSearchesList = new JList<>(recentModel);
        recentSearchesList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        recentSearchesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText("<html><font color='#9aa0a6' size='4'>&#9202;</font> &nbsp;" + value.toString() + "</html>");
                label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return label;
            }
        });
        recentSearchesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = recentSearchesList.getSelectedValue();
                if (selected != null) searchField.setText(selected);
            }
        });
        JScrollPane recentScroll = new JScrollPane(recentSearchesList);
        recentScroll.setBorder(BorderFactory.createTitledBorder("Recent Searches"));
        panel.add(recentScroll, BorderLayout.CENTER);
        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        return panel;
    }

    private void performSearch() {
        String title = searchField.getText().trim();
        if (!title.isEmpty()) {
            if (!recentModel.contains(title)) recentModel.add(0, title);
            sendCommand("SEARCH", title + "|Unknown", "");
        }
    }

    private void setupRecentSearches() {
        recentModel.addElement("Binary Search");
        recentModel.addElement("Quick Sort");
    }

    private JPanel createAddPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add New Snippet"));
        addTitleField = new RoundedTextField();
        String[] languages = {"C++", "Java", "Python", "C#", "JavaScript", "HTML/CSS", "PHP", "SQL", "Swift", "Rust"};
        languageDropdown = new JComboBox<>(languages);
        languageDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addCodeArea = new JTextArea(10, 40);
        addCodeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        addCodeArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        RoundedButton submitBtn = new RoundedButton("Add Snippet", BUTTON_GREEN, TEXT_LIGHT, 25);
        submitBtn.setPreferredSize(new Dimension(180, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(addTitleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; panel.add(new JLabel("Language:"), gbc);
        gbc.gridx = 1; panel.add(languageDropdown, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST; panel.add(new JLabel("Code:"), gbc);
        gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(addCodeArea), gbc);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitBtn, gbc);
        submitBtn.addActionListener(e -> submitForm("ADD", addTitleField, addCodeArea));
        return panel;
    }

    private JPanel createUpdatePage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Update Existing Snippet"));
        updateTitleField = new RoundedTextField();
        String[] languages = {"C++", "Java", "Python", "C#", "JavaScript", "HTML/CSS", "PHP", "SQL", "Swift", "Rust"};
        updateLanguageDropdown = new JComboBox<>(languages);
        updateLanguageDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateCodeArea = new JTextArea(10, 40);
        updateCodeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        updateCodeArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        RoundedButton submitBtn = new RoundedButton("Update Snippet", BUTTON_ORANGE, TEXT_LIGHT, 25);
        submitBtn.setPreferredSize(new Dimension(180, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; panel.add(updateTitleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Language:"), gbc);
        gbc.gridx = 1; panel.add(updateLanguageDropdown, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel.add(new JLabel("Code:"), gbc);
        gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(updateCodeArea), gbc);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitBtn, gbc);
        submitBtn.addActionListener(e -> submitForm("UPDATE", updateTitleField, updateCodeArea));
        return panel;
    }

    private JPanel createDeletePage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Delete Snippet"));
        panel.setBackground(Color.WHITE);
        deleteTitleField = new RoundedTextField();
        RoundedButton deleteBtn = new RoundedButton("Confirm Delete", BUTTON_RED, TEXT_LIGHT, 25);
        deleteBtn.setPreferredSize(new Dimension(180, 45));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(deleteTitleField, gbc);
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        panel.add(deleteBtn, gbc);
        deleteBtn.addActionListener(e -> sendCommand("DELETE", deleteTitleField.getText().trim() + "|Unknown", ""));
        return panel;
    }

    private void submitForm(String command, RoundedTextField titleField, JTextArea codeArea) {
        String title = titleField.getText().trim();
        String lang = "Unknown";
        if (command.equals("ADD") && languageDropdown != null) {
            lang = (String) languageDropdown.getSelectedItem();
        } else if (command.equals("UPDATE") && updateLanguageDropdown != null) {
            lang = (String) updateLanguageDropdown.getSelectedItem();
        }
        sendCommand(command, title + "|" + lang, codeArea.getText().trim());
        titleField.setText("");
        codeArea.setText("");
    }

    private void sendCommand(String command, String titleAndLang, String code) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(backendPath + "input.txt"))) {
                writer.write(command + "|" + titleAndLang + "|");
                if (code != null && !code.isEmpty()) {
                    writer.newLine();
                    writer.write(code);
                }
            }
            outputArea.setText("Processing...");
            Process process = new ProcessBuilder(backendPath + "app.exe")
                    .directory(new File(backendPath))
                    .start();
            process.waitFor();
            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(backendPath + "output.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) result.append(line).append("\n");
            }
            outputArea.setText(result.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Backend error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new CodeSnippetApp().setVisible(true);
        });
    }
}
