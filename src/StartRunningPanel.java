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

        // Panel de Selección de "Service List"
        JLabel createServiceLabel = new JLabel("Select Service List:");
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

        // Crear el archivo invokeServices.ps1
        String psScriptPath = BASE_DIR + "invokeServices.ps1";
        createPS1Script(psScriptPath, servers, services);

        // Llamar al script generado
        if (executeScript(psScriptPath)) {
            JOptionPane.showMessageDialog(this, "Script executed successfully!");
        } else {
            showErrorDialog("Failed to execute the script.");
        }
    }

    private void createPS1Script(String psScriptPath, List<String> servers, List<String> services) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(psScriptPath))) {
            // Encabezado de parámetros
            writer.write("param(\n");
            writer.write("    [string[]]$ComputerNames,\n");
            writer.write("    [string[]]$Services\n");
            writer.write(")\n\n");

            // Usar Invoke-Command para iniciar servicios remotamente
            writer.write("foreach ($service in $Services) {\n");
            writer.write("    Invoke-Command -ComputerName $ComputerNames -ScriptBlock {\n");
            writer.write("        param($serviceName)\n");
            writer.write("        try {\n");
            writer.write("            Start-Service -Name $serviceName\n");
            writer.write("            Write-Host \"Started $serviceName on $env:COMPUTERNAME\"\n");
            writer.write("        } catch {\n");
            writer.write("            Write-Error \"Failed to start $serviceName on $env:COMPUTERNAME\"\n");
            writer.write("        }\n");
            writer.write("    } -ArgumentList $service\n");
            writer.write("}\n");

            writer.flush();
        } catch (IOException e) {
            showErrorDialog("Error creating PowerShell script: " + e.getMessage());
        }
    }

    private boolean executeScript(String scriptPath) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) {
            showErrorDialog("This tool only works on Windows.");
            return false;
        }

        try {
            // Convertir servidores y servicios en formato de lista para PowerShell
            List<String> servers = readLinesFromFile(BASE_DIR + "SetupServer/" + groupComboBox.getSelectedItem() + ".txt");
            List<String> services = readLinesFromFile(BASE_DIR + "CreateServiceList/" + createServiceListComboBox.getSelectedItem() + ".txt");

            String serverList = String.join(",", servers);
            String serviceList = String.join(",", services);

            // Ejecutar PowerShell
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-ExecutionPolicy", "Bypass",
                    "-File", scriptPath,
                    "-ComputerNames", "@(" + serverList + ")",
                    "-Services", "@(" + serviceList + ")"
            );
            pb.inheritIO();
            return pb.start().waitFor() == 0;

        } catch (IOException | InterruptedException e) {
            showErrorDialog("Error executing script: " + e.getMessage());
            return false;
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
