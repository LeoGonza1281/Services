import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StartRunningPanel extends JPanel {
    private JComboBox<String> serverComboBox;
    private JComboBox<String> environmentComboBox;
    private JTextField serviceField;
    private JRadioButton primaryButton;
    private JRadioButton secondaryButton;
    private ButtonGroup serviceGroup;


    public StartRunningPanel(List<String> availableServers) {
        setLayout(new BorderLayout());

        // ComboBox de servidores
        serverComboBox = new JComboBox<>(availableServers.toArray(new String[0]));
        JPanel serverPanel = new JPanel(new FlowLayout());
        serverPanel.add(new JLabel("Select Server:"));
        serverPanel.add(serverComboBox);
        add(serverPanel, BorderLayout.NORTH);

        // ComboBox de ambientes (Desarrollo, Preproducción, Producción)
        String[] environments = {"Development", "Preproductión", "Productión", "All Environments"};
        environmentComboBox = new JComboBox<>(environments);
        JPanel environmentPanel = new JPanel(new FlowLayout());
        environmentPanel.add(new JLabel("Select Environment:"));
        environmentPanel.add(environmentComboBox);
        add(environmentPanel, BorderLayout.CENTER);

        // Campo para ingresar el nombre del servicio
        serviceField = new JTextField(20);
        JPanel servicePanel = new JPanel(new FlowLayout());
        servicePanel.add(new JLabel("Service to Restart:"));
        servicePanel.add(serviceField);
        add(servicePanel, BorderLayout.SOUTH);

        // RadioButtons para seleccionar si es Primario o Secundario
        primaryButton = new JRadioButton("Primary");
        secondaryButton = new JRadioButton("Secondary");
        serviceGroup = new ButtonGroup();
        serviceGroup.add(primaryButton);
        serviceGroup.add(secondaryButton);

        JPanel rolePanel = new JPanel(new FlowLayout());
        rolePanel.add(new JLabel("Service Type:"));
        rolePanel.add(primaryButton);
        rolePanel.add(secondaryButton);
        add(rolePanel, BorderLayout.EAST);

        // Puedes agregar más elementos aquí, como un botón de confirmación
    }

    // Método para actualizar la lista de servidores cuando se registra un nuevo servidor
    public void updateServerList(List<String> servers) {
        serverComboBox.removeAllItems();
        for (String server : servers) {
            serverComboBox.addItem(server);
        }
    }

    // Método para obtener los valores seleccionados
    public String getSelectedEnvironment() {
        return (String) environmentComboBox.getSelectedItem();
    }

    public String getSelectedServer() {
        return (String) serverComboBox.getSelectedItem();
    }

    public String getServiceToRestart() {
        return serviceField.getText();
    }

    public String getServiceType() {
        if (primaryButton.isSelected()) {
            return "Primary";
        } else if (secondaryButton.isSelected()) {
            return "Secondary";
        } else {
            return null; // No se ha seleccionado ningún tipo
        }
    }
}
