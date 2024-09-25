import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        // Ruta del script de PowerShell
        String scriptPath = "C:\\ruta\\a\\tu\\script\\Start-Service.ps1"; // Cambia esta ruta según tu configuración

        // Crear el proceso para ejecutar el script de PowerShell
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", scriptPath, selectedServer, selectedServiceList);

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
                JOptionPane.showMessageDialog(this, "Services started successfully:\n" + output.toString());
            } else {
                JOptionPane.showMessageDialog(this, "Error starting services. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
        }
    }
}
