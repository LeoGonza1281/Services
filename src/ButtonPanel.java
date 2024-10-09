import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonPanel extends JPanel {
    public JButton setupServerButton;
    public JButton createServiceListButton;
    public JButton startRunningButton;
    public JButton createReportButton;

    public ButtonPanel() {
        // Use GridBagLayout for flexible layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons fill horizontally
        gbc.anchor = GridBagConstraints.CENTER;

        // Initialize buttons with text
        setupServerButton = new JButton("Setup Server");
        createServiceListButton = new JButton("Create ServiceList");
        startRunningButton = new JButton("Start Running");
        createReportButton = new JButton("Create Report");

        // Set consistent button properties
        JButton[] buttons = {setupServerButton, createServiceListButton, startRunningButton, createReportButton};
        for (JButton button : buttons) {
            button.setForeground(Color.BLACK);
            button.setFont(new Font("Arial", Font.BOLD, 14)); // Set a readable font size
            button.setPreferredSize(new Dimension(200, 40)); // Set a preferred size
            button.setMinimumSize(new Dimension(200, 40)); // Minimum size to ensure text fits
            button.setMaximumSize(new Dimension(200, 40)); // Maximum size to prevent overflow
        }

        // Add buttons to the panel
        for (JButton button : buttons) {
            add(button, gbc);
            gbc.gridy++;
        }

        // Action for the "Create Report" button
        createReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getSource());
                createReport();
            }
        });
    }

    private void createReport() {
        // Create an instance of the report generator
        ReportGenerator reportGenerator = new ReportGenerator();
        // Call the method to generate the report
        String reportStatus = reportGenerator.generateReport();
        // Show the status in a dialog box
        JOptionPane.showMessageDialog(this, reportStatus);
    }
}
