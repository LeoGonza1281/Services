import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.Files;
import java.io.IOException;


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

        environmentLabel = new JLabel(currentEnvironment, SwingConstants.CENTER);
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

        String groupName = getUserInput("Enter the group name:");
        if (isEmpty(groupName)) return;

        // Eliminar espacios en blanco
        groupName = groupName.trim().replaceAll("\\s+", "");

        if (groupName.isEmpty()) {
            showError("Group name cannot be empty after removing spaces.");
            return;
        }

        String fileName = currentEnvironment + "." + groupName + ".txt";
        currentGroupFile = new File(getSetupServerDirectory(), fileName);

        if (currentGroupFile.exists()) {
            showError("A group with this name already exists: " + fileName);
            return;
        }

        try {
            if (currentGroupFile.createNewFile()) {
                currentGroup = groupName;
                updateEnvironmentLabel();
                showMessage("Group file created: " + fileName);
            } else {
                showError("Error creating group file.");
            }
        } catch (IOException ex) {
            showError("Error creating group file: " + ex.getMessage());
        }
    }

    private void editGroup() {
        if (!setCurrentGroupFromSelection("Select a group to rename:")) return;

        String newGroupName = getUserInput("Enter the new group name:");
        if (isEmpty(newGroupName)) return;

        String newFileName = currentEnvironment + "." + newGroupName + ".txt";
        File newGroupFile = new File(getSetupServerDirectory(), newFileName);

        if (newGroupFile.exists()) {
            showError("A group with this name already exists.");
            return;
        }

        if (currentGroupFile.renameTo(newGroupFile)) {
            currentGroupFile = newGroupFile;
            currentGroup = newGroupName;
            updateEnvironmentLabel();
            showMessage("Group renamed successfully.");
        } else {
            showError("Error renaming group.");
        }
    }

    private void deleteGroup() {
        if (!setCurrentGroupFromSelection("Select a group to delete:")) return;

        if (!currentGroupFile.getName().startsWith(currentEnvironment + ".")) {
            showError("Invalid group format for deletion.");
            return;
        }

        if (!confirmDelete(currentGroupFile.getName())) return;

        try {
            Files.delete(currentGroupFile.toPath());
            resetCurrentGroup();
            showMessage("Group deleted successfully.");
        } catch (IOException ex) {
            showError("Error deleting group file: " + ex.getMessage());
        }
    }


    private void addServer() {
        if (!setCurrentGroupFromSelection("Select a group to add server:")) return;

        String serverInput = JOptionPane.showInputDialog(this, "Enter server names separated by commas (e.g., Server01, Server02):");
        if (serverInput == null || serverInput.trim().isEmpty()) {
            showMessage("No servers were entered.");
            return;
        }

        String[] serverArray = serverInput.split("\\s*,\\s*");
        ArrayList<String> existingServers = readServersFromFile();

        ArrayList<String> newServers = new ArrayList<>();
        for (String server : serverArray) {
            if (!existingServers.contains(server)) {
                newServers.add(server);
            }
        }

        if (newServers.isEmpty()) {
            showMessage("No new servers were added.");
            return;
        }

        existingServers.addAll(newServers);
        writeServersToFile(existingServers, "Servers added successfully.");
    }

    private void editServer() {
        if (!setCurrentGroupFromSelection("Select a group to edit server:")) return;

        ArrayList<String> existingServers = readServersFromFile();
        if (existingServers.isEmpty()) {
            showMessage("No servers to edit.");
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

        for (String server : existingServers) {
            JCheckBox checkBox = new JCheckBox(server);
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(200, 150));

        int option = JOptionPane.showConfirmDialog(this, scrollPane, "Select Server to Edit", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String serverToEdit = null;
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    serverToEdit = checkBox.getText();
                    break;
                }
            }

            if (serverToEdit == null) {
                showMessage("No server was selected to edit.");
                return;
            }

            String newServerName = JOptionPane.showInputDialog(this, "Enter new name for the server:", serverToEdit);
            if (newServerName != null && !newServerName.isEmpty() && !existingServers.contains(newServerName)) {
                int index = existingServers.indexOf(serverToEdit);
                existingServers.set(index, newServerName);
                writeServersToFile(existingServers, "Server updated successfully.");
            } else {
                showMessage("The new server name is invalid or already exists.");
            }
        }
    }

    private void deleteServer() {
        if (!validateCurrentGroupFile()) return;

        ArrayList<String> servers = readServersFromFile();
        if (servers.isEmpty()) {
            showMessage("No servers found to delete.");
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

        for (String server : servers) {
            JCheckBox checkBox = new JCheckBox(server);
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(200, 150));

        int option = JOptionPane.showConfirmDialog(this, scrollPane, "Select Servers to Delete", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            ArrayList<String> serversToDelete = new ArrayList<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    serversToDelete.add(checkBox.getText());
                }
            }

            if (!serversToDelete.isEmpty()) {
                servers.removeAll(serversToDelete);
                writeServersToFile(servers, "Servers deleted successfully.");
            } else {
                showMessage("No servers were selected for deletion.");
            }
        }
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
            currentGroup = selectedGroupFile.replace(currentEnvironment + ".", "").replace(".txt", "");
            updateEnvironmentLabel();
            showGroupContentInTextArea(); // Mostrar contenido en el panel de texto
            return true;
        }
        return false;
    }

    private void showGroupContentInTextArea() {
        if (!validateCurrentGroupFile()) return;

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentGroupFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException ex) {
            showError("Error reading file: " + ex.getMessage());
        }
        textArea.setText(content.toString());
    }

    private ArrayList<String> readServersFromFile() {
        ArrayList<String> servers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentGroupFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                servers.add(line);
            }
        } catch (IOException ex) {
            showError("Error reading file: " + ex.getMessage());
        }
        return servers;
    }

    private void writeServersToFile(ArrayList<String> servers, String successMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentGroupFile))) {
            for (String server : servers) {
                writer.write(server);
                writer.newLine();
            }
            showMessage(successMessage);
            showGroupContentInTextArea();
        } catch (IOException ex) {
            showError("Error writing file: " + ex.getMessage());
        }
    }

    private String[] getGroupFiles() {
        File setupServerDir = getSetupServerDirectory();

        // Filtrar solo los archivos con formato [environment].[groupname].txt
        return setupServerDir.list((dir, name) ->
                name.startsWith(currentEnvironment + ".") &&
                        name.endsWith(".txt") &&
                        !name.equals(currentEnvironment + ".txt") // Excluir [environment].txt
        );
    }


    private File getSetupServerDirectory() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, "Documents/StartServices/SetupServer");
    }

    private boolean validateCurrentGroupFile() {
        if (currentGroupFile == null) {
            showMessage("No group selected.");
            return false;
        }
        if (!currentGroupFile.exists()) {
            showMessage("Group file does not exist.");
            return false;
        }
        return true;
    }

    private void resetCurrentGroup() {
        currentGroupFile = null;
        currentGroup = null;
        updateEnvironmentLabel();
        textArea.setText("");
    }

    private boolean environmentNotSelected() {
        if (currentEnvironment == null || currentEnvironment.isEmpty() || currentEnvironment.equals("No environment selected")) {
            showMessage("No environment selected.");
            return true;
        }
        return false;
    }

    private boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String getUserInput(String message) {
        return JOptionPane.showInputDialog(this, message);
    }

    private boolean confirmDelete(String name) {
        return JOptionPane.showConfirmDialog(this, "Do you really want to delete " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}

