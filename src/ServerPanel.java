import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerPanel extends JPanel {
    private List<String> registeredServers;
    private JButton selectEnvButton, addButton, editButton;
    private JTextArea documentTextArea;
    private String fileName;

    public ServerPanel() {
        registeredServers = new ArrayList<>();

        // Crear botones
        selectEnvButton = new JButton("Select Environment");
        addButton = new JButton("Add server");
        editButton = new JButton("Edit server");

        // Establecer tamaño para los botones
        Dimension buttonSize = new Dimension(150, 30);
        selectEnvButton.setPreferredSize(buttonSize);
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);

        // Panel para botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(selectEnvButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(editButton);

        // Layout principal
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.WEST);

        // Área de texto
        documentTextArea = new JTextArea(20, 30);
        JScrollPane scrollPane = new JScrollPane(documentTextArea);
        add(scrollPane, BorderLayout.CENTER);

        // Acciones de los botones
        selectEnvButton.addActionListener(e -> selectEnvironment());
        addButton.addActionListener(e -> addServerToFile());
        editButton.addActionListener(e -> openEditServerWindow());
    }

    private void selectEnvironment() {
        String[] options = {"Developing", "Preproduction", "Production"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Select an environment:",
                "Environment Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                fileName = "Developing.txt";
                break;
            case 1:
                fileName = "Preproduction.txt";
                break;
            case 2:
                fileName = "Production.txt";
                break;
            default:
                fileName = null;
                return;
        }

        documentTextArea.setText("");
        loadTextFileContent();
        JOptionPane.showMessageDialog(this, "Selected environment: " + fileName);
    }

    private void loadTextFileContent() {
        if (fileName == null) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                documentTextArea.append(line + "\n");
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "File not found: " + fileName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private void addServerToFile() {
        if (fileName == null) {
            JOptionPane.showMessageDialog(this, "Please select an environment first.");
            return;
        }

        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        JTextField serverNameField = new JTextField();
        inputPanel.add(new JLabel("Server Name:"));
        inputPanel.add(serverNameField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Server", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String serverName = serverNameField.getText().trim();

            if (!serverName.isEmpty()) {
                String contentToAdd = "Server: " + serverName;
                documentTextArea.append(contentToAdd + "\n");
                appendToTextFile(contentToAdd);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter the server name.");
            }
        }
    }

    private void appendToTextFile(String content) {
        File file = new File(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
        }
    }

    // Abrir ventana para editar servidores
    private void openEditServerWindow() {
        if (fileName == null) {
            JOptionPane.showMessageDialog(this, "Please select an environment first.");
            return;
        }

        List<String> servers = loadServersFromFile();
        if (servers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No servers found to edit.");
            return;
        }

        // Crear la ventana para editar servidores
        JFrame editFrame = new JFrame("Edit Servers");
        editFrame.setSize(400, 300);
        editFrame.setLayout(new BorderLayout());

        // Crear la lista de servidores
        JList<String> serverList = new JList<>(servers.toArray(new String[0]));
        JScrollPane scrollPane = new JScrollPane(serverList);
        editFrame.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botón "Save"
        JPanel bottomPanel = new JPanel();
        JButton saveButton = new JButton("Edit");
        bottomPanel.add(saveButton);
        editFrame.add(bottomPanel, BorderLayout.SOUTH);

        // Acción del botón Save
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener el servidor seleccionado
                String selectedServer = serverList.getSelectedValue();
                if (selectedServer != null) {
                    // Permitir editar el servidor seleccionado
                    String newServerName = JOptionPane.showInputDialog(editFrame, "Edit Server Name:", selectedServer);
                    if (newServerName != null && !newServerName.trim().isEmpty()) {
                        updateServerInFile(selectedServer, newServerName.trim());
                        documentTextArea.setText(""); // Limpiar el área de texto
                        loadTextFileContent(); // Recargar el contenido actualizado
                        editFrame.dispose(); // Cerrar la ventana de edición
                    }
                }
            }
        });

        editFrame.setVisible(true);
    }

    // Cargar servidores del archivo de texto
    private List<String> loadServersFromFile() {
        List<String> servers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Server:")) {
                    servers.add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
        return servers;
    }

    // Actualizar el archivo de texto con el nuevo nombre del servidor
    private void updateServerInFile(String oldServer, String newServer) {
        File file = new File(fileName);
        List<String> updatedLines = new ArrayList<>();

        // Leer todas las líneas del archivo
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(oldServer)) {
                    updatedLines.add("Server: " + newServer);
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
            return;
        }

        // Escribir las líneas actualizadas de nuevo en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
        }
    }

    public List<String> getRegisteredServers() {
        return registeredServers;
    }
}
