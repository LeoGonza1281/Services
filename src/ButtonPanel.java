import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {
    public JButton setupServerButton;
    public JButton createServiceListButton;
    public JButton startRunningButton;
    public JButton createReportButton;

    public ButtonPanel() {
        // Usar un GridBagLayout para centrar los botones
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Espaciado entre los botones
        gbc.anchor = GridBagConstraints.CENTER; // Centrar los botones

        // Botones principales
        setupServerButton = new JButton("Setup Server");
        createServiceListButton = new JButton("Create ServiceList");
        startRunningButton = new JButton("Start Running");
        createReportButton = new JButton("Create Report");

        // Configurar estilos de los botones
        setupServerButton.setPreferredSize(new Dimension(120, 30)); // Tamaño personalizado
        createServiceListButton.setPreferredSize(new Dimension(120, 30));
        startRunningButton.setPreferredSize(new Dimension(120, 30));
        createReportButton.setPreferredSize(new Dimension(120, 30));

        // Estilo de botones
        for (JButton button : new JButton[]{setupServerButton, createServiceListButton, startRunningButton, createReportButton}) {
            button.setForeground(Color.BLACK);
        }

        // Añadir los botones al panel
        gbc.gridy = 0; // Fila inicial
        add(setupServerButton, gbc);

        gbc.gridy++;
        add(createServiceListButton, gbc);

        gbc.gridy++;
        add(startRunningButton, gbc);

        gbc.gridy++;
        add(createReportButton, gbc);
    }
}
