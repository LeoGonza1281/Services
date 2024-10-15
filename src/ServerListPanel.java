import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ServerListPanel extends JPanel {
    private DefaultListModel<String> serverListModel;
    private JList<String> serverList; // Lista para mostrar los servidores
    private JButton addButton, editButton, eliminateButton; // Botones para las acciones
    private JTextField serverNameField; // Campo de texto para el nombre del servidor

    public ServerListPanel() {
        setLayout(new BorderLayout());

        // Crear el modelo de lista para servidores
        serverListModel = new DefaultListModel<>();
        serverList = new JList<>(serverListModel);
        serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Crear los botones
        addButton = new JButton("Add Server");
        editButton = new JButton("Edit Server");
        eliminateButton = new JButton("Eliminate Server");

        // Campo de texto para el nombre del servidor
        serverNameField = new JTextField(15);

        // Crear un panel para la entrada y los botones
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Server Name:"));
        inputPanel.add(serverNameField);
        inputPanel.add(addButton);
        inputPanel.add(editButton);
        inputPanel.add(eliminateButton);

        // Añadir acción para el botón "Add Server"
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverName = serverNameField.getText().trim();
                if (!serverName.isEmpty() && !serverListModel.contains(serverName)) {
                    serverListModel.addElement(serverName); // Agregar servidor a la lista
                    serverNameField.setText(""); // Limpiar el campo
                } else {
                    JOptionPane.showMessageDialog(ServerListPanel.this,
                            "Please enter a valid server name or it already exists.");
                }
            }
        });

        // Añadir acción para el botón "Edit Server"
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedServer = serverList.getSelectedValue();
                String newServerName = serverNameField.getText().trim();
                if (selectedServer != null && !newServerName.isEmpty()) {
                    serverListModel.setElementAt(newServerName, serverList.getSelectedIndex()); // Editar servidor
                    serverNameField.setText(""); // Limpiar el campo
                } else {
                    JOptionPane.showMessageDialog(ServerListPanel.this,
                            "Please select a server to edit and enter a new name.");
                }
            }
        });

        // Añadir acción para el botón "Eliminate Server"
        eliminateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedServer = serverList.getSelectedValue();
                if (selectedServer != null) {
                    serverListModel.removeElement(selectedServer); // Eliminar servidor de la lista
                    serverNameField.setText(""); // Limpiar el campo
                } else {
                    JOptionPane.showMessageDialog(ServerListPanel.this,
                            "Please select a server to eliminate.");
                }
            }
        });

        // Añadir los componentes al panel
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(serverList), BorderLayout.CENTER); // Añadir JScrollPane para el JList
    }

    public List<String> getRegisteredServers() {
        return (List<String>) serverList;
    }
}
