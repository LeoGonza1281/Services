import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerPanel extends JPanel {
    private List<String> registeredServers;
    private JTextField serverNameField;
    private JButton selectEnvButton, addButton, editButton;
    private DefaultListModel<String> serverListModel;
    private JTextArea documentTextArea;
    private String fileName; // Archivo de texto seleccionado

    public ServerPanel() {
        registeredServers = new ArrayList<>();
        serverNameField = new JTextField(15); // Aquí es donde se ingresa el servidor

        // Crear botones
        selectEnvButton = new JButton("Select Environment");
        addButton = new JButton("Add server");
        editButton = new JButton("Edit server");

        // Establecer el mismo tamaño para todos los botones
        Dimension buttonSize = new Dimension(150, 30); // Definir un tamaño fijo
        selectEnvButton.setPreferredSize(buttonSize);
        selectEnvButton.setMinimumSize(buttonSize);
        selectEnvButton.setMaximumSize(buttonSize);

        addButton.setPreferredSize(buttonSize);
        addButton.setMinimumSize(buttonSize);
        addButton.setMaximumSize(buttonSize);

        editButton.setPreferredSize(buttonSize);
        editButton.setMinimumSize(buttonSize);
        editButton.setMaximumSize(buttonSize);

        // Usar un panel con BoxLayout para alinear los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(selectEnvButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio entre botones
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio entre botones
        buttonPanel.add(editButton);

        serverListModel = new DefaultListModel<>();
        JList<String> serverList = new JList<>(serverListModel);

        // Usar BorderLayout para dividir la pantalla
        setLayout(new BorderLayout());

        // Añadir panel de botones a la izquierda
        add(buttonPanel, BorderLayout.WEST);

        // Crear un área de texto para el documento
        documentTextArea = new JTextArea(20, 30); // Área de texto con un ancho ajustado
        JScrollPane scrollPane = new JScrollPane(documentTextArea); // Añadir scroll al área de texto
        add(scrollPane, BorderLayout.CENTER);

        // Acción para seleccionar el entorno
        selectEnvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectEnvironment();
            }
        });

        // Acción para añadir servidor
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addServerToTextAreaAndFile(); // Agregar texto al JTextArea y al archivo
            }
        });
    }

    // Seleccionar el entorno (archivo de texto)
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

        // Limpiar el área de texto y cargar el archivo seleccionado
        documentTextArea.setText("");
        loadTextFileContent();
    }

    // Agrega el texto ingresado al JTextArea y al archivo de texto
    private void addServerToTextAreaAndFile() {
        if (fileName == null) {
            JOptionPane.showMessageDialog(this, "Please select an environment first.");
            return;
        }

        String serverName = serverNameField.getText().trim();
        if (!serverName.isEmpty()) {
            // Añadir el servidor al JTextArea
            documentTextArea.append(serverName + "\n");

            // Limpiar el campo de texto
            serverNameField.setText("");

            // También añadir al archivo de texto
            appendToTextFile(serverName);
        }
    }

    // Añade contenido al archivo de texto seleccionado
    private void appendToTextFile(String content) {
        File file = new File(fileName);
        System.out.println("Attempting to write to file: " + file.getAbsolutePath()); // Línea de depuración

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
            writer.newLine(); // Añadir nueva línea
            System.out.println("Server added: " + content); // Línea de depuración
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Carga el contenido del archivo de texto en el JTextArea
    private void loadTextFileContent() {
        if (fileName == null) return;

        // Leer el archivo y mostrar su contenido en el JTextArea
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                documentTextArea.append(line + "\n");
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "File not found: " + fileName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getRegisteredServers() {
        return registeredServers;
    }
}
