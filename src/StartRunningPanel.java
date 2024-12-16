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
        System.out.println("Component added at GridBagConstraints (" + x + ", " + y + ")");
    }

    public void updateAllComboBoxes() {
        // Actualizar el ComboBox de "Environment"
        environmentComboBox.setModel(new DefaultComboBoxModel<>(getFilteredEnvironments()));
        System.out.println("Updated Environment ComboBox");

        // Limpiar y actualizar el ComboBox de "Group"
        groupComboBox.removeAllItems();
        updateGroupComboBox();
        System.out.println("Updated Group ComboBox");

        // Actualizar el ComboBox de "Service List"
        createServiceListComboBox.setModel(new DefaultComboBoxModel<>(getFilteredCreateServiceLists()));
        System.out.println("Updated Service List ComboBox");
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
                    System.out.println("Added group: " + groupName);
                }
            }
        }
    }

    // Obtiene los entornos filtrados desde el directorio SetupServer
    private String[] getFilteredEnvironments() {
        File folder = new File(BASE_DIR + "SetupServer/");
        File[] files = folder.listFiles((dir, name) ->
                name.endsWith(".txt") &&                // Solo archivos con extensión .txt
                        !name.equals("Environments.txt") &&     // Excluye específicamente Environments.txt
                        name.split("\\.").length == 2           // Asegura que sea solo un nombre de entorno
        );
        String[] environments = getFileNamesWithoutExtension(files);  // Extrae solo los nombres de los archivos sin la extensión ".txt"
        System.out.println("Filtered environments: " + String.join(", ", environments));
        return environments;
    }

    // Obtiene las listas de servicios filtradas desde el directorio CreateServiceList
    private String[] getFilteredCreateServiceLists() {
        File folder = new File(BASE_DIR + "CreateServiceList/");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt") && !name.equalsIgnoreCase("List.txt"));
        String[] serviceLists = getFileNamesWithoutExtension(files);  // Extrae solo los nombres de los archivos sin la extensión ".txt"
        System.out.println("Filtered service lists: " + String.join(", ", serviceLists));
        return serviceLists;
    }

    // Extrae los nombres de los archivos sin la extensión ".txt"
    private String[] getFileNamesWithoutExtension(File[] files) {
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName().replace(".txt", ""));  // Elimina la extensión ".txt"
                System.out.println("Processed file: " + file.getName());
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
                System.out.println("Read line: " + line.trim());
            }
        } catch (IOException e) {
            showErrorDialog("Error reading file: " + filePath);  // Muestra un error si no se puede leer el archivo
            System.out.println("Error reading file: " + filePath + " - " + e.getMessage());
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
            System.out.println("Selection incomplete.");
            return;
        }

        String groupFilePath = BASE_DIR + "SetupServer/" + selectedGroup + ".txt";
        List<String> servers = readLinesFromFile(groupFilePath);  // Lee los servidores del archivo seleccionado
        List<String> services = readLinesFromFile(BASE_DIR + "CreateServiceList/" + selectedServiceList + ".txt");  // Lee los servicios del archivo seleccionado

        if (servers.isEmpty() || services.isEmpty()) {
            showErrorDialog("No servers or services found.");  // Muestra un error si no se encuentran servidores o servicios
            System.out.println("No servers or services found.");
            return;
        }

        // Crear el archivo PowerShell invokeServices.ps1
        String psScriptPath = BASE_DIR + "invokeServices.ps1";
        createPS1Script(psScriptPath, servers, services);  // Crea el script PowerShell
        System.out.println("Created PowerShell script at: " + psScriptPath);

        // Ejecuta el script generado
        if (executeScript(psScriptPath)) {
            JOptionPane.showMessageDialog(this, "Script executed successfully!");  // Muestra un mensaje de éxito
            System.out.println("Script executed successfully.");
        } else {
            showErrorDialog("Failed to execute the script.");  // Muestra un error si falla la ejecución
            System.out.println("Failed to execute the script.");
        }
    }

    // Crea el archivo PowerShell que invoca los servicios en los servidores
    private void createPS1Script(String psScriptPath, List<String> servers, List<String> services) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(psScriptPath))) {
            for (String server : servers) {
                for (String service : services) {
                    writer.write(String.format("Invoke-Command -ComputerName %s -FilePath c:\\Scripts\\DiskCollect.ps1 -ArgumentList \"%s\"\n",
                            server, service));  // Escribe las instrucciones PowerShell para cada servidor y servicio
                    System.out.println("Writing PowerShell command for server: " + server + " service: " + service);
                }
            }
        } catch (IOException e) {
            showErrorDialog("Error creating PowerShell script.");  // Muestra un error si falla la creación del script
            System.out.println("Error creating PowerShell script: " + e.getMessage());
        }
    }

    // Ejecuta el script PowerShell creado y devuelve true si se ejecuta correctamente, false en caso contrario
    private boolean executeScript(String psScriptPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Unrestricted", "-File", psScriptPath);
            Process process = pb.start();  // Inicia el proceso PowerShell
            int exitCode = process.waitFor();  // Espera la finalización del proceso
            System.out.println("PowerShell script exit code: " + exitCode);
            return exitCode == 0;  // Devuelve true si la ejecución fue exitosa
        } catch (Exception e) {
            showErrorDialog("Error executing PowerShell script.");  // Muestra un error si falla la ejecución
            System.out.println("Error executing PowerShell script: " + e.getMessage());
            return false;
        }
    }

    // Muestra un cuadro de diálogo de error con el mensaje proporcionado
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        System.out.println("Error dialog shown: " + message);
    }
}
