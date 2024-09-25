import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartRunningPanel extends JPanel {
    private JComboBox<String> serverComboBox;
    private JComboBox<String> serviceListComboBox;
    private JButton startButton;

    public StartRunningPanel() {
        setLayout(new BorderLayout());

        // Combo box para seleccionar el servidor (ejemplo estático)
        serverComboBox = new JComboBox<>(new String[] { "Server01", "Server02" });
        add(new JLabel("Select Server:"), BorderLayout.WEST);
        add(serverComboBox, BorderLayout.CENTER);

        // Combo box para seleccionar la lista de servicios (ejemplo estático)
        serviceListComboBox = new JComboBox<>(new String[] { "List A", "List B" });
        add(new JLabel("Select Service List:"), BorderLayout.NORTH);
        add(serviceListComboBox, BorderLayout.SOUTH);

        // Botón para iniciar
        startButton = new JButton("Start Running");
        add(startButton, BorderLayout.EAST);

        // Acción del botón para iniciar el proceso
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServiceOnServer();
            }
        });
    }

    private void startServiceOnServer() {
        String selectedServer = (String) serverComboBox.getSelectedItem();
        String selectedServiceList = (String) serviceListComboBox.getSelectedItem();

        // Aquí llamas al script de PowerShell usando ProcessBuilder
        System.out.println("Starting services on " + selectedServer + " with " + selectedServiceList);
    }
}
