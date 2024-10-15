import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerPanel extends JPanel {
    private JTextField environmentTextField;
    private JButton createEnvironmentButton;
    private JComboBox<String> environmentComboBox;
    private JButton switchPanelButton;
    private List<String> environments; // Para almacenar los entornos creados
    private CardLayout cardLayout; // Añadido para manejar el cambio de panel
    private JPanel mainPanel; // Añadido para el panel principal
    private GroupServerPanel groupServerPanel; // Añadido para el panel de grupos y servidores

    public ServerPanel(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout; // Inicializa el CardLayout
        this.mainPanel = mainPanel; // Inicializa el panel principal
        environments = new ArrayList<>();
        loadEnvironmentsFromFile(); // Cargar entornos al iniciar el panel
        groupServerPanel = new GroupServerPanel(); // Inicializa el GroupServerPanel

        // Cuadro de texto para el nombre del entorno
        environmentTextField = new JTextField(15);
        createEnvironmentButton = new JButton("Create Environment");

        // Dropdown para seleccionar entornos
        environmentComboBox = new JComboBox<>();

        // Llenar el JComboBox con los entornos existentes
        for (String env : environments) {
            environmentComboBox.addItem(env);
        }

        // Panel para crear entornos
        JPanel createEnvironmentPanel = new JPanel();
        createEnvironmentPanel.add(new JLabel("Environment Name:"));
        createEnvironmentPanel.add(environmentTextField);
        createEnvironmentPanel.add(createEnvironmentButton);
        createEnvironmentPanel.add(environmentComboBox);

        // Agregar acción al botón de crear entorno
        createEnvironmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createEnvironment();
            }
        });

        // Botón para cambiar al panel de grupos y servidores
        switchPanelButton = new JButton("Switch to Group and Server Panel");
        switchPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToGroupServerPanel(); // Cambia al panel de grupos y servidores
            }
        });

        // Layout principal
        setLayout(new BorderLayout());
        add(createEnvironmentPanel, BorderLayout.NORTH);
        add(switchPanelButton, BorderLayout.SOUTH);
    }

    private void loadEnvironmentsFromFile() {
        File file = new File("Environments.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    environments.add(line.trim());
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading environments from file: " + e.getMessage());
            }
        }
    }

    private void createEnvironment() {
        String environmentName = environmentTextField.getText().trim();

        if (!environmentName.isEmpty()) {
            // Verificar si el entorno ya existe
            if (!environments.contains(environmentName)) {
                environments.add(environmentName);
                environmentComboBox.addItem(environmentName);
                environmentTextField.setText(""); // Limpiar el cuadro de texto

                // Guardar el entorno en el archivo Environments.txt
                saveEnvironmentToFile(environmentName);

                // Crear un archivo de texto para el nuevo entorno
                createEnvironmentFile(environmentName);
            } else {
                JOptionPane.showMessageDialog(this, "Environment already exists.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid environment name.");
        }
    }

    private void saveEnvironmentToFile(String environmentName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Environments.txt", true))) { // 'true' para agregar al archivo
            writer.write(environmentName);
            writer.newLine(); // Nueva línea después de cada nombre de entorno
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving environment to file: " + e.getMessage());
        }
    }

    private void createEnvironmentFile(String environmentName) {
        File environmentFile = new File(environmentName + ".txt");

        // Verifica si el archivo ya existe
        if (!environmentFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(environmentFile))) {
                writer.write("This is the environment file for: " + environmentName);
                writer.newLine(); // Puedes agregar más contenido aquí si es necesario
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error creating environment file: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "File for this environment already exists.");
        }
    }

    private void switchToGroupServerPanel() {
        // Cambia al panel de grupos y servidores utilizando el CardLayout
        mainPanel.add(groupServerPanel, "GroupServerPanel"); // Añadir el panel al mainPanel
        cardLayout.show(mainPanel, "GroupServerPanel"); // Mostrar el panel de grupos y servidores
        System.out.println("Switching to Group and Server Panel...");
    }

    public List<String> getRegisteredEnvironments() {
        return environments; // Devuelve la lista de entornos
    }
}
