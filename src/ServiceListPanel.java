import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ServiceListPanel extends JPanel {
    private JList<String> servicesList;
    private DefaultListModel<String> serviceModel;
    private JButton addButton;
    private JTextArea selectedServicesArea;
    private JTextField searchField; // Campo de texto para la búsqueda

    public ServiceListPanel() {
        setLayout(new BorderLayout());

        // Panel para la búsqueda
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        // Campo de búsqueda
        searchField = new JTextField(15);
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterServices();
            }
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH); // Agregar panel de búsqueda en la parte superior

        // Obtener la lista de servicios desde el sistema utilizando ServiceFetcher
        serviceModel = new DefaultListModel<>();
        List<String> services = ServiceFetcher.fetchServices(); // Aquí suponemos que ServiceFetcher devuelve una lista de servicios
        for (String service : services) {
            serviceModel.addElement(service);
        }

        servicesList = new JList<>(serviceModel);
        servicesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(servicesList), BorderLayout.CENTER);

        // Botón para añadir servicios a la lista seleccionada
        addButton = new JButton("Add to ServiceList");
        add(addButton, BorderLayout.SOUTH);

        // Área de texto para mostrar los servicios seleccionados
        selectedServicesArea = new JTextArea(10, 30);
        add(new JScrollPane(selectedServicesArea), BorderLayout.EAST);

        // Acción del botón para añadir servicios
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSelectedServices();
            }
        });
    }

    private void addSelectedServices() {
        // Obtener los servicios seleccionados
        List<String> selectedServices = servicesList.getSelectedValuesList();
        if (selectedServices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one service");
            return;
        }

        // Mostrar los servicios seleccionados y escribir en el archivo
        for (String service : selectedServices) {
            selectedServicesArea.append(service + "\n");
            writeToFile(service + "\n");
        }
    }

    private void filterServices() {
        String searchText = searchField.getText().toLowerCase(); // Obtener texto de búsqueda
        DefaultListModel<String> filteredModel = new DefaultListModel<>(); // Crear nuevo modelo filtrado

        // Filtrar los servicios
        for (int i = 0; i < serviceModel.size(); i++) {
            String service = serviceModel.getElementAt(i);
            if (service.toLowerCase().contains(searchText)) {
                filteredModel.addElement(service); // Agregar servicios que coincidan
            }
        }

        // Actualizar la lista de servicios
        servicesList.setModel(filteredModel);
    }

    // Método para escribir los servicios en el archivo ServersList.txt
    private void writeToFile(String serviceName) {
        String serversFileName = "ServersList.txt"; // Archivo donde se escribirán los servicios

        try (FileWriter writer = new FileWriter(serversFileName, true)) { // Modo 'true' para añadir al final del archivo
            writer.write(serviceName); // Escribir el nombre del servicio
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
        }
    }
}
