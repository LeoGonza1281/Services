import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class StartRunningPanel extends JPanel {
    private JComboBox<String> serverComboBox;
    private JList<String> serviceList;
    private DefaultListModel<String> serviceListModel;
    private JButton startButton;

    public StartRunningPanel() {
        setLayout(new BorderLayout());

        // Combo box para seleccionar el servidor (ejemplo estático)
        serverComboBox = new JComboBox<>(new String[]{"Server01", "Server02"});
        JPanel serverPanel = new JPanel(new FlowLayout());
        serverPanel.add(new JLabel("Select Server:"));
        serverPanel.add(serverComboBox);
        add(serverPanel, BorderLayout.NORTH);

        // Lista para seleccionar los servicios disponibles
        serviceListModel = new DefaultListModel<>();
        serviceList = new JList<>(serviceListModel);
        serviceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(serviceList), BorderLayout.CENTER);

        // Botón para iniciar el proceso
        startButton = new JButton("Start Running");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Acción del botón para iniciar el proceso
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServiceOnServer();
            }
        });

        // Cargar servicios dinámicamente
        loadServices();
    }

    // Método para cargar los servicios disponibles
    private void loadServices() {
        List<String> services = ServiceFetcher.fetchServices(); // Obtener servicios del sistema
        for (String service : services) {
            serviceListModel.addElement(service); // Agregar cada servicio a la lista
        }
    }

    // Método para iniciar los servicios seleccionados en el servidor
    private void startServiceOnServer() {
        String selectedServer = (String) serverComboBox.getSelectedItem();
        List<String> selectedServices = serviceList.getSelectedValuesList();

        if (selectedServices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one service");
            return;
        }

        // Ruta del script de PowerShell
        String scriptPath = "C:\\ruta\\a\\tu\\script\\Start-Service.ps1"; // Cambia esta ruta según tu configuración

        // Crear el comando de PowerShell con los servicios seleccionados
        String serviceNames = String.join(",", selectedServices);

        // Crear el proceso para ejecutar el script de PowerShell
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", scriptPath, selectedServer, serviceNames);

        try {
            // Iniciar el proceso
            Process process = processBuilder.start();

            // Leer la salida del proceso
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                JOptionPane.showMessageDialog(this, "Services started successfully on " + selectedServer + ":\n" + output.toString());
            } else {
                JOptionPane.showMessageDialog(this, "Error starting services. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
        }
    }
}
