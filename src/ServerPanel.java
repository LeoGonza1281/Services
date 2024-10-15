import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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

    public ServerPanel() {
        environments = new ArrayList<>();
        loadExistingEnvironments(); // Cargar entornos existentes al inicializar

        // Cuadro de texto para el nombre del entorno
        environmentTextField = new JTextField(15);
        createEnvironmentButton = new JButton("Create Environment");

        // Dropdown para seleccionar entornos
        environmentComboBox = new JComboBox<>();

        // Agregar los entornos existentes al JComboBox
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

        // Botón para cambiar a panel de servidores
        switchPanelButton = new JButton("Switch to Server Panel");
        switchPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToServerPanel();
            }
        });

        // Layout principal
        setLayout(new BorderLayout());
        add(createEnvironmentPanel, BorderLayout.NORTH);
        add(switchPanelButton, BorderLayout.SOUTH);
    }

    private void loadExistingEnvironments() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Environments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                environments.add(line.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading environments: " + e.getMessage());
        }
    }

    private void createEnvironment() {
        String environmentName = environmentTextField.getText().trim();

        if (!environmentName.isEmpty() && !environments.contains(environmentName)) {
            environments.add(environmentName);
            environmentComboBox.addItem(environmentName);
            environmentTextField.setText(""); // Limpiar el cuadro de texto

            // Guardar el entorno en el archivo Environments.txt
            saveEnvironmentToFile(environmentName);

            // Crear un archivo de texto para el nuevo entorno
            createEnvironmentFile(environmentName);
        } else {
            String message = environments.contains(environmentName)
                    ? "Environment already exists."
                    : "Please enter a valid environment name.";
            JOptionPane.showMessageDialog(this, message);
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(environmentName + ".txt"))) {
            writer.write("This is the environment file for: " + environmentName);
            writer.newLine(); // Puedes agregar más contenido aquí si es necesario
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating environment file: " + e.getMessage());
        }
    }

    private void switchToServerPanel() {
        // Lógica para cambiar al panel de servidores
        // Aquí deberías implementar la lógica para crear y mostrar el nuevo panel
        System.out.println("Switching to Server Panel...");
    }

    public List<String> getRegisteredEnvironments() {
        return environments; // Devuelve la lista de entornos
    }
}
