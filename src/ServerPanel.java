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
                saveEnvironmentToFile(environmentName); // Aquí se guarda el entorno en Environments.txt

                // Crear un archivo de texto para el nuevo entorno
                createEnvironmentFile(environmentName); // Aquí se crea el archivo .txt del entorno
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
            try {
                // Simplemente crea un archivo vacío sin escribir contenido automático
                if (environmentFile.createNewFile()) {
                    System.out.println("File for " + environmentName + " created successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not create file for this environment.");
                }
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
