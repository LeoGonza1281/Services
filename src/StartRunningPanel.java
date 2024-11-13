import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StartRunningPanel extends JPanel {
    private JComboBox<String> environmentComboBox;
    private JComboBox<String> groupComboBox;
    private JComboBox<String> createServiceListComboBox;
    private JButton runButton;
    private JButton refreshButton;
    private static final String BASE_DIR = System.getProperty("user.home") + "/Documents/StartServices/";

    // Constructor
    public StartRunningPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel de Selección de "Environment"
        JLabel environmentLabel = new JLabel("Select Environment:");
        environmentComboBox = new JComboBox<>(getFilteredEnvironments());
        environmentComboBox.addActionListener(e -> updateGroupComboBox());
        addComponent(environmentLabel, gbc, 0, 0);
        addComponent(environmentComboBox, gbc, 1, 0);

        // Panel de Selección de "Group"
        JLabel groupLabel = new JLabel("Select Group:");
        groupComboBox = new JComboBox<>();
        addComponent(groupLabel, gbc, 0, 1);
        addComponent(groupComboBox, gbc, 1, 1);

        // Panel de Selección de "Select List"
        JLabel createServiceLabel = new JLabel("Select List:");
        createServiceListComboBox = new JComboBox<>(getFilteredCreateServiceLists());
        addComponent(createServiceLabel, gbc, 0, 2);
        addComponent(createServiceListComboBox, gbc, 1, 2);

        // Botón para actualizar los ComboBoxes
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshComboBoxes());
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(refreshButton, gbc);

        // Botón para ejecutar el script
        runButton = new JButton("Run Script");
        runButton.addActionListener(e -> runSelectedGroupScript());
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(runButton, gbc);
    }

    private void addComponent(Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        add(comp, gbc);
    }

    private void refreshComboBoxes() {
        updateEnvironmentComboBox();
        updateCreateServiceListComboBox();
    }

    private void updateEnvironmentComboBox() {
        environmentComboBox.removeAllItems();
        String[] environments = getFilteredEnvironments();
        for (String environment : environments) {
            environmentComboBox.addItem(environment);
        }
        if (environmentComboBox.getItemCount() > 0) {
            environmentComboBox.setSelectedIndex(0);
            updateGroupComboBox();
        }
    }

    private void updateCreateServiceListComboBox() {
        createServiceListComboBox.removeAllItems();
        String[] serviceLists = getFilteredCreateServiceLists();
        for (String list : serviceLists) {
            createServiceListComboBox.addItem(list);
        }
    }

    private String[] getFilteredEnvironments() {
        File folder = new File(BASE_DIR + "SetupServer/");
        File[] files = folder.listFiles((dir, name) -> name.matches("[A-Za-z0-9_]+\\.txt") && !name.equalsIgnoreCase("Environments.txt"));
        return getFileNamesWithoutExtension(files);
    }

    private String[] getFilteredCreateServiceLists() {
        File folder = new File(BASE_DIR + "CreateServiceList/");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt") && !name.equalsIgnoreCase("List.txt"));
        return getFileNamesWithoutExtension(files);
    }

    private String[] getFileNamesWithoutExtension(File[] files) {
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName().replace(".txt", ""));
            }
        }
        return fileNames.toArray(new String[0]);
    }

    private void updateGroupComboBox() {
        groupComboBox.removeAllItems();
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        if (selectedEnvironment != null) {
            List<String> groups = readLinesFromFile(BASE_DIR + "SetupServer/" + selectedEnvironment + ".txt");
            for (String group : groups) {
                if (group.endsWith(".txt")) {
                    group = group.replace(".txt", "");
                }
                groupComboBox.addItem(group);
            }
        }
    }

    private List<String> readLinesFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            showErrorDialog("Error reading file: " + filePath);
        }
        return lines;
    }

    private void runSelectedGroupScript() {
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        String selectedGroup = (String) groupComboBox.getSelectedItem();
        String selectedServiceList = (String) createServiceListComboBox.getSelectedItem();

        if (selectedEnvironment == null || selectedGroup == null || selectedServiceList == null) {
            showErrorDialog("Please select environment, group, and service list.");
            return;
        }

        String groupFilePath = BASE_DIR + "SetupServer/" + selectedGroup + ".txt";
        List<String> servers = readLinesFromFile(groupFilePath);
        List<String> services = readLinesFromFile(BASE_DIR + "CreateServiceList/" + selectedServiceList + ".txt");

        if (servers.isEmpty() || services.isEmpty()) {
            showErrorDialog("No servers or services found.");
            return;
        }

        String scriptPath = BASE_DIR + "runServices.sh";
        createScript(scriptPath, servers, services);

        if (executeScript(scriptPath)) {
            JOptionPane.showMessageDialog(this, "Script executed successfully!");
        } else {
            showErrorDialog("Failed to execute the script.");
        }
    }

    private String getOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "windows";
        if (os.contains("mac")) return "mac";
        if (os.contains("nix") || os.contains("nux") || os.contains("aix")) return "linux";
        return "unknown";
    }

    private boolean executeScript(String scriptPath) {
        String os = getOperatingSystem();
        ProcessBuilder pb;
        try {
            if (!os.equals("windows")) {
                new ProcessBuilder("chmod", "+x", scriptPath).start().waitFor();
            }

            pb = os.equals("windows") ?
                    new ProcessBuilder("cmd.exe", "/c", scriptPath) :
                    new ProcessBuilder("/bin/bash", scriptPath);

            pb.inheritIO();
            return pb.start().waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            showErrorDialog("Error executing script: " + e.getMessage());
            return false;
        }
    }

    private void createScript(String scriptPath, List<String> servers, List<String> services) {
        String os = getOperatingSystem();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scriptPath))) {
            if (os.equals("windows")) writer.write("@echo off\n");
            else writer.write("#!/bin/bash\n");

            StringBuilder serverList = new StringBuilder();
            for (String server : servers) serverList.append(server).append(",");
            if (serverList.length() > 0) serverList.setLength(serverList.length() - 1);

            String psScriptPath = BASE_DIR + "invokeServices.ps1";
            if (os.equals("windows")) {
                writer.write("powershell.exe Invoke-Command -ComputerName " + serverList + " -FilePath " + psScriptPath + "\n");
            } else {
                writer.write("for server in " + serverList + "; do\n");
                writer.write("  echo \"Starting services on $server\"\n");
            }

            for (String service : services) {
                for (String server : servers) {
                    if (os.equals("windows")) {
                        writer.write("powershell.exe -Command \"Start-Service -Name '" + service + "' -ComputerName '" + server + "'\"\n");
                    } else {
                        writer.write("ssh " + server + " 'sudo systemctl start " + service + "'\n");
                    }
                }
            }
            writer.flush();
        } catch (IOException e) {
            showErrorDialog("Error creating script: " + e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
