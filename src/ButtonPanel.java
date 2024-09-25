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
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        setupServerButton = new JButton("Setup Server");
        createServiceListButton = new JButton("Create ServiceList");
        startRunningButton = new JButton("Start Running");
        createReportButton = new JButton("Create Report");

        for (JButton button : new JButton[]{setupServerButton, createServiceListButton, startRunningButton, createReportButton}) {
            button.setPreferredSize(new Dimension(120, 30));
            button.setForeground(Color.BLACK);
        }

        add(setupServerButton, gbc);
        gbc.gridy++;
        add(createServiceListButton, gbc);
        gbc.gridy++;
        add(startRunningButton, gbc);
        gbc.gridy++;
        add(createReportButton, gbc);

        // Agregar acción para el botón "Create Report"
        createReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createReport();
            }
        });
    }

    private void createReport() {
        // Crear una instancia del generador de informes
        ReportGenerator reportGenerator = new ReportGenerator();
        // Llamar al método para generar el informe
        String reportStatus = reportGenerator.generateReport();
        // Mostrar el estado en un diálogo
        JOptionPane.showMessageDialog(this, reportStatus);
    }
}
