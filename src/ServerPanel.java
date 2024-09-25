import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerPanel extends JPanel {
    private JTextField serverNameField;
    private JTextField ipField;
    private JTextField osField;
    private JButton saveButton;
    private JTextArea displayArea;

    public ServerPanel() {
        setLayout(new BorderLayout());

        // Crear campos para ingresar los datos del servidor
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Server Name:"));
        serverNameField = new JTextField();
        formPanel.add(serverNameField);

        formPanel.add(new JLabel("IP Address:"));
        ipField = new JTextField();
        formPanel.add(ipField);

        formPanel.add(new JLabel("Operating System:"));
        osField = new JTextField();
        formPanel.add(osField);

        add(formPanel, BorderLayout.CENTER);

        // Botón para guardar el servidor
        saveButton = new JButton("Register Server");
        add(saveButton, BorderLayout.SOUTH);

        // Área de texto para mostrar los servidores registrados
        displayArea = new JTextArea(10, 30);
        add(new JScrollPane(displayArea), BorderLayout.EAST);

        // Acción del botón para registrar el servidor
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerServer();
            }
        });
    }

    private void registerServer() {
        // Obtener los datos ingresados
        String serverName = serverNameField.getText();
        String ip = ipField.getText();
        String os = osField.getText();

        // Validar los campos (esto se puede extender con más validaciones)
        if (serverName.isEmpty() || ip.isEmpty() || os.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }

        // Guardar la información (puede ser en un archivo o base de datos)
        // Aquí lo mostramos en el área de texto por simplicidad
        displayArea.append("Server Registered: \n");
        displayArea.append("Name: " + serverName + "\n");
        displayArea.append("IP: " + ip + "\n");
        displayArea.append("OS: " + os + "\n\n");

        // Limpiar los campos
        serverNameField.setText("");
        ipField.setText("");
        osField.setText("");
    }
}
