import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ManageReportsPanel extends JPanel {
    private JTextField reportNameTextField;
    private JComboBox<String> serviceComboBox;
    private JComboBox<String> groupComboBox;
    private JButton generateReportButton;
    private JButton viewReportButton;
    private static final String BASE_DIR = System.getProperty("user.home") + "/Documents/StartServices/";
    private static final String REPORTS_DIR = BASE_DIR + "CreateReports/";

    public ManageReportsPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font largeFont = new Font("Arial", Font.BOLD, 16);

        JLabel reportNameLabel = new JLabel("Report Name:");
        reportNameLabel.setFont(largeFont);
        reportNameTextField = new JTextField();
        reportNameTextField.setFont(largeFont);
        reportNameTextField.setPreferredSize(new Dimension(400, 50));
        addComponent(reportNameLabel, gbc, 0, 0);
        addComponent(reportNameTextField, gbc, 1, 0);

        JLabel serviceLabel = new JLabel("Select Service:");
        serviceLabel.setFont(largeFont);
        serviceComboBox = new JComboBox<>(getFilteredServices());
        serviceComboBox.setFont(largeFont);
        serviceComboBox.setPreferredSize(new Dimension(400, 50));
        addComponent(serviceLabel, gbc, 0, 1);
        addComponent(serviceComboBox, gbc, 1, 1);

        JLabel groupLabel = new JLabel("Select Group:");
        groupLabel.setFont(largeFont);
        groupComboBox = new JComboBox<>(getFilteredGroups());
        groupComboBox.setFont(largeFont);
        groupComboBox.setPreferredSize(new Dimension(400, 50));
        addComponent(groupLabel, gbc, 0, 2);
        addComponent(groupComboBox, gbc, 1, 2);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        generateReportButton = new JButton("Generate Report");
        generateReportButton.setFont(largeFont);
        generateReportButton.setPreferredSize(new Dimension(200, 50));
        generateReportButton.addActionListener(e -> generateReport());

        viewReportButton = new JButton("View Reports");
        viewReportButton.setFont(largeFont);
        viewReportButton.setPreferredSize(new Dimension(200, 50));
        viewReportButton.addActionListener(e -> viewReports());

        buttonPanel.add(generateReportButton);
        buttonPanel.add(viewReportButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    private void addComponent(Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        add(comp, gbc);
    }
    public void updateAllComboboxes() {
        serviceComboBox.setModel(new DefaultComboBoxModel<>(getFilteredServices()));
        groupComboBox.setModel(new DefaultComboBoxModel<>(getFilteredGroups()));
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

        File reportDir = new File(REPORTS_DIR);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        String reportFilePath = REPORTS_DIR + reportName + "_Report.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFilePath))) {
            writer.write(selectedService + "\n");
            writer.write( selectedGroup + "\n");
        } catch (IOException e) {
            showErrorDialog("Error writing report file: " + e.getMessage());
        }
        JOptionPane.showMessageDialog(this, "Report generated successfully!");
    }

    private void viewReports() {
        try {
            File reportsDir = new File(REPORTS_DIR);
            if (!reportsDir.exists() || reportsDir.listFiles().length == 0) {
                showErrorDialog("No reports available.");
                return;
            }
            Desktop.getDesktop().open(reportsDir);
        } catch (IOException e) {
            showErrorDialog("Error opening reports folder: " + e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
