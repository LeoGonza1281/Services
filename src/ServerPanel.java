import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerPanel extends JPanel {
    private static final String ENVIRONMENT_FILE_PATH = getEnvironmentFilePath();
    private static final String ENVIRONMENT_DIR_PATH = getEnvironmentDirPath();

    private JTextField environmentTextField;
    private JButton createEnvironmentButton;
    private JButton deleteEnvironmentButton;
    private JComboBox<String> environmentComboBox;
    private JButton switchPanelButton;
    private List<String> environments; // To store created environments
    private CardLayout cardLayout; // To manage panel switching
    private JPanel mainPanel; // Main panel
    private GroupServerPanel groupServerPanel; // Panel for groups and servers

    public ServerPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        environments = new ArrayList<>();
        loadEnvironmentsFromFile();
        groupServerPanel = new GroupServerPanel();

        environmentTextField = new JTextField(15);
        createEnvironmentButton = new JButton("Create Environment");
        deleteEnvironmentButton = new JButton("Delete Environment");
        environmentComboBox = new JComboBox<>();

        for (String env : environments) {
            environmentComboBox.addItem(env);
        }

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.add(environmentTextField);
        inputPanel.add(createEnvironmentButton);
        inputPanel.add(deleteEnvironmentButton);
        inputPanel.add(environmentComboBox);

        createEnvironmentButton.addActionListener(e -> createEnvironment());
        deleteEnvironmentButton.addActionListener(e -> deleteEnvironment());
        environmentComboBox.addActionListener(e -> {
            String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
            if (selectedEnvironment != null) {
                groupServerPanel.setEnvironmentName(selectedEnvironment);
            }
        });

        switchPanelButton = new JButton("Switch to Group and Server Panel");
        switchPanelButton.addActionListener(e -> switchToGroupServerPanel());

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(switchPanelButton, BorderLayout.SOUTH);
    }

    private static String getEnvironmentFilePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return System.getProperty("user.home") + "\\Documents\\StartServices\\SetupServer\\Environments.txt";
        } else {
            return System.getProperty("user.home") + "/Documents/StartServices/SetupServer/Environments.txt";
        }
    }

    private static String getEnvironmentDirPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return System.getProperty("user.home") + "\\Documents\\StartServices\\SetupServer";
        } else {
            return System.getProperty("user.home") + "/Documents/StartServices/SetupServer";
        }
    }

    private void loadEnvironmentsFromFile() {
        File file = new File(ENVIRONMENT_FILE_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    environments.add(line.trim());
                }
            } catch (IOException e) {
                showErrorDialog("Error loading environments from file: " + e.getMessage());
            }
        }
    }

    private void createEnvironment() {
        String environmentName = environmentTextField.getText().trim();
        if (!isValidEnvironmentName(environmentName)) {
            showErrorDialog("Environment name contains invalid characters.");
            return;
        }
        if (!environmentName.isEmpty()) {
            if (!environments.contains(environmentName)) {
                environments.add(environmentName);
                environmentComboBox.addItem(environmentName);
                environmentTextField.setText("");
                saveEnvironmentToFile(environmentName);
                createEnvironmentFile(environmentName);
                showSuccessDialog("Environment created successfully.");
            } else {
                showErrorDialog("Environment already exists.");
            }
        } else {
            showErrorDialog("Please enter a valid environment name.");
        }
    }

    private void deleteEnvironment() {
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        if (selectedEnvironment == null) {
            showErrorDialog("No environment selected.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the environment: " + selectedEnvironment + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            File environmentFile = new File(ENVIRONMENT_DIR_PATH, selectedEnvironment + ".txt");
            try {
                Files.delete(Paths.get(environmentFile.getAbsolutePath()));
                environments.remove(selectedEnvironment);
                environmentComboBox.removeItem(selectedEnvironment);
                saveEnvironmentsToFile();
                showSuccessDialog("Environment deleted successfully.");
            } catch (IOException e) {
                showErrorDialog("Error deleting environment: " + e.getMessage());
            }
        }
    }

    private void saveEnvironmentsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENVIRONMENT_FILE_PATH))) {
            for (String env : environments) {
                writer.write(env);
                writer.newLine();
            }
        } catch (IOException e) {
            showErrorDialog("Error saving environments to file: " + e.getMessage());
        }
    }

    private boolean isValidEnvironmentName(String name) {
        return name.matches("^[a-zA-Z0-9_]+$");
    }

    private void saveEnvironmentToFile(String environmentName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENVIRONMENT_FILE_PATH, true))) {
            writer.write(environmentName);
            writer.newLine();
        } catch (IOException e) {
            showErrorDialog("Error saving environment to file: " + e.getMessage());
        }
    }

    private void createEnvironmentFile(String environmentName) {
        File environmentDir = new File(ENVIRONMENT_DIR_PATH);
        if (!environmentDir.exists()) {
            environmentDir.mkdirs();
        }
        File environmentFile = new File(environmentDir, environmentName + ".txt");
        if (!environmentFile.exists()) {
            try {
                if (!environmentFile.createNewFile()) {
                    showErrorDialog("Could not create file for this environment.");
                }
            } catch (IOException e) {
                showErrorDialog("Error creating environment file: " + e.getMessage());
            }
        }
    }

    private void switchToGroupServerPanel() {
        mainPanel.add(groupServerPanel, "GroupServerPanel");
        cardLayout.show(mainPanel, "GroupServerPanel");
        System.out.println("Switching to Group and Server Panel...");
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
