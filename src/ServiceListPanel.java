import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceListPanel extends JPanel {
    private JList<String> servicesList;
    private DefaultListModel<String> serviceModel;
    private JButton addButton, createListButton, editListButton, deleteListButton;
    private JTextArea selectedServicesArea;
    private JTextField searchField;
    private JComboBox<String> listDropdown;
    private List<String> createdLists;

    // Base directory for storing files
    private static final String BASE_DIR = System.getProperty("user.home") + "/Documents/StartServices/CreateServiceList/";

    public ServiceListPanel() {
        setLayout(new BorderLayout());

        // Initialize the list of created files
        createdLists = new ArrayList<>();
        new File(BASE_DIR).mkdirs(); // Create base directory if it doesn't exist

        // Top Panel with controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Search field
        searchField = new JTextField(15);
        searchField.addActionListener(e -> filterServices());

        // Create New List button
        createListButton = new JButton("Create New List");
        createListButton.addActionListener(e -> createNewList());

        // Dropdown for selecting lists
        listDropdown = new JComboBox<>();
        listDropdown.addItem("Select a list");
        loadCreatedLists(); // Load existing lists from List.txt

        // Edit List button
        editListButton = new JButton("Edit List");
        editListButton.addActionListener(e -> editSelectedList());

        // Delete List button
        deleteListButton = new JButton("Delete List");
        deleteListButton.addActionListener(e -> deleteSelectedList());

        // Add components to the top panel in desired order
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(createListButton);
        topPanel.add(listDropdown);
        topPanel.add(editListButton);
        topPanel.add(deleteListButton);

        add(topPanel, BorderLayout.NORTH);

        // Service list initialization
        serviceModel = new DefaultListModel<>();
        List<String> services = ServiceFetcher.fetchServices(); // Assume fetchServices is correctly implemented
        services.forEach(serviceModel::addElement);

        servicesList = new JList<>(serviceModel);
        servicesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(servicesList), BorderLayout.CENTER);

        // Button to add selected services
        addButton = new JButton("Add to ServiceList");
        addButton.addActionListener(e -> addSelectedServices());
        add(addButton, BorderLayout.SOUTH);

        // Text area for displaying selected services
        selectedServicesArea = new JTextArea(10, 30);
        selectedServicesArea.setEditable(false);
        add(new JScrollPane(selectedServicesArea), BorderLayout.EAST);

        // Dropdown action listener for list selection
        listDropdown.addActionListener(e -> {
            String selectedItem = (String) listDropdown.getSelectedItem();
            if (selectedItem != null && !selectedItem.equals("Select a list")) {
                loadSelectedListServices(selectedItem);
            } else {
                selectedServicesArea.setText("");
            }
        });
    }

    // Filters services based on search text
    private void filterServices() {
        String searchText = searchField.getText().toLowerCase();
        DefaultListModel<String> filteredModel = new DefaultListModel<>();
        for (int i = 0; i < serviceModel.size(); i++) {
            String service = serviceModel.getElementAt(i);
            if (service.toLowerCase().contains(searchText)) {
                filteredModel.addElement(service);
            }
        }
        servicesList.setModel(filteredModel);
    }

    // Adds selected services to the chosen list
    private void addSelectedServices() {
        List<String> selectedServices = servicesList.getSelectedValuesList();
        if (selectedServices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one service");
            return;
        }

        String selectedList = (String) listDropdown.getSelectedItem();
        if (selectedList == null || selectedList.equals("Select a list")) {
            JOptionPane.showMessageDialog(this, "Please select a valid list.");
            return;
        }

        selectedServicesArea.setText("");
        for (String service : selectedServices) {
            selectedServicesArea.append(service + "\n");
            appendServiceToFile(service, selectedList);
        }
        JOptionPane.showMessageDialog(this, "Services added to " + selectedList);
    }

    // Appends a service to the selected list file
    private void appendServiceToFile(String serviceName, String selectedList) {
        String fileName = BASE_DIR + selectedList + ".txt";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(serviceName + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
        }
    }

    // Creates a new list
    private void createNewList() {
        String listName = JOptionPane.showInputDialog(this, "Enter list name:");
        if (listName != null && !listName.trim().isEmpty() && !createdLists.contains(listName.trim())) {
            listName = listName.trim();
            createdLists.add(listName);
            listDropdown.addItem(listName);
            writeListNameToFile(listName);
            createNewFile(listName);
            JOptionPane.showMessageDialog(this, "List created: " + listName);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid or duplicate list name.");
        }
    }

    // Adds a new list name to List.txt
    private void writeListNameToFile(String listName) {
        try (FileWriter writer = new FileWriter(BASE_DIR + "List.txt", true)) {
            writer.write(listName + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to List.txt: " + e.getMessage());
        }
    }

    // Creates a new list file
    private void createNewFile(String listName) {
        try (FileWriter writer = new FileWriter(BASE_DIR + listName + ".txt")) {
            // Empty file creation
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating file: " + e.getMessage());
        }
    }

    // Loads created lists into dropdown
    private void loadCreatedLists() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BASE_DIR + "List.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                createdLists.add(line);
                listDropdown.addItem(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading List.txt: " + e.getMessage());
        }
    }

    // Loads services from the selected list file
    private void loadSelectedListServices(String listName) {
        selectedServicesArea.setText("");
        try (BufferedReader reader = new BufferedReader(new FileReader(BASE_DIR + listName + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                selectedServicesArea.append(line + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading " + listName + ".txt: " + e.getMessage());
        }
    }

    // Edits the selected list (dummy function for now)
    // Edits the selected service in the selected list
    // Método para elegir entre editar o eliminar un servicio de la lista seleccionada
    private void editSelectedList() {
        String selectedList = (String) listDropdown.getSelectedItem();

        if (selectedList == null || selectedList.equals("Select a list")) {
            JOptionPane.showMessageDialog(this, "Please select a valid list.");
            return;
        }

        // Cargar los servicios de la lista seleccionada
        List<String> services = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BASE_DIR + selectedList + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                services.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading " + selectedList + ".txt: " + e.getMessage());
            return;
        }

        if (services.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The selected list is empty.");
            return;
        }

        // Mostrar los servicios en un JComboBox para que el usuario seleccione
        String[] serviceArray = services.toArray(new String[0]);
        String selectedService = (String) JOptionPane.showInputDialog(
                this,
                "Select a service:",
                "Edit or Delete Service",
                JOptionPane.PLAIN_MESSAGE,
                null,
                serviceArray,
                serviceArray[0]
        );

        if (selectedService == null) {
            // Si el usuario cancela la operación
            return;
        }

        // Opciones para Editar o Eliminar
        String[] options = {"Edit", "Delete"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Do you want to edit or delete the selected service?",
                "Choose Action",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Si el usuario cancela la operación
        if (choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        if (choice == 0) { // Opción de Editar
            editServiceInFile(selectedList, selectedService);
        } else if (choice == 1) { // Opción de Eliminar
            deleteServiceFromFile(selectedList, selectedService);
        }

        // Recargar la lista de servicios en el área de texto
        loadSelectedListServices(selectedList);
    }

    // Método para editar un servicio
    private void editServiceInFile(String listName, String oldService) {
        String newServiceName = JOptionPane.showInputDialog(this, "Enter new service name:", oldService);

        if (newServiceName == null || newServiceName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid service name. No changes made.");
            return;
        }

        updateServiceInFile(listName, oldService, newServiceName.trim());
        JOptionPane.showMessageDialog(this, "Service updated successfully.");
    }

    // Método para eliminar un servicio
    private void deleteServiceFromFile(String listName, String serviceToDelete) {
        File file = new File(BASE_DIR + listName + ".txt");
        List<String> updatedServices = new ArrayList<>();

        // Leer y eliminar el servicio del archivo
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(serviceToDelete)) {
                    updatedServices.add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading " + listName + ".txt: " + e.getMessage());
            return;
        }

        // Escribir los servicios actualizados en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String service : updatedServices) {
                writer.write(service);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to " + listName + ".txt: " + e.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Service deleted successfully.");
    }

    // Método para actualizar un servicio en el archivo correspondiente
    private void updateServiceInFile(String listName, String oldService, String newService) {
        File file = new File(BASE_DIR + listName + ".txt");
        List<String> updatedServices = new ArrayList<>();

        // Leer y actualizar los servicios
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(oldService)) {
                    updatedServices.add(newService);
                } else {
                    updatedServices.add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading " + listName + ".txt: " + e.getMessage());
            return;
        }

        // Escribir los servicios actualizados en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String service : updatedServices) {
                writer.write(service);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to " + listName + ".txt: " + e.getMessage());
        }
    }



    // Deletes the selected list
    private void deleteSelectedList() {
        String selectedList = (String) listDropdown.getSelectedItem();
        if (!selectedList.equals("Select a list")) {
            createdLists.remove(selectedList);
            listDropdown.removeItem(selectedList);
            new File(BASE_DIR + selectedList + ".txt").delete();
            JOptionPane.showMessageDialog(this, "List deleted: " + selectedList);
        }
    }
}
