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

    // Constructor
    public StartRunningPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. Panel de Selección de "Environment"
        JLabel environmentLabel = new JLabel("Select Environment:");
        environmentComboBox = new JComboBox<>(getFilteredEnvironments());
        environmentComboBox.addActionListener(e -> {
            String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
            updateGroupComboBox(selectedEnvironment);
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(environmentLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(environmentComboBox, gbc);

        // 2. Panel de Selección de "Group"
        JLabel groupLabel = new JLabel("Select Group:");
        groupComboBox = new JComboBox<>();
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(groupLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(groupComboBox, gbc);

        // 3. Panel de Selección de "Select List"
        JLabel createServiceLabel = new JLabel("Select List:");
        createServiceListComboBox = new JComboBox<>(getFilteredCreateServiceLists());
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(createServiceLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(createServiceListComboBox, gbc);

        // 4. Botón para ejecutar el script
        runButton = new JButton("Run Script");
        runButton.addActionListener(e -> runSelectedGroupScript());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(runButton, gbc);
    }

    // Método para obtener archivos de SetupServer con formato [environment].txt y excluir Environment.txt
    private String[] getFilteredEnvironments() {
        String userHome = System.getProperty("user.home");
        File folder = new File(userHome + "/Documents/StartServices/SetupServer/");

        // Filtramos los archivos que cumplan con el patrón [A-Za-z0-9_]+.txt y excluimos "Environment.txt"
        File[] files = folder.listFiles((dir, name) ->
                name.matches("[A-Za-z0-9_]+\\.txt") && !name.equalsIgnoreCase("Environment.txt"));

        List<String> environments = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                environments.add(file.getName().replace(".txt", ""));
            }
        }
        return environments.toArray(new String[0]);
    }

    // Actualizar ComboBox de grupos basado en el archivo seleccionado de ambiente
    private void updateGroupComboBox(String selectedEnvironment) {
        groupComboBox.removeAllItems();
        List<String> groups = getGroupsForFile(selectedEnvironment);
        for (String group : groups) {
            groupComboBox.addItem(group);
        }
    }

    // Obtener los grupos del archivo seleccionado
    private List<String> getGroupsForFile(String environmentFile) {
        String userHome = System.getProperty("user.home");
        List<String> groups = new ArrayList<>();
        File file = new File(userHome + "/Documents/StartServices/SetupServer/" + environmentFile + ".txt");

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    groups.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return groups;
    }

    // Método para obtener los archivos de "Create Service List" con un filtro para excluir "List.txt"
    private String[] getFilteredCreateServiceLists() {
        String userHome = System.getProperty("user.home");
        File folder = new File(userHome + "/Documents/StartServices/CreateServiceList");

        // Filtramos los archivos para excluir "List.txt"
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt") && !name.equalsIgnoreCase("List.txt"));

        List<String> createServiceLists = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                createServiceLists.add(file.getName().replace(".txt", ""));
            }
        }
        return createServiceLists.toArray(new String[0]);
    }

    // Ejecutar script basado en selección
    private void runSelectedGroupScript() {
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        String selectedGroup = (String) groupComboBox.getSelectedItem();
        String selectedCreateService = (String) createServiceListComboBox.getSelectedItem();

        if (selectedGroup == null || selectedCreateService == null) {
            JOptionPane.showMessageDialog(this, "Please select a group and a list to run.");
            return;
        }

        System.out.println("Selected Environment: " + selectedEnvironment);
        System.out.println("Selected Group: " + selectedGroup);
        System.out.println("Selected List: " + selectedCreateService);

        JOptionPane.showMessageDialog(this, "Execution complete!");
    }
}
