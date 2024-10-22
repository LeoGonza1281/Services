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
    private JList<String> servicesList;
    private JButton runButton;

    // Constructor
    public StartRunningPanel() {
        setLayout(new BorderLayout());

        // 1. Panel de Selección de "Environment"
        JPanel environmentPanel = new JPanel(new FlowLayout());
        JLabel environmentLabel = new JLabel("Select Environment:");
        environmentComboBox = new JComboBox<>(getEnvironments());
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
                updateServiceList(selectedGroup); // Actualiza la lista de servicios según el grupo seleccionado
            }
        });
        groupPanel.add(groupLabel);
        groupPanel.add(groupComboBox);
        add(groupPanel, BorderLayout.CENTER);

        // 3. Panel de Selección de "Servicios"
        JPanel servicesPanel = new JPanel(new BorderLayout());
        JLabel servicesLabel = new JLabel("Select Services:");
        servicesList = new JList<>();
        JScrollPane servicesScrollPane = new JScrollPane(servicesList);
        servicesPanel.add(servicesLabel, BorderLayout.NORTH);
        servicesPanel.add(servicesScrollPane, BorderLayout.CENTER);
        add(servicesPanel, BorderLayout.SOUTH);

        // 4. Botón para ejecutar el script
        runButton = new JButton("Run Script");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runSelectedServicesScript(); // Ejecutar los servicios seleccionados
            }
        });
        add(runButton, BorderLayout.SOUTH);
    }

    // Método para obtener los entornos (environments) creados
    private String[] getEnvironments() {
        // Simulación: Puedes cargar los environments desde un archivo o base de datos
        // Aquí simplemente estoy simulando con una lista de entornos predefinidos
        return new String[]{"Environment1", "Environment2", "Environment3"};
    }

    // Método para actualizar el JComboBox de grupos basado en el entorno seleccionado
    private void updateGroupComboBox(String selectedEnvironment) {
        groupComboBox.removeAllItems();
        List<String> groups = getGroupsForEnvironment(selectedEnvironment);
        for (String group : groups) {
            groupComboBox.addItem(group);
        }
    }

    // Método para obtener los grupos creados en el entorno seleccionado
    private List<String> getGroupsForEnvironment(String environment) {
        // Simulación: Aquí puedes cargar los grupos desde un archivo o base de datos
        // Simulando que se cargan dinámicamente según el environment seleccionado
        return loadGroupsFromFile(environment);
    }

    // Método para cargar los grupos desde un archivo
    private List<String> loadGroupsFromFile(String environment) {
        List<String> groups = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(environment + "_groups.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                groups.add(line.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading groups from file: " + e.getMessage());
        }
        return groups;
    }

    // Método para actualizar la lista de servicios basados en el grupo seleccionado
    private void updateServiceList(String selectedGroup) {
        DefaultListModel<String> serviceModel = new DefaultListModel<>();
        List<String> services = getServicesForGroup(selectedGroup);
        for (String service : services) {
            serviceModel.addElement(service);
        }
        servicesList.setModel(serviceModel);
    }

    // Método para obtener los servicios del grupo seleccionado
    private List<String> getServicesForGroup(String group) {
        // Simulación: Aquí puedes cargar los servicios desde un archivo o base de datos
        return loadServicesFromFile(group);
    }

    // Método para cargar los servicios desde un archivo
    private List<String> loadServicesFromFile(String group) {
        List<String> services = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(group + "_services.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                services.add(line.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading services from file: " + e.getMessage());
        }
        return services;
    }

    // Método para ejecutar el script con los servicios seleccionados
    private void runSelectedServicesScript() {
        List<String> selectedServices = servicesList.getSelectedValuesList();
        if (selectedServices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one service to run.");
            return;
        }

        String selectedEnvironment = (String) environmentComboBox.getSelectedItem();
        String selectedGroup = (String) groupComboBox.getSelectedItem();

        JOptionPane.showMessageDialog(this, "Running script for " + selectedEnvironment +
                " in group " + selectedGroup + " with services " + selectedServices);

        // Aquí puedes agregar el código para ejecutar los servicios (PowerShell o script de tu preferencia)
    }
}
