import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerPanel extends JPanel {
    private List<String> registeredServers;
    private JButton setupServerButton, addButton, editButton;
    private JComboBox<String> environmentComboBox;
    private JTextArea documentTextArea;
    private String fileName;

    public ServerPanel() {
        registeredServers = new ArrayList<>();

        // Crear botones
        setupServerButton = new JButton("Setup Server");
        addButton = new JButton("Add Server");
        editButton = new JButton("Edit Server");

        String[] environments = {"Development", "Preproductión", "Productión", "All Environments"};
        environmentComboBox = new JComboBox<>(environments);
        JPanel environmentPanel = new JPanel(new FlowLayout());
        environmentPanel.add(new JLabel("Select Environment:"));
        environmentPanel.add(environmentComboBox);
        add(environmentPanel, BorderLayout.WEST);

        // Establecer tamaño para los botones
        Dimension buttonSize = new Dimension(150, 30);
        setupServerButton.setPreferredSize(buttonSize);
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);

        // Panel para botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(setupServerButton);
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
        setupServerButton.addActionListener(e -> executePowerShellScript());
        addButton.addActionListener(e -> addServerToFile());
        editButton.addActionListener(e -> openEditServerWindow());
    }

    private void loadTextFileContent() {
        // Carga el contenido del archivo
        if (fileName == null) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            registeredServers.clear(); // Limpiar la lista antes de cargar
            while ((line = reader.readLine()) != null) {
                documentTextArea.append(line + "\n");
                if (line.startsWith("Server: ")) {
                    registeredServers.add(line.replace("Server: ", "").trim());
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "File not found: " + fileName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private void addServerToFile() {
        // Agrega un servidor al archivo
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
                registeredServers.add(serverName);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter the server name.");
            }
        }
    }

    private void appendToTextFile(String content) {
        // Agrega contenido al archivo
        File file = new File(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
        }
    }

    private void openEditServerWindow() {
        // Abre la ventana para editar un servidor
        if (registeredServers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No servers to edit.");
            return;
        }

        String[] serverArray = registeredServers.toArray(new String[0]);
        String serverToEdit = (String) JOptionPane.showInputDialog(
                this,
                "Select a server to edit:",
                "Edit Server",
                JOptionPane.PLAIN_MESSAGE,
                null,
                serverArray,
                serverArray[0]);

        if (serverToEdit != null && !serverToEdit.isEmpty()) {
            JPanel inputPanel = new JPanel(new GridLayout(1, 2));
            JTextField newServerNameField = new JTextField(serverToEdit);
            inputPanel.add(new JLabel("New Server Name:"));
            inputPanel.add(newServerNameField);

            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Edit Server", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newServerName = newServerNameField.getText().trim();

                if (!newServerName.isEmpty()) {
                    // Actualizar el archivo y la lista de servidores
                    registeredServers.remove(serverToEdit);
                    registeredServers.add(newServerName);
                    updateTextFileContent(serverToEdit, newServerName);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid server name.");
                }
            }
        }
    }

    private void updateTextFileContent(String oldServer, String newServer) {
        // Actualiza el contenido del archivo
        File file = new File(fileName);
        StringBuilder updatedContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Server: " + oldServer)) {
                    line = "Server: " + newServer;
                }
                updatedContent.append(line).append("\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(updatedContent.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
        }

        // Actualizar el área de texto con el contenido editado
        documentTextArea.setText(updatedContent.toString());
    }

    // Acción para ejecutar el script PowerShell
    private void executePowerShellScript() {
        if (registeredServers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No servers to execute the script on.");
            return;
        }

        String scriptPath = "c:\\Scripts\\DiskCollect.ps1";  // Ruta de tu script PowerShell
        String[] servers = registeredServers.toArray(new String[0]);

        // Construir el comando de PowerShell
        String command = buildPowerShellCommand(servers, scriptPath);
        try {
            // Ejecutar el comando de PowerShell
            Process process = Runtime.getRuntime().exec(command);

            // Leer la salida
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Leer los errores
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                output.append("ERROR: ").append(line).append("\n");
            }

            int exitCode = process.waitFor();
            JOptionPane.showMessageDialog(this, "Script executed with exit code: " + exitCode + "\n" + output.toString());

        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(this, "Error executing PowerShell script: " + e.getMessage());
        }
    }

    private String buildPowerShellCommand(String[] servers, String scriptPath) {
        StringBuilder command = new StringBuilder("powershell.exe Invoke-Command -ComputerName ");

        for (int i = 0; i < servers.length; i++) {
            command.append(servers[i]);
            if (i < servers.length - 1) {
                command.append(",");
            }
        }

        command.append(" -FilePath ").append(scriptPath);
        return command.toString();
    }

    public List<String> getRegisteredServers() {
        return registeredServers; // Devuelve la lista de servidores
    }
}