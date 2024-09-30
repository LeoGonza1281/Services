import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ServiceListPanel extends JPanel {
    private JList<String> servicesList;
    private DefaultListModel<String> serviceModel;
    private JButton addButton;
    private JTextArea selectedServicesArea;

    public ServiceListPanel() {
        setLayout(new BorderLayout());

        // Obtener la lista de servicios desde el sistema utilizando ServiceFetcher
        serviceModel = new DefaultListModel<>();
        List<String> services = ServiceFetcher.fetchServices();
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
        System.out.println("dsdsajkajkkjdsakjsadsak");
        if (selectedServices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one service");
            return;
        }


        // Mostrar los servicios seleccionados
        for (String service : selectedServices) {
            selectedServicesArea.append(service + "\n");
            writeToFile(service + "\n");
        }

        // Aquí podrías guardar la lista de servicios para usarla más tarde
    }
    private void writeToFile(String serverName) {
        String serversFileName = "ServersFile.txt";
        try {
            System.out.println("Vamos a escribir al archivo");
            File serverFile = new File(serversFileName);
            FileWriter writer = new FileWriter(serverFile, true); // Append mode
            writer.write(serverName);
            writer.close();
            System.out.println("Finish writing the file");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
