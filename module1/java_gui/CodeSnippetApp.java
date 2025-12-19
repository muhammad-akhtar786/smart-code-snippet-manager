

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class CodeSnippetApp extends JFrame {

    private JTextField snippetTitleField;
    private JTextArea snippetCodeArea;
    private JTextArea outputArea;

    // Backend folder path (MUST end with \\)
    private final String backendPath =
            "C:\\Users\\mohsi\\Documents\\Smart Code Snippet and Recommendation System\\C++Backend\\";

    public CodeSnippetApp() {
        setTitle("Smart Code Snippet System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ----- Heading -----
        JLabel heading = new JLabel("Smart Code Snippet Manager", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(new Color(30, 144, 255));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(heading, BorderLayout.NORTH);

        // ----- Input Panel -----
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                "Snippet Details",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                Color.DARK_GRAY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Title:"), gbc);

        snippetTitleField = new JTextField();
        snippetTitleField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(snippetTitleField, gbc);

        // Code
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Code:"), gbc);

        snippetCodeArea = new JTextArea(10, 50);
        snippetCodeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        snippetCodeArea.setLineWrap(true);
        snippetCodeArea.setWrapStyleWord(true);
        JScrollPane codeScroll = new JScrollPane(snippetCodeArea);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(codeScroll, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // ----- Buttons -----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton addButton = new JButton("Add / Update");
        JButton searchButton = new JButton("Search");
        JButton deleteButton = new JButton("Delete");

        Font btnFont = new Font("Arial", Font.BOLD, 16);
        addButton.setFont(btnFont);
        searchButton.setFont(btnFont);
        deleteButton.setFont(btnFont);

        addButton.setBackground(new Color(60, 179, 113));
        addButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(30, 144, 255));
        searchButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ----- Output -----
        outputArea = new JTextArea(8, 40);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setEditable(false);

        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                "Output",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                Color.DARK_GRAY
        ));

        add(outputScroll, BorderLayout.EAST);

        // ----- Actions -----
        addButton.addActionListener(e -> sendCommand("ADD"));
        searchButton.addActionListener(e -> sendCommand("SEARCH"));
        deleteButton.addActionListener(e -> sendCommand("DELETE"));
    }

    private void sendCommand(String command) {
        String title = snippetTitleField.getText().trim();
        String code = snippetCodeArea.getText();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title cannot be empty!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // ---- Write input.txt ----
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(backendPath + "input.txt", false)
            );

            if (command.equals("ADD")) {
                writer.write("ADD|" + title + "|");
                writer.newLine();
                writer.write(code); // multiline safe
            } else {
                writer.write(command + "|" + title + "|");
            }

            writer.close();

            // ---- Run C++ backend ----
            ProcessBuilder pb = new ProcessBuilder(backendPath + "app.exe");
            pb.directory(new File(backendPath)); // 🔴 CRITICAL FIX
            pb.redirectErrorStream(true);

            Process process = pb.start();
            process.waitFor();

            // ---- Read output.txt ----
            BufferedReader reader = new BufferedReader(
                    new FileReader(backendPath + "output.txt")
            );

            outputArea.setText("");
            String line;
            while ((line = reader.readLine()) != null) {
                outputArea.append(line + "\n");
            }
            reader.close();

            // ---- Clear fields ----
            if (!command.equals("SEARCH")) {
                snippetTitleField.setText("");
                snippetCodeArea.setText("");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Backend error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CodeSnippetApp().setVisible(true));
    }
}
