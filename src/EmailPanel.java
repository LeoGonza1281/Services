import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class EmailPanel extends JPanel {
    private DefaultListModel<String> emailListModel;
    private JList<String> emailList;
    private JTextField emailTextField;
    private File emailFile;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JLabel emailLabel;

    public EmailPanel() {
        setLayout(new BorderLayout());

        createEmailFile();

        emailListModel = new DefaultListModel<>();
        emailList = new JList<>(emailListModel);
        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        emailList.setCellRenderer(new CustomListCellRenderer());
        loadEmailsFromFile();

        JScrollPane scrollPane = new JScrollPane(emailList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Etiqueta "Email"
        emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // Campo de texto para el email
        emailTextField = new JTextField(20);
        emailTextField.setFont(new Font("Arial", Font.PLAIN, 16));

        // Crear botones
        addButton = createResponsiveButton("Add");
        editButton = createResponsiveButton("Edit");
        deleteButton = createResponsiveButton("Delete");

        bottomPanel.add(emailLabel);
        bottomPanel.add(emailTextField);
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Añadir acciones a los botones
        addButton.addActionListener(e -> addEmail());
        deleteButton.addActionListener(e -> deleteEmail());
        editButton.addActionListener(e -> editEmail());

        // Listener para ajustar tamaños dinámicamente
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                adjustComponentSizes();
            }
        });

        setPreferredSize(new Dimension(800, 600));
    }

    private JButton createResponsiveButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(120, 40)); // Tamaño inicial
        return button;
    }

    private void adjustComponentSizes() {
        int width = getWidth();
        int height = getHeight();

        // Ajustar tamaño de los botones basado en la ventana
        int buttonWidth = Math.max(120, width / 10);
        int buttonHeight = Math.max(40, height / 15);

        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);

        // Ajustar tamaño del texto en los botones
        int fontSize = Math.max(16, Math.min(width / 50, height / 30));
        Font buttonFont = new Font("Arial", Font.PLAIN, fontSize);
        addButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);

        // Ajustar tamaño del campo de texto
        int textFieldWidth = Math.max(20, width / 45);
        emailTextField.setColumns(textFieldWidth);
        emailTextField.setFont(new Font("Arial", Font.PLAIN, fontSize));

        // Ajustar tamaño de la etiqueta
        int labelFontSize = Math.max(12, fontSize - 4); // Hacer la etiqueta más pequeña
        emailLabel.setFont(new Font("Arial", Font.PLAIN, labelFontSize));

        // Ajustar tamaño del texto en la lista
        emailList.setFont(new Font("Arial", Font.PLAIN, fontSize));
        emailList.setFixedCellHeight(Math.max(30, height / 20)); // Altura dinámica para cada elemento de la lista

        revalidate(); // Actualizar el diseño
        repaint();
    }

    private void addEmail() {
        String newEmail = emailTextField.getText().trim();
        if (!newEmail.isEmpty() && isValidEmail(newEmail)) {
            emailListModel.addElement(newEmail);
            saveEmailsToFile();
            emailTextField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmail() {
        int selectedIndex = emailList.getSelectedIndex();
        if (selectedIndex != -1) {
            emailListModel.remove(selectedIndex);
            saveEmailsToFile();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an email to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editEmail() {
        int selectedIndex = emailList.getSelectedIndex();
        if (selectedIndex != -1) {
            String currentEmail = emailListModel.getElementAt(selectedIndex);
            String newEmail = JOptionPane.showInputDialog(this, "Edit email:", currentEmail);
            if (newEmail != null && !newEmail.trim().isEmpty() && isValidEmail(newEmail.trim())) {
                emailListModel.setElementAt(newEmail.trim(), selectedIndex);
                saveEmailsToFile();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an email to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }

    private void createEmailFile() {
        String userHome = System.getProperty("user.home");
        File documentsDir = new File(userHome, "Documents/StartServices/EmailList");
        if (!documentsDir.exists()) {
            documentsDir.mkdirs();
        }
        emailFile = new File(documentsDir, "Emails.txt");
        try {
            if (emailFile.createNewFile()) {
                System.out.println("Archivo Emails.txt creado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEmailsFromFile() {
        emailListModel.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(emailFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                emailListModel.addElement(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEmailsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(emailFile))) {
            for (int i = 0; i < emailListModel.size(); i++) {
                writer.write(emailListModel.getElementAt(i));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class CustomListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(list.getFont()); // Usar la fuente dinámica de la lista
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            if (isSelected) {
                label.setBackground(Color.decode("#d1e7fd"));
                label.setForeground(Color.BLACK);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }
            return label;
        }
    }
}
