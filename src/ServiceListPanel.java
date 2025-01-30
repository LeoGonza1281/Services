import javax.swing.*;
import java.awt.*;
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

    private static final String BASE_DIR = System.getProperty("user.home") + "/Documents/StartServices/CreateServiceList/";

    public ServiceListPanel() {
        setLayout(new BorderLayout(10, 10));

        createdLists = new ArrayList<>();
        new File(BASE_DIR).mkdirs();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));

        searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.addActionListener(e -> filterServices());

        createListButton = new JButton("Create New List");
        createListButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        createListButton.addActionListener(e -> createNewList());

        listDropdown = new JComboBox<>();
        listDropdown.setFont(new Font("SansSerif", Font.PLAIN, 14));
        listDropdown.addItem("Select a list");
        loadCreatedLists();

        editListButton = new JButton("Edit List");
        editListButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        editListButton.addActionListener(e -> editSelectedList());

        deleteListButton = new JButton("Delete List");
        deleteListButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteListButton.addActionListener(e -> deleteSelectedList());

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(createListButton);
        topPanel.add(listDropdown);
        topPanel.add(editListButton);
        topPanel.add(deleteListButton);

        add(topPanel, BorderLayout.NORTH);

        serviceModel = new DefaultListModel<>();
        List<String> services = ServiceFetcher.fetchServices();
        services.forEach(serviceModel::addElement);

        servicesList = new JList<>(serviceModel);
        servicesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        servicesList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(servicesList), BorderLayout.CENTER);

        addButton = new JButton("Add to ServiceList");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addButton.addActionListener(e -> addSelectedServices());
        add(addButton, BorderLayout.SOUTH);

        selectedServicesArea = new JTextArea(10, 30);
        selectedServicesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        selectedServicesArea.setEditable(false);
        add(new JScrollPane(selectedServicesArea), BorderLayout.EAST);

        listDropdown.addActionListener(e -> {
            String selectedItem = (String) listDropdown.getSelectedItem();
            if (selectedItem != null && !selectedItem.equals("Select a list")) {
                loadSelectedListServices(selectedItem);
            } else {
                selectedServicesArea.setText("");
            }
        });

        setPreferredSize(new Dimension(800, 600));

        // Add component listener to handle resizing
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                adjustFontSizes();
            }
        });
    }

    private void adjustFontSizes() {
        int width = getWidth();
        int height = getHeight();

        // Determina si el panel está en pantalla completa
        boolean isFullScreen = (width >= Toolkit.getDefaultToolkit().getScreenSize().width)
                && (height >= Toolkit.getDefaultToolkit().getScreenSize().height);

        // Define el tamaño base según si está en pantalla completa
        int baseSize = isFullScreen ? Math.min(width, height) / 50 : Math.min(width, height) / 30;

        Font font = new Font("SansSerif", Font.PLAIN, baseSize);
        searchField.setFont(font);
        createListButton.setFont(font.deriveFont(Font.BOLD));
        listDropdown.setFont(font);
        editListButton.setFont(font.deriveFont(Font.BOLD));
        deleteListButton.setFont(font.deriveFont(Font.BOLD));
        addButton.setFont(font.deriveFont(Font.BOLD));
        selectedServicesArea.setFont(new Font("Monospaced", Font.PLAIN, baseSize));

        // Ajusta el tamaño de fuente para los elementos del JList
        servicesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                renderer.setFont(new Font("SansSerif", Font.BOLD, baseSize)); // Ajusta el tamaño de la fuente aquí
                return renderer;
            }
        });
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

        for (String service : selectedServices) {
            appendServiceToFile(service, selectedList);
        }

        loadSelectedListServices(selectedList);
        JOptionPane.showMessageDialog(this, "Services added to " + selectedList);
    }

    private void appendServiceToFile(String serviceName, String selectedList) {
        String fileName = BASE_DIR + selectedList + ".txt";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(serviceName + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
        }
    }

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

    private void writeListNameToFile(String listName) {
        try (FileWriter writer = new FileWriter(BASE_DIR + "List.txt", true)) {
            writer.write(listName + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to List.txt: " + e.getMessage());
        }
    }

    private void createNewFile(String listName) {
        try (FileWriter writer = new FileWriter(BASE_DIR + listName + ".txt")) {
            // Empty file creation
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating file: " + e.getMessage());
        }
    }

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

    // Edits the selected list
    private void editSelectedList() {
        String selectedList = (String) listDropdown.getSelectedItem();
        if (selectedList == null || selectedList.equals("Select a list")) {
            JOptionPane.showMessageDialog(this, "Please select a valid list to edit.");
            return;
        }

        // Load services from the selected list
        List<String> services = new ArrayList<>();
        String fileName = BASE_DIR + selectedList + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                services.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading the list file: " + e.getMessage());
            return;
        }

        // Create a panel with checkboxes for each service
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (String service : services) {
            JCheckBox checkBox = new JCheckBox(service);
            checkBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
            checkBoxes.add(checkBox);
            checkboxPanel.add(checkBox);
        }

        // Show the checkboxes in a scrollable dialog
        int option = JOptionPane.showConfirmDialog(this, new JScrollPane(checkboxPanel),
                "Select services to edit or delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            List<String> selectedServices = new ArrayList<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedServices.add(checkBox.getText());
                }
            }

            if (!selectedServices.isEmpty()) {
                int action = JOptionPane.showOptionDialog(this, "What would you like to do with the selected services?",
                        "Edit or Delete Services", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, new String[]{"Edit", "Delete", "Cancel"}, "Cancel");

                if (action == 0) { // Edit
                    for (String service : selectedServices) {
                        String newService = JOptionPane.showInputDialog(this, "Edit service:", service);
                        if (newService != null && !newService.trim().isEmpty()) {
                            services.set(services.indexOf(service), newService.trim());
                        }
                    }
                } else if (action == 1) { // Delete
                    services.removeAll(selectedServices);
                }

                // Write updated services back to the file
                try (FileWriter writer = new FileWriter(fileName)) {
                    for (String service : services) {
                        writer.write(service + "\n");
                    }
                    JOptionPane.showMessageDialog(this, "Changes saved to " + selectedList);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage());
                }

                // Reload the updated services in the text area
                loadSelectedListServices(selectedList);
            } else {
                JOptionPane.showMessageDialog(this, "No services selected.");
            }
        }
    }


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
