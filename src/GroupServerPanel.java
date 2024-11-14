import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class GroupServerPanel extends JPanel {
    private JTextArea textArea;
    private JLabel environmentLabel;
    private File currentGroupFile;
    private String currentEnvironment = "No environment selected";
    private String currentGroup;

    public GroupServerPanel() {
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.add(createGroupPanel());
        leftPanel.add(createServerPanel());

        textArea = new JTextArea();
        textArea.setEditable(false);

        environmentLabel = new JLabel(currentEnvironment, JLabel.CENTER);
        add(environmentLabel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    private JPanel createGroupPanel() {
        JPanel groupPanel = new JPanel(new GridLayout(3, 1));
        groupPanel.setBorder(BorderFactory.createTitledBorder("Grupos"));

        addButton(groupPanel, "Add", e -> addGroup());
        addButton(groupPanel, "Edit", e -> editGroup());
        addButton(groupPanel, "Delete", e -> deleteGroup());

        return groupPanel;
    }

    private JPanel createServerPanel() {
        JPanel serverPanel = new JPanel(new GridLayout(3, 1));
        serverPanel.setBorder(BorderFactory.createTitledBorder("Servidores"));

        addButton(serverPanel, "Add Server", e -> addServer());
        addButton(serverPanel, "Edit", e -> editServer());
        addButton(serverPanel, "Delete Server", e -> deleteServer());

        return serverPanel;
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        panel.add(button);
    }

    public void setEnvironmentName(String environmentName) {
        this.currentEnvironment = environmentName;
        updateEnvironmentLabel();
    }

    private void updateEnvironmentLabel() {
        String displayText = "Environment: " + currentEnvironment;
        if (currentGroup != null) displayText += " | Group: " + currentGroup;
        environmentLabel.setText(displayText);
    }

    private void addGroup() {
        if (environmentNotSelected()) return;

        String groupNumber = getUserInput("Enter the group number:");
        if (isEmpty(groupNumber)) return;

        String fileName = currentEnvironment + ".Group" + groupNumber + ".txt";
        currentGroupFile = new File(getSetupServerDirectory(), fileName);

        if (currentGroupFile.exists()) {
            showError("A group with this number already exists: " + fileName);
            return;
        }

        try {
            if (currentGroupFile.createNewFile()) {
                appendToEnvironmentFile(fileName);
                currentGroup = "Group " + groupNumber;
                updateEnvironmentLabel();
                showMessage("Group file created: " + fileName);
            } else {
                showError("Error creating group file.");
            }
        } catch (IOException ex) {
            showError("Error creating group file: " + ex.getMessage());
        }
    }

    private void addServer() {
        if (!setCurrentGroupFromSelection("Select a group to add server:")) return;

        // Solicitar al usuario que ingrese los servidores manualmente, separados por comas
        String serverInput = JOptionPane.showInputDialog(this, "Enter server names separated by commas (e.g., Server01, Server02):");

        if (serverInput == null || serverInput.trim().isEmpty()) {
            showMessage("No servers were entered.");
            return;
        }

        // Separar los servidores por comas y eliminar espacios extra
        String[] serverArray = serverInput.split("\\s*,\\s*");

        // Leer los servidores existentes del archivo
        ArrayList<String> existingServers = readServersFromFile();

        // Añadir solo los servidores que no están duplicados
        ArrayList<String> newServers = new ArrayList<>();
        for (String server : serverArray) {
            if (!existingServers.contains(server)) {
                newServers.add(server);
            }
        }

        if (newServers.isEmpty()) {
            showMessage("No new servers were added (all servers were already in the list).");
            return;
        }

        // Agregar los nuevos servidores a la lista existente
        existingServers.addAll(newServers);

        // Guardar los servidores actualizados en el archivo
        writeServersToFile(existingServers, "Servers added successfully.");
    }



    private void editServer() {
        if (!setCurrentGroupFromSelection("Select a group to edit server:")) return;

        // Leer los servidores existentes del archivo
        ArrayList<String> existingServers = readServersFromFile();

        if (existingServers.isEmpty()) {
            showMessage("No servers to edit.");
            return;
        }

        // Crear un JPanel para contener los JCheckBox
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Crear los JCheckBox para los servidores existentes
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
        for (String server : existingServers) {
            JCheckBox checkBox = new JCheckBox(server);
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }

        // Crear un JScrollPane para que la lista sea deslizable
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(200, 150)); // Tamaño pequeño y deslizable

        // Mostrar el diálogo para seleccionar el servidor a editar
        int option = JOptionPane.showConfirmDialog(this, scrollPane, "Select Server to Edit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            // Obtener el servidor seleccionado para editar
            String serverToEdit = null;
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    serverToEdit = checkBox.getText();  // El servidor seleccionado
                    break;  // Solo debe ser uno
                }
            }

            if (serverToEdit == null) {
                showMessage("No server was selected to edit.");
                return;
            }

            // Mostrar un cuadro de texto para editar el nombre del servidor
            String newServerName = JOptionPane.showInputDialog(this, "Enter new name for the server:", serverToEdit);

            if (newServerName != null && !newServerName.isEmpty()) {
                // Verificar si el nuevo nombre ya existe
                if (existingServers.contains(newServerName)) {
                    showMessage("The new server name already exists.");
                    return;
                }

                // Actualizar el servidor en la lista
                int index = existingServers.indexOf(serverToEdit);
                existingServers.set(index, newServerName);

                // Guardar la lista de servidores actualizada en el archivo
                writeServersToFile(existingServers, "Server updated successfully.");
            }
        }
    }


    private void deleteServer() {
        if (!validateCurrentGroupFile()) return;

        // Leer los servidores existentes del archivo
        ArrayList<String> servers = readServersFromFile();
        if (servers.isEmpty()) {
            showMessage("No servers found in the group file to delete.");
            return;
        }

        // Crear un JPanel para contener los JCheckBox
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Crear los JCheckBox para los servidores existentes
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
        for (String server : servers) {
            JCheckBox checkBox = new JCheckBox(server);
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }

        // Crear un JScrollPane para que la lista sea deslizable
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(200, 150)); // Tamaño pequeño y deslizable

        // Mostrar el diálogo para seleccionar servidores a eliminar
        int option = JOptionPane.showConfirmDialog(this, scrollPane, "Select Servers to Delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            // Obtener los servidores seleccionados para eliminar
            ArrayList<String> serversToDelete = new ArrayList<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {  // Si está seleccionado, lo marcamos para eliminar
                    serversToDelete.add(checkBox.getText());
                }
            }

            if (serversToDelete.isEmpty()) {
                showMessage("No servers were selected for deletion.");
                return;
            }

            // Eliminar los servidores seleccionados
            servers.removeAll(serversToDelete);

            // Guardar la lista de servidores actualizada en el archivo
            writeServersToFile(servers, "Servers deleted successfully.");
        }
    }


    private void editGroup() {
        if (!setCurrentGroupFromSelection("Select a group to rename:")) return;

        String newGroupNumber = getUserInput("Enter the new group number:");
        if (isEmpty(newGroupNumber)) return;

        File newGroupFile = new File(getSetupServerDirectory(), currentEnvironment + ".Group" + newGroupNumber + ".txt");
        if (newGroupFile.exists()) {
            showError("A group with this number already exists.");
            return;
        }

        if (currentGroupFile.renameTo(newGroupFile)) {
            currentGroupFile = newGroupFile;
            currentGroup = "Group " + newGroupNumber;
            updateEnvironmentLabel();
            showMessage("Group renamed successfully.");
        } else {
            showError("Error renaming group.");
        }
    }

    private void deleteGroup() {
        if (!setCurrentGroupFromSelection("Select a group to delete:")) return;
        if (!confirmDelete(currentGroupFile.getName())) return;

        if (currentGroupFile.delete()) {
            removeGroupFromEnvironmentFile(currentGroupFile.getName());
            resetCurrentGroup();
            showMessage("Group deleted successfully.");
        } else {
            showError("Error deleting group file.");
        }
    }

    private void resetCurrentGroup() {
        currentGroupFile = null;
        currentGroup = null;
        updateEnvironmentLabel();
    }

    private File getSetupServerDirectory() {
        String userHome = System.getProperty("user.home");
        String osName = System.getProperty("os.name").toLowerCase();
        File directory;

        if (osName.contains("win")) {
            directory = new File(userHome + "\\Documents\\StartServices\\SetupServer");
        } else if (osName.contains("mac")) {
            directory = new File(userHome + "/Documents/StartServices/SetupServer");
        } else {
            directory = new File(userHome + "/SetupServer");
        }

        if (!directory.exists()) {
            directory.mkdirs();
        }

        System.out.println("Directory path: " + directory.getAbsolutePath()); // Para verificar la ruta
        return directory;
    }

    private void appendToEnvironmentFile(String groupName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(getSetupServerDirectory(), currentEnvironment + ".txt"), true))) {
            writer.write(groupName + "\n");
        }
    }

    private boolean validateCurrentGroupFile() {
        if (currentGroupFile == null) {
            showMessage("Please select or create a group first.");
            return false;
        }
        return true;
    }

    private boolean setCurrentGroupFromSelection(String message) {
        String[] groupFiles = getGroupFiles();
        if (groupFiles == null || groupFiles.length == 0) {
            showMessage("No group files found.");
            return false;
        }

        String selectedGroupFile = (String) JOptionPane.showInputDialog(this, message, "Select Group", JOptionPane.QUESTION_MESSAGE, null, groupFiles, groupFiles[0]);
        if (selectedGroupFile != null) {
            currentGroupFile = new File(getSetupServerDirectory(), selectedGroupFile);
            currentGroup = selectedGroupFile;
            updateEnvironmentLabel();
            return true;
        }
        return false;
    }

    private String[] getGroupFiles() {
        File directory = getSetupServerDirectory();
        String environmentPrefix = currentEnvironment + ".Group";

        return directory.list((dir, name) ->
                name.startsWith(environmentPrefix) &&
                        name.matches(Pattern.quote(environmentPrefix) + "\\d+\\.txt")
        );
    }

    private void writeServersToFile(ArrayList<String> servers, String successMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentGroupFile))) {
            for (String server : servers) {
                writer.write(server);
                writer.newLine();
            }
            showMessage(successMessage);
        } catch (IOException e) {
            showError("Error writing to file: " + e.getMessage());
        }
    }

    private ArrayList<String> readServersFromFile() {
        ArrayList<String> servers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentGroupFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                servers.add(line);
            }
        } catch (IOException e) {
            showError("Error reading file: " + e.getMessage());
        }
        return servers;
    }

    private boolean environmentNotSelected() {
        if (currentEnvironment.equals("No environment selected")) {
            showMessage("Please select an environment first.");
            return true;
        }
        return false;
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private boolean confirmDelete(String itemName) {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + itemName + "?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
        return choice == JOptionPane.YES_OPTION;
    }

    private String getUserInput(String message) {
        return JOptionPane.showInputDialog(this, message);
    }

    private String getUserInput(String message, String defaultValue) {
        return JOptionPane.showInputDialog(this, message, defaultValue);
    }

    private String getUserSelection(String message, ArrayList<String> options) {
        return (String) JOptionPane.showInputDialog(this, message, "Select Option", JOptionPane.QUESTION_MESSAGE, null, options.toArray(), options.get(0));
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void removeGroupFromEnvironmentFile(String groupName) {
        File environmentFile = new File(getSetupServerDirectory(), currentEnvironment + ".txt");
        ArrayList<String> remainingGroups = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(environmentFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(groupName)) remainingGroups.add(line);
            }
        } catch (IOException e) {
            showError("Error reading environment file: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(environmentFile))) {
            for (String group : remainingGroups) {
                writer.write(group);
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Error updating environment file: " + e.getMessage());
        }
    }
}
