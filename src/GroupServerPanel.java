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
            String fileName = environment + ".Group" + groupNumber + ".txt";

            // Usar la ruta en Documents/StartService
            String userHome = System.getProperty("user.home");
            currentGroupFile = new File(userHome + "/Documents/StartServices", fileName);

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

                    // Agregar el número del grupo con la palabra "Grupo" al archivo del entorno
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
    }

    private void editGroup() {
        // Verificar si hay grupos existentes
        File directory = new File(System.getProperty("user.home") + "/Documents/StartServices/SeutupServer"); // El directorio correcto
        String[] groupFiles = directory.list((dir, name) -> name.startsWith(currentEnvironment + ".Group") && name.endsWith(".txt"));

        if (groupFiles == null || groupFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No group files found.");
            return;
        }

        // Convertir los nombres de los archivos en nombres de grupos (Grupo [Número])
        String[] groupNames = new String[groupFiles.length];
        for (int i = 0; i < groupFiles.length; i++) {
            String fileName = groupFiles[i];
            // Extraer solo el número del grupo de algo como "Environment.Group[Numero].txt"
            String groupNumber = fileName.substring(fileName.indexOf("Group") + 5, fileName.indexOf(".txt"));
            groupNames[i] = "Grupo " + groupNumber;
        }

        // Mostrar un cuadro de diálogo para seleccionar un grupo
        String selectedGroup = (String) JOptionPane.showInputDialog(this,
                "Select a group to edit:",
                "Edit Group",
                JOptionPane.PLAIN_MESSAGE,
                null,
                groupNames, // Mostrar solo los nombres de los grupos
                groupNames[0]);

        if (selectedGroup != null) {
            // Obtener el número del grupo seleccionado
            String groupNumber = selectedGroup.replace("Grupo ", "");

            // Buscar el archivo correspondiente al grupo seleccionado
            currentGroupFile = new File(directory, currentEnvironment + ".Group" + groupNumber + ".txt");

            // Pedir al usuario el nuevo número del grupo
            String newGroupNumber = JOptionPane.showInputDialog(this, "Enter the new group number:");
            if (newGroupNumber != null && !newGroupNumber.trim().isEmpty()) {
                String newFileName = currentEnvironment + ".Group" + newGroupNumber + ".txt";
                File newGroupFile = new File(directory, newFileName);

                // Renombrar el archivo
                if (currentGroupFile.renameTo(newGroupFile)) {
                    JOptionPane.showMessageDialog(this, "Group file renamed to: " + newFileName);
                    currentGroupFile = newGroupFile;
                    loadFileContent(currentGroupFile.getPath()); // Cargar el archivo renombrado
                } else {
                    JOptionPane.showMessageDialog(this, "Error renaming the group file.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid group number.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No group selected.");
        }
    }

    private void deleteGroup() {
        // Verificar si hay grupos existentes
        File directory = new File(System.getProperty("user.home") + "/Documents/StartServices/SetupServer"); // Directorio correcto
        String[] groupFiles = directory.list((dir, name) -> name.startsWith(currentEnvironment + ".Group") && name.endsWith(".txt"));

        if (groupFiles == null || groupFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No group files found.");
            return;
        }

        // Convertir los nombres de los archivos en nombres de grupos (Grupo [Número])
        String[] groupNames = new String[groupFiles.length];
        for (int i = 0; i < groupFiles.length; i++) {
            String fileName = groupFiles[i];
            // Extraer solo el número del grupo de algo como "Environment.Group[Numero].txt"
            String groupNumber = fileName.substring(fileName.indexOf("Group") + 5, fileName.indexOf(".txt"));
            groupNames[i] = "Grupo " + groupNumber;
        }

        // Mostrar un cuadro de diálogo para seleccionar un grupo
        String selectedGroup = (String) JOptionPane.showInputDialog(this,
                "Select a group to delete:",
                "Delete Group",
                JOptionPane.PLAIN_MESSAGE,
                null,
                groupNames, // Mostrar solo los nombres de los grupos
                groupNames[0]);

        if (selectedGroup != null) {
            // Obtener el número del grupo seleccionado
            String groupNumber = selectedGroup.replace("Grupo ", "");

            // Buscar el archivo correspondiente al grupo seleccionado
            currentGroupFile = new File(directory, currentEnvironment + ".Group" + groupNumber + ".txt");

            // Eliminar el archivo
            if (currentGroupFile.delete()) {
                JOptionPane.showMessageDialog(this, "Group file deleted.");
                textArea.setText(""); // Limpiar el área de texto después de eliminar
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting the group file.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No group selected.");
        }
    }

    public String getCurrentEnvironment() {
        return currentEnvironment;
    }
}
