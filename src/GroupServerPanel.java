import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GroupServerPanel extends JPanel {
    private JTextArea textArea; // Área para mostrar el contenido del archivo
    private JLabel environmentLabel; // Etiqueta para mostrar el nombre del entorno actual
    private File currentGroupFile; // Archivo actual del grupo
    private String currentEnvironment; // Nombre del entorno actual
    private List<String> groupNames; // Lista de nombres de grupos creados

    public GroupServerPanel() {
        setLayout(new BorderLayout()); // Layout principal

        // Inicializar la lista de grupos
        groupNames = new ArrayList<>();

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
            // Formato del nombre del archivo de grupo
            String fileName = "Group" + groupNumber + ".txt";

            // Ubicación de la carpeta del entorno en Documents/StartServices/SetupServer
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

            // Intentar crear el nuevo archivo de grupo
            try {
                if (currentGroupFile.createNewFile()) {
                    JOptionPane.showMessageDialog(this, "Group file created: " + fileName);
                    loadFileContent(currentGroupFile.getPath()); // Cargar el archivo en el área de texto

                    // Ubicación del archivo general del entorno
                    File environmentFile = new File(userHome + "/Documents/StartServices/SetupServer", environment + ".txt");

                    // Verificar que el archivo general del entorno existe
                    if (environmentFile.exists()) {
                        // Añadir el grupo al archivo general del entorno
                        try (FileWriter writer = new FileWriter(environmentFile, true)) {
                            writer.write("Grupo " + groupNumber + "\n"); // Añadir "Grupo" seguido del número
                            groupNames.add("Group" + groupNumber); // Agregar a la lista de grupos
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(this, "Error writing to environment file: " + e.getMessage());
                        }
                    } else {
                        // Crear el archivo si no existe y agregar el grupo
                        try {
                            if (environmentFile.createNewFile()) {
                                // Si el archivo se creó correctamente, se agrega el grupo
                                try (FileWriter writer = new FileWriter(environmentFile, true)) {
                                    writer.write("Grupo " + groupNumber + "\n"); // Añadir "Grupo" seguido del número
                                    groupNames.add("Group" + groupNumber); // Agregar a la lista de grupos
                                } catch (IOException e) {
                                    JOptionPane.showMessageDialog(this, "Error writing to environment file: " + e.getMessage());
                                }
                            }
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(this, "Error creating environment file: " + e.getMessage());
                        }
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
        // Verificar si hay grupos disponibles para editar
        if (groupNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No groups available to edit.");
            return;
        }

        // Mostrar un cuadro de diálogo para seleccionar un grupo a editar
        String selectedGroup = (String) JOptionPane.showInputDialog(this, "Select a group to edit:", "Edit Group",
                JOptionPane.PLAIN_MESSAGE, null, groupNames.toArray(), groupNames.get(0));

        // Si el usuario selecciona un grupo
        if (selectedGroup != null) {
            // Pedir el nuevo nombre del grupo
            String newGroupName = JOptionPane.showInputDialog(this, "Enter new name for " + selectedGroup + ":");
            if (newGroupName != null && !newGroupName.trim().isEmpty()) {
                // Actualizar el nombre del archivo
                File oldFile = new File(currentGroupFile.getParent(), selectedGroup + ".txt");
                File newFile = new File(currentGroupFile.getParent(), newGroupName + ".txt");

                // Renombrar el archivo
                if (oldFile.renameTo(newFile)) {
                    JOptionPane.showMessageDialog(this, "Group renamed to: " + newGroupName);
                    loadFileContent(newFile.getPath()); // Cargar el nuevo archivo en el área de texto

                    // Actualizar la lista de nombres de grupos
                    groupNames.remove(selectedGroup);
                    groupNames.add(newGroupName);
                } else {
                    JOptionPane.showMessageDialog(this, "Error renaming group file.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid group name.");
            }
        }
    }

    private void deleteGroup() {
        // Verificar si hay grupos disponibles para eliminar
        if (groupNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No groups available to delete.");
            return;
        }

        // Mostrar un cuadro de diálogo para seleccionar un grupo a eliminar
        String selectedGroup = (String) JOptionPane.showInputDialog(this, "Select a group to delete:", "Delete Group",
                JOptionPane.PLAIN_MESSAGE, null, groupNames.toArray(), groupNames.get(0));

        // Si el usuario selecciona un grupo
        if (selectedGroup != null) {
            // Confirmar la eliminación
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the group " + selectedGroup + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            // Si el usuario confirma la eliminación
            if (confirm == JOptionPane.YES_OPTION) {
                // Obtener el archivo del grupo
                File groupFile = new File(currentGroupFile.getParent(), selectedGroup + ".txt");

                // Intentar eliminar el archivo
                if (groupFile.delete()) {
                    JOptionPane.showMessageDialog(this, "Group " + selectedGroup + " deleted successfully.");
                    loadFileContent(""); // Limpiar el área de texto

                    // Actualizar la lista de grupos
                    groupNames.remove(selectedGroup);
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting group file.");
                }
            }
        }
    }



    public String getCurrentEnvironment() {
        return currentEnvironment;
    }
}
