import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class GroupServerPanel extends JPanel {
    private JTextArea textArea;
    private JLabel environmentLabel;
    private File currentGroupFile;
    private String currentEnvironment;
    private String currentGroup;

    public GroupServerPanel() {
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));

        // Panel de Grupos
        JPanel groupPanel = createGroupPanel();
        // Panel de Servidores
        JPanel serverPanel = createServerPanel();

        // Configuración del panel izquierdo y área de texto
        leftPanel.add(groupPanel);
        leftPanel.add(serverPanel);
        textArea = new JTextArea();
        textArea.setEditable(false);

        // Etiqueta de entorno y scroll para texto
        environmentLabel = new JLabel("No environment selected", JLabel.CENTER);
        add(environmentLabel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        currentEnvironment = "No environment selected";
    }

    private JPanel createGroupPanel() {
        JPanel groupPanel = new JPanel(new GridLayout(4, 1));
        groupPanel.setBorder(BorderFactory.createTitledBorder("Grupos"));

        JButton addGroupButton = new JButton("Add");
        addGroupButton.addActionListener(e -> addGroup());

        JButton editGroupButton = new JButton("Edit");
        editGroupButton.addActionListener(e -> editGroup());

        JButton deleteGroupButton = new JButton("Delete");
        deleteGroupButton.addActionListener(e -> deleteGroup());

        groupPanel.add(addGroupButton);
        groupPanel.add(editGroupButton);
        groupPanel.add(deleteGroupButton);

        return groupPanel;
    }

    private JPanel createServerPanel() {
        JPanel serverPanel = new JPanel(new GridLayout(4, 1));
        serverPanel.setBorder(BorderFactory.createTitledBorder("Servidores"));

        JButton addServerButton = new JButton("Add Server");
        addServerButton.addActionListener(e -> addServer());

        JButton editServerButton = new JButton("Edit");
        editServerButton.addActionListener(e -> editServer());

        JButton deleteServerButton = new JButton("Delete Server");
        deleteServerButton.addActionListener(e -> deleteServer());

        serverPanel.add(addServerButton);
        serverPanel.add(editServerButton);
        serverPanel.add(deleteServerButton);

        return serverPanel;
    }

    void setEnvironmentName(String environmentName) {
        this.currentEnvironment = environmentName;
        updateEnvironmentLabel();
    }

    private void updateEnvironmentLabel() {
        String displayText = "Environment: " + currentEnvironment;
        if (currentGroup != null) {
            displayText += " | Group: " + currentGroup;
        }
        environmentLabel.setText(displayText);
    }

    private void addGroup() {
        if (currentEnvironment.equals("No environment selected")) {
            JOptionPane.showMessageDialog(this, "Please select an environment first.");
            return;
        }

        String groupNumber = JOptionPane.showInputDialog(this, "Enter the group number:");
        if (groupNumber == null || groupNumber.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid group number.");
            return;
        }

        String fileName = currentEnvironment + ".Group" + groupNumber + ".txt";
        File setupServerDirectory = getSetupServerDirectory();
        currentGroupFile = new File(setupServerDirectory, fileName);

        if (currentGroupFile.exists()) {
            JOptionPane.showMessageDialog(this, "A group with this number already exists: " + fileName);
            return;
        }

        try {
            if (currentGroupFile.createNewFile()) {
                JOptionPane.showMessageDialog(this, "Group file created: " + fileName);
                appendToEnvironmentFile("Group " + groupNumber);
                currentGroup = "Group " + groupNumber;
                updateEnvironmentLabel();
            } else {
                JOptionPane.showMessageDialog(this, "Error creating group file.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error creating group file: " + ex.getMessage());
        }
    }

    private void addServer() {
        String[] groupFiles = getGroupFiles();
        if (groupFiles == null || groupFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No groups found. Please create a group first.");
            return;
        }

        String selectedGroup = (String) JOptionPane.showInputDialog(this, "Select a group to add server:",
                "Add Server", JOptionPane.PLAIN_MESSAGE, null, groupFiles, groupFiles[0]);

        if (selectedGroup != null) {
            String groupNumber = selectedGroup.replace(currentEnvironment + ".Group", "").replace(".txt", "");
            currentGroupFile = new File(getSetupServerDirectory(), currentEnvironment + ".Group" + groupNumber + ".txt");
            currentGroup = "Group " + groupNumber;
            updateEnvironmentLabel();

            String newServer = JOptionPane.showInputDialog(this, "Enter server details to add:");
            if (newServer != null && !newServer.trim().isEmpty()) {
                try (FileWriter writer = new FileWriter(currentGroupFile, true)) {
                    writer.write(newServer + "\n");
                    JOptionPane.showMessageDialog(this, "Server added to " + currentGroup);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error adding server: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid server details.");
            }
        }
    }

    private void editServer() {
        if (currentGroupFile == null || !currentGroupFile.exists()) {
            JOptionPane.showMessageDialog(this, "No group selected or group file does not exist.");
            return;
        }

        ArrayList<String> servers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentGroupFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                servers.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading group file: " + e.getMessage());
            return;
        }

        if (servers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No servers found in the group file to edit.");
            return;
        }

        String selectedServer = (String) JOptionPane.showInputDialog(
                this, "Select a server to edit:", "Edit Server", JOptionPane.PLAIN_MESSAGE, null,
                servers.toArray(new String[0]), servers.get(0));

        if (selectedServer != null) {
            String newServerDetails = JOptionPane.showInputDialog(this, "Enter new details for the selected server:", selectedServer);

            if (newServerDetails != null && !newServerDetails.trim().isEmpty()) {
                int index = servers.indexOf(selectedServer);
                if (index >= 0) {
                    servers.set(index, newServerDetails);  // Reemplazar el servidor en la lista

                    try (FileWriter writer = new FileWriter(currentGroupFile)) {
                        for (String server : servers) {
                            writer.write(server + "\n");  // Reescribir todos los servidores
                        }
                        JOptionPane.showMessageDialog(this, "Server details updated successfully.");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, "Error updating server details: " + e.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid server details.");
            }
        }
    }


    private void deleteServer() {
        if (currentGroupFile == null || !currentGroupFile.exists()) {
            JOptionPane.showMessageDialog(this, "No group selected or group file does not exist.");
            return;
        }

        ArrayList<String> servers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentGroupFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                servers.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading group file: " + e.getMessage());
            return;
        }

        if (servers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No servers found in the group file to delete.");
            return;
        }

        String selectedServer = (String) JOptionPane.showInputDialog(
                this, "Select a server to delete:", "Delete Server",
                JOptionPane.PLAIN_MESSAGE, null, servers.toArray(new String[0]), servers.get(0));

        if (selectedServer != null) {
            servers.remove(selectedServer);

            try (FileWriter writer = new FileWriter(currentGroupFile)) {
                for (String server : servers) {
                    writer.write(server + "\n");
                }
                JOptionPane.showMessageDialog(this, "Server deleted successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error deleting server: " + e.getMessage());
            }
        }
    }

    private void editGroup() {
        String[] groupFiles = getGroupFiles();
        if (groupFiles == null || groupFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No groups found to edit. Please create a group first.");
            return;
        }

        // Selecciona el archivo de grupo que deseas renombrar
        String selectedGroup = (String) JOptionPane.showInputDialog(
                this, "Select a group to rename:", "Edit Group",
                JOptionPane.PLAIN_MESSAGE, null, groupFiles, groupFiles[0]);

        if (selectedGroup != null) {
            // Pedimos el nuevo número del grupo al usuario
            String newGroupNumber = JOptionPane.showInputDialog(this, "Enter the new group number:");
            if (newGroupNumber == null || newGroupNumber.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid group number.");
                return;
            }

            // Construimos el nombre del nuevo archivo
            File oldGroupFile = new File(getSetupServerDirectory(), selectedGroup);
            File newGroupFile = new File(getSetupServerDirectory(), currentEnvironment + ".Group" + newGroupNumber + ".txt");

            // Verificamos si el archivo nuevo ya existe
            if (newGroupFile.exists()) {
                JOptionPane.showMessageDialog(this, "A group with this number already exists.");
                return;
            }

            // Renombramos el archivo
            if (oldGroupFile.renameTo(newGroupFile)) {
                JOptionPane.showMessageDialog(this, "Group renamed successfully.");
                currentGroupFile = newGroupFile;  // Actualizamos el archivo actual
                currentGroup = "Group " + newGroupNumber;  // Actualizamos el nombre del grupo
                updateEnvironmentLabel();  // Refrescamos la etiqueta del entorno
            } else {
                JOptionPane.showMessageDialog(this, "Error renaming group.");
            }
        }
    }


    private void deleteGroup() {
        String[] groupFiles = getGroupFiles();
        if (groupFiles == null || groupFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No groups found to delete.");
            return;
        }

        String selectedGroup = (String) JOptionPane.showInputDialog(this, "Select a group to delete:",
                "Delete Group", JOptionPane.PLAIN_MESSAGE, null, groupFiles, groupFiles[0]);

        if (selectedGroup != null) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + selectedGroup + "?",
                    "Delete Group", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                File groupFile = new File(getSetupServerDirectory(), selectedGroup);
                if (groupFile.delete()) {
                    JOptionPane.showMessageDialog(this, "Group deleted successfully.");
                    if (groupFile.equals(currentGroupFile)) {
                        currentGroupFile = null;
                        currentGroup = null;
                        updateEnvironmentLabel();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting group file.");
                }
            }
        }
    }

    private String[] getGroupFiles() {
        File setupServerDirectory = getSetupServerDirectory();
        return setupServerDirectory.list((dir, name) -> name.startsWith(currentEnvironment + ".Group") && name.endsWith(".txt"));
    }

    private void appendToEnvironmentFile(String groupName) {
        File environmentFile = new File(getSetupServerDirectory(), currentEnvironment + ".txt");
        try (FileWriter writer = new FileWriter(environmentFile, true)) {
            writer.write(groupName + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to environment file: " + e.getMessage());
        }
    }

    private File getSetupServerDirectory() {
        File setupServerDirectory = new File("SetupServer");
        if (!setupServerDirectory.exists()) {
            setupServerDirectory.mkdir();
        }
        return setupServerDirectory;
    }
}
