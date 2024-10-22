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
    private JButton addButton, createListButton;
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

        // Ensure the base directory exists
        new File(BASE_DIR).mkdirs();

        // Panel for searching and creating lists
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Search field
        searchField = new JTextField(15);
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterServices();
            }
        });

        // Button to create a new list
        createListButton = new JButton("Create New List");
        createListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewList();
            }
        });

        // Dropdown for selecting lists
        listDropdown = new JComboBox<>();
        listDropdown.addItem("Select a list");

        // Load existing lists from List.txt
        loadCreatedLists();

        // Add components to the top panel
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(createListButton);
        topPanel.add(listDropdown);

        add(topPanel, BorderLayout.NORTH);

        // Get the list of services from the system using ServiceFetcher
        serviceModel = new DefaultListModel<>();
        List<String> services = ServiceFetcher.fetchServices(); // Assume this method is correctly implemented
        for (String service : services) {
            serviceModel.addElement(service);
        }

        servicesList = new JList<>(serviceModel);
        servicesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(servicesList), BorderLayout.CENTER);

        // Button to add services to the selected list
        addButton = new JButton("Add to ServiceList");
        add(addButton, BorderLayout.SOUTH);

        // Text area to show selected services
        selectedServicesArea = new JTextArea(10, 30);
        selectedServicesArea.setEditable(false); // Make it non-editable
        add(new JScrollPane(selectedServicesArea), BorderLayout.EAST);

        // Action for adding services
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSelectedServices();
            }
        });

        // Action for list selection from dropdown
        listDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) listDropdown.getSelectedItem();
                if (selectedItem != null && !selectedItem.equals("Select a list")) {
                    int selectedIndex = listDropdown.getSelectedIndex() - 1; // Adjust for "Select a list"
                    if (selectedIndex >= 0 && selectedIndex < createdLists.size()) {
                        String selectedListName = createdLists.get(selectedIndex);
                        loadSelectedListServices(selectedListName); // Load the selected list's services
                    }
                } else {
                    selectedServicesArea.setText(""); // Clear if "Select a list"
                }
            }
        });
    }

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

        // Clear the previous selected services
        selectedServicesArea.setText("");

        // Add selected services to the selected list in the JTextArea
        for (String service : selectedServices) {
            selectedServicesArea.append(service + "\n");
            writeToFile(service, selectedList); // Write to the existing list file
        }

        // Optionally, display a confirmation message
        JOptionPane.showMessageDialog(this, "Services added to " + selectedList);
    }

    private void writeToFile(String serviceName, String selectedList) {
        // Construct the filename based on the selected list
        String fileName = BASE_DIR + selectedList + ".txt"; // Use the existing file in the base directory

        try (FileWriter writer = new FileWriter(fileName, true)) { // Append mode
            writer.write(serviceName + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
        }
    }

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

    private void createNewList() {
        String listName = JOptionPane.showInputDialog(this, "Enter list name:");
        if (listName != null && !listName.trim().isEmpty()) {
            // Trim the input to remove extra spaces
            listName = listName.trim();

            // Check if the list already exists
            if (!createdLists.contains(listName)) {
                createdLists.add(listName);
                listDropdown.addItem(listName); // Add directly as the list name without ".txt"
                JOptionPane.showMessageDialog(this, "List created: " + listName);

                // Write the name of the new list to List.txt
                writeToListFile(listName);

                // Create the actual new list file
                createNewListFile(listName);
            } else {
                JOptionPane.showMessageDialog(this, "List already exists.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "List name cannot be empty.");
        }
    }

    private void writeToListFile(String listName) {
        File file = new File(BASE_DIR + "List.txt");
        try (FileWriter writer = new FileWriter(file, true)) { // Append mode
            writer.write(listName + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to List.txt: " + e.getMessage());
        }
    }

    private void createNewListFile(String listName) {
        File newListFile = new File(BASE_DIR + listName + ".txt");
        try (FileWriter writer = new FileWriter(newListFile)) {
            writer.write(""); // Create empty file for the new list
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating file: " + e.getMessage());
        }
    }

    private void loadCreatedLists() {
        File file = new File(BASE_DIR + "List.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    createdLists.add(line);
                    listDropdown.addItem(line); // Add the name directly, without ".txt"
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading List.txt: " + e.getMessage());
            }
        }
    }

    private void loadSelectedListServices(String listName) {
        selectedServicesArea.setText(""); // Clear the text area
        File file = new File(BASE_DIR + listName + ".txt"); // Load the corresponding file
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    selectedServicesArea.append(line + "\n"); // Display each service in the text area
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading " + listName + ".txt: " + e.getMessage());
            }
        }
    }
}
