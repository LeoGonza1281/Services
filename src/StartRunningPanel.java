import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StartRunningPanel extends JPanel {
    private JComboBox<String> environmentComboBox; // ComboBox para seleccionar el entorno
    private JComboBox<String> groupComboBox;      // ComboBox para seleccionar el grupo
    private JComboBox<String> createServiceListComboBox; // ComboBox para seleccionar la lista de servicios
    private JButton runButton;                   // Botón para ejecutar el script
    private static final String BASE_DIR = System.getProperty("user.home") + "/Documents/StartServices/";

    // Constructor del panel
    public StartRunningPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Aumentar espaciado
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fuente grande para los componentes
        Font largeFont = new Font("Arial", Font.BOLD, 16);

        // Panel de Selección de "Environment"
        JLabel environmentLabel = new JLabel("Select Environment:");
        environmentLabel.setFont(largeFont);
        environmentComboBox = new JComboBox<>(getFilteredEnvironments());
        environmentComboBox.setFont(largeFont);
        environmentComboBox.setPreferredSize(new Dimension(400, 50)); // Tamaño más grande
        environmentComboBox.addActionListener(e -> updateGroupComboBox());
        addComponent(environmentLabel, gbc, 0, 0);
        addComponent(environmentComboBox, gbc, 1, 0);

        // Panel de Selección de "Group"
        JLabel groupLabel = new JLabel("Select Group:");
        groupLabel.setFont(largeFont);
        groupComboBox = new JComboBox<>();
        groupComboBox.setFont(largeFont);
        groupComboBox.setPreferredSize(new Dimension(400, 50)); // Tamaño más grande
        addComponent(groupLabel, gbc, 0, 1);
        addComponent(groupComboBox, gbc, 1, 1);

        // Panel de Selección de "Service List"
        JLabel createServiceLabel = new JLabel("Select Service List:");
        createServiceLabel.setFont(largeFont);
        createServiceListComboBox = new JComboBox<>(getFilteredCreateServiceLists());
        createServiceListComboBox.setFont(largeFont);
        createServiceListComboBox.setPreferredSize(new Dimension(400, 50)); // Tamaño más grande
        addComponent(createServiceLabel, gbc, 0, 2);
        addComponent(createServiceListComboBox, gbc, 1, 2);

        // Botón para ejecutar el script
        runButton = new JButton("Run Script");
        runButton.setFont(largeFont);
        runButton.setPreferredSize(new Dimension(200, 50)); // Tamaño más grande
        runButton.addActionListener(e -> runSelectedGroupScript());
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(runButton, gbc);
    }

    // Método para agregar componentes al panel con GridBagLayout
    private void addComponent(Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        add(comp, gbc);
    }

    public void updateAllComboBoxes() {
        environmentComboBox.setModel(new DefaultComboBoxModel<>(getFilteredEnvironments()));
        groupComboBox.removeAllItems();
        updateGroupComboBox();
        createServiceListComboBox.setModel(new DefaultComboBoxModel<>(getFilteredCreateServiceLists()));
    }

    private void updateGroupComboBox() {
        groupComboBox.removeAllItems();
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        if (selectedEnvironment != null) {
            File folder = new File(BASE_DIR + "SetupServer/");
            File[] files = folder.listFiles((dir, name) ->
                    name.startsWith(selectedEnvironment + ".") &&
                            name.endsWith(".txt") &&
                            name.split("\\.").length > 2
            );
            if (files != null) {
                for (File file : files) {
                    String groupName = file.getName().replace(".txt", "");
                    groupComboBox.addItem(groupName);
                }
            }
        }
    }

    private String[] getFilteredEnvironments() {
        File folder = new File(BASE_DIR + "SetupServer/");
        File[] files = folder.listFiles((dir, name) ->
                name.endsWith(".txt") &&
                        !name.equals("Environments.txt") &&
                        name.split("\\.").length == 2
        );
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

        String psScriptPath = BASE_DIR + "invokeServices.ps1";
        createPS1Script(psScriptPath, servers, services);

        if (executeScript(psScriptPath)) {
            JOptionPane.showMessageDialog(this, "Script executed successfully!");
        } else {
            showErrorDialog("Failed to execute the script.");
        }
    }

    private void createPS1Script(String psScriptPath, List<String> servers, List<String> services) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(psScriptPath))) {
            writer.write("param(\n");
            writer.write("    [string[]]$ComputerNames,\n");
            writer.write("    [string[]]$Services\n");
            writer.write(")\n\n");

            writer.write("\n# Lista de Servidores\n");
            writer.write("$ComputerNames = @(\n");
            for (String server : servers) {
                writer.write("    \"" + server + "\"\n");
            }
            writer.write(")\n");

            writer.write("\n# Lista de Servicios\n");
            writer.write("$Services = @(\n");
            for (String service : services) {
                writer.write("    \"" + service + "\"\n");
            }
            writer.write(")\n");

            writer.write("\n# Reiniciar los servicios en los servidores\n");
            writer.write("foreach ($service in $Services) {\n");
            writer.write("    try {\n");
            writer.write("        Write-Host \"Attempting to Restart $service on all servers: $ComputerNames\"\n");
            writer.write("        Invoke-Command -ComputerName $ComputerNames -ScriptBlock {\n");
            writer.write("            param($serviceName)\n");
            writer.write("            Restart-Service -Name $serviceName\n");
            writer.write("        } -ArgumentList $service\n");
            writer.write("    } catch {\n");
            writer.write("        Write-Error \"Failed to start $service on $ComputerNames\"\n");
            writer.write("    }\n");
            writer.write("}\n");
        } catch (IOException e) {
            showErrorDialog("Error creating PowerShell script: " + e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private boolean executeScript(String scriptPath) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) {
            showErrorDialog("This tool only works on Windows.");
            return false;
        }

        try {
            List<String> servers = readLinesFromFile(BASE_DIR + "SetupServer/" + groupComboBox.getSelectedItem() + ".txt");
            List<String> services = readLinesFromFile(BASE_DIR + "CreateServiceList/" + createServiceListComboBox.getSelectedItem() + ".txt");

            String serverList = String.join(",", servers);
            String serviceList = String.join(",", services);

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
}
