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
    private void editSelectedList() {
        JOptionPane.showMessageDialog(this, "Editing list: " + listDropdown.getSelectedItem());
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
