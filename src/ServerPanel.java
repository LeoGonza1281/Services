import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerPanel extends JPanel {
    private JTextField environmentTextField;
    private JButton createEnvironmentButton;
    private JComboBox<String> environmentComboBox;
    private JButton switchPanelButton;
    private List<String> environments; // Para almacenar los entornos creados
    private CardLayout cardLayout; // Para manejar el cambio de panel
    private JPanel mainPanel; // Panel principal
    private GroupServerPanel groupServerPanel; // Panel de grupos y servidores
    private File setupServerDirectory; // Directorio de SetupServer que se creará dinámicamente

    // Constructor actualizado que recibe el directorio de la aplicación
    public ServerPanel(CardLayout cardLayout, JPanel mainPanel, File appDirectory) {
        this.cardLayout = cardLayout; // Inicializa el CardLayout
        this.mainPanel = mainPanel; // Inicializa el panel principal
        environments = new ArrayList<>();
        this.setupServerDirectory = getSetupServerDirectory(appDirectory); // Configura el directorio base dinámico

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

        // Panel para colocar los elementos en una misma línea
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // FlowLayout para poner en una línea

        // Añadir el cuadro de texto, el botón y el dropdown al mismo panel
        inputPanel.add(environmentTextField);
        inputPanel.add(createEnvironmentButton);
        inputPanel.add(environmentComboBox);

        // Agregar acción al botón de crear entorno
        createEnvironmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createEnvironment();
            }
        });

        // Acción para seleccionar el entorno desde el dropdown
        environmentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
                groupServerPanel.loadFileContent(selectedEnvironment + ".txt"); // Cargar contenido del archivo seleccionado
                groupServerPanel.setEnvironmentName(selectedEnvironment); // Mostrar el nombre del entorno seleccionado
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

        // Layout principal: BorderLayout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER); // Panel de entrada en el centro
        add(switchPanelButton, BorderLayout.SOUTH); // Botón para cambiar de panel abajo
    }

    // Método para obtener el directorio base según el sistema operativo
    private File getSetupServerDirectory(File appDirectory) {
        File setupServerDir = new File(appDirectory, "SetupServer"); // Directorio SetupServer

        // Crear el directorio si no existe
        if (!setupServerDir.exists()) {
            if (setupServerDir.mkdirs()) {
                System.out.println("SetupServer directory created successfully.");
            } else {
                System.out.println("Failed to create SetupServer directory.");
            }
        }

        return setupServerDir;
    }

    private void loadEnvironmentsFromFile() {
        File file = new File(setupServerDirectory, "Environments.txt"); // Usamos setupServerDirectory como base
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
            // Check if the environment already exists
            if (!environments.contains(environmentName)) {
                // Add the new environment to the list
                environments.add(environmentName);
                environmentComboBox.addItem(environmentName); // Add to the JComboBox
                environmentTextField.setText(""); // Clear the text field

                // Save the environment to the file
                saveEnvironmentToFile(environmentName);
            } else {
                JOptionPane.showMessageDialog(this, "Environment already exists.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid environment name.");
        }
    }

    private void saveEnvironmentToFile(String environmentName) {
        File file = new File(setupServerDirectory, "Environments.txt"); // File path
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) { // Append mode
            writer.write(environmentName);
            writer.newLine(); // New line after each environment name
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving environment to file: " + e.getMessage());
        }
    }


    // Eliminar el método createEnvironmentFile ya que no se utilizará
    // private void createEnvironmentFile(String environmentName) {
    //     // Define la ruta de la carpeta que llevará el nombre del entorno dentro de setupServerDirectory
    //     File environmentFolder = new File(setupServerDirectory, environmentName);
    //     ...
    // }

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
