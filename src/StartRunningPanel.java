import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StartRunningPanel extends JPanel {
    private JComboBox<String> environmentComboBox;
    private JComboBox<String> groupComboBox;
    private JComboBox<String> serviceListComboBox;
    private JComboBox<String> createServiceListComboBox;
    private JButton runButton;

    // Constructor
    public StartRunningPanel() {
        setLayout(new BorderLayout());

        // 1. Panel de Selección de "Environment"
        JPanel environmentPanel = new JPanel(new FlowLayout());
        JLabel environmentLabel = new JLabel("Select Environment:");
        environmentComboBox = new JComboBox<>(getEnvironments());  // Carga los entornos disponibles
        environmentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
                updateGroupComboBox(selectedEnvironment); // Actualiza los grupos según el entorno
            }
        });
        environmentPanel.add(environmentLabel);
        environmentPanel.add(environmentComboBox);
        add(environmentPanel, BorderLayout.NORTH);

        // 2. Panel de Selección de "Grupo"
        JPanel groupPanel = new JPanel(new FlowLayout());
        JLabel groupLabel = new JLabel("Select Group:");
        groupComboBox = new JComboBox<>();
        groupComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedGroup = (String) groupComboBox.getSelectedItem();
                updateServiceListComboBox(selectedGroup); // Actualiza la lista de servidores
            }
        });
        groupPanel.add(groupLabel);
        groupPanel.add(groupComboBox);
        add(groupPanel, BorderLayout.CENTER);

        // 3. Panel de Selección de "Service List"
        JPanel servicePanel = new JPanel(new FlowLayout());
        JLabel serviceLabel = new JLabel("Select Service List:");
        serviceListComboBox = new JComboBox<>();
        servicePanel.add(serviceLabel);
        servicePanel.add(serviceListComboBox);
        add(servicePanel, BorderLayout.SOUTH);

        // 4. Nuevo Panel de Selección de "Create Service List"
        JPanel createServicePanel = new JPanel(new FlowLayout());
        JLabel createServiceLabel = new JLabel("Select Create Service List:");
        createServiceListComboBox = new JComboBox<>(getCreateServiceLists());  // Carga las listas de creación de servicio
        createServicePanel.add(createServiceLabel);
        createServicePanel.add(createServiceListComboBox);
        add(createServicePanel, BorderLayout.WEST);  // Lo agregamos en una posición diferente

        // 5. Botón para ejecutar el script
        runButton = new JButton("Run Script");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runSelectedGroupScript(); // Ejecutar el script para el grupo seleccionado
            }
        });
        add(runButton, BorderLayout.SOUTH);
    }

    // Método para obtener los entornos (environments) disponibles
    private String[] getEnvironments() {
        String userHome = System.getProperty("user.home");
        File folder = new File(userHome + "\\Documents\\StartServices\\SetupServer"); // Ruta completa
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt") && !name.equals("Environments.txt") && name.matches("[A-Za-z0-9_]+\\.txt"));

        List<String> environments = new ArrayList<>();
        for (File file : files) {
            environments.add(file.getName().replace(".txt", ""));  // Agrega el nombre del archivo sin la extensión
        }
        return environments.toArray(new String[0]);
    }

    // Método para actualizar el JComboBox de grupos basado en el entorno seleccionado
    private void updateGroupComboBox(String selectedEnvironment) {
        groupComboBox.removeAllItems();
        List<String> groups = getGroupsForEnvironment(selectedEnvironment);
        for (String group : groups) {
            groupComboBox.addItem(group);
        }
    }

    // Método para obtener los grupos basados en el entorno seleccionado
    private List<String> getGroupsForEnvironment(String environment) {
        return loadItemsFromFile(environment, "\\.Group\\d+\\.txt"); // Cargar grupos
    }

    // Método genérico para cargar archivos basados en un patrón
    private List<String> loadItemsFromFile(String environment, String pattern) {
        String userHome = System.getProperty("user.home");
        List<String> items = new ArrayList<>();
        // Filtro para encontrar archivos con formato específico
        File folder = new File(userHome + "\\Documents\\StartServices\\SetupServer");
        File[] files = folder.listFiles((dir, name) -> name.matches(environment + pattern));

        for (File file : files) {
            items.add(file.getName().replace(".txt", ""));  // Agrega el nombre del archivo sin la extensión
        }
        return items;
    }

    // Método para actualizar el JComboBox de servidores basado en el grupo seleccionado
    private void updateServiceListComboBox(String selectedGroup) {
        serviceListComboBox.removeAllItems();
        List<String> services = loadItemsFromFile(selectedGroup, "_services\\.txt"); // Cargar servidores del grupo
        for (String service : services) {
            serviceListComboBox.addItem(service);
        }
    }

    // Método para cargar los elementos de "CreateServiceList" desde la carpeta
    private String[] getCreateServiceLists() {
        String userHome = System.getProperty("user.home");
        File folder = new File(userHome + "\\Documents\\StartServices\\CreateServiceList"); // Ruta completa
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt") && !name.equals("List.txt"));

        List<String> createServiceLists = new ArrayList<>();

        if (files != null) {  // Verificar que no sea null
            for (File file : files) {
                createServiceLists.add(file.getName().replace(".txt", ""));  // Agrega el nombre del archivo sin la extensión
            }
        }

        return createServiceLists.toArray(new String[0]);
    }

    // Método para ejecutar el script con el grupo seleccionado
    private void runSelectedGroupScript() {
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        String selectedGroup = (String) groupComboBox.getSelectedItem();
        String selectedService = (String) serviceListComboBox.getSelectedItem();
        String selectedCreateService = (String) createServiceListComboBox.getSelectedItem();

        // Depuración: Verifica los valores seleccionados
        System.out.println("Selected Environment: " + selectedEnvironment);
        System.out.println("Selected Group: " + selectedGroup);
        System.out.println("Selected Service: " + selectedService);
        System.out.println("Selected Create Service List: " + selectedCreateService);

        // Verifica si se han seleccionado los servicios y las listas
        if (selectedService == null || selectedCreateService == null) {
            JOptionPane.showMessageDialog(this, "Please select a service and a create service list to run.");
            return;
        }

        // Obtener la lista de servidores de los archivos de texto
        List<String> selectedServers = getServersFromGroupFile(selectedGroup);

        // Obtener la lista de servicios de los archivos de texto
        List<String> selectedServices = getServicesFromServiceFile(selectedService);

        // Definir la carpeta donde se guardarán los scripts
        String userHome = System.getProperty("user.home");
        File scriptsFolder = new File(userHome + "\\Documents\\StartServices");

        // Asegurarnos de que la carpeta exista
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdirs();
        }

        // Crear el primer script (con invoke-Command)
        String script1Content = createInvokeCommandScript(selectedServers);
        createScriptFile(scriptsFolder, "InvokeCommand_Script.ps1", script1Content);

        // Crear el segundo script (con Restart-Service)
        String script2Content = createRestartServiceScript(selectedServices);
        createScriptFile(scriptsFolder, "RestartService_Script.ps1", script2Content);

        // Ejecutar los scripts
        executeScript(new File(scriptsFolder, "InvokeCommand_Script.ps1"));
        executeScript(new File(scriptsFolder, "RestartService_Script.ps1"));

        // Mostrar mensaje al usuario
        JOptionPane.showMessageDialog(this, "Scripts created and executed successfully!");
    }

    // Método para crear el archivo del script "invoke-Command"
    private String createInvokeCommandScript(List<String> servers) {
        StringBuilder scriptContent = new StringBuilder("invoke-Command -ComputerName ");
        for (int i = 0; i < servers.size(); i++) {
            scriptContent.append(servers.get(i));
            if (i < servers.size() - 1) {
                scriptContent.append(", ");
            }
        }
        scriptContent.append(" -Filepath c:\\Scripts\\DiskCollect.ps1");
        return scriptContent.toString();
    }

    // Método para crear el archivo del script "Restart-Service"
    private String createRestartServiceScript(List<String> services) {
        StringBuilder scriptContent = new StringBuilder();
        for (String service : services) {
            scriptContent.append("Restart-Service -Name ").append(service).append("\n");
        }
        return scriptContent.toString();
    }

    // Método para crear un archivo con el contenido del script
    private void createScriptFile(File folder, String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(folder, fileName)))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para ejecutar el script con Runtime
    private void executeScript(File scriptFile) {
        try {
            Process process = Runtime.getRuntime().exec("powershell.exe -ExecutionPolicy Bypass -File " + scriptFile.getAbsolutePath());
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener la lista de servidores de un grupo desde el archivo
    private List<String> getServersFromGroupFile(String group) {
        // Lógica para obtener los servidores desde el archivo de grupo
        return new ArrayList<>();
    }

    // Método para obtener los servicios de un archivo de servicio
    private List<String> getServicesFromServiceFile(String service) {
        // Lógica para obtener los servicios desde el archivo de servicio
        return new ArrayList<>();
    }
}
