import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ManageReportsPanel extends JPanel {
    private JTextField reportNameTextField;    // Campo de texto para ingresar el nombre del reporte
    private JComboBox<String> serviceComboBox; // ComboBox para seleccionar el servicio
    private JComboBox<String> groupComboBox;  // ComboBox para seleccionar el grupo
    private JButton generateReportButton;      // Botón para generar el reporte
    private static final String BASE_DIR = System.getProperty("user.home") + "/Documents/StartServices/";

    public ManageReportsPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Aumentar espaciado
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fuente grande para los componentes
        Font largeFont = new Font("Arial", Font.BOLD, 16);

        // Panel para ingresar el nombre del reporte
        JLabel reportNameLabel = new JLabel("Report Name:");
        reportNameLabel.setFont(largeFont);
        reportNameTextField = new JTextField();
        reportNameTextField.setFont(largeFont);
        reportNameTextField.setPreferredSize(new Dimension(400, 50)); // Tamaño más grande
        addComponent(reportNameLabel, gbc, 0, 0);
        addComponent(reportNameTextField, gbc, 1, 0);

        // Panel de Selección de "Service"
        JLabel serviceLabel = new JLabel("Select Services List:");
        serviceLabel.setFont(largeFont);
        serviceComboBox = new JComboBox<>(getFilteredServices());
        serviceComboBox.setFont(largeFont);
        serviceComboBox.setPreferredSize(new Dimension(400, 50)); // Tamaño más grande
        addComponent(serviceLabel, gbc, 0, 1);
        addComponent(serviceComboBox, gbc, 1, 1);

        // Panel de Selección de "Group"
        JLabel groupLabel = new JLabel("Select Group:");
        groupLabel.setFont(largeFont);
        groupComboBox = new JComboBox<>(getFilteredGroups());
        groupComboBox.setFont(largeFont);
        groupComboBox.setPreferredSize(new Dimension(400, 50)); // Tamaño más grande
        addComponent(groupLabel, gbc, 0, 2);
        addComponent(groupComboBox, gbc, 1, 2);

        // Botón para generar el reporte
        generateReportButton = new JButton("Generate Report");
        generateReportButton.setFont(largeFont);
        generateReportButton.setPreferredSize(new Dimension(200, 50)); // Tamaño más grande
        generateReportButton.addActionListener(e -> generateReport());
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(generateReportButton, gbc);
    }

    private void addComponent(Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        add(comp, gbc);
    }

    private String[] getFilteredServices() {
        File folder = new File(BASE_DIR + "CreateServiceList/");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt") && !name.equalsIgnoreCase("List.txt"));
        return getFileNamesWithoutExtension(files);
    }

    private String[] getFilteredGroups() {
        File folder = new File(BASE_DIR + "SetupServer/");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt") && name.split("\\.").length > 2);
        return getFileNamesWithoutExtension(files);
    }

    private String[] getFileNamesWithoutExtension(File[] files) {
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName().replace(".txt", ""));
            }
        }
        return fileNames.toArray(new String[0]);
    }

    private void generateReport() {
        String selectedService = (String) serviceComboBox.getSelectedItem();
        String selectedGroup = (String) groupComboBox.getSelectedItem();
        String reportName = reportNameTextField.getText().trim();

        if (selectedService == null || selectedGroup == null || reportName.isEmpty()) {
            showErrorDialog("Please select service, group, and provide a report name.");
            return;
        }

        String groupFilePath = BASE_DIR + "SetupServer/" + selectedGroup + ".txt";
        List<String> servers = readLinesFromFile(groupFilePath);

        if (servers.isEmpty()) {
            showErrorDialog("No servers found.");
            return;
        }

        String reportPath = BASE_DIR + "reports/" + reportName + "_Report.txt";
        createReport(reportPath, servers, selectedService);
        JOptionPane.showMessageDialog(this, "Report generated successfully!");
    }

    private List<String> readLinesFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            showErrorDialog("Error reading file: " + filePath);
        }
        return lines;
    }

    private void createReport(String reportPath, List<String> servers, String service) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportPath))) {
            writer.write("Report for Service: " + service + "\n");
            writer.write("Selected Group: " + groupComboBox.getSelectedItem() + "\n");
            writer.write("\nList of Servers:\n");
            for (String server : servers) {
                writer.write("- " + server + "\n");
            }
        } catch (IOException e) {
            showErrorDialog("Error creating report: " + e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
