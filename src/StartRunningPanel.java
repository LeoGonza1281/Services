import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StartRunningPanel extends JPanel {
    private JComboBox<String> environmentComboBox;
    private JComboBox<String> groupComboBox;
    private JComboBox<String> createServiceListComboBox;
    private JButton runButton;
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

        // Botón para ejecutar el script
        runButton = new JButton("Run Script");
        runButton.addActionListener(e -> runSelectedGroupScript());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(runButton, gbc);
    }

    // Método para añadir componentes
    private void addComponent(Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        add(comp, gbc);
    }

    // Obtener archivos de SetupServer con formato [environment].txt excluyendo Environment.txt
    private String[] getFilteredEnvironments() {
        File folder = new File(BASE_DIR + "SetupServer/");
        File[] files = folder.listFiles((dir, name) -> name.matches("[A-Za-z0-9_]+\\.txt") && !name.equalsIgnoreCase("Environments.txt"));
        return getFileNamesWithoutExtension(files);
    }

    // Obtener archivos de "Create Service List" excluyendo "List.txt"
    private String[] getFilteredCreateServiceLists() {
        File folder = new File(BASE_DIR + "CreateServiceList/");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt") && !name.equalsIgnoreCase("List.txt"));
        return getFileNamesWithoutExtension(files);
    }

    // Obtener nombres de archivos sin extensión
    private String[] getFileNamesWithoutExtension(File[] files) {
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName().replace(".txt", ""));
            }
        }
        return fileNames.toArray(new String[0]);
    }

    // Actualizar ComboBox de grupos
    private void updateGroupComboBox() {
        groupComboBox.removeAllItems();
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        List<String> groups = readLinesFromFile(BASE_DIR + "SetupServer/" + selectedEnvironment + ".txt");
        for (String group : groups) {
            groupComboBox.addItem(group);
        }
    }

    // Método para leer líneas de un archivo
    private List<String> readLinesFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line.trim());
                }
            } catch (IOException e) {
                showErrorDialog("Error reading file: " + filePath);
            }
        }
        return lines;
    }

    // Método para ejecutar el script
    private void runSelectedGroupScript() {
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        String selectedGroup = (String) groupComboBox.getSelectedItem();
        String selectedServiceList = (String) createServiceListComboBox.getSelectedItem();

        if (selectedEnvironment == null || selectedGroup == null || selectedServiceList == null) {
            showErrorDialog("Please make sure to select environment, group, and list.");
            return;
        }

        List<String> servers = readLinesFromFile(BASE_DIR + "SetupServer/" + selectedEnvironment + ".txt");
        List<String> services = readLinesFromFile(BASE_DIR + "CreateServiceList/" + selectedServiceList + ".txt");

        runButton.setEnabled(false);

        // Uso de SwingWorker para evitar congelar la interfaz
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                for (String server : servers) {
                    for (String service : services) {
                        invokeAndStartService(server, service);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
                JOptionPane.showMessageDialog(StartRunningPanel.this, "Execution complete!");
            }
        }.execute();
    }

    // Método para invocar y encender los servicios
    private void invokeAndStartService(String server, String service) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("invoke-command", server, service);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Failed to start service: " + service + " on server: " + server);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error executing command on server: " + server);
        }
    }

    // Mostrar cuadro de diálogo de error
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
