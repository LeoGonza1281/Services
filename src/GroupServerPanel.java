import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GroupServerPanel extends JPanel {
    private JTextArea textArea; // Área para mostrar el contenido del archivo
    private JLabel environmentLabel; // Etiqueta para mostrar el nombre del entorno actual
    private File currentGroupFile; // Archivo actual del grupo
    private String currentEnvironment; // Nombre del entorno actual

    public GroupServerPanel() {
        setLayout(new BorderLayout()); // Layout principal

        // Panel para Grupos y Servidores
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(2, 1)); // Dos secciones (Grupos y Servidores)

        // Panel para Grupos
        JPanel groupPanel = new JPanel();
        groupPanel.setBorder(BorderFactory.createTitledBorder("Grupos"));
        groupPanel.setLayout(new GridLayout(4, 1)); // 4 filas: 1 título y 3 botones

        JButton addGroupButton = new JButton("Add");
        JButton editGroupButton = new JButton("Edit");
        JButton deleteGroupButton = new JButton("Delete");

        groupPanel.add(addGroupButton);
        groupPanel.add(editGroupButton);
        groupPanel.add(deleteGroupButton);

        // Panel para Servidores
        JPanel serverPanel = new JPanel();
        serverPanel.setBorder(BorderFactory.createTitledBorder("Servidores"));
        serverPanel.setLayout(new GridLayout(4, 1)); // 4 filas: 1 título y 3 botones

        JButton addServerButton = new JButton("Add");
        JButton editServerButton = new JButton("Edit");
        JButton deleteServerButton = new JButton("Delete");

        serverPanel.add(addServerButton);
        serverPanel.add(editServerButton);
        serverPanel.add(deleteServerButton);

        // Añadir los paneles de Grupos y Servidores al panel izquierdo
        leftPanel.add(groupPanel);
        leftPanel.add(serverPanel);

        // Área de texto para mostrar el contenido del archivo
        textArea = new JTextArea();
        textArea.setEditable(false); // Hacer el área de texto no editable
        JScrollPane scrollPane = new JScrollPane(textArea); // Para hacer scroll

        // Etiqueta para mostrar el nombre del entorno actual
        environmentLabel = new JLabel("No environment selected");
        environmentLabel.setHorizontalAlignment(JLabel.CENTER); // Centrar el texto

        // Añadir los componentes al panel principal
        add(environmentLabel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        // Acción para el botón "Add" de Grupos
        addGroupButton.addActionListener(e -> addGroup());

        // Inicializar el entorno actual
        currentEnvironment = "No environment selected"; // Valor inicial por defecto
    }

    // Método para agregar un nuevo grupo (archivo de texto)
    private void addGroup() {
        // Obtener el entorno actual
        String environment = getCurrentEnvironment();

        // Verificar que el entorno no sea "No environment selected"
        if (environment.equals("No environment selected")) {
            JOptionPane.showMessageDialog(this, "Please select an environment first.");
            return;
        }

        // Pedir al usuario el número del grupo
        String groupNumber = JOptionPane.showInputDialog(this, "Enter the group number:");
        if (groupNumber != null && !groupNumber.trim().isEmpty()) {
            // Formato cambiado a [Environment].Group[Numero].txt
            String fileName = environment + ".Group" + groupNumber + ".txt";
            currentGroupFile = new File(fileName);

            try {
                if (currentGroupFile.createNewFile()) {
                    JOptionPane.showMessageDialog(this, "Group file created: " + fileName);
                    loadFileContent(currentGroupFile.getPath()); // Cargar el archivo en el área de texto
                } else {
                    JOptionPane.showMessageDialog(this, "Group file already exists: " + fileName);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error creating group file: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid group number.");
        }
    }

    // Método para obtener el entorno actual desde la etiqueta
    private String getCurrentEnvironment() {
        return environmentLabel.getText().replace("Current Environment: ", "").trim();
    }

    // Método para cargar el contenido del archivo en el área de texto
    public void loadFileContent(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.setText(""); // Limpiar el área de texto
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append(line + "\n"); // Añadir cada línea al área de texto
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
            }
        } else {
            textArea.setText("File does not exist."); // Mensaje si el archivo no existe
        }
    }

    // Método para establecer el nombre del entorno seleccionado
    public void setEnvironmentName(String environmentName) {
        currentEnvironment = environmentName;
        environmentLabel.setText("Current Environment: " + environmentName);
    }
}
