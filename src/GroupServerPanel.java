import javax.swing.*;
import java.awt.*;
import java.io.File;
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
        editGroupButton.addActionListener(e -> editGroup());
        deleteGroupButton.addActionListener(e -> deleteGroup());

        // Inicializar el entorno actual
        currentEnvironment = "No environment selected"; // Valor inicial por defecto
    }

    // Método para establecer el nombre del entorno
    void setEnvironmentName(String environmentName) {
        this.currentEnvironment = environmentName;
        environmentLabel.setText("Environment: " + environmentName); // Actualizar la etiqueta
    }

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
            String fileName = "Group" + groupNumber + ".txt";

            // Usar la ruta en Documents/StartService dentro de la carpeta del environment
            String userHome = System.getProperty("user.home");
            File environmentFolder = new File(userHome + "/Documents/StartServices/SetupServer/" + environment);

            // Crear la carpeta si no existe
            if (!environmentFolder.exists()) {
                environmentFolder.mkdirs(); // Crear todas las carpetas necesarias
            }

            // Crear el archivo del grupo dentro de la carpeta del environment
            currentGroupFile = new File(environmentFolder, fileName);

            // Verificar si ya existe un archivo con el mismo nombre
            if (currentGroupFile.exists()) {
                JOptionPane.showMessageDialog(this, "A group with this number already exists: " + fileName);
                return; // Salir si ya existe el archivo
            }

            // Intentar crear el nuevo archivo
            try {
                if (currentGroupFile.createNewFile()) {
                    JOptionPane.showMessageDialog(this, "Group file created: " + fileName);
                    loadFileContent(currentGroupFile.getPath()); // Cargar el archivo en el área de texto

                    // Agregar el número del grupo con la palabra "Grupo" al archivo del entorno general (NO en la carpeta del environment)
                    File environmentFile = new File(userHome + "/Documents/StartServices/SetupServer", environment + ".txt");
                    try (FileWriter writer = new FileWriter(environmentFile, true)) {
                        writer.write("Grupo " + groupNumber + "\n"); // Añadir "Grupo" seguido del número
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, "Error writing to environment file: " + e.getMessage());
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Error creating group file.");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error creating group file: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid group number.");
        }
    }

    void loadFileContent(String path) {
        // Aquí iría el código para cargar el contenido del archivo en el área de texto
        // Si no necesitas modificar esto, puedes dejarlo vacío
    }

    private void editGroup() {
        // Código para editar un grupo (puedes dejarlo igual que antes)
    }

    private void deleteGroup() {
        // Código para eliminar un grupo (puedes dejarlo igual que antes)
    }

    public String getCurrentEnvironment() {
        return currentEnvironment;
    }
}
