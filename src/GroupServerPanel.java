import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GroupServerPanel extends JPanel {
    private JTextArea textArea; // Área para mostrar el contenido del archivo

    public GroupServerPanel() {
        setLayout(new GridLayout(1, 2)); // Dos columnas

        // Panel para Grupos y Servidores
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(2, 1)); // Dos secciones (Grupos y Servidores)

        // Panel para Grupos
        JPanel groupPanel = new JPanel();
        groupPanel.setBorder(BorderFactory.createTitledBorder("Grupos"));
        groupPanel.setLayout(new GridLayout(4, 1)); // 4 filas: 1 título y 3 botones

        JButton addGroupButton = new JButton("Add");
        JButton editGroupButton = new JButton("Edit");
        JButton deleteGroupButton = new JButton("Delete");

        groupPanel.add(addGroupButton);
        groupPanel.add(editGroupButton);
        groupPanel.add(deleteGroupButton);

        // Panel para Servidores
        JPanel serverPanel = new JPanel();
        serverPanel.setBorder(BorderFactory.createTitledBorder("Servidores"));
        serverPanel.setLayout(new GridLayout(4, 1)); // 4 filas: 1 título y 3 botones

        JButton addServerButton = new JButton("Add");
        JButton editServerButton = new JButton("Edit");
        JButton deleteServerButton = new JButton("Delete");

        serverPanel.add(addServerButton);
        serverPanel.add(editServerButton);
        serverPanel.add(deleteServerButton);

        // Añadir los paneles de Grupos y Servidores al panel izquierdo
        leftPanel.add(groupPanel);
        leftPanel.add(serverPanel);

        // Área de texto para mostrar el contenido del archivo
        textArea = new JTextArea();
        textArea.setEditable(false); // Hacer el área de texto no editable
        JScrollPane scrollPane = new JScrollPane(textArea); // Para hacer scroll

        // Añadir ambos paneles al panel principal
        add(leftPanel);
        add(scrollPane);
    }

    // Método para cargar el contenido del archivo en el área de texto
    public void loadFileContent(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.setText(""); // Limpiar el área de texto
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append(line + "\n"); // Añadir cada línea al área de texto
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
            }
        } else {
            textArea.setText("File does not exist."); // Mensaje si el archivo no existe
        }
    }
}
