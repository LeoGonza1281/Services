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
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel de Selección de "Environment"
        JLabel environmentLabel = new JLabel("Select Environment:");
        environmentComboBox = new JComboBox<>(getFilteredEnvironments()); // Inicializa el ComboBox con los entornos filtrados
        environmentComboBox.addActionListener(e -> updateGroupComboBox()); // Actualiza los grupos automáticamente al cambiar el entorno
        addComponent(environmentLabel, gbc, 0, 0);
        addComponent(environmentComboBox, gbc, 1, 0);

        // Panel de Selección de "Group"
        JLabel groupLabel = new JLabel("Select Group:");
        groupComboBox = new JComboBox<>();
        addComponent(groupLabel, gbc, 0, 1);
        addComponent(groupComboBox, gbc, 1, 1);

        // Panel de Selección de "Service List"
        JLabel createServiceLabel = new JLabel("Select Service List:");
        createServiceListComboBox = new JComboBox<>(getFilteredCreateServiceLists()); // Inicializa el ComboBox con las listas de servicios filtradas
        addComponent(createServiceLabel, gbc, 0, 2);
        addComponent(createServiceListComboBox, gbc, 1, 2);

        // Botón para ejecutar el script
        runButton = new JButton("Run Script");
        runButton.addActionListener(e -> runSelectedGroupScript()); // Ejecuta el script cuando se hace clic en el botón
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

    // Actualiza el ComboBox de "Group" basado en el entorno seleccionado
    private void updateGroupComboBox() {
        groupComboBox.removeAllItems();  // Limpia el ComboBox de grupos
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();  // Obtiene el entorno seleccionado

        if (selectedEnvironment != null) {
            File folder = new File(BASE_DIR + "SetupServer/");
            File[] files = folder.listFiles((dir, name) ->
                    name.startsWith(selectedEnvironment + ".") &&  // Filtra los archivos por el nombre del entorno
                            name.endsWith(".txt") &&
                            name.split("\\.").length > 2  // Asegura que el archivo tiene el formato adecuado
            );

            if (files != null) {
                for (File file : files) {
                    String groupName = file.getName().replace(".txt", "");  // Extrae el nombre del grupo sin la extensión
                    groupComboBox.addItem(groupName);  // Agrega los grupos al ComboBox
                }
            }
        }
    }

    // Obtiene los entornos filtrados desde el directorio SetupServer
    private String[] getFilteredEnvironments() {
        File folder = new File(BASE_DIR + "SetupServer/");
        File[] files = folder.listFiles((dir, name) -> name.matches("[A-Za-z0-9_]+\\.txt") && !name.equalsIgnoreCase("Environments.txt"));
        return getFileNamesWithoutExtension(files);  // Extrae solo los nombres de los archivos sin la extensión ".txt"
    }

    // Obtiene las listas de servicios filtradas desde el directorio CreateServiceList
    private String[] getFilteredCreateServiceLists() {
        File folder = new File(BASE_DIR + "CreateServiceList/");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt") && !name.equalsIgnoreCase("List.txt"));
        return getFileNamesWithoutExtension(files);  // Extrae solo los nombres de los archivos sin la extensión ".txt"
    }

    // Extrae los nombres de los archivos sin la extensión ".txt"
    private String[] getFileNamesWithoutExtension(File[] files) {
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName().replace(".txt", ""));  // Elimina la extensión ".txt"
            }
        }
        return fileNames.toArray(new String[0]);
    }

    // Lee las líneas de un archivo y las devuelve como una lista
    private List<String> readLinesFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());  // Agrega cada línea del archivo a la lista
            }
        } catch (IOException e) {
            showErrorDialog("Error reading file: " + filePath);  // Muestra un error si no se puede leer el archivo
        }
        return lines;
    }

    // Ejecuta el script seleccionado en función de los entornos, grupos y listas de servicios
    private void runSelectedGroupScript() {
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        String selectedGroup = (String) groupComboBox.getSelectedItem();
        String selectedServiceList = (String) createServiceListComboBox.getSelectedItem();

        if (selectedEnvironment == null || selectedGroup == null || selectedServiceList == null) {
            showErrorDialog("Please select environment, group, and service list.");  // Muestra un error si no se seleccionan todos los elementos
            return;
        }

        String groupFilePath = BASE_DIR + "SetupServer/" + selectedGroup + ".txt";
        List<String> servers = readLinesFromFile(groupFilePath);  // Lee los servidores del archivo seleccionado
        List<String> services = readLinesFromFile(BASE_DIR + "CreateServiceList/" + selectedServiceList + ".txt");  // Lee los servicios del archivo seleccionado

        if (servers.isEmpty() || services.isEmpty()) {
            showErrorDialog("No servers or services found.");  // Muestra un error si no se encuentran servidores o servicios
            return;
        }

        // Crear el archivo PowerShell invokeServices.ps1
        String psScriptPath = BASE_DIR + "invokeServices.ps1";
        createPS1Script(psScriptPath, servers, services);  // Crea el script PowerShell

        // Ejecuta el script generado
        if (executeScript(psScriptPath)) {
            JOptionPane.showMessageDialog(this, "Script executed successfully!");  // Muestra un mensaje de éxito
        } else {
            showErrorDialog("Failed to execute the script.");  // Muestra un error si falla la ejecución
        }
    }

    // Crea el archivo PowerShell que invoca los servicios en los servidores
    private void createPS1Script(String psScriptPath, List<String> servers, List<String> services) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(psScriptPath))) {
            // Escribimos los parámetros del script
            writer.write("param(\n");
            writer.write("    [string[]]$ComputerNames,\n");
            writer.write("    [string[]]$Services\n");
            writer.write(")\n\n");

            // Escribimos los servidores en el script
            writer.write("\n# Lista de Servidores\n");
            writer.write("$ComputerNames = @(\n");
            for (int i = 0; i < servers.size(); i++) {
                writer.write("    \"" + servers.get(i) + "\"");
                if (i < servers.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }
            writer.write(")\n");

            // Escribimos los servicios en el script
            writer.write("\n# Lista de Servicios\n");
            writer.write("$Services = @(\n");
            for (int i = 0; i < services.size(); i++) {
                writer.write("    \"" + services.get(i) + "\"");
                if (i < services.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }
            writer.write(")\n");

            writer.write("\n# Iniciar los servicios en los servidores\n");
            writer.write("foreach ($service in $Services) {\n");
            writer.write("    foreach ($server in $ComputerNames) {\n");
            writer.write("        try {\n");

            writer.write("            # Imprimir el servicio y servidor que se está utilizando\n");
            writer.write("            Write-Host \"Attempting to start $service on $server\"\n");

            writer.write("            Invoke-Command -ComputerName $server -ScriptBlock {\n");
            writer.write("                param($serviceName)\n");
            writer.write("                Write-Host \"Starting service: $serviceName\"\n");
            writer.write("                Stop-Service -Name $serviceName\n");
            writer.write("                Start-Service -Name $serviceName\n");
            writer.write("                Write-Host \"Started $serviceName on $env:COMPUTERNAME\"\n");
            writer.write("            } -ArgumentList $service\n");

            writer.write("        } catch {\n");
            writer.write("            Write-Error \"Failed to start $service on $server\"\n");
            writer.write("        }\n");
            writer.write("    }\n");
            writer.write("}\n");

            writer.flush();  // Asegurarse de que el contenido esté escrito al archivo
        } catch (IOException e) {
            showErrorDialog("Error creating PowerShell script: " + e.getMessage());  // Muestra un error si no se puede crear el script
        }
    }

    // Muestra un cuadro de diálogo de error con el mensaje proporcionado
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Ejecuta el script PowerShell y devuelve si la ejecución fue exitosa
    private boolean executeScript(String scriptPath) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) {
            showErrorDialog("This tool only works on Windows.");  // Verifica si el sistema operativo es Windows
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
            showErrorDialog("Error executing script: " + e.getMessage());  // Muestra un error si no se puede ejecutar el script
            return false;
        }
    }
}
