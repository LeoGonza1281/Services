import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServiceListPanel extends JPanel {
    private JList<String> servicesList;
    private DefaultListModel<String> serviceModel;
    private JButton addButton;
    private JTextArea selectedServicesArea;

    public ServiceListPanel() {
        setLayout(new BorderLayout());

        // Lista de servicios disponibles (ejemplo estático)
        serviceModel = new DefaultListModel<>();
        serviceModel.addElement("Service A");
        serviceModel.addElement("Service B");
        serviceModel.addElement("Service C");
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
        java.util.List<String> selectedServices = servicesList.getSelectedValuesList();
        if (selectedServices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one service");
            return;
        }

        // Mostrar los servicios seleccionados
        for (String service : selectedServices) {
            selectedServicesArea.append(service + "\n");
        }

        // Aquí podrías guardar la lista de servicios para usarla más tarde
    }
}
