import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StartRunningPanel extends JPanel {
    private JComboBox<String> serverComboBox;

    public StartRunningPanel(List<String> availableServers) {
        setLayout(new BorderLayout());

        // ComboBox de servidores
        serverComboBox = new JComboBox<>(availableServers.toArray(new String[0]));
        JPanel serverPanel = new JPanel(new FlowLayout());
        serverPanel.add(new JLabel("Select Server:"));
        serverPanel.add(serverComboBox);
        add(serverPanel, BorderLayout.NORTH);

        // Puedes agregar más elementos aquí, como la lista de servicios o el botón de inicio
    }

    // Método para actualizar la lista de servidores cuando se registra un nuevo servidor
    public void updateServerList(List<String> servers) {
        serverComboBox.removeAllItems();
        for (String server : servers) {
            serverComboBox.addItem(server);
        }
    }
}
