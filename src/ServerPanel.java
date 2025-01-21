import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

    private static final int SMALL_SCREEN_WIDTH = 800;
    private static final int SMALL_SCREEN_HEIGHT = 600;

    public ServerPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        environments = new ArrayList<>();
        loadEnvironmentsFromFile();
        groupServerPanel = new GroupServerPanel();

        environmentTextField = new JTextField(15);
        environmentTextField.setFont(new Font("Arial", Font.PLAIN, 18)); // Agrandar el texto

        createEnvironmentButton = new JButton("Create Environment");
        createEnvironmentButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Agrandar texto
        createEnvironmentButton.setPreferredSize(new Dimension(250, 50)); // Tamaño personalizado para el botón

        deleteEnvironmentButton = new JButton("Delete Environment");
        deleteEnvironmentButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Agrandar texto
        deleteEnvironmentButton.setPreferredSize(new Dimension(250, 50)); // Tamaño personalizado para el botón

        environmentComboBox = new JComboBox<>();
        environmentComboBox.setFont(new Font("Arial", Font.PLAIN, 18)); // Agrandar texto
        environmentComboBox.setPreferredSize(new Dimension(250, 40)); // Tamaño personalizado para el ComboBox

        for (String env : environments) {
            environmentComboBox.addItem(env);
        }

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Añadir componentes en el orden deseado
        inputPanel.add(environmentTextField);
        inputPanel.add(createEnvironmentButton);
        inputPanel.add(environmentComboBox);
        inputPanel.add(deleteEnvironmentButton);

        createEnvironmentButton.addActionListener(e -> createEnvironment());
        deleteEnvironmentButton.addActionListener(e -> deleteEnvironment());
        environmentComboBox.addActionListener(e -> {
            String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
            if (selectedEnvironment != null) {
                groupServerPanel.setEnvironmentName(selectedEnvironment);
            }
        });

        switchPanelButton = new JButton("Switch to Group and Server Panel");
        switchPanelButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Agrandar texto
        switchPanelButton.setPreferredSize(new Dimension(300, 50)); // Tamaño personalizado para el botón
        switchPanelButton.addActionListener(e -> switchToGroupServerPanel());

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(switchPanelButton, BorderLayout.SOUTH);

        // Detectar cambios en el tamaño de la ventana
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponentSizes(getWidth(), getHeight());
            }
        });

        // Ajustar el tamaño inicial de los componentes
        adjustComponentSizes(getWidth(), getHeight());
    }

    private void adjustComponentSizes(int width, int height) {
        if (width < SMALL_SCREEN_WIDTH || height < SMALL_SCREEN_HEIGHT) {
            // Pantallas pequeñas: Reducir el tamaño de los botones y el texto
            environmentTextField.setFont(new Font("Arial", Font.PLAIN, 14));
            environmentTextField.setPreferredSize(new Dimension(200, 35));

            createEnvironmentButton.setFont(new Font("Arial", Font.PLAIN, 14));
            createEnvironmentButton.setPreferredSize(new Dimension(200, 40));

            deleteEnvironmentButton.setFont(new Font("Arial", Font.PLAIN, 14));
            deleteEnvironmentButton.setPreferredSize(new Dimension(200, 40));

            environmentComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
            environmentComboBox.setPreferredSize(new Dimension(200, 35));

            switchPanelButton.setFont(new Font("Arial", Font.PLAIN, 14));
            switchPanelButton.setPreferredSize(new Dimension(250, 40));
        } else {
            // Pantallas grandes: Mantener el tamaño más grande
            environmentTextField.setFont(new Font("Arial", Font.PLAIN, 18));
            environmentTextField.setPreferredSize(new Dimension(250, 50));

            createEnvironmentButton.setFont(new Font("Arial", Font.PLAIN, 18));
            createEnvironmentButton.setPreferredSize(new Dimension(250, 50));

            deleteEnvironmentButton.setFont(new Font("Arial", Font.PLAIN, 18));
            deleteEnvironmentButton.setPreferredSize(new Dimension(250, 50));

            environmentComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
            environmentComboBox.setPreferredSize(new Dimension(250, 40));

            switchPanelButton.setFont(new Font("Arial", Font.PLAIN, 18));
            switchPanelButton.setPreferredSize(new Dimension(300, 50));
        }
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
        // Eliminar todos los espacios
        String environmentName = environmentTextField.getText().replaceAll("\\s+", "");

        // Validación de que el nombre no esté vacío
        if (environmentName.isEmpty()) {
            showErrorDialog("Please enter a valid environment name.");
            return;
        }

        // Comprobar si el nombre del entorno ya existe
        if (environments.contains(environmentName)) {
            showErrorDialog("Environment already exists.");
            return;
        }

        // Agregar el nuevo entorno a la lista y combo box
        environments.add(environmentName);
        environmentComboBox.addItem(environmentName);
        environmentTextField.setText(""); // Limpiar el campo de texto

        // Guardar el entorno en el archivo y crear su archivo asociado
        saveEnvironmentToFile(environmentName);
        createEnvironmentFile(environmentName);

        showSuccessDialog("Environment created successfully.");
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
