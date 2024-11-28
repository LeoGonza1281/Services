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

        String groupName = getUserInput("Enter the group name:");
        if (isEmpty(groupName)) return;

        String fileName = currentEnvironment + "." + groupName + ".txt";
        currentGroupFile = new File(getSetupServerDirectory(), fileName);

        if (currentGroupFile.exists()) {
            showError("A group with this name already exists: " + fileName);
            return;
        }

        try {
            if (currentGroupFile.createNewFile()) {
                appendToEnvironmentFile(fileName);
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

        File newGroupFile = new File(getSetupServerDirectory(), currentEnvironment + "." + newGroupName + ".txt");
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
        if (!confirmDelete(currentGroupFile.getName())) return;

        if (currentGroupFile.delete()) {
            removeGroupFromEnvironmentFile(currentGroupFile.getName());
            resetCurrentGroup();
            showMessage("Group deleted successfully.");
        } else {
            showError("Error deleting group file.");
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
            return true;
        }
        return false;
    }

    private String[] getGroupFiles() {
        File directory = getSetupServerDirectory();
        String pattern = Pattern.quote(currentEnvironment + ".") + "[^\\.]+\\.txt";
        return directory.list((dir, name) -> name.matches(pattern));
    }


    private ArrayList<String> readServersFromFile() {
        ArrayList<String> servers = new ArrayList<>();
        if (!validateCurrentGroupFile()) return servers;

        try (BufferedReader reader = new BufferedReader(new FileReader(currentGroupFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                servers.add(line.trim());
            }
        } catch (IOException ex) {
            showError("Error reading servers from file: " + ex.getMessage());
        }
        return servers;
    }

    private void writeServersToFile(ArrayList<String> servers, String successMessage) {
        try (PrintWriter writer = new PrintWriter(currentGroupFile)) {
            for (String server : servers) {
                writer.println(server);
            }
            showMessage(successMessage);
        } catch (IOException ex) {
            showError("Error writing servers to file: " + ex.getMessage());
        }
    }

    private void appendToEnvironmentFile(String groupFileName) {
        File environmentFile = getEnvironmentFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(environmentFile, true))) {
            writer.println(groupFileName);
        } catch (IOException ex) {
            showError("Error appending to environment file: " + ex.getMessage());
        }
    }

    private void removeGroupFromEnvironmentFile(String groupFileName) {
        File environmentFile = getEnvironmentFile();
        ArrayList<String> entries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(environmentFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(groupFileName)) {
                    entries.add(line);
                }
            }
        } catch (IOException ex) {
            showError("Error reading environment file: " + ex.getMessage());
            return;
        }

        try (PrintWriter writer = new PrintWriter(environmentFile)) {
            for (String entry : entries) {
                writer.println(entry);
            }
        } catch (IOException ex) {
            showError("Error writing to environment file: " + ex.getMessage());
        }
    }

    private boolean validateCurrentGroupFile() {
        if (currentGroupFile == null || !currentGroupFile.exists()) {
            showError("No valid group file selected.");
            return false;
        }
        return true;
    }

    private File getEnvironmentFile() {
        return new File(getSetupServerDirectory(), currentEnvironment + ".txt");
    }

    private File getSetupServerDirectory() {
        return new File(System.getProperty("user.home") + "\\Documents\\StartServices\\SetupServer");
    }

    private void resetCurrentGroup() {
        currentGroup = null;
        currentGroupFile = null;
        updateEnvironmentLabel();
    }

    private boolean confirmDelete(String itemName) {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + itemName + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }

    private String getUserInput(String message) {
        return JOptionPane.showInputDialog(this, message);
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private boolean environmentNotSelected() {
        if (currentEnvironment.equals("No environment selected")) {
            showError("No environment selected.");
            return true;
        }
        return false;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
