import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerPanel extends JPanel {
    private static final String ENVIRONMENT_FILE_PATH = getEnvironmentFilePath();
    private static final String ENVIRONMENT_DIR_PATH = getEnvironmentDirPath();

    private JTextField environmentTextField;
    private JButton createEnvironmentButton;
    private JComboBox<String> environmentComboBox;
    private JButton switchPanelButton;
    private List<String> environments; // To store created environments
    private CardLayout cardLayout; // To manage panel switching
    private JPanel mainPanel; // Main panel
    private GroupServerPanel groupServerPanel; // Panel for groups and servers

    public ServerPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout; // Initialize the CardLayout
        this.mainPanel = mainPanel; // Initialize the main panel
        environments = new ArrayList<>();
        loadEnvironmentsFromFile(); // Load environments at panel initialization
        groupServerPanel = new GroupServerPanel(); // Initialize the GroupServerPanel

        // Text field for environment name
        environmentTextField = new JTextField(15);
        createEnvironmentButton = new JButton("Create Environment");

        // Dropdown for selecting environments
        environmentComboBox = new JComboBox<>();

        // Fill JComboBox with existing environments
        for (String env : environments) {
            environmentComboBox.addItem(env);
        }

        // Input panel layout
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // FlowLayout for inline components
        inputPanel.add(environmentTextField);
        inputPanel.add(createEnvironmentButton);
        inputPanel.add(environmentComboBox);

        // Add action to create environment button
        createEnvironmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createEnvironment();
            }
        });

        // Action for selecting environment from dropdown
        environmentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
                if (selectedEnvironment != null) {
                    File environmentFile = new File(ENVIRONMENT_DIR_PATH, selectedEnvironment + ".txt");
                    groupServerPanel.setEnvironmentName(selectedEnvironment); // Display selected environment name
                }
            }
        });

        // Button to switch to the group and server panel
        switchPanelButton = new JButton("Switch to Group and Server Panel");
        switchPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToGroupServerPanel(); // Switch to the group and server panel
            }
        });

        // Main layout: BorderLayout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER); // Input panel in the center
        add(switchPanelButton, BorderLayout.SOUTH); // Button to switch panels at the bottom
    }

    private static String getEnvironmentFilePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return System.getProperty("user.home") + "\\Documents\\StartServices\\SetupServer\\Environments.txt";
        } else {
            // Para Linux y macOS
            return System.getProperty("user.home") + "/Documents/StartServices/SetupServer/Environments.txt";
        }
    }

    private static String getEnvironmentDirPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return System.getProperty("user.home") + "\\Documents\\StartServices\\SetupServer";
        } else {
            // Para Linux y macOS
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

        // Validate environment name
        if (!isValidEnvironmentName(environmentName)) {
            showErrorDialog("Environment name contains invalid characters.");
            return;
        }

        if (!environmentName.isEmpty()) {
            // Check if environment already exists
            if (!environments.contains(environmentName)) {
                environments.add(environmentName);
                environmentComboBox.addItem(environmentName);
                environmentTextField.setText(""); // Clear the text field

                // Save the environment in the file
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

    private boolean isValidEnvironmentName(String name) {
        // Define valid characters for the environment name
        return name.matches("^[a-zA-Z0-9_]+$"); // Allow letters, numbers, and underscores
    }

    private void saveEnvironmentToFile(String environmentName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENVIRONMENT_FILE_PATH, true))) { // 'true' to append to the file
            writer.write(environmentName);
            writer.newLine(); // New line after each environment name
        } catch (IOException e) {
            showErrorDialog("Error saving environment to file: " + e.getMessage());
        }
    }

    private void createEnvironmentFile(String environmentName) {
        File environmentDir = new File(ENVIRONMENT_DIR_PATH);
        if (!environmentDir.exists()) {
            environmentDir.mkdirs(); // Create the folder if it doesn't exist
        }

        File environmentFile = new File(environmentDir, environmentName + ".txt");

        // Check if the file already exists
        if (!environmentFile.exists()) {
            try {
                // Create an empty file
                if (environmentFile.createNewFile()) {
                    System.out.println("File for " + environmentName + " created successfully.");
                } else {
                    showErrorDialog("Could not create file for this environment.");
                }
            } catch (IOException e) {
                showErrorDialog("Error creating environment file: " + e.getMessage());
            }
        } else {
            showErrorDialog("File for this environment already exists.");
        }
    }

    private void switchToGroupServerPanel() {
        // Switch to the group and server panel using the CardLayout
        mainPanel.add(groupServerPanel, "GroupServerPanel"); // Add the panel to the mainPanel
        cardLayout.show(mainPanel, "GroupServerPanel"); // Show the group and server panel
        System.out.println("Switching to Group and Server Panel...");
    }

    public List<String> getRegisteredEnvironments() {
        return environments; // Return the list of environments
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
