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
                // Aquí se podría hacer algo más si fuera necesario al seleccionar un grupo.
            }
        });
        groupPanel.add(groupLabel);
        groupPanel.add(groupComboBox);
        add(groupPanel, BorderLayout.CENTER);

        // 3. Botón para ejecutar el script (Si lo necesitas)
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
        return loadGroupsFromFile(environment);
    }

    // Método para cargar los grupos desde los archivos
    private List<String> loadGroupsFromFile(String environment) {
        String userHome = System.getProperty("user.home");
        List<String> groups = new ArrayList<>();
        // Filtro para encontrar archivos con formato [Environment].Group[number].txt
        File folder = new File(userHome + "\\Documents\\StartServices\\SetupServer");
        File[] files = folder.listFiles((dir, name) -> name.matches(environment + "\\.Group\\d+\\.txt"));

        for (File file : files) {
            groups.add(file.getName().replace(".txt", ""));  // Agrega el nombre del archivo sin la extensión
        }
        return groups;
    }

    // Método para ejecutar el script con el grupo seleccionado
    private void runSelectedGroupScript() {
        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        String selectedGroup = (String) groupComboBox.getSelectedItem();

        if (selectedGroup == null) {
            JOptionPane.showMessageDialog(this, "Please select a group to run.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Running script for " + selectedEnvironment + " with group " + selectedGroup);

        // Aquí puedes agregar el código para ejecutar el script (PowerShell o cualquier otro tipo de script)
    }
}
