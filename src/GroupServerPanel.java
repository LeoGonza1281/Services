import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
        JPanel groupPanel = new JPanel(new GridLayout(4, 1));
        groupPanel.setBorder(BorderFactory.createTitledBorder("Grupos"));
        JButton addGroupButton = new JButton("Add");
        JButton editGroupButton = new JButton("Edit");
        JButton deleteGroupButton = new JButton("Delete");

        groupPanel.add(addGroupButton);
        groupPanel.add(editGroupButton);
        groupPanel.add(deleteGroupButton);

        // Panel de Servidores
        JPanel serverPanel = new JPanel(new GridLayout(4, 1));
        serverPanel.setBorder(BorderFactory.createTitledBorder("Servidores"));
        JButton addServerButton = new JButton("AddServer");
        JButton editServerButton = new JButton("Edit");
        JButton deleteServerButton = new JButton("Delete");

        serverPanel.add(addServerButton);
        serverPanel.add(editServerButton);
        serverPanel.add(deleteServerButton);

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

        // Acción de botones
        addGroupButton.addActionListener(e -> addGroup());
        editGroupButton.addActionListener(e -> editGroup());
        deleteGroupButton.addActionListener(e -> deleteGroup());
        addServerButton.addActionListener(e -> addServer());

        currentEnvironment = "No environment selected";
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
                loadFileContent(currentGroupFile.getPath());
                appendToEnvironmentFile("Grupo " + groupNumber);
                currentGroup = "Group " + groupNumber;
                updateEnvironmentLabel();
            } else {
                JOptionPane.showMessageDialog(this, "Error creating group file.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error creating group file: " + ex.getMessage());
        }
    }

    public void loadFileContent(String path) {
        textArea.setText("");
        File file = new File(path);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Error: The file does not exist at the specified path: " + path);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            textArea.setText(content.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
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
                    loadFileContent(currentGroupFile.getPath());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error adding server: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid server details.");
            }
        }
    }

    private void editGroup() {
        // Implementation remains the same
    }

    private void deleteGroup() {
        // Implementation remains the same
    }

    private File getSetupServerDirectory() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        String setupServerPath;

        if (os.contains("win")) {
            setupServerPath = userHome + "\\Documents\\StartServices\\SetupServer"; // Windows
        } else {
            setupServerPath = userHome + "/Documents/StartServices/SetupServer"; // macOS
        }

        File setupServerDirectory = new File(setupServerPath);
        if (!setupServerDirectory.exists()) {
            setupServerDirectory.mkdirs();
        }
        return setupServerDirectory;
    }

    private void appendToEnvironmentFile(String content) {
        File environmentFile = new File(getSetupServerDirectory(), currentEnvironment + ".txt");
        try (FileWriter writer = new FileWriter(environmentFile, true)) {
            writer.write(content + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to environment file: " + e.getMessage());
        }
    }

    private String[] getGroupFiles() {
        File directory = getSetupServerDirectory();
        return directory.list((dir, name) -> name.startsWith(currentEnvironment + ".Group") && name.endsWith(".txt"));
    }

    private String getCurrentEnvironment() {
        return currentEnvironment;
    }
}
